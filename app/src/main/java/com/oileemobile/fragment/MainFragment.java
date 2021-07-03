package com.oileemobile.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.activity.AddAddressActivity;
import com.oileemobile.activity.AddVehicleActivity;
import com.oileemobile.activity.MyAddressActivity;
import com.oileemobile.activity.MyVehiclesActivity;
import com.oileemobile.databinding.FragmentMainBinding;
import com.oileemobile.models.AddressModel;
import com.oileemobile.models.VehicleModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.UserAllAddressResponse;
import com.oileemobile.network.response.UserAllVehiclesResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.Utils;

import static android.view.View.GONE;

public class MainFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private FragmentMainBinding binding;
    private OnMainFragmentInteractionListener mListener;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    //private AddressResultReceiver resultReceiver;
    /*private List<AddressModel> addressList;
    private List<VehicleModel> vehicleList;
    private AddressListAdapter addressAdapter;
    private VehicleListAdapter vehicleAdapter;*/

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        setupMaps();
        /*addressList = new ArrayList<>();
        vehicleList = new ArrayList<>();*/

        /*addressAdapter = new AddressListAdapter(addressList, null);
        binding.selectAddressList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.selectAddressList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        binding.selectAddressList.setAdapter(addressAdapter);*/

        /*binding.selectVehicleList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.selectVehicleList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));*/

        binding.homepageOilChange.setOnClickListener(this);
        binding.defaultAddress.getRoot().setOnClickListener(this);
        binding.defaultVehicle.getRoot().setOnClickListener(this);

        return binding.getRoot();
    }

    private void setupMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //resultReceiver = new AddressResultReceiver(new Handler());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        setupLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null)
                    return;

                try {
                    setCurrentLocation(locationResult.getLocations().get(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        if(Utils.isLocationPermissionGranted(getActivity()))
            startLocationUpdates();
        else
            Utils.requestLocationPermission(this);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if(Utils.isLocationPermissionGranted(getActivity())) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
        }
    }

    private void setCurrentLocation(Location location) {
        mCurrentLocation = location;
        setMapLocation();
        //startAddressIntentService(mCurrentLocation);
    }

    /*private void startAddressIntentService(Location location) {
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.RECEIVER, resultReceiver);
        intent.putExtra(FetchAddressIntentService.LOCATION_DATA_EXTRA, location);
        getActivity().startService(intent);
    }*/

    private void animateCamera() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 18.0f));
    }

    @SuppressLint("MissingPermission")
    private void setMapLocation() {
        if(mMap != null) {
            if(Utils.isLocationPermissionGranted(getActivity())) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setCompassEnabled(false);
            }

            animateCamera();
        }
    }

    private void setupLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setNumUpdates(1);
    }

    @Override
    public void onStart() {
        super.onStart();
        getSavedAddress();
        getSavedVehicles();
    }

    private void toggleLoadingVisibility(ImageView arrow, ProgressBar progressBar) {
        arrow.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void getSavedAddress() {
        ServiceManager.callServerApi(getActivity(), ServiceManager.API_GET_ALL_USER_ADDRESS, new ApiCallBack<>(new ApiResponseListener<UserAllAddressResponse>() {
            @Override
            public void onApiSuccess(UserAllAddressResponse response) {
                toggleLoadingVisibility(binding.ivAddressArrow, binding.pbAddress);
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    List<AddressModel> addressList = response.getData();
                    boolean addressFound = false;
                    for(AddressModel addressModel : addressList) {
                        if(addressModel.isIsDefault()) {
                            binding.defaultAddress.addressType.setText(addressModel.getTag());
                            binding.defaultAddress.addressDesc.setText(addressModel.getFullAddress());
                            binding.defaultAddress.textDefault.setVisibility(View.VISIBLE);
                            addressFound = true;
                            break;
                        }
                    }
                    //TransitionManager.beginDelayedTransition(binding.mainView);
                    /*addressAdapter = new AddressListAdapter(addressList, onAddressSelectedListener);
                    binding.selectAddressList.setAdapter(addressAdapter);*/
                    binding.noAddressSection.setVisibility(addressFound ? View.GONE : View.VISIBLE);
                    binding.defaultAddress.getRoot().setVisibility(addressFound ? View.VISIBLE : GONE);
                    binding.noAddressSection.setOnClickListener(MainFragment.this);
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                toggleLoadingVisibility(binding.ivAddressArrow, binding.pbAddress);
            }
        }), false);
    }

    private void getSavedVehicles() {
        ServiceManager.callServerApi(getActivity(), ServiceManager.API_GET_ALL_USER_VEHICLE, new ApiCallBack<>(new ApiResponseListener<UserAllVehiclesResponse>() {
            @Override
            public void onApiSuccess(UserAllVehiclesResponse response) {
                toggleLoadingVisibility(binding.ivVehicleArrow, binding.pbVehicle);
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    List<VehicleModel> vehicleList = response.getData();
                    boolean vehicleFound = false;
                    for(VehicleModel vehicleModel : vehicleList) {
                        if(vehicleModel.isIsDefault()) {
                            binding.defaultVehicle.vehicleName.setText(vehicleModel.getMake());
                            String desc = vehicleModel.getYear() + " | " + vehicleModel.getModel();
                            binding.defaultVehicle.vehicleDesc.setText(desc);
                            Utils.loadPicassoImage(vehicleModel.getVehicle_image(), binding.defaultVehicle.vehicleImage,
                                    binding.defaultVehicle.progressBar, R.drawable.car_demo);
                            binding.defaultVehicle.textDefault.setVisibility(View.VISIBLE);
                            vehicleFound = true;
                            break;
                        }
                    }
                    //TransitionManager.beginDelayedTransition(binding.mainView);
                    /*vehicleAdapter = new VehicleListAdapter(vehicleList, onVehicleSelectedListener);
                    binding.selectVehicleList.setAdapter(vehicleAdapter);*/
                    binding.noVehicleSection.setVisibility(vehicleFound ? View.GONE : View.VISIBLE);
                    binding.defaultVehicle.getRoot().setVisibility(vehicleFound ? View.VISIBLE : GONE);
                    binding.noVehicleSection.setOnClickListener(MainFragment.this);
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                toggleLoadingVisibility(binding.ivVehicleArrow, binding.pbVehicle);
            }
        }), false);
    }

    /*private VehicleListAdapter.OnVehicleSelectedListener onVehicleSelectedListener = position ->
            ActivityController.startActivity(getActivity(), MyVehiclesActivity.class);

    private AddressListAdapter.OnAddressSelectedListener onAddressSelectedListener = position ->
            ActivityController.startActivity(getActivity(), MyAddressActivity.class);*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homepage_oil_change:
                if (mListener != null) {
                    mListener.onOilChangeButtonPressed();
                }
                break;

            case R.id.no_address_section:
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.IS_FIRST_ITEM, true);
                ActivityController.startActivity(getActivity(), AddAddressActivity.class, bundle);
                break;

            case R.id.no_vehicle_section:
                bundle = new Bundle();
                bundle.putBoolean(Constants.IS_FIRST_ITEM, true);
                ActivityController.startActivity(getActivity(), AddVehicleActivity.class, bundle);
                break;

            case R.id.default_address:
                ActivityController.startActivity(getActivity(), MyAddressActivity.class);
                break;

            case R.id.default_vehicle:
                ActivityController.startActivity(getActivity(), MyVehiclesActivity.class);
                break;
        }
    }

    /*class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if(resultData == null)
                return;

            String result = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY);
            String currentAddress = getString(R.string.address_unavailable);
            if(result != null) {
                if(resultCode != FetchAddressIntentService.RESULT_FAILURE)
                    currentAddress = result;
            }

            binding.homepageAddress.setText(currentAddress);
        }
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == 123) {
                if(data != null) {
                    AddressModel addressModel = (AddressModel) data.getSerializableExtra("address");
                    addressList.add(addressModel);
                    addressAdapter.notifyDataSetChanged();
                    binding.noAddressSection.setVisibility(GONE);
                    binding.selectAddressList.setVisibility(View.VISIBLE);
                }
            } else if (requestCode == 124) {
                if(data != null) {
                    VehicleModel vehicleModel = (VehicleModel) data.getSerializableExtra("vehicle");
                    vehicleList.add(vehicleModel);
                    vehicleAdapter.notifyDataSetChanged();
                    binding.noVehicleSection.setVisibility(GONE);
                    binding.selectVehicleList.setVisibility(View.VISIBLE);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Utils.LOCATION_PERMISSION_REQUEST_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startLocationUpdates();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnMainFragmentInteractionListener) {
            mListener = (OnMainFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMainFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnMainFragmentInteractionListener {
        void onOilChangeButtonPressed();
    }
}
