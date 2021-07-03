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
import com.oileemobile.adapters.AddressListAdapter;
import com.oileemobile.databinding.ActivitySelectAddressBinding;
import com.oileemobile.models.AddressModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.UserAllAddressResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.Utils;

import static com.oileemobile.adapters.AddressListAdapter.OnAddressSelectedListener;

public class SelectAddressActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySelectAddressBinding binding;
    private List<AddressModel> dataList;
    private AddressListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_address);

        setupToolbar();
        dataList = new ArrayList<>();
        binding.noAddressSection.setOnClickListener(this);

        binding.selectAddressList.setLayoutManager(new LinearLayoutManager(this));
        binding.selectAddressList.addItemDecoration(new DividerItemDecoration(SelectAddressActivity.this, DividerItemDecoration.VERTICAL));
        binding.addNewAddress.addMoreText.setText(R.string.add_new_address);
        binding.addNewAddressSection.setOnClickListener(this);
        adapter = new AddressListAdapter(dataList, onAddressSelectedListener);
        callGetMyAddressApi(true);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.select_address);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no_address_section:
            case R.id.add_new_address_section:
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.IS_FIRST_ITEM, dataList.size() == 0);
                ActivityController.startActivityForResult(this, AddAddressActivity.class, bundle, 123);
                break;
        }
    }

    private OnAddressSelectedListener onAddressSelectedListener = new OnAddressSelectedListener() {
        @Override
        public void onAddressSelected(int position) {
            Class klass = SelectDateTimeActivity.class;
            Bundle bundle = getIntent().getExtras();
            if(bundle == null) {
                bundle = new Bundle();
                klass = SelectVehicleActivity.class;
            }

            bundle.putSerializable("address", dataList.get(position));
            ActivityController.startActivity(SelectAddressActivity.this, klass, bundle, false, false, false);
        }
    };

    private void callGetMyAddressApi(boolean showProgress) {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_ALL_USER_ADDRESS, new ApiCallBack<>(new ApiResponseListener<UserAllAddressResponse>() {
            @Override
            public void onApiSuccess(UserAllAddressResponse response) {
                binding.cardAddress.setVisibility(View.VISIBLE);
                if(response.getData() != null) {
                    dataList = response.getData();
                    if(dataList.size() > 0) {
                        TransitionManager.beginDelayedTransition(binding.mainView);
                        adapter = new AddressListAdapter(dataList, onAddressSelectedListener);
                        binding.selectAddressList.setAdapter(adapter);
                        binding.noAddressSection.setVisibility(View.GONE);
                        binding.addNewAddressSection.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(SelectAddressActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), showProgress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 123) {
            if(resultCode == RESULT_OK) {
                binding.noAddressSection.setVisibility(View.GONE);
                binding.selectAddressList.setVisibility(View.VISIBLE);

                if(data != null) {
                    AddressModel addressModel = (AddressModel) data.getSerializableExtra("address");
                    dataList.add(0, addressModel);
                    adapter.notifyDataSetChanged();
                    binding.noAddressSection.setVisibility(View.GONE);
                    binding.addNewAddressSection.setVisibility(View.VISIBLE);
                    callGetMyAddressApi(false);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
