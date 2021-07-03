package com.oileemobile.fragment.technician;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.activity.technician.TechnicianJobDetailActivity;
import com.oileemobile.activity.technician.TechnicianMainActivity;
import com.oileemobile.adapters.technician.NotificationListAdapter;
import com.oileemobile.databinding.FragmentTechnicianNotificationBinding;
import com.oileemobile.models.technician.NotificationModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.ApiResponse;
import com.oileemobile.network.response.technician.TechnicianNotificationResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.Utils;

public class TechnicianNotificationFragment extends Fragment {
    private FragmentTechnicianNotificationBinding binding;
    private List<NotificationModel> notificationList;
    private NotificationListAdapter mAdapter;

    public TechnicianNotificationFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_technician_notification, container, false);
        callGetNotificationsApi();

        return binding.getRoot();
    }

    private void updateHeaderView() {
        if(getActivity() instanceof TechnicianMainActivity) {
            ((TechnicianMainActivity) getActivity()).updateHeaderView(100);
        }
    }

    public void callClearNotificationsApi() {
        ServiceManager.callServerApi(getActivity(), ServiceManager.API_TECHNICIAN_NOTIFICATION_CLEAR, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    Toast.makeText(getActivity(), R.string.all_notifications_clear, Toast.LENGTH_SHORT).show();
                    notificationList.clear();
                    mAdapter.notifyDataSetChanged();
                    binding.textNoNotifications.setVisibility(View.VISIBLE);
                } else {
                    Utils.showSnackbar(getActivity(), response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(getActivity(), failureMessage);
            }
        }), true);
    }

    private void callGetNotificationsApi() {
        ServiceManager.callServerApi(getActivity(), ServiceManager.API_GET_NOTIFICATIONS, new ApiCallBack<>(new ApiResponseListener<TechnicianNotificationResponse>() {
            @Override
            public void onApiSuccess(TechnicianNotificationResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    notificationList = response.getData();
                    if(notificationList.size() > 0) {
                        mAdapter = new NotificationListAdapter(notificationList, (position, view) -> {
                            NotificationModel item = notificationList.get(position);
                            if (item.getJob_id() != -1) {
                                Bundle bundle = new Bundle();
                                bundle.putInt(Constants.JOB_ID, item.getJob_id());
                                ActivityController.startActivity(getActivity(), TechnicianJobDetailActivity.class, bundle);
                                callMarkNotificationAsReadApi(position);
                            }
                        });
                        binding.recyclerNotifications.setAdapter(mAdapter);
                    } else {
                        binding.textNoNotifications.setVisibility(View.VISIBLE);
                    }
                    updateHeaderView();
                } else {
                    Utils.showSnackbar(getActivity(), response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(getActivity(), failureMessage);
            }
        }), true);
    }

    private void callMarkNotificationAsReadApi(int position) {
        NotificationModel item = notificationList.get(position);
        item.setIs_read(true);
        mAdapter.notifyItemChanged(position);

        ServiceManager.callServerApi(getActivity(), ServiceManager.API_CUSTOMER_NOTIFICATION_READ, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {

            }

            @Override
            public void onApiFailure(String failureMessage) {

            }
        }), item.getId());
    }
}
