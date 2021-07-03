package com.oileemobile.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.theartofdev.edmodo.cropper.CropImage;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.oileemobile.R;
import com.oileemobile.activity.CustomerMainActivity;
import com.oileemobile.activity.CustomerRegisterActivity;
import com.oileemobile.activity.PhoneNumberActivity;
import com.oileemobile.databinding.ActivityCustomerRegisterBinding;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.CustomerProfileResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.Utils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MyProfileFragment extends Fragment implements View.OnClickListener {

    private ActivityCustomerRegisterBinding binding;
    private DatePickerDialog datePickerDialog;
    private File selectedImageFile;
    private Date dobDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()), R.layout.activity_customer_register, container, false);

        binding.registerTitle.setVisibility(View.GONE);
        binding.registerSubtitle.setVisibility(View.GONE);
        binding.registerPasswordSection.setVisibility(View.GONE);
        binding.registerSubmit.setText(R.string.save_changes);
        binding.myToolbar.getRoot().setVisibility(View.GONE);
        binding.registerHeader.setVisibility(View.GONE);
        binding.checkTerms.setVisibility(View.GONE);
        setupUserDetails();
        createDatePickerDialog();

        binding.registerBirth.setOnClickListener(this);
        binding.registerSubmit.setOnClickListener(this);
        binding.registerImage.setOnClickListener(this);
        binding.textImageUpload.setOnClickListener(this);
        binding.registerNumber.setOnClickListener(this);

        return binding.getRoot();
    }

    private void setupUserDetails() {
        binding.registerName.setText(PrefManager.getString(PrefManager.USER_NAME));
        binding.registerEmail.setText(PrefManager.getString(PrefManager.USER_EMAIL));
        binding.registerNumber.setText(PrefManager.getString(PrefManager.USER_PHONE));
        binding.registerNumber.setEnabled(true);
        binding.registerSubmit.setEnabled(true);

        String dob = PrefManager.getString(PrefManager.USER_DOB);
        try {
            dobDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(dob);
        } catch (ParseException e) {
            e.printStackTrace();
            dobDate = new Date();
        }

        binding.registerBirth.setText(dob);
        Utils.loadPicassoImage(PrefManager.getString(PrefManager.USER_IMAGE), binding.registerImage, null);
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

        String number = binding.registerNumber.getText().toString();
        if(TextUtils.isEmpty(number) || number.length() != 10) {
            binding.registerNumberLayout.setError(getString(R.string.enter_valid_number));
            return;
        }

        String dob = binding.registerBirth.getText().toString().trim();
        if(TextUtils.isEmpty(dob)) {
            binding.registerBirthLayout.setError(getString(R.string.select_your_birthday));
            return;
        }

        callUpdateProfileApi(name, email, number, dob);
    }

    private Map<String, RequestBody> getMultipartData(String name, String email, String number, String dob) {
        Map<String, RequestBody> dataMap = new HashMap<>();
        dataMap.put("name", RequestBody.create(MediaType.parse("text/plain"), name));
        dataMap.put("email", RequestBody.create(MediaType.parse("text/plain"), email));
        dataMap.put("phone_number", RequestBody.create(MediaType.parse("text/plain"), number));
        dataMap.put("dob", RequestBody.create(MediaType.parse("text/plain"), dob));
        dataMap.put("_method", RequestBody.create(MediaType.parse("text/plain"), "PUT"));
        return dataMap;
    }

    private void callUpdateProfileApi(String name, String email, String phone_number, String dob) {
        Map<String, RequestBody> dataMap = getMultipartData(name, email, phone_number, dob);

        ServiceManager.callMultipartServerApi(getActivity(), ServiceManager.API_UPDATE_CUSTOMER_PROFILE,
                new ApiCallBack<>(new ApiResponseListener<CustomerProfileResponse>() {
                    @Override
                    public void onApiSuccess(CustomerProfileResponse response) {
                        if(response.getMessage().toLowerCase().contains("successfully")) {
                            PrefManager.putString(PrefManager.USER_NAME, name);
                            PrefManager.putString(PrefManager.USER_EMAIL, email);
                            PrefManager.putString(PrefManager.USER_PHONE, phone_number);
                            PrefManager.putString(PrefManager.USER_DOB, dob);
                            callGetProfileApi();
                            Toast.makeText(getActivity(), R.string.profile_saved_sucecssfully, Toast.LENGTH_SHORT).show();
                        } else {
                            Utils.showSnackbar(getActivity(), response.getMessage());
                        }
                    }

                    @Override
                    public void onApiFailure(String failureMessage) {
                        Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    }
                }), dataMap, selectedImageFile, "avatar");
    }

    private void callGetProfileApi() {
        ServiceManager.callServerApi(getActivity(), ServiceManager.API_GET_CUSTOMER_PROFILE, new ApiCallBack<>(new ApiResponseListener<CustomerProfileResponse>() {
            @Override
            public void onApiSuccess(CustomerProfileResponse response) {
                CustomerProfileResponse.CustomerData data = response.getData();
                PrefManager.putBoolean(PrefManager.IS_LOGGED, true);
                PrefManager.putInt(PrefManager.USER_ID, data.getId());
                PrefManager.putString(PrefManager.USER_NAME, data.getName());
                PrefManager.putString(PrefManager.USER_EMAIL, data.getEmail());
                PrefManager.putString(PrefManager.USER_PHONE, data.getPhone_number());
                PrefManager.putString(PrefManager.USER_DOB, data.getDob());
                PrefManager.putString(PrefManager.USER_IMAGE, data.getAvatar());

                if(getActivity() instanceof CustomerMainActivity) {
                    ((CustomerMainActivity) getActivity()).updateUserImage();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {

            }
        }), false);
    }

    private void createDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        calendar.setTime(dobDate);

        datePickerDialog = DatePickerDialog.newInstance((view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            String date = sdf.format(calendar.getTime());
            binding.registerBirth.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMaxDate(Calendar.getInstance());
        datePickerDialog.showYearPickerFirst(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_submit:
                checkValidationAndProceed();
                break;

            case R.id.register_birth:
                datePickerDialog.show(getChildFragmentManager(), "");
                break;

            case R.id.register_number:
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.ACTIVITY_TYPE, PhoneNumberActivity.TYPE_EDIT_PROFILE);
                ActivityController.startActivityForResult(this, PhoneNumberActivity.class, bundle, false, false, 1212);
                break;

            case R.id.register_image:
            case R.id.text_image_upload:
                Utils.startImagePicker(this, 500);
                break;
        }
    }

    private void onImageSelected(File file) {
        Utils.loadPicassoImage(file, binding.registerImage, null);
        selectedImageFile = file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1212 && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                String number = data.getStringExtra(CustomerRegisterActivity.MOBILE_NUMBER);
                binding.registerNumber.setText(number);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                onImageSelected(new File(resultUri.getPath()));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }
        }
    }
}
