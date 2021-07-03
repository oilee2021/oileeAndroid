package com.oileemobile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.transition.TransitionManager;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.oileemobile.R;
import com.oileemobile.adapters.ReminderVehicleAdapter;
import com.oileemobile.databinding.ActivityAddReminderBinding;
import com.oileemobile.models.VehicleModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiRequest;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.ReportIssueResponse;
import com.oileemobile.network.response.UserAllVehiclesResponse;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.Utils;

public class AddReminderActivity extends BaseActivity implements View.OnClickListener {
    private ActivityAddReminderBinding binding;
    private List<VehicleModel> dataList;
    private ReminderVehicleAdapter vehicleAdapter;
    private int selectedVehicle;
    private String selectedDate;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_reminder);
        setupToolbar();

        selectedVehicle = -1;
        setupDatePickerDialog();
        binding.textSelectDate.setOnClickListener(this);
        binding.scheduleSubmit.setOnClickListener(this);
        callGetMyVehiclesApi(true);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.add_reminder);
        binding.myToolbar.getRoot().setBackgroundResource(R.drawable.main_background_gradient);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_select_date:
                datePickerDialog.show(getSupportFragmentManager(), "");
                break;

            case R.id.schedule_submit:
                callCreateReminderApi();
                break;
        }
    }

    private void setupDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance((view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
            binding.textSelectDateShow.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(calendar.getTime()));
            if(selectedVehicle != -1) {
                binding.scheduleSubmit.setEnabled(true);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMinDate(Calendar.getInstance());
        datePickerDialog.showYearPickerFirst(true);
    }

    private void callGetMyVehiclesApi(boolean showProgress) {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_ALL_USER_VEHICLE, new ApiCallBack<>(new ApiResponseListener<UserAllVehiclesResponse>() {
            @Override
            public void onApiSuccess(UserAllVehiclesResponse response) {
                if(response.getData() != null) {
                    dataList = response.getData();
                    if(dataList.size() > 0) {
                        TransitionManager.beginDelayedTransition(binding.mainView);
                        vehicleAdapter = new ReminderVehicleAdapter(dataList, position -> {
                            if(selectedVehicle != position) {
                                if (selectedVehicle != -1) {
                                    ReminderVehicleAdapter.MyViewHolder oldViewHolder =
                                            (ReminderVehicleAdapter.MyViewHolder) binding.recyclerVehicle.findViewHolderForAdapterPosition(selectedVehicle);
                                    tickSelectedPack(oldViewHolder.binding.vehicleTick, false);
                                }
                                selectedVehicle = position;
                                if (selectedDate != null) {
                                    binding.scheduleSubmit.setEnabled(true);
                                }
                            }
                        });
                        binding.recyclerVehicle.addItemDecoration(new DividerItemDecoration(AddReminderActivity.this, DividerItemDecoration.VERTICAL));
                        binding.recyclerVehicle.setAdapter(vehicleAdapter);
                    } else {
                        binding.textNoResults.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(AddReminderActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), showProgress);
    }

    private void callCreateReminderApi() {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setCustomer_id(String.valueOf(PrefManager.getInt(PrefManager.USER_ID, -1)));
        apiRequest.setVehicle_id(String.valueOf(dataList.get(selectedVehicle).getId()));
        apiRequest.setReminder_date(selectedDate);

        ServiceManager.callServerApi(this,ServiceManager.API_ADD_SCHEDULE_REMINDER, new ApiCallBack<>(new ApiResponseListener<ReportIssueResponse>() {
            @Override
            public void onApiSuccess(ReportIssueResponse response) {
                if(response.getStatus() == 200 || response.getStatus() == 201) {
                    Toast.makeText(AddReminderActivity.this, R.string.reminder_saved_successfully, Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(AddReminderActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), apiRequest);
    }

    private void tickSelectedPack(ImageView view, boolean tick) {
        android.transition.TransitionManager.beginDelayedTransition(binding.mainView);
        view.setImageResource(tick ? R.drawable.ic_tick : R.drawable.ic_untick);
    }
}
