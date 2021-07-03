package com.oileemobile.activity.technician;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.transition.TransitionManager;

import java.util.ArrayList;
import java.util.List;

import com.oileemobile.R;
import com.oileemobile.activity.BaseActivity;
import com.oileemobile.adapters.technician.TechnicianJobAdapter;
import com.oileemobile.databinding.ActivityUpcomingJobsBinding;
import com.oileemobile.models.technician.TechnicianJobModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.technician.TechnicianJobsResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.Utils;

public class UpcomingJobsActivity extends BaseActivity {

    private ActivityUpcomingJobsBinding binding;
    private List<TechnicianJobModel> jobList;
    private TechnicianJobAdapter jobAdapter;
    private int lastClickedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upcoming_jobs);
        setupToolbar();
        setupRecyclerView();

        callUpcomingJobsApi(true);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.upcoming_jobs);
        binding.myToolbar.getRoot().setBackgroundResource(R.drawable.main_background_gradient);
    }

    private void setupRecyclerView() {
        jobList = new ArrayList<>();
        jobAdapter = new TechnicianJobAdapter(jobList, (position, view) -> {
            lastClickedItem = position;
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.JOB_ID, jobList.get(position).getId());
            ActivityController.startActivityForResult(UpcomingJobsActivity.this, TechnicianJobDetailActivity.class, bundle, 123);
        });

        binding.recyclerJobs.setAdapter(jobAdapter);
    }

    private void callUpcomingJobsApi(boolean showProgress) {
        ServiceManager.callServerApi(this, ServiceManager.API_TECHNICIAN_ACCEPTED_JOBS, new ApiCallBack<>(new ApiResponseListener<TechnicianJobsResponse>() {
            @Override
            public void onApiSuccess(TechnicianJobsResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    jobList.clear();
                    jobList.addAll(response.getData());
                    jobAdapter.notifyDataSetChanged();
                    if(jobList.size() > 0) {
                        TransitionManager.beginDelayedTransition(binding.mainView);
                        binding.textNoJobs.setVisibility(View.INVISIBLE);
                    } else {
                        binding.textNoJobs.setVisibility(View.VISIBLE);
                    }
                } else {
                    Utils.showSnackbar(UpcomingJobsActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(UpcomingJobsActivity.this, failureMessage);
            }
        }), showProgress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_FIRST_USER) {
            callUpcomingJobsApi(false);
        }
    }
}
