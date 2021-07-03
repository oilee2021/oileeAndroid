package com.oileemobile.activity.technician;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.transition.TransitionManager;

import java.util.ArrayList;
import java.util.List;

import com.oileemobile.R;
import com.oileemobile.activity.BaseActivity;
import com.oileemobile.adapters.technician.TechnicianOrderHistoryAdapter;
import com.oileemobile.databinding.ActivityTechnicianBookingHistoryBinding;
import com.oileemobile.interfaces.RecyclerViewItemClickListener;
import com.oileemobile.models.technician.TechnicianOrderHistoryModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.technician.TechnicianOrderHistoryResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.Utils;

public class TechnicianBookingHistory extends BaseActivity implements View.OnClickListener, RecyclerViewItemClickListener {
    private ActivityTechnicianBookingHistoryBinding binding;
    private int selectedItem;
    private int lastClickedItem;
    private TechnicianOrderHistoryAdapter mAdapter;
    private List<TechnicianOrderHistoryModel> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_technician_booking_history);
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.oilee_history);

        selectedItem = -1;
        orderList = new ArrayList<>();
        mAdapter = new TechnicianOrderHistoryAdapter(orderList, this);
        binding.recyclerOrderList.setAdapter(mAdapter);

        binding.textHistoryCompleted.setOnClickListener(this);
        binding.textHistoryCancelled.setOnClickListener(this);

        binding.textHistoryCompleted.callOnClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_history_completed:
                if(selectedItem != 0) {
                    setItemSelected(0, true);
                    callCompletedOrdersApi();
                }
                break;

            case R.id.text_history_cancelled:
                if(selectedItem != 1) {
                    setItemSelected(1, true);
                    callCancelledOrdersApi();
                }
                break;
        }
    }

    @Override
    public void onItemClick(int position, View view) {
        lastClickedItem = position;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.JOB_ID, orderList.get(position).getId());
        ActivityController.startActivityForResult(this, TechnicianJobDetailActivity.class, bundle, 123);
    }

    private void setItemSelected(int position, boolean setSelected) {
        TextView textView;
        if (position == 0) textView = binding.textHistoryCompleted;
        else textView = binding.textHistoryCancelled;

        if (setSelected) {
            if(selectedItem != position) {
                TransitionManager.beginDelayedTransition(binding.mainView);
                textView.setBackgroundResource(R.drawable.background_rounded_white);
                textView.setTextColor(ContextCompat.getColor(this, R.color.lightBlack));
                if (selectedItem != -1) setItemSelected(selectedItem, false);
                selectedItem = position;
            }
        } else {
            textView.setBackgroundResource(0);
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }
    }

    private void checkForListSize(List<TechnicianOrderHistoryModel> list) {
        binding.textNoResults.setVisibility(list.size() > 0 ? View.INVISIBLE : View.VISIBLE);
    }

    private void callPendingOrdersApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_TECHNICIAN_PENDING_ORDERS, new ApiCallBack<>(new ApiResponseListener<TechnicianOrderHistoryResponse>() {
            @Override
            public void onApiSuccess(TechnicianOrderHistoryResponse response) {
                orderList.clear();
                if(response.getStatus() == 200) {
                    if(response.getData() != null) {
                        orderList.addAll(response.getData());
                    }
                }
                mAdapter.setItems(orderList);
                checkForListSize(orderList);
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(TechnicianBookingHistory.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), true);
    }

    private void callCompletedOrdersApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_TECHNICIAN_COMPLETED_ORDERS, new ApiCallBack<>(new ApiResponseListener<TechnicianOrderHistoryResponse>() {
            @Override
            public void onApiSuccess(TechnicianOrderHistoryResponse response) {
                orderList.clear();
                if(response.getStatus() == 200) {
                    if(response.getData() != null) {
                        orderList.addAll(response.getData());
                    }
                }
                mAdapter.setItems(orderList);
                checkForListSize(orderList);
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(TechnicianBookingHistory.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), true);
    }

    private void callCancelledOrdersApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_TECHNICIAN_CANCELLED_ORDERS, new ApiCallBack<>(new ApiResponseListener<TechnicianOrderHistoryResponse>() {
            @Override
            public void onApiSuccess(TechnicianOrderHistoryResponse response) {
                orderList.clear();
                if(response.getStatus() == 200) {
                    if(response.getData() != null) {
                        orderList.addAll(response.getData());
                    }
                }
                mAdapter.setItems(orderList);
                checkForListSize(orderList);
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(TechnicianBookingHistory.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), true);
    }
}
