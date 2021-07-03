package com.oileemobile.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.oileemobile.R;
import com.oileemobile.databinding.ActivityCreatePasswordBinding;
import com.oileemobile.helpers.HideErrorLine;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiRequest;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.ForgotPasswordResponse;
import com.oileemobile.network.response.ResetPasswordResponse;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.Utils;

public class CreatePasswordActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCreatePasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_password);
        setupToolbar();

        binding.btnSubmit.setOnClickListener(this);

        binding.passwordText.addTextChangedListener(new HideErrorLine(binding.passwordLayout));
        binding.confirmPasswordText.addTextChangedListener(new HideErrorLine(binding.confirmPasswordLayout));
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText("");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            validateAndProceed();
        }
    }

    private void validateAndProceed() {
        String password = binding.passwordText.getText().toString();
        if(TextUtils.isEmpty(password)) {
            binding.passwordLayout.setError(getString(R.string.enter_a_password));
            return;
        }

        String confirmPassword = binding.confirmPasswordText.getText().toString();
        if(TextUtils.isEmpty(confirmPassword)) {
            binding.confirmPasswordLayout.setError(getString(R.string.enter_a_password));
            return;
        }

        if(!password.equals(confirmPassword)) {
            binding.confirmPasswordLayout.setError(getString(R.string.password_mismatch));
            return;
        }

        callResetPasswordApi(password, confirmPassword);
    }

    private void callResetPasswordApi(String password, String confirmPassword) {
        ForgotPasswordResponse.ForgotPasswordData data = (ForgotPasswordResponse.ForgotPasswordData) getIntent().getSerializableExtra("data");
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setEmail(data.getEmail());
        apiRequest.setToken(data.getToken());
        apiRequest.setPassword(password);
        apiRequest.setPassword_confirmation(confirmPassword);

        int apiType;
        if(PrefManager.getInt(PrefManager.USER_TYPE, Constants.TYPE_CUSTOMER) == Constants.TYPE_CUSTOMER) {
            apiType = ServiceManager.API_RESET_PASSWORD;
        } else {
            apiType = ServiceManager.API_TECHNICIAN_RESET_PASSWORD;
        }

        ServiceManager.callServerApi(this, apiType, new ApiCallBack<>(new ApiResponseListener<ResetPasswordResponse>() {
            @Override
            public void onApiSuccess(ResetPasswordResponse response) {
                if(!response.getMessage().toLowerCase().contains("invalid")) {
                    Toast.makeText(CreatePasswordActivity.this, R.string.password_changed_successfully, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(CreatePasswordActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), apiRequest);
    }
}
