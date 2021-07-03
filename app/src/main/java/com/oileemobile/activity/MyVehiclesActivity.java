package com.oileemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.transition.TransitionManager;

import java.util.ArrayList;
import java.util.List;

import com.oileemobile.R;
import com.oileemobile.activity.technician.AddTechnicianVehicle;
import com.oileemobile.adapters.VehicleListAdapter;
import com.oileemobile.databinding.ActivityMyVehiclesBinding;
import com.oileemobile.models.VehicleModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.UserAllVehiclesResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.Utils;

public class MyVehiclesActivity extends BaseActivity implements VehicleListAdapter.OnVehicleSelectedListener,
        View.OnClickListener {

    private ActivityMyVehiclesBinding binding;
    private List<VehicleModel> vehicleList;
    private VehicleListAdapter vehicleAdapter;
    private int lastClickedItem;
    private int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_vehicles);
        setupToolbar();

        binding.addNewVehicle.addMoreText.setText(R.string.add_new_vehicle);
        binding.addNewVehicle.getRoot().setOnClickListener(this);

        userType = PrefManager.getInt(PrefManager.USER_TYPE, Constants.TYPE_CUSTOMER);
        vehicleList = new ArrayList<>();
        vehicleAdapter = new VehicleListAdapter(vehicleList, MyVehiclesActivity.this);
        callGetMyVehiclesApi(true);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.saved_vehicles);
        binding.myToolbar.getRoot().setBackgroundResource(R.drawable.main_background_gradient);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.add_new_vehicle) {
            if(userType == Constants.TYPE_CUSTOMER) {
                ActivityController.startActivityForResult(this, AddVehicleActivity.class, 121);
            } else {
                ActivityController.startActivityForResult(this, AddTechnicianVehicle.class, 121);
            }
        }
    }

    @Override
    public void onVehicleSelected(int position) {
        lastClickedItem = position;
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ACTIVITY_TYPE, AddVehicleActivity.EDIT_VEHICLE);
        bundle.putSerializable("vehicle_model", vehicleList.get(position));
        if(userType == Constants.TYPE_CUSTOMER) {
            ActivityController.startActivityForResult(this, AddVehicleActivity.class, bundle, 122);
        } else {
            ActivityController.startActivityForResult(this, AddTechnicianVehicle.class, bundle, 122);
        }
    }

    private void callGetMyVehiclesApi(boolean showProgress) {
        if(userType == Constants.TYPE_CUSTOMER) callGetCustomerVehiclesApi(showProgress);
        else callGetTechnicianVehiclesApi(showProgress);
    }

    private void callGetCustomerVehiclesApi(boolean showProgress) {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_ALL_USER_VEHICLE, new ApiCallBack<>(new ApiResponseListener<UserAllVehiclesResponse>() {
            @Override
            public void onApiSuccess(UserAllVehiclesResponse response) {
                if(response.getData() != null) {
                    vehicleList = response.getData();
                    binding.rvVehicle.setLayoutManager(new LinearLayoutManager(MyVehiclesActivity.this));
                    binding.rvVehicle.addItemDecoration(new DividerItemDecoration(MyVehiclesActivity.this, DividerItemDecoration.VERTICAL));
                    vehicleAdapter = new VehicleListAdapter(vehicleList, MyVehiclesActivity.this);
                    TransitionManager.beginDelayedTransition(binding.mainView);
                    binding.rvVehicle.setAdapter(vehicleAdapter);
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(MyVehiclesActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), showProgress);
    }

    private void callGetTechnicianVehiclesApi(boolean showProgress) {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_ALL_TECHNICIAN_VEHICLE, new ApiCallBack<>(new ApiResponseListener<UserAllVehiclesResponse>() {
            @Override
            public void onApiSuccess(UserAllVehiclesResponse response) {
                if(response.getData() != null) {
                    vehicleList = response.getData();
                    if (vehicleList.size() > 0) {
                        binding.rvVehicle.setLayoutManager(new LinearLayoutManager(MyVehiclesActivity.this));
                        binding.rvVehicle.addItemDecoration(new DividerItemDecoration(MyVehiclesActivity.this, DividerItemDecoration.VERTICAL));
                        vehicleAdapter = new VehicleListAdapter(vehicleList, MyVehiclesActivity.this);
                        TransitionManager.beginDelayedTransition(binding.mainView);
                        binding.rvVehicle.setAdapter(vehicleAdapter);
                        binding.textNoResults.setVisibility(View.INVISIBLE);
                        binding.rvVehicle.setVisibility(View.VISIBLE);
                    } else {
                        binding.textNoResults.setVisibility(View.VISIBLE);
                        binding.rvVehicle.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(MyVehiclesActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), showProgress);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 121) {
            if (data != null) {
                if(resultCode == RESULT_OK) {
                    callGetMyVehiclesApi(false);
                }
            }
        } else if(requestCode == 122) {
            if(resultCode == 2) {
                vehicleList.remove(lastClickedItem);
                vehicleAdapter.notifyDataSetChanged();
                callGetMyVehiclesApi(false);
            } else if(resultCode == RESULT_OK) {
                callGetMyVehiclesApi(false);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
