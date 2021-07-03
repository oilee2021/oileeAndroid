package com.oileemobile.fragment.technician;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.oileemobile.R;
import com.oileemobile.activity.technician.TechnicianJobDetailActivity;
import com.oileemobile.activity.technician.TechnicianMainActivity;
import com.oileemobile.adapters.technician.TechnicianJobAdapter;
import com.oileemobile.databinding.FragmentTechnicianMainBinding;
import com.oileemobile.dialogs.InformationDialog;
import com.oileemobile.dialogs.TipDialogFragment;
import com.oileemobile.interfaces.RecyclerViewItemClickListener;
import com.oileemobile.models.technician.TechnicianJobModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiRequest;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.ApiResponse;
import com.oileemobile.network.response.technician.BackgroundCheckResponse;
import com.oileemobile.network.response.technician.TechnicianJobsResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.ProgressDialog;
import com.oileemobile.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class TechnicianMainFragment extends Fragment implements RecyclerViewItemClickListener, View.OnClickListener, TipDialogFragment.OnTechnicianTipListener {

    private FragmentTechnicianMainBinding binding;
    private List<TechnicianJobModel> jobList;
    private TechnicianJobAdapter jobAdapter;
    private Handler mHandler;
    private Runnable mRunnable;
    private int lastItemClicked;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    private boolean isFromSettings;
    private Call verificationApiCall;

    public TechnicianMainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_technician_main, container, false);

        binding.textHomeTitle.setText(getGreetingsText());
        binding.textHomeSubtitle.setText(getDayText());
        lastItemClicked = -1;
        jobList = new ArrayList<>();
        jobAdapter = new TechnicianJobAdapter(jobList, this);
        binding.recyclerJobList.setAdapter(jobAdapter);
        mHandler = new Handler();
        mRunnable = () -> {
            callTechnicianBackgroundCheckApi();
            mHandler.postDelayed(mRunnable, Constants.MAX_INTERVAL);
        };
        updateHeaderView(150, false);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        setupLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null) {
                    Toast.makeText(getActivity(), R.string.unable_to_fetch_location, Toast.LENGTH_SHORT).show();
                    ProgressDialog.hideProgressDialog();
                    return;
                }

                try {
                    Location location = locationResult.getLocations().get(0);
                    callUpdateLocationApi(location.getLatitude(), location.getLongitude());
                    //setCurrentLocation(locationResult.getLocations().get(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        binding.buttonGrantPermission.setOnClickListener(this);
        if(Utils.isLocationPermissionGranted(getActivity())) {
            startLocationUpdates();
            //callGetTechnicianJobsApi(true);
        } else {
            binding.linearPermission.setVisibility(View.VISIBLE);
        }

        TipDialogFragment.newInstance(String.valueOf(0), 0, null);

        return binding.getRoot();
    }

    @Override
    public void onTechnicianTip(double amount) {

    }

    @Override
    public void onStart() {
        super.onStart();
        if(Utils.isLocationPermissionGranted(getActivity())) {
            binding.linearPermission.setVisibility(View.INVISIBLE);
            if(isFromSettings) {
                isFromSettings = false;
                ProgressDialog.showProgressDialog(getActivity());
                startLocationUpdates();
            } else {
                mHandler.postDelayed(mRunnable, Constants.MAX_INTERVAL);
            }
        } else {
            binding.linearPermission.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        mHandler.removeCallbacks(mRunnable);
        super.onStop();
    }

    private void setupLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setNumUpdates(1);
    }

    private void updateHeaderView(int height, boolean showProgress) {
        if(getActivity() instanceof TechnicianMainActivity) {
            ((TechnicianMainActivity) getActivity()).updateHeaderView(height, showProgress);
        }
    }

    private String getGreetingsText() {
        String greeting;
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour > 4 && hour < 12) {
            greeting = "Good Morning, ";
        } else if (hour > 11 && hour < 17) {
            greeting = "Good Afternoon, ";
        } else {
            greeting = "Good Evening, ";
        }

        return greeting + PrefManager.getString(PrefManager.USER_NAME);
    }

    private String getDayText() {
        return "It's " + new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
    }

    private void updateJobCountText() {
        try {
            String subTitle = getDayText() + " and you have " + jobList.size() + " new " +
                    getResources().getQuantityString(R.plurals.jobs, jobList.size()) + " for today.";
            binding.textHomeSubtitle.setText(subTitle);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_grant_permission) {
            Utils.requestLocationPermission(this);
        }
    }

    @Override
    public void onItemClick(int position, View view) {
        lastItemClicked = position;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.JOB_ID, jobList.get(position).getId());
        ActivityController.startActivityForResult(this, TechnicianJobDetailActivity.class, bundle, false, false, 123);
    }

    private void callUpdateLocationApi(double lat, double lng) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setLat(String.valueOf(lat));
        apiRequest.setLng(String.valueOf(lng));

        ServiceManager.callServerApi(getActivity(), ServiceManager.API_UPDATE_TECHNICIAN_LOCATION, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    //callTechnicianVerificationApi(true);
                    callTechnicianBackgroundCheckApi();
                } else {
                    Utils.showSnackbar(getActivity(), response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(getActivity(), failureMessage);
            }
        }), apiRequest, false);
    }

    private void callTechnicianVerificationApi(boolean isFirst) {
        if (verificationApiCall != null) verificationApiCall.cancel();
        verificationApiCall = ServiceManager.callServerApi(getActivity(), ServiceManager.API_TECHNICIAN_VERIFICATION_API, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    if(!response.getMessage().toLowerCase().contains("not activated")) {
                        PrefManager.putBoolean(PrefManager.IS_ACTIVATED, true);
                        callGetTechnicianJobsApi(isFirst);
                    } else {
                        PrefManager.putBoolean(PrefManager.IS_ACTIVATED, false);
                        binding.linearReview.setVisibility(View.VISIBLE);
                        binding.recyclerJobList.setVisibility(View.GONE);
                        binding.textHomeTitle.setText(getGreetingsText());
                        binding.textHomeSubtitle.setText(getDayText());
                        updateHeaderView(150, true);
                        ProgressDialog.hideProgressDialog();
                    }
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                ProgressDialog.hideProgressDialog();
            }
        }), false);
    }

    private void callTechnicianBackgroundCheckApi() {
        ServiceManager.callServerApi(getActivity(), ServiceManager.API_TECHNICIAN_BACKGROUND_CHECK, new ApiCallBack<>(new ApiResponseListener<BackgroundCheckResponse>() {
            @Override
            public void onApiSuccess(BackgroundCheckResponse response) {
                if(response.getData().getStatus().equalsIgnoreCase("SUCCESS")) {
                    callTechnicianVerificationApi(true);
                } else {
                    binding.linearReview.setVisibility(View.VISIBLE);
                    binding.recyclerJobList.setVisibility(View.GONE);
                    binding.textHomeTitle.setText(getGreetingsText());
                    binding.textHomeSubtitle.setText(getDayText());
                    updateHeaderView(150, true);
                    ProgressDialog.hideProgressDialog();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                binding.linearReview.setVisibility(View.VISIBLE);
                binding.recyclerJobList.setVisibility(View.GONE);
                binding.textHomeTitle.setText(getGreetingsText());
                binding.textHomeSubtitle.setText(getDayText());
                updateHeaderView(150, true);
                ProgressDialog.hideProgressDialog();
            }
        }), PrefManager.getString(PrefManager.REQUISITION_ID));
    }

    private void callGetTechnicianJobsApi(boolean isFirst) {
        if(PrefManager.getBoolean(PrefManager.IS_ACTIVATED)) {
            binding.linearPermission.setVisibility(View.INVISIBLE);
            binding.linearReview.setVisibility(View.INVISIBLE);
            ServiceManager.callServerApi(getActivity(), ServiceManager.API_TECHNICIAN_JOBS, new ApiCallBack<>(new ApiResponseListener<TechnicianJobsResponse>() {
                @Override
                public void onApiSuccess(TechnicianJobsResponse response) {
                    ProgressDialog.hideProgressDialog();
                    if (isFirst) {
                        mHandler.postDelayed(mRunnable, Constants.MAX_INTERVAL);
                    }
                    jobList.clear();
                    jobList.addAll(response.getData());
                    jobAdapter.notifyDataSetChanged();
                    updateJobCountText();
                    if (jobList.size() > 0) {
                        binding.textNoJobs.setVisibility(View.INVISIBLE);
                        binding.recyclerJobList.setVisibility(View.VISIBLE);
                        updateHeaderView(200, isFirst);
                    } else {
                        binding.textNoJobs.setVisibility(View.VISIBLE);
                        updateHeaderView(150, true);
                    }
                }

                @Override
                public void onApiFailure(String failureMessage) {
                    ProgressDialog.hideProgressDialog();
                    if (isFirst) {
                        Utils.showSnackbar(getActivity(), failureMessage);
                    }
                }
            }), false);
        } else {
            ProgressDialog.hideProgressDialog();
        }
    }

    private void startLocationUpdates() {
        if(Utils.isLocationPermissionGranted(getActivity())) {
            ProgressDialog.showProgressDialog(getActivity());
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
        }
    }

    private void openAppSettings() {
        isFromSettings = true;
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }

    private void showConfirmationDialog() {
        new InformationDialog(R.string.mission_permission, R.string.missing_location_permission,
                R.string.open_settings, this::openAppSettings).show(getChildFragmentManager(), "");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Utils.LOCATION_PERMISSION_REQUEST_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.linearPermission.setVisibility(View.INVISIBLE);
                startLocationUpdates();
            } else {
                if(!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showConfirmationDialog();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == Activity.RESULT_OK) {
            jobList.remove(lastItemClicked);
            jobAdapter.notifyItemRemoved(lastItemClicked);
            updateJobCountText();
            if(jobList.isEmpty()) binding.textNoJobs.setVisibility(View.VISIBLE);
        }
    }
}
