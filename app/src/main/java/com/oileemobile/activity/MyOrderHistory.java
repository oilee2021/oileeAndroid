package com.oileemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.transition.TransitionManager;

import java.util.ArrayList;
import java.util.List;

import com.oileemobile.R;
import com.oileemobile.adapters.OrderHistoryAdapter;
import com.oileemobile.databinding.ActivityMyOrderHistoryBinding;
import com.oileemobile.interfaces.RecyclerViewItemClickListener;
import com.oileemobile.models.OrderHistoryModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.OrderHistoryResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.Utils;

public class MyOrderHistory extends BaseActivity implements View.OnClickListener, RecyclerViewItemClickListener {
    private ActivityMyOrderHistoryBinding binding;
    private int selectedItem;
    private List<OrderHistoryModel> orderList;
    private OrderHistoryAdapter mAdapter;
    private int lastClickedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_order_history);
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.oilee_history);

        selectedItem = -1;
        orderList = new ArrayList<>();
        mAdapter = new OrderHistoryAdapter(orderList, this);
        binding.recyclerOrderList.setAdapter(mAdapter);

        binding.textHistoryPending.setOnClickListener(this);
        binding.textHistoryCompleted.setOnClickListener(this);
        binding.textHistoryCancelled.setOnClickListener(this);

        binding.textHistoryPending.callOnClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_history_pending:
                if(selectedItem != 0) {
                    setItemSelected(0, true);
                    callPendingOrdersApi();
                }
                break;

            case R.id.text_history_completed:
                if(selectedItem != 1) {
                    setItemSelected(1, true);
                    callCompletedOrdersApi();
                }
                break;

            case R.id.text_history_cancelled:
                if(selectedItem != 2) {
                    setItemSelected(2, true);
                    callCancelledOrdersApi();
                }
                break;
        }
    }

    private void checkForListSize(List<OrderHistoryModel> list) {
        binding.textNoResults.setVisibility(list.size() > 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onItemClick(int position, View view) {
        lastClickedItem = position;
        OrderHistoryModel model;
        model = orderList.get(position);
        Bundle bundle = new Bundle();
        //bundle.putSerializable("order_item", model);
        bundle.putInt(Constants.JOB_ID, model.getId());
        bundle.putInt(Constants.ACTIVITY_TYPE, selectedItem);
        ActivityController.startActivityForResult(this, OrderDetailActivity.class, bundle, 123);
    }

    private void setItemSelected(int position, boolean setSelected) {
        TextView textView;
        if (position == 0) textView = binding.textHistoryPending;
        else if (position == 1) textView = binding.textHistoryCompleted;
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

    private void callPendingOrdersApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_PENDING_ORDERS, new ApiCallBack<>(new ApiResponseListener<OrderHistoryResponse>() {
            @Override
            public void onApiSuccess(OrderHistoryResponse response) {
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
                Toast.makeText(MyOrderHistory.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), true);
    }

    private void callCompletedOrdersApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_COMPLETED_ORDERS                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       , new ApiCallBack<>(new ApiResponseListener<OrderHistoryResponse>() {
            @Override
            public void onApiSuccess(OrderHistoryResponse response) {
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
                Toast.makeText(MyOrderHistory.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), true);
    }

    private void callCancelledOrdersApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_CANCELLED_ORDERS                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       , new ApiCallBack<>(new ApiResponseListener<OrderHistoryResponse>() {
            @Override
            public void onApiSuccess(OrderHistoryResponse response) {
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
                Toast.makeText(MyOrderHistory.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();


            }
        }), true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == RESULT_OK) {
            orderList.remove(lastClickedItem);
            mAdapter.notifyItemRemoved(lastClickedItem);
            checkForListSize(orderList);
        }
    }
}
