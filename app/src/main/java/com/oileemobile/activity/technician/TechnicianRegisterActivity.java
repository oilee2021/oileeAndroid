package com.oileemobile.activity.technician;

import android.app.DatePickerDialog;
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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.oileemobile.R;
import com.oileemobile.activity.BaseActivity;
import com.oileemobile.activity.TermsActivity;
import com.oileemobile.databinding.ActivityTechnicianRegisterBinding;
import com.oileemobile.helpers.HideErrorLine;
import com.oileemobile.models.VehicleModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.RegisterResponse;
import com.oileemobile.network.response.UserAddVehicleResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.ProgressDialog;
import com.oileemobile.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.oileemobile.activity.CustomerRegisterActivity.MOBILE_NUMBER;
import static com.oileemobile.activity.technician.AddTechnicianVehicle.ADD_VEHICLE_REGISTER;

public class TechnicianRegisterActivity extends BaseActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    private ActivityTechnicianRegisterBinding binding;
    private boolean isTermsAgreed;
    //private ImageSelectDialogFragment imageSelectDialog;
    private int selectedImageDialog;
    private File userImage, driverLicenseImage, insuranceImage, vehicleRegistrationImage, vehicleImage;
    //private String authToken;
    private VehicleModel vehicleModel;
    private RegisterResponse.RegisterData registerResponse;
    private Calendar mCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_technician_register);
        setupToolbar();
        handleIntentData();
        setTermsText();
        Utils.updateFirebaseToken();

        //imageSelectDialog = new ImageSelectDialogFragment(this);

        binding.registerName.addTextChangedListener(new HideErrorLine(binding.registerNameLayout));
        binding.registerEmail.addTextChangedListener(new HideErrorLine(binding.registerEmailLayout));
        binding.registerPassword.addTextChangedListener(new HideErrorLine(binding.registerPasswordLayout));
        binding.registerNumber.addTextChangedListener(new HideErrorLine(binding.registerNumberLayout));
        binding.registerSsn.addTextChangedListener(new HideErrorLine(binding.registerSsnLayout));

        binding.registerAddress.addTextChangedListener(new HideErrorLine(binding.registerAddressLayout));
        binding.registerCity.addTextChangedListener(new HideErrorLine(binding.registerCityLayout));
        binding.registerState.addTextChangedListener(new HideErrorLine(binding.registerStateLayout));
        binding.registerZip.addTextChangedListener(new HideErrorLine(binding.registerZipLayout));
        binding.registerDob.addTextChangedListener(new HideErrorLine(binding.registerDobLayout));

        binding.imageDriverLicense.setOnClickListener(this);
        binding.linearDriverLicense.setOnClickListener(this);
        binding.imageInsuranceInformation.setOnClickListener(this);
        binding.linearInsuranceInformation.setOnClickListener(this);
        binding.imageVehicleRegistration.setOnClickListener(this);
        binding.linearVehicleRegistration.setOnClickListener(this);
        binding.textImageUpload.setOnClickListener(this);
        binding.registerSubmit.setOnClickListener(this);
        binding.noVehicleSection.setOnClickListener(this);
        binding.rowVehicle.getRoot().setOnClickListener(this);
        binding.checkTerms.setOnCheckedChangeListener(this);
        binding.checkConsent.setOnCheckedChangeListener(this);
        binding.registerDob.setOnClickListener(this);
    }

    private void handleIntentData() {
        String number = getIntent().getStringExtra(MOBILE_NUMBER);
        binding.registerNumber.setText(number);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText("");
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
                ds.linkColor = ContextCompat.getColor(TechnicianRegisterActivity.this, R.color.colorPrimary);
            }
        };
        spannableString.setSpan(clickableSpan, text.indexOf("terms"), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.checkTerms.setMovementMethod(LinkMovementMethod.getInstance());
        binding.checkTerms.setText(spannableString);
    }

    private void openTermsActivity() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.IS_TERMS_AGREED, isTermsAgreed);
        ActivityController.startActivityForResult(this, TermsActivity.class, bundle, 1124);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_submit:
                checkValidationAndProceed();
                break;

            case R.id.text_image_upload:
                selectedImageDialog = 0;
                Utils.startImagePicker(this, 500);
                break;

            case R.id.image_driver_license:
            case R.id.linear_driver_license:
                selectedImageDialog = 1;
                Utils.startImagePicker(this);
                break;

            case R.id.image_insurance_information:
            case R.id.linear_insurance_information:
                selectedImageDialog = 2;
                Utils.startImagePicker(this);
                break;

            case R.id.image_vehicle_registration:
            case R.id.linear_vehicle_registration:
                selectedImageDialog = 3;
                Utils.startImagePicker(this);
                break;

            case R.id.no_vehicle_section:
            case R.id.row_vehicle:
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.ACTIVITY_TYPE, ADD_VEHICLE_REGISTER);
                bundle.putSerializable("vehicle_model", vehicleModel);
                ActivityController.startActivityForResult(this, AddTechnicianVehicle.class, bundle, 1123);
                break;

            case R.id.register_dob:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                    mCalendar.set(year, month, dayOfMonth);
                    updateDateText();
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
                break;
        }
    }

    private void updateDateText() {
        binding.registerDob.setText(Utils.getTimeFromDate("MM/dd/yyyy", mCalendar.getTime()));
    }

    public void onImageSelected(File file) {
        ImageView imageView;
        switch (selectedImageDialog) {
            case 0:
                imageView = binding.registerImage;
                userImage = file;
                break;

            case 1:
                imageView = binding.imageDriverLicense;
                driverLicenseImage = file;
                break;

            case 2:
                imageView = binding.imageInsuranceInformation;
                insuranceImage = file;
                break;

            default:
                imageView = binding.imageVehicleRegistration;
                vehicleRegistrationImage = file;
        }

        Utils.loadPicassoImage(file, imageView, null);
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

        String ssn = binding.registerSsn.getText().toString().trim();
        if(TextUtils.isEmpty(ssn) || ssn.length() != 9) {
            binding.registerSsnLayout.setError(getString(R.string.enter_valid_ssn));
            return;
        }

        String dob = binding.registerDob.getText().toString().trim();
        if(TextUtils.isEmpty(dob)) {
            binding.registerDobLayout.setError(getString(R.string.enter_dob));
            return;
        }

        String password = binding.registerPassword.getText().toString();
        if(TextUtils.isEmpty(password)) {
            binding.registerPassword.setError(getString(R.string.enter_a_password));
            return;
        }

        String address = binding.registerAddress.getText().toString();
        if(TextUtils.isEmpty(address)) {
            binding.registerAddress.setError(getString(R.string.enter_an_address));
            return;
        }

        String city = binding.registerCity.getText().toString();
        if(TextUtils.isEmpty(city)) {
            binding.registerCity.setError(getString(R.string.enter_a_city));
            return;
        }

        String state = binding.registerState.getText().toString();
        if(TextUtils.isEmpty(state)) {
            binding.registerState.setError(getString(R.string.enter_a_state));
            return;
        }

        String zipCode = binding.registerZip.getText().toString();
        if(TextUtils.isEmpty(zipCode)) {
            binding.registerZip.setError(getString(R.string.enter_a_zip_code));
            return;
        }

        if(driverLicenseImage == null) {
            Utils.showSnackbar(this, R.string.add_driver_license);
            return;
        }

        if(insuranceImage == null) {
            Utils.showSnackbar(this, R.string.add_insurance_information);
            return;
        }

        if(vehicleRegistrationImage == null) {
            Utils.showSnackbar(this, R.string.add_vehicle_registration);
            return;
        }

        if(vehicleModel == null) {
            Utils.showSnackbar(this, R.string.add_vehicle_error);
            return;
        }

        callRegisterApi(getRegisterMultipartData(name, email, password, ssn, address, city, state, zipCode));
    }

    private Map<String, RequestBody> getRegisterMultipartData(String name, String email, String password, String ssn,
                                                              String address, String city, String state, String zipCode) {
        Map<String, RequestBody> dataMap = new HashMap<>();
        dataMap.put("name", RequestBody.create(MediaType.parse("text/plain"), name));
        dataMap.put("email", RequestBody.create(MediaType.parse("text/plain"), email));
        dataMap.put("phone_number", RequestBody.create(MediaType.parse("text/plain"), binding.registerNumber.getText().toString()));
        dataMap.put("password", RequestBody.create(MediaType.parse("text/plain"), password));
        dataMap.put("ssn", RequestBody.create(MediaType.parse("text/plain"), ssn));
        dataMap.put("dob", RequestBody.create(MediaType.parse("text/plain"), Utils.getTimeFromDate("yyyy-MM-dd", mCalendar.getTime())));
        dataMap.put("address", RequestBody.create(MediaType.parse("text/plain"), address));
        dataMap.put("city", RequestBody.create(MediaType.parse("text/plain"), city));
        dataMap.put("region", RequestBody.create(MediaType.parse("text/plain"), state));
        dataMap.put("country", RequestBody.create(MediaType.parse("text/plain"), "US"));
        dataMap.put("postal_code", RequestBody.create(MediaType.parse("text/plain"), zipCode));
        dataMap.put("fcm_token", RequestBody.create(MediaType.parse("text/plain"), PrefManager.getString(PrefManager.FCM_TOKEN)));
        return dataMap;
    }

    private void callRegisterApi(Map<String, RequestBody> dataMap) {
        ProgressDialog.showProgressDialog(this);
        ServiceManager.callTechnicianRegisterApi(this, ServiceManager.API_TECHNICIAN_REGISTER, new ApiCallBack<>(new ApiResponseListener<RegisterResponse>() {
            @Override
            public void onApiSuccess(RegisterResponse response) {
                switch (response.getStatus()) {
                    case 201:
                        Toast.makeText(TechnicianRegisterActivity.this, R.string.registered_successfully, Toast.LENGTH_SHORT).show();
                        registerResponse = response.getData();
                        PrefManager.putString(PrefManager.TOKEN, registerResponse.getToken());
                        if (vehicleModel != null) {
                            callAddVehicleApi();
                        } else {
                            ProgressDialog.hideProgressDialog();
                            Utils.setTechnicianLoggedIn(TechnicianRegisterActivity.this, registerResponse.getToken());
                        }
                        break;

                    case 1:
                        binding.registerEmailLayout.setError(getString(R.string.email_already_exists));
                        break;

                    case 2:
                        binding.registerNumberLayout.setError(getString(R.string.mobile_already_exists));
                        break;

                    case 3:
                        binding.registerSsnLayout.setError(getString(R.string.ssn_already_exists));
                        break;

                    default:
                        Toast.makeText(TechnicianRegisterActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                ProgressDialog.hideProgressDialog();
                Utils.showSnackbar(TechnicianRegisterActivity.this, failureMessage);
            }
        }), dataMap, userImage, driverLicenseImage, insuranceImage, vehicleRegistrationImage, false);
    }

    private void callAddVehicleApi() {
        ServiceManager.callMultipartServerApi(this, ServiceManager.API_ADD_TECHNICIAN_VEHICLE, new ApiCallBack<>(new ApiResponseListener<UserAddVehicleResponse>() {
                    @Override
                    public void onApiSuccess(UserAddVehicleResponse response) {
                        ProgressDialog.hideProgressDialog();
                        if(response.getStatus() == StatusCodes.SUCCESS) {
                            Utils.setTechnicianLoggedIn(TechnicianRegisterActivity.this, registerResponse.getToken());
                            Toast.makeText(TechnicianRegisterActivity.this, R.string.registered_successfully, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TechnicianRegisterActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onApiFailure(String failureMessage) {
                        ProgressDialog.hideProgressDialog();
                        Utils.showSnackbar(TechnicianRegisterActivity.this, failureMessage);
                    }
                }), -1, AddTechnicianVehicle.getAddTechnicianVehicleMultipartData(vehicleModel, false),
                vehicleImage, "image", false);
    }

    private void setupVehicleData() {
        if(vehicleModel != null) {
            binding.rowVehicle.getRoot().setVisibility(View.VISIBLE);
            binding.rowVehicle.vehicleName.setText(vehicleModel.getMake());
            String desc = vehicleModel.getYear() + " | " + vehicleModel.getModel();
            binding.rowVehicle.vehicleDesc.setText(desc);
            Utils.loadPicassoImage(vehicleImage, binding.rowVehicle.vehicleImage, null, R.drawable.car_demo);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            if(buttonView == binding.checkTerms) {
                if (!isTermsAgreed) {
                    openTermsActivity();
                    binding.checkTerms.setChecked(false);
                } else if(binding.checkConsent.isChecked()) {
                    binding.registerSubmit.setEnabled(true);
                }
            } else {
                if(binding.checkTerms.isChecked()) {
                    binding.registerSubmit.setEnabled(true);
                }
            }
        } else {
            binding.registerSubmit.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1124 && resultCode == RESULT_OK) {
            isTermsAgreed = true;
            binding.checkTerms.setChecked(true);
        } else if(requestCode == 1123 && resultCode == RESULT_OK) {
            if(data != null) {
                binding.noVehicleSection.setVisibility(View.GONE);
                vehicleModel = (VehicleModel) data.getSerializableExtra("vehicle");
                vehicleImage = vehicleModel.getVehicle_image() != null ? new File(vehicleModel.getVehicle_image()) : null;
                setupVehicleData();
            }
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
