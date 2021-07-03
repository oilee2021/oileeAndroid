package com.oileemobile.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.transition.TransitionManager;

import java.util.ArrayList;
import java.util.List;

import com.oileemobile.R;
import com.oileemobile.adapters.ScheduleReminderAdapter;
import com.oileemobile.databinding.ActivityScheduleReminderBinding;
import com.oileemobile.dialogs.ConfirmationDialog;
import com.oileemobile.models.ScheduleReminderModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiRequest;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.ApiResponse;
import com.oileemobile.network.response.ReminderListResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Utils;

public class ScheduleReminderActivity extends BaseActivity implements View.OnClickListener {

    private ActivityScheduleReminderBinding binding;
    private List<ScheduleReminderModel> data;
    private ScheduleReminderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule_reminder);
        setupToolbar();

        binding.scheduleSubmit.setOnClickListener(this);
        callScheduleReminderListApi(true);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.schedule_a_reminder);
        binding.myToolbar.getRoot().setBackgroundResource(R.drawable.main_background_gradient);
    }

    private void callDeleteReminderApi(int position) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.set_method("delete");

        ServiceManager.callServerApi(this, ServiceManager.API_DELETE_REMINDER, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    Toast.makeText(ScheduleReminderActivity.this, R.string.reminder_deleted_successfully, Toast.LENGTH_SHORT).show();
                    data.remove(position);
                    adapter.notifyItemRemoved(position);
                    if(data.size() == 0) {
                        binding.textNoResults.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(ScheduleReminderActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(ScheduleReminderActivity.this, failureMessage, Toast.LENGTH_SHORT).show();
            }
        }), data.get(position).getId(), apiRequest);
    }

    private void callScheduleReminderListApi(boolean showProgress) {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_SCHEDULE_REMINDER, new ApiCallBack<>(new ApiResponseListener<ReminderListResponse>() {
            @Override
            public void onApiSuccess(ReminderListResponse response) {
                data = new ArrayList<>();
                if(response.getStatus() == 200) {
                    if(response.getData() != null) {
                        data = response.getData();
                        if(data.size() > 0) {
                            if(showProgress) {
                                TransitionManager.beginDelayedTransition(binding.mainView);
                            }
                            adapter = new ScheduleReminderAdapter(data, (position, view) -> {
                                new ConfirmationDialog(R.string.delete, R.string.reminder_delete_confirmation_text, R.string.delete, R.string.cancel, which -> {
                                    if(which == DialogInterface.BUTTON_POSITIVE) {
                                        callDeleteReminderApi(position);
                                    }
                                }).show(getSupportFragmentManager(), "");
                            });
                            binding.recyclerScheduleReminder.setAdapter(adapter);
                        }
                    }
                }

                if(data == null || data.size() == 0) {
                    binding.textNoResults.setVisibility(View.VISIBLE);
                } else {
                    binding.textNoResults.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                binding.textNoResults.setVisibility(View.VISIBLE);
                Toast.makeText(ScheduleReminderActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), showProgress);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.schedule_submit) {
            ActivityController.startActivityForResult(this, AddReminderActivity.class, 1231);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1231 && resultCode == RESULT_OK) {
            callScheduleReminderListApi(false);
        }
    }
}
