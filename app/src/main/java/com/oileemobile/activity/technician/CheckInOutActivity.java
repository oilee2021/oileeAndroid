package com.oileemobile.activity.technician;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.transition.TransitionManager;

import java.util.ArrayList;
import java.util.List;

import com.oileemobile.R;
import com.oileemobile.activity.BaseActivity;
import com.oileemobile.adapters.CheckInJobListAdapter;
import com.oileemobile.databinding.ActivityCheckInOutBinding;
import com.oileemobile.models.technician.CheckInOutModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.technician.CheckInOutResponse;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.Utils;

public class CheckInOutActivity extends BaseActivity implements View.OnClickListener {
    private ActivityCheckInOutBinding binding;
    private int selectedItem;
    private List<CheckInOutModel.CheckInOutBean> checkInList, checkOutList;
    private CheckInJobListAdapter mAdapter;
    private int jobId;
    private boolean isFromJobStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_in_out);
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);

        selectedItem = -1;
        checkInList = new ArrayList<>();
        checkOutList = new ArrayList<>();
        binding.recyclerJobList.setItemAnimator(null);

        binding.textCheckIn.setOnClickListener(this);
        binding.textCheckOut.setOnClickListener(this);
        jobId = getIntent().getIntExtra(Constants.JOB_ID, -1);
        if(jobId == -1) binding.textCheckIn.callOnClick();
        else {
            isFromJobStatus = true;
            binding.textCheckOut.callOnClick();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_check_in:
                if(selectedItem != 0) {
                    setItemSelected(0, true);
                    callCheckInDataApi();
                }
                break;

            case R.id.text_check_out:
                if(selectedItem != 1) {
                    setItemSelected(1, true);
                    callCheckOutDataApi();
                }
                break;
        }
    }

    private void setItemSelected(int position, boolean setSelected) {
        TextView textView;
        if (position == 0) textView = binding.textCheckIn;
        else textView = binding.textCheckOut;

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

    private void setupRecyclerView(int totalJobs, int totalQuarts, List<CheckInOutModel.CheckInOutBean> list, int type) {
        binding.textTotalJobs.setText(String.format("Total Jobs - %s", totalJobs));
        binding.textTotalQuarts.setText(String.format("Total Quarts - %s", totalQuarts));
        if(list.size() > 0) {
            binding.recyclerJobList.setVisibility(View.VISIBLE);
            binding.textNoJobs.setVisibility(View.INVISIBLE);
            mAdapter = new CheckInJobListAdapter(this, list, binding.mainView, type);
            binding.recyclerJobList.setAdapter(mAdapter);
            if(isFromJobStatus && jobId != -1) {
                isFromJobStatus = false;
                for(int i = 0; i < list.size(); i++) {
                    if(list.get(i).getId() == jobId) {
                        int position = i;
                        binding.recyclerJobList.post(() -> {
                            CheckInJobListAdapter.MyViewHolder viewHolder = (CheckInJobListAdapter.MyViewHolder)
                                    binding.recyclerJobList.findViewHolderForAdapterPosition(position);
                            if(viewHolder != null) viewHolder.binding.jobHeader.callOnClick();
                            binding.recyclerJobList.smoothScrollToPosition(position);
                        });
                        break;
                    }
                }
            }
        } else {
            binding.recyclerJobList.setVisibility(View.GONE);
            binding.textNoJobs.setVisibility(View.VISIBLE);
        }
    }

    private void callCheckInDataApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_CHECK_IN_DATA, new ApiCallBack<>(new ApiResponseListener<CheckInOutResponse>() {
            @Override
            public void onApiSuccess(CheckInOutResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    binding.mainView.setVisibility(View.VISIBLE);
                    binding.myToolbar.toolbarTitle.setText(R.string.check_in);
                    CheckInOutModel model = response.getData();
                    checkInList = model.getJob_list();
                    setupRecyclerView(model.getTotal_jobs(), model.getTotal_quarts(), checkInList, 1);
                } else {
                    Utils.showSnackbar(CheckInOutActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(CheckInOutActivity.this, failureMessage);
            }
        }), true);
    }

    private void callCheckOutDataApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_CHECK_OUT_DATA, new ApiCallBack<>(new ApiResponseListener<CheckInOutResponse>() {
            @Override
            public void onApiSuccess(CheckInOutResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    binding.mainView.setVisibility(View.VISIBLE);
                    binding.myToolbar.toolbarTitle.setText(R.string.check_out);
                    CheckInOutModel model = response.getData();
                    checkOutList = model.getJob_list();
                    setupRecyclerView(model.getTotal_jobs(), model.getTotal_quarts(), checkOutList, 2);
                } else {
                    Utils.showSnackbar(CheckInOutActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(CheckInOutActivity.this, failureMessage);
            }
        }), true);
    }
}
