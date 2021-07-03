package com.oileemobile.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import java.util.ArrayList;
import java.util.List;

import com.oileemobile.R;
import com.oileemobile.adapters.CustomerNotificationAdapter;
import com.oileemobile.databinding.ActivityNotificationBinding;
import com.oileemobile.models.CustomerNotificationModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.ApiResponse;
import com.oileemobile.network.response.CustomerNotificationResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.Utils;

public class NotificationActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {

    private ActivityNotificationBinding binding;
    private List<CustomerNotificationModel> notificationList;
    private CustomerNotificationAdapter adapter;
    private PopupMenu popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        setupToolbar();
        setupPopupMenu();
        setupRecyclerView();
        callCustomerNotificationApi();
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarMenu.setVisibility(View.VISIBLE);
        binding.myToolbar.toolbarMenu.setOnClickListener(this);
        binding.myToolbar.toolbarTitle.setText(R.string.title_notifications);
    }

    private void setupRecyclerView() {
        notificationList = new ArrayList<>();
        adapter = new CustomerNotificationAdapter(notificationList, (position, view) -> {
            CustomerNotificationModel item = notificationList.get(position);
            if (item.getData() != null && item.getData().getOrder_id() != -1) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.JOB_ID, item.getData().getOrder_id());
                ActivityController.startActivity(NotificationActivity.this, OrderDetailActivity.class, bundle);
                callMarkNotificationAsReadApi(position);
            }
        });
        binding.recyclerNotifications.setAdapter(adapter);
    }

    private void setupPopupMenu() {
        popupMenu = new PopupMenu(this, binding.myToolbar.toolbarMenu);
        popupMenu.getMenuInflater().inflate(R.menu.menu_technician_notifications, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        callClearNotificationsApi();
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.toolbar_menu) {
            popupMenu.show();
        }
    }

    private void callCustomerNotificationApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_USER_NOTIFICATIONS, new ApiCallBack<>(new ApiResponseListener<CustomerNotificationResponse>() {
            @Override
            public void onApiSuccess(CustomerNotificationResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    notificationList.addAll(response.getData());
                    adapter.notifyDataSetChanged();
                    if(notificationList.size() == 0) binding.textNoNotifications.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(NotificationActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(NotificationActivity.this, failureMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        }), true);
    }

    private void callClearNotificationsApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_CUSTOMER_NOTIFICATION_CLEAR, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    Toast.makeText(NotificationActivity.this, R.string.all_notifications_clear, Toast.LENGTH_SHORT).show();
                    notificationList.clear();
                    adapter.notifyDataSetChanged();
                    binding.textNoNotifications.setVisibility(View.VISIBLE);
                } else {
                    Utils.showSnackbar(NotificationActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(NotificationActivity.this, failureMessage);
            }
        }), true);
    }

    private void callMarkNotificationAsReadApi(int position) {
        CustomerNotificationModel item = notificationList.get(position);
        item.setIs_read(true);
        adapter.notifyItemChanged(position);

        ServiceManager.callServerApi(this, ServiceManager.API_CUSTOMER_NOTIFICATION_READ, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {

            }

            @Override
            public void onApiFailure(String failureMessage) {

            }
        }), item.getId());
    }
}
