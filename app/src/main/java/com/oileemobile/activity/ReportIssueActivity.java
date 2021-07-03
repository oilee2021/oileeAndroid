package com.oileemobile.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.transition.TransitionManager;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oileemobile.R;
import com.oileemobile.databinding.ActivityReportIssueBinding;
import com.oileemobile.dialogs.ConfirmationDialog;
import com.oileemobile.dialogs.InformationDialog;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.ReportIssueResponse;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.Utils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ReportIssueActivity extends BaseActivity implements View.OnClickListener {

    private ActivityReportIssueBinding binding;
    private ImageView lastClickedView;
    private List<File> selectedFiles;
    private int userType;
    //private ImageSelectDialogFragment imageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report_issue);
        setupToolbar();

        userType = PrefManager.getInt(PrefManager.USER_TYPE, Constants.TYPE_CUSTOMER);
        selectedFiles = new ArrayList<>();
        //imageDialog = new ImageSelectDialogFragment(this);

        binding.imageAttach1.setOnClickListener(this);
        binding.imageAttach2.setOnClickListener(this);
        binding.imageAttach3.setOnClickListener(this);
        binding.reportSubmit.setOnClickListener(this);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.report_an_issue);
        binding.myToolbar.getRoot().setBackgroundResource(R.drawable.main_background_gradient);
    }

    private void showImageSelectDialog() {
        if(lastClickedView.getTag() == null) {
            Utils.startImagePicker(this);
        } else {
            showRemovePictureDialog();
        }
    }

    private void showRemovePictureDialog() {
        ConfirmationDialog.OnClickListener clickListener = (which) -> {
            if(which == DialogInterface.BUTTON_POSITIVE) {
                int position = (int) lastClickedView.getTag();
                selectedFiles.remove(position);
                lastClickedView.setTag(null);
                TransitionManager.beginDelayedTransition(binding.mainView);
                lastClickedView.setImageResource(R.drawable.ic_add);
            }
        };

        new ConfirmationDialog(R.string.remove_picture, R.string.remove_picture_text,
                R.string.remove, R.string.cancel, clickListener)
                .show(getSupportFragmentManager(), "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_attach1:
                lastClickedView = binding.imageAttach1;
                showImageSelectDialog();
                break;

            case R.id.image_attach2:
                lastClickedView = binding.imageAttach2;
                showImageSelectDialog();
                break;

            case R.id.image_attach3:
                lastClickedView = binding.imageAttach3;
                showImageSelectDialog();
                break;

            case R.id.report_submit:
                checkValidationAndProceed();
                break;
        }
    }

    private void checkValidationAndProceed() {
        String body = binding.editReportText.getText().toString().trim();
        if(TextUtils.isEmpty(body)) {
            binding.editReportText.setError(getString(R.string.field_blank_error));
            return;
        }

        callReportIssueApi(getMultipartData(body));
    }

    private Map<String, RequestBody> getMultipartData(String body) {
        Map<String, RequestBody> dataMap = new HashMap<>();
        dataMap.put("body", RequestBody.create(MediaType.parse("text/plain"), body));
        return dataMap;
    }

    public void onImageSelected(File file) {
        selectedFiles.add(file);
        Utils.loadPicassoImage(file, lastClickedView, null);
        TransitionManager.beginDelayedTransition(binding.mainView);
        lastClickedView.setTag(selectedFiles.indexOf(file));
    }

    private void callReportIssueApi(Map<String, RequestBody> dataMap) {
        int apiType;
        if(userType == Constants.TYPE_CUSTOMER) apiType = ServiceManager.API_CREATE_SUPPORT_QUERY;
        else apiType = ServiceManager.API_CREATE_TECHNICIAN_SUPPORT_QUERY;
        ServiceManager.callReportIssueApi(this, apiType, new ApiCallBack<>(new ApiResponseListener<ReportIssueResponse>() {
            @Override
            public void onApiSuccess(ReportIssueResponse response) {
                if(response.getStatus() == 200) {
                    InformationDialog.OnClickListener clickListener = () -> {
                        setResult(RESULT_OK);
                        finish();
                    };
                    new InformationDialog(R.string.success, R.string.issue_reported_successfully, clickListener)
                            .show(getSupportFragmentManager(), "");
                } else {
                    Toast.makeText(ReportIssueActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(ReportIssueActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), dataMap, selectedFiles);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                onImageSelected(new File(resultUri.getPath()));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }
        }
    }
}
