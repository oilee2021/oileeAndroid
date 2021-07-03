package com.oileemobile.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.google.firebase.auth.FirebaseAuth;

import com.oileemobile.R;
import com.oileemobile.databinding.ActivityOtpBinding;
import com.oileemobile.helpers.PhoneAuthenticator;
import com.oileemobile.interfaces.OtpSentListener;
import com.oileemobile.interfaces.OtpVerificationListener;
import com.oileemobile.utils.Utils;

public class OtpActivity extends BaseActivity implements View.OnClickListener {

    public static final String PHONE_AUTHENTICATOR = "phone_authenticator";
    private ActivityOtpBinding binding;
    private PhoneAuthenticator phoneAuthenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp);

        binding.otpResend.setText(Html.fromHtml(getString(R.string.otp_resend_text)));

        phoneAuthenticator = getIntent().getParcelableExtra(PHONE_AUTHENTICATOR);

        binding.otpSubmit.setOnClickListener(this);
        binding.otpResend.setOnClickListener(this);

        binding.otp1.addTextChangedListener(new MyTextWatcher(binding.otp1, null, binding.otp2));
        binding.otp2.addTextChangedListener(new MyTextWatcher(binding.otp2, binding.otp1, binding.otp3));
        binding.otp3.addTextChangedListener(new MyTextWatcher(binding.otp3, binding.otp2, binding.otp4));
        binding.otp4.addTextChangedListener(new MyTextWatcher(binding.otp4, binding.otp3, binding.otp5));
        binding.otp5.addTextChangedListener(new MyTextWatcher(binding.otp5, binding.otp4, binding.otp6));
        binding.otp6.addTextChangedListener(new MyTextWatcher(binding.otp6, binding.otp5, null));

        setupOtpSubtitleText();
    }

    private OtpSentListener otpSentListener = new OtpSentListener() {
        @Override
        public void onOtpSent() {
            Toast.makeText(OtpActivity.this, R.string.otp_resent, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNumberVerified() {

        }
    };

    private OtpVerificationListener otpVerificationListener = new OtpVerificationListener() {
        @Override
        public void onOtpVerificationSuccess() {
            Toast.makeText(OtpActivity.this, R.string.verified, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onOtpVerificationFail() {
            Utils.showSnackbar(OtpActivity.this, R.string.incorrect_otp);
        }
    };

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.otp_submit) {
            String otp = getOtpString();
            if(otp.length() != 6) {
                Toast.makeText(this, R.string.enter_valid_otp, Toast.LENGTH_SHORT).show();
                return;
            }

            phoneAuthenticator.verifyOtp(FirebaseAuth.getInstance(), otp, otpVerificationListener);
        } else if(v.getId() == R.id.otp_resend) {
            phoneAuthenticator.resendOtp(OtpActivity.this, otpSentListener);
        }
    }

    private void setupOtpSubtitleText() {
        SpannableString numberText = new SpannableString(phoneAuthenticator.getCountryCode() + "-"
                + phoneAuthenticator.getMobileNumber());
        numberText.setSpan(new StyleSpan(Typeface.BOLD), 0, numberText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String text = getString(R.string.six_digit_code_on, numberText);
        binding.otpSubtitle.setText(text);
    }

    private String getOtpString() {
        return binding.otp1.getText().toString() + binding.otp2.getText().toString() + binding.otp3.getText().toString() + binding.otp4.getText().toString()
                + binding.otp5.getText().toString() + binding.otp6.getText().toString();
    }

    private class MyTextWatcher implements TextWatcher {

        private EditText thisEditText, prevEditText, nextEditText;

        private MyTextWatcher(EditText thisEditText, EditText prevEditText, EditText nextEditText) {
            this.thisEditText = thisEditText;
            this.prevEditText = prevEditText;
            this.nextEditText = nextEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length() == 0) {
                if (prevEditText != null) {
                    setEditTextFocus(prevEditText);
                }
            } else if(s.length() > 1) {
                thisEditText.setText(s.toString().substring(s.length() - 1));
            } else {
                if (nextEditText != null) {
                    setEditTextFocus(nextEditText);
                } else {
                    Utils.hideKeyboard(OtpActivity.this);
                }
            }

            binding.otpSubmit.setEnabled(getOtpString().length() == 6);
        }
    }

    private void setEditTextFocus(EditText editText) {
        editText.requestFocus();
    }
}
