package com.oileemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.oileemobile.R;
import com.oileemobile.activity.technician.TechnicianRegisterActivity;
import com.oileemobile.databinding.ActivityPhoneNumberBinding;
import com.oileemobile.helpers.PhoneAuthenticator;
import com.oileemobile.interfaces.OtpSentListener;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiRequest;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.ApiResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.ProgressDialog;

public class PhoneNumberActivity extends BaseActivity implements View.OnClickListener {

    private ActivityPhoneNumberBinding binding;
    private Bundle dataBundle;
    private PhoneAuthenticator phoneAuthenticator;
    private int activityType, userType;
    private String mobileNumber;

    public static final int TYPE_LOGIN = 1;
    public static final int TYPE_EDIT_PROFILE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_number);

        activityType = getIntent().getIntExtra(Constants.ACTIVITY_TYPE, TYPE_LOGIN);
        userType = PrefManager.getInt(PrefManager.USER_TYPE, Constants.TYPE_CUSTOMER);
        binding.mobileSubmit.setOnClickListener(this);

        binding.mobileNumber.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Constants.MAX_MOBILE_NUMBER) });
        binding.mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() <= Constants.MIN_MOBILE_NUMBER)
                    binding.mobileSubmit.setEnabled(false);
                else
                    binding.mobileSubmit.setEnabled(true);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.mobile_submit) {
            checkValidationAndProceed();
        }
    }

    private OtpSentListener otpSentListener = new OtpSentListener() {
        @Override
        public void onOtpSent() {
            dataBundle.putParcelable(OtpActivity.PHONE_AUTHENTICATOR, phoneAuthenticator);
            ActivityController.startActivityForResult(PhoneNumberActivity.this, OtpActivity.class, dataBundle, 111);
        }

        @Override
        public void onNumberVerified() {
            handleOtpVerification();
            Toast.makeText(PhoneNumberActivity.this, R.string.verified, Toast.LENGTH_SHORT).show();
        }
    };

    private void checkValidationAndProceed() {
        mobileNumber = binding.mobileNumber.getText().toString().trim();
        if(TextUtils.isEmpty(mobileNumber) || mobileNumber.length() < Constants.MIN_MOBILE_NUMBER) {
            binding.mobileNumber.setError(getString(R.string.enter_valid_number));
            return;
        }

        dataBundle = new Bundle();
        dataBundle.putString(CustomerRegisterActivity.COUNTRY_CODE, binding.ccp.getSelectedCountryCodeWithPlus());
        dataBundle.putString(CustomerRegisterActivity.MOBILE_NUMBER, mobileNumber);
        callCheckMobileApi();
    }

    private void callCheckMobileApi() {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setMobile_number(mobileNumber);
        ProgressDialog.showProgressDialog(this);
        int apiType;
        if(userType == Constants.TYPE_CUSTOMER) {
            apiType = ServiceManager.API_CUSTOMER_CHECK_MOBILE;
        } else {
            apiType = ServiceManager.API_TECHNICIAN_CHECK_MOBILE;
        }
        ServiceManager.callServerApi(this, apiType, new ApiCallBack<>(checkMobileResponseListener), apiRequest, false);
    }

    private ApiResponseListener<ApiResponse> checkMobileResponseListener = new ApiResponseListener<ApiResponse>() {
        @Override
        public void onApiSuccess(ApiResponse response) {
            ProgressDialog.hideProgressDialog();
            if(response.getStatus() == StatusCodes.SUCCESS) {
                if(activityType == TYPE_EDIT_PROFILE) {
                    binding.mobileNumber.setError(getString(R.string.number_already_exists));
                } else {
                    ActivityController.startActivity(PhoneNumberActivity.this, LoginActivity.class, dataBundle);
                }
            } else {
                phoneAuthenticator = new PhoneAuthenticator(binding.ccp.getSelectedCountryCodeWithPlus(), mobileNumber);
                phoneAuthenticator.sendOtp(PhoneNumberActivity.this, otpSentListener);
            }
        }

        @Override
        public void onApiFailure(String failureMessage) {
            ProgressDialog.hideProgressDialog();
            Toast.makeText(PhoneNumberActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
        }
    };

    private void handleOtpVerification() {
        if(activityType == TYPE_EDIT_PROFILE) {
            Intent intent = new Intent();
            intent.putExtras(dataBundle);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            if(userType == Constants.TYPE_CUSTOMER) {
                ActivityController.startActivity(this, CustomerRegisterActivity.class, dataBundle, true);
            } else {
                ActivityController.startActivity(this, TechnicianRegisterActivity.class, dataBundle, true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 111 && resultCode == RESULT_OK) {
            handleOtpVerification();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
