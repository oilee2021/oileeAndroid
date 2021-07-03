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
import com.oileemobile.adapters.VehicleListAdapter;
import com.oileemobile.databinding.ActivitySelectVehicleBinding;
import com.oileemobile.models.AddressModel;
import com.oileemobile.models.VehicleModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.UserAllVehiclesResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.Utils;

public class SelectVehicleActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySelectVehicleBinding binding;
    private List<VehicleModel> dataList;
    private VehicleListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_vehicle);

        setupToolbar();
        dataList = new ArrayList<>();
        adapter = new VehicleListAdapter(dataList, position -> {
            Bundle bundle = new Bundle();
            AddressModel addressModel = (AddressModel) getIntent().getSerializableExtra("address");
            bundle.putSerializable("address", addressModel);
            bundle.putSerializable("vehicle", dataList.get(position));
            ActivityController.startActivity(SelectVehicleActivity.this, SelectDateTimeActivity.class, bundle, false, false, false);
        });
        binding.selectVehicleList.setAdapter(adapter);

        binding.noVehicleSection.setOnClickListener(this);
        binding.selectVehicleList.setLayoutManager(new LinearLayoutManager(this));
        binding.selectVehicleList.addItemDecoration(new DividerItemDecoration(SelectVehicleActivity.this, DividerItemDecoration.VERTICAL));
        binding.addNewVehicle.addMoreText.setText(R.string.add_new_vehicle);
        binding.addNewVehicleSection.setOnClickListener(this);
        callGetMyVehiclesApi(true);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.select_vehicle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no_vehicle_section:
            case R.id.add_new_vehicle_section:
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.IS_FIRST_ITEM, dataList.size() == 0);
                ActivityController.startActivityForResult(this, AddVehicleActivity.class, bundle, 123);
                break;
        }
    }

    private void callGetMyVehiclesApi(boolean showProgress) {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_ALL_USER_VEHICLE, new ApiCallBack<>(new ApiResponseListener<UserAllVehiclesResponse>() {
            @Override
            public void onApiSuccess(UserAllVehiclesResponse response) {
                binding.cardVehicles.setVisibility(View.VISIBLE);
                if(response.getData() != null) {
                    dataList = response.getData();
                    if(dataList.size() > 0) {
                        TransitionManager.beginDelayedTransition(binding.mainView);
                        adapter = new VehicleListAdapter(dataList, position -> {
                            Bundle bundle = new Bundle();
                            AddressModel addressModel = (AddressModel) getIntent().getSerializableExtra("address");
                            bundle.putSerializable("address", addressModel);
                            bundle.putSerializable("vehicle", dataList.get(position));
                            ActivityController.startActivity(SelectVehicleActivity.this, SelectDateTimeActivity.class, bundle, false, false, false);
                        });
                        binding.selectVehicleList.setAdapter(adapter);
                        binding.noVehicleSection.setVisibility(View.GONE);
                        binding.addNewVehicleSection.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(SelectVehicleActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), showProgress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 123) {
            if(resultCode == RESULT_OK) {
                binding.noVehicleSection.setVisibility(View.GONE);
                binding.selectVehicleList.setVisibility(View.VISIBLE);

                if(data != null) {
                    VehicleModel vehicleModel = (VehicleModel) data.getSerializableExtra("vehicle");
                    dataList.add(0, vehicleModel);
                    adapter.notifyDataSetChanged();
                    binding.noVehicleSection.setVisibility(View.GONE);
                    binding.addNewVehicleSection.setVisibility(View.VISIBLE);
                    callGetMyVehiclesApi(false);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
