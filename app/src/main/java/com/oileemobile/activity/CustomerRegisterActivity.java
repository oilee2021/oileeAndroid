package com.oileemobile.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.theartofdev.edmodo.cropper.CropImage;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.oileemobile.R;
import com.oileemobile.databinding.ActivityCustomerRegisterBinding;
import com.oileemobile.helpers.HideErrorLine;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.RegisterResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.Utils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class CustomerRegisterActivity extends BaseActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    public static final String MOBILE_NUMBER = "mobile_number";
    public static final String COUNTRY_CODE = "country_code";
    private ActivityCustomerRegisterBinding binding;
    private DatePickerDialog datePickerDialog;
    //private ImageSelectDialogFragment imageDialog;
    private File selectedImageFile;
    private boolean isTermsAgreed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer_register);
        setupToolbar();
        handleIntentData();
        createDatePickerDialog();
        setTermsText();
        Utils.updateFirebaseToken();

        binding.registerName.addTextChangedListener(new HideErrorLine(binding.registerNameLayout));
        binding.registerEmail.addTextChangedListener(new HideErrorLine(binding.registerEmailLayout));
        binding.registerPassword.addTextChangedListener(new HideErrorLine(binding.registerPasswordLayout));
        binding.registerNumber.addTextChangedListener(new HideErrorLine(binding.registerNumberLayout));
        binding.registerBirth.addTextChangedListener(new HideErrorLine(binding.registerBirthLayout));

        binding.registerSubmit.setOnClickListener(this);
        binding.registerBirth.setOnClickListener(this);
        binding.registerBirth.setLongClickable(false);
        binding.registerImage.setOnClickListener(this);
        binding.textImageUpload.setOnClickListener(this);
        binding.checkTerms.setOnCheckedChangeListener(this);
    }

    private void handleIntentData() {
        String number = getIntent().getStringExtra(MOBILE_NUMBER);
        binding.registerNumber.setText(number);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText("");
    }

    private void createDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance((view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            String date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(calendar.getTime());
            binding.registerBirth.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMaxDate(Calendar.getInstance());
        datePickerDialog.showYearPickerFirst(true);
    }

    private void setTermsText() {
        String text = getString(R.string.terms_n_conditions_text);
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                openTermsActivity();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.linkColor = ContextCompat.getColor(CustomerRegisterActivity.this, R.color.colorPrimary);
            }
        };
        spannableString.setSpan(clickableSpan, text.indexOf("terms"), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.checkTerms.setMovementMethod(LinkMovementMethod.getInstance());
        binding.checkTerms.setText(spannableString);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_submit:
                checkValidationAndProceed();
                break;

            case R.id.register_birth:
                datePickerDialog.show(getSupportFragmentManager(), "");
                break;

            case R.id.text_image_upload:
            case R.id.register_image:
                Utils.startImagePicker(this, 500);
                /*imageDialog = new ImageSelectDialogFragment(this);
                imageDialog.show(getSupportFragmentManager(), "image_dialog");*/
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            if(!isTermsAgreed) {
                openTermsActivity();
                binding.checkTerms.setChecked(false);
            } else {
                binding.registerSubmit.setEnabled(true);
            }
        } else {
            binding.registerSubmit.setEnabled(false);
        }
    }

    public void onImageSelected(File file) {
        Utils.loadPicassoImage(file, binding.registerImage, null);
        selectedImageFile = file;
    }

    private void openTermsActivity() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.IS_TERMS_AGREED, isTermsAgreed);
        ActivityController.startActivityForResult(this, TermsActivity.class, bundle, 1124);
    }

    private void checkValidationAndProceed() {
        String name = binding.registerName.getText().toString().trim();
        if(TextUtils.isEmpty(name)) {
            binding.registerNameLayout.setError(getString(R.string.enter_a_name));
            return;
        }

        String email = binding.registerEmail.getText().toString().trim();
        if(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.registerEmail.setError(getString(R.string.enter_valid_email));
            return;
        }

        String password = binding.registerPassword.getText().toString();
        if(TextUtils.isEmpty(password)) {
            binding.registerPassword.setError(getString(R.string.enter_a_password));
            return;
        }

        String dob = binding.registerBirth.getText().toString().trim();
        if(TextUtils.isEmpty(dob)) {
            binding.registerBirthLayout.setError(getString(R.string.select_your_birthday));
            return;
        }

        callRegisterApi(getRegisterMultipartData(name, email, password, dob));
    }

    private Map<String, RequestBody> getRegisterMultipartData(String name, String email, String password, String dob) {
        Map<String, RequestBody> dataMap = new HashMap<>();
        dataMap.put("name", RequestBody.create(MediaType.parse("text/plain"), name));
        dataMap.put("email", RequestBody.create(MediaType.parse("text/plain"), email));
        dataMap.put("phone_number", RequestBody.create(MediaType.parse("text/plain"), binding.registerNumber.getText().toString()));
        dataMap.put("password", RequestBody.create(MediaType.parse("text/plain"), password));
        dataMap.put("dob", RequestBody.create(MediaType.parse("text/plain"), dob));
        dataMap.put("fcm_token", RequestBody.create(MediaType.parse("text/plain"), PrefManager.getString(PrefManager.FCM_TOKEN)));
        return dataMap;
    }

    private void callRegisterApi(Map<String, RequestBody> dataMap) {
        ServiceManager.callMultipartServerApi(this, ServiceManager.API_CUSTOMER_REGISTER, new ApiCallBack<>(new ApiResponseListener<RegisterResponse>() {
            @Override
            public void onApiSuccess(RegisterResponse response) {
                Toast.makeText(CustomerRegisterActivity.this, R.string.registered_successfully, Toast.LENGTH_SHORT).show();
                Utils.setCustomerLoggedIn(CustomerRegisterActivity.this, response.getData().getToken());
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(CustomerRegisterActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), dataMap, selectedImageFile, "avatar");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1124 && resultCode == RESULT_OK) {
            isTermsAgreed = true;
            binding.checkTerms.setChecked(true);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
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
