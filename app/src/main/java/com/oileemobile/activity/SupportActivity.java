package com.oileemobile.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.transition.TransitionManager;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.adapters.SupportQueryAdapter;
import com.oileemobile.databinding.ActivitySupportBinding;
import com.oileemobile.dialogs.ConfirmationDialog;
import com.oileemobile.dialogs.InformationDialog;
import com.oileemobile.dialogs.InformationDialogUpdated;
import com.oileemobile.interfaces.RecyclerViewItemClickListener;
import com.oileemobile.models.SupportQueryModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.SupportQueryResponse;
import com.oileemobile.network.response.technician.SupportEligibleResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.Utils;

public class SupportActivity extends BaseActivity implements View.OnClickListener, RecyclerViewItemClickListener {
    private ActivitySupportBinding binding;
    private List<SupportQueryModel> data;
    private boolean canReportIssue;
    private int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_support);
        setupToolbar();
        canReportIssue = false;
        userType = PrefManager.getInt(PrefManager.USER_TYPE, Constants.TYPE_CUSTOMER);

        binding.buttonQuery.setOnClickListener(this);
        callSupportEligibleApi();
        callSupportListingApi(true);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.support);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_query) {
            if(canReportIssue) {
                ActivityController.startActivityForResult(this, ReportIssueActivity.class, 1123);
            } else {


                InformationDialogUpdated.OnClickListener clickListener = (which) -> {
                    if(which == DialogInterface.BUTTON_POSITIVE) {

                        callPhoneNumber();
                    }
                };


                new InformationDialogUpdated(R.string.support, R.string.support_error_demo_text_update, clickListener)
                        .show(getSupportFragmentManager(), "");
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhoneNumber();
            }
        }
    }
    public void callPhoneNumber() {
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SupportActivity.this, new String[]{
                            Manifest.permission.CALL_PHONE}, 101);
                    return;
                }
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+"(562) 247-5311"));
                startActivity(callIntent);
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+"(562) 247-5311"));
                startActivity(callIntent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    @Override
    public void onItemClick(int position, View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("support_model", data.get(position));
        ActivityController.startActivity(this, QueryDetailActivity.class, bundle);
    }

    private void callSupportListingApi(boolean showProgress) {
        int apiType;
        if(userType == Constants.TYPE_CUSTOMER) apiType = ServiceManager.API_GET_SUPPORT_QUERY;
        else apiType = ServiceManager.API_GET_TECHNICIAN_SUPPORT_QUERY;

        ServiceManager.callServerApi(this, apiType, new ApiCallBack<>(new ApiResponseListener<SupportQueryResponse>() {
            @Override
            public void onApiSuccess(SupportQueryResponse response) {
                if(response.getStatus() == 200) {
                    data = response.getData();
                    if(data.size() > 0) {
                        TransitionManager.beginDelayedTransition(binding.mainView);

                        hideErrorText();
                        SupportQueryAdapter adapter = new SupportQueryAdapter(data, SupportActivity.this);
                        binding.recyclerQueries.setAdapter(adapter);
                    } else {
                        binding.textNoResults.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(SupportActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), showProgress);
    }

    private void callSupportEligibleApi() {
        int apiType;
        if(userType == Constants.TYPE_CUSTOMER) apiType = ServiceManager.API_SUPPORT_ELIGIBLE;
        else apiType = ServiceManager.API_TECHNICIAN_SUPPORT_ELIGIBLE;

        ServiceManager.callServerApi(this, apiType, new ApiCallBack<>(new ApiResponseListener<SupportEligibleResponse>() {
            @Override
            public void onApiSuccess(SupportEligibleResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    SupportEligibleResponse.EligibleModel data = response.getEligibleModel();
                    canReportIssue = data.isEligible();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {

            }
        }), false);
    }

    private void hideErrorText() {
        binding.view1.setVisibility(View.VISIBLE);
        binding.textNoResults.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1123 && resultCode == RESULT_OK) {
            callSupportListingApi(false);
        }
    }
}
