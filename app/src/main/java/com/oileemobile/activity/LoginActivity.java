package com.oileemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.oileemobile.R;
import com.oileemobile.databinding.ActivityLoginBinding;
import com.oileemobile.helpers.HideErrorLine;
import com.oileemobile.helpers.PhoneAuthenticator;
import com.oileemobile.interfaces.OtpSentListener;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiRequest;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.ForgotPasswordResponse;
import com.oileemobile.network.response.LoginResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.ProgressDialog;
import com.oileemobile.utils.Utils;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLoginBinding binding;
    private String phoneNumber, countryCode;
    private PhoneAuthenticator phoneAuthenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        setupToolbar();

        handleBundleData();
        Utils.updateFirebaseToken();

        binding.loginSubmit.setOnClickListener(this);
        binding.loginForgotPassword.setOnClickListener(this);

        binding.loginNumber.addTextChangedListener(new HideErrorLine(binding.loginNumberLayout));
        binding.loginPassword.addTextChangedListener(new HideErrorLine(binding.loginPasswordLayout));
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText("");
    }

    private void handleBundleData() {
        countryCode = getIntent().getStringExtra(CustomerRegisterActivity.COUNTRY_CODE);
        phoneNumber = getIntent().getStringExtra(CustomerRegisterActivity.MOBILE_NUMBER);
        binding.loginNumber.setText(phoneNumber);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.login_submit:
                validateAndProceed();
                break;

            case R.id.login_forgot_password:
                phoneAuthenticator = new PhoneAuthenticator(countryCode, phoneNumber);
                phoneAuthenticator.sendOtp(this, otpSentListener);
                break;
        }
    }

    private OtpSentListener otpSentListener = new OtpSentListener() {
        @Override
        public void onOtpSent() {
            Bundle bundle = new Bundle();
            bundle.putParcelable(OtpActivity.PHONE_AUTHENTICATOR, phoneAuthenticator);
            ActivityController.startActivityForResult(LoginActivity.this, OtpActivity.class, bundle, 111);
        }

        @Override
        public void onNumberVerified() {
            Toast.makeText(LoginActivity.this, R.string.verified, Toast.LENGTH_SHORT).show();
            callForgotPasswordApi();
        }
    };

    private void validateAndProceed() {
        String number = getIntent().getStringExtra(CustomerRegisterActivity.MOBILE_NUMBER);
        if(TextUtils.isEmpty(number)) {
            binding.loginNumberLayout.setError(getString(R.string.enter_valid_number));
            return;
        }

        String password = binding.loginPassword.getText().toString();
        if(TextUtils.isEmpty(password)) {
            binding.loginPasswordLayout.setError(getString(R.string.enter_a_password));
            return;
        }

        if(PrefManager.getInt(PrefManager.USER_TYPE, Constants.TYPE_CUSTOMER) == Constants.TYPE_CUSTOMER) {
            callCustomerLoginApi(getLoginApiRequest(number, password));
        } else {
            callTechnicianLoginApi(getLoginApiRequest(number, password));
        }
    }

    private ApiRequest getLoginApiRequest(String number, String password) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setPhone_number(number);
        apiRequest.setPassword(password);
        apiRequest.setFcm_token(PrefManager.getString(PrefManager.FCM_TOKEN));
        return apiRequest;
    }

    private void callCustomerLoginApi(ApiRequest apiRequest) {
        ProgressDialog.showProgressDialog(this);
        ServiceManager.callServerApi(this, ServiceManager.API_CUSTOMER_LOGIN, new ApiCallBack<>(new ApiResponseListener<LoginResponse>() {
            @Override
            public void onApiSuccess(LoginResponse response) {
                if(response.getStatus() == 200) {
                    Toast.makeText(LoginActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                    Utils.setCustomerLoggedIn(LoginActivity.this, response.getData().getToken());
                } else {
                    ProgressDialog.hideProgressDialog();
                    binding.loginPasswordLayout.setError(getString(R.string.incorrect_password));
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                ProgressDialog.hideProgressDialog();
                Toast.makeText(LoginActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), apiRequest, false);
    }

    private void callTechnicianLoginApi(ApiRequest apiRequest) {
        ProgressDialog.showProgressDialog(this);
        ServiceManager.callServerApi(this, ServiceManager.API_TECHNICIAN_LOGIN, new ApiCallBack<>(new ApiResponseListener<LoginResponse>() {
            @Override
            public void onApiSuccess(LoginResponse response) {
                if(response.getStatus() == 200) {
                    Toast.makeText(LoginActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                    Utils.setTechnicianLoggedIn(LoginActivity.this, response.getData().getToken());
                } else {
                    ProgressDialog.hideProgressDialog();
                    binding.loginPasswordLayout.setError(getString(R.string.incorrect_password));
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                ProgressDialog.hideProgressDialog();
                Toast.makeText(LoginActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), apiRequest);
    }

    private void callForgotPasswordApi() {
        if(PrefManager.getInt(PrefManager.USER_TYPE, Constants.TYPE_CUSTOMER) == Constants.TYPE_CUSTOMER) {
            callCustomerForgotPasswordApi();
        } else {
            callTechnicianForgotPasswordApi();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 111 && resultCode == RESULT_OK) {
            callForgotPasswordApi();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void callCustomerForgotPasswordApi() {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setPhone_number(phoneNumber);

        ServiceManager.callServerApi(this, ServiceManager.API_FORGOT_PASSWORD, new ApiCallBack<>(new ApiResponseListener<ForgotPasswordResponse>() {
            @Override
            public void onApiSuccess(ForgotPasswordResponse response) {
                ForgotPasswordResponse.ForgotPasswordData data = response.getData();
                if(response.getMessage().toLowerCase().contains("successfully")
                        && data != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("phone_number", phoneNumber);
                    bundle.putSerializable("data", data);
                    ActivityController.startActivity(LoginActivity.this, CreatePasswordActivity.class, bundle);
                } else {
                    Toast.makeText(LoginActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(LoginActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), apiRequest);
    }

    private void callTechnicianForgotPasswordApi() {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setPhone_number(phoneNumber);

        ServiceManager.callServerApi(this, ServiceManager.API_TECHNICIAN_FORGOT_PASSWORD, new ApiCallBack<>(new ApiResponseListener<ForgotPasswordResponse>() {
            @Override
            public void onApiSuccess(ForgotPasswordResponse response) {
                ForgotPasswordResponse.ForgotPasswordData data = response.getData();
                if(response.getMessage().toLowerCase().contains("successfully")
                        && data != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("phone_number", phoneNumber);
                    bundle.putSerializable("data", data);
                    ActivityController.startActivity(LoginActivity.this, CreatePasswordActivity.class, bundle);
                } else {
                    Toast.makeText(LoginActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(LoginActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), apiRequest);
    }
}
