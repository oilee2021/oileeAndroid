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
import com.oileemobile.databinding.ActivityMyAddressBinding;
import com.oileemobile.models.AddressModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.UserAllAddressResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.Utils;

public class MyAddressActivity extends BaseActivity implements AddressListAdapter.OnAddressSelectedListener,
        View.OnClickListener {

    private ActivityMyAddressBinding binding;
    private List<AddressModel> addressList = new ArrayList<>();
    private AddressListAdapter addressAdapter;
    private int lastClickedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_address);
        setupToolbar();
        callGetMyAddressApi(true);

        binding.addNewAddress.addMoreText.setText(R.string.add_new_address);
        binding.addNewAddress.getRoot().setOnClickListener(this);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.saved_addresses);
        binding.myToolbar.getRoot().setBackgroundResource(R.drawable.main_background_gradient);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.add_new_address) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.IS_FIRST_ITEM, addressList.isEmpty());
            ActivityController.startActivityForResult(this, AddAddressActivity.class, bundle, 121);
        }
    }

    @Override
    public void onAddressSelected(int position) {
        lastClickedItem = position;
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ACTIVITY_TYPE, AddAddressActivity.EDIT_ADDRESS);
        bundle.putSerializable("address_model", addressList.get(position));
        ActivityController.startActivityForResult(this, AddAddressActivity.class, bundle, 122);
    }

    private void callGetMyAddressApi(boolean showProgress) {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_ALL_USER_ADDRESS, new ApiCallBack<>(new ApiResponseListener<UserAllAddressResponse>() {
            @Override
            public void onApiSuccess(UserAllAddressResponse response) {
                if(response.getData() != null) {
                    addressList = response.getData();
                    if (addressList.size() > 0) {
                        binding.rvAddress.setLayoutManager(new LinearLayoutManager(MyAddressActivity.this));
                        binding.rvAddress.addItemDecoration(new DividerItemDecoration(MyAddressActivity.this, DividerItemDecoration.VERTICAL));
                        addressAdapter = new AddressListAdapter(addressList, MyAddressActivity.this);
                        TransitionManager.beginDelayedTransition(binding.mainView);
                        binding.rvAddress.setAdapter(addressAdapter);
                        binding.textNoResults.setVisibility(View.INVISIBLE);
                        binding.rvAddress.setVisibility(View.VISIBLE);
                    } else {
                        binding.textNoResults.setVisibility(View.VISIBLE);
                        binding.rvAddress.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(MyAddressActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), showProgress);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 121) {
            if (data != null) {
                if(resultCode == RESULT_OK) {
                    callGetMyAddressApi(false);
                }
            }
        } else if(requestCode == 122) {
            if(resultCode == 2) {
                addressList.remove(lastClickedItem);
                addressAdapter.notifyDataSetChanged();
                callGetMyAddressApi(false);
            } else if(resultCode == RESULT_OK) {
                callGetMyAddressApi(false);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
