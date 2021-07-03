package com.oileemobile.fragment.technician;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.oileemobile.R;
import com.oileemobile.activity.AddVehicleActivity;
import com.oileemobile.activity.CustomerRegisterActivity;
import com.oileemobile.activity.PhoneNumberActivity;
import com.oileemobile.activity.technician.AddTechnicianVehicle;
import com.oileemobile.activity.technician.TechnicianMainActivity;
import com.oileemobile.databinding.FragmentTechnicianProfileBinding;
import com.oileemobile.models.VehicleModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.RegisterResponse;
import com.oileemobile.network.response.UserAllVehiclesResponse;
import com.oileemobile.network.response.technician.TechnicianProfileResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.Utils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TechnicianProfileFragment extends Fragment implements View.OnClickListener {

    private FragmentTechnicianProfileBinding binding;
    private int selectedImageDialog;
    private File userImage, driverLicenseImage, insuranceImage, vehicleRegistrationImage;
    private VehicleModel vehicleModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()), R.layout.fragment_technician_profile, container, false);

        binding.registerPasswordSection.setVisibility(View.GONE);
        binding.registerSubmit.setText(R.string.save_changes);
        setupUserDetails();

        binding.registerSubmit.setOnClickListener(this);
        binding.registerImage.setOnClickListener(this);
        binding.textImageUpload.setOnClickListener(this);
        binding.registerNumber.setOnClickListener(this);
        binding.noVehicleSection.setOnClickListener(this);
        binding.rowVehicle.getRoot().setOnClickListener(this);
        updateHeaderView();
        callGetTechnicianVehiclesApi(false);

        return binding.getRoot();
    }

    private void setupUserDetails() {
        binding.registerName.setText(PrefManager.getString(PrefManager.USER_NAME));
        binding.registerEmail.setText(PrefManager.getString(PrefManager.USER_EMAIL));
        binding.registerNumber.setText(PrefManager.getString(PrefManager.USER_PHONE));
        binding.registerSsn.setText(PrefManager.getString(PrefManager.USER_SSN));
        binding.registerNumber.setEnabled(true);
        binding.registerSubmit.setEnabled(true);
        Utils.loadPicassoImage(PrefManager.getString(PrefManager.USER_IMAGE), binding.registerImage, null);
        Utils.loadPicassoImage(PrefManager.getString(PrefManager.USER_DRIVER_LICENSE), binding.imageDriverLicense, null);
        Utils.loadPicassoImage(PrefManager.getString(PrefManager.USER_INSURANCE_INFORMATION), binding.imageInsuranceInformation, null);
        Utils.loadPicassoImage(PrefManager.getString(PrefManager.USER_VEHICLE_REGISTRATION), binding.imageVehicleRegistration, null);

        binding.registerAddress.setText(PrefManager.getString(PrefManager.TECHNICIAN_ADDRESS));
        binding.registerCity.setText(PrefManager.getString(PrefManager.TECHNICIAN_CITY));
        binding.registerState.setText(PrefManager.getString(PrefManager.TECHNICIAN_REGION));
        binding.registerZip.setText(PrefManager.getString(PrefManager.POSTAL_CODE));
    }

    private void updateHeaderView() {
        if(getActivity() instanceof TechnicianMainActivity) {
            ((TechnicianMainActivity) getActivity()).updateHeaderView(150, false);
        }
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

        String ssn = binding.registerSsn.getText().toString().trim();
        if(TextUtils.isEmpty(ssn) || ssn.length() != 9) {
            binding.registerSsnLayout.setError(getString(R.string.enter_valid_ssn));
            return;
        }

        callUpdateProfileApi(getMultipartData(name, email, number, ssn));
    }

    private Map<String, RequestBody> getMultipartData(String name, String email, String number, String ssn) {
        Map<String, RequestBody> dataMap = new HashMap<>();
        dataMap.put("name", RequestBody.create(MediaType.parse("text/plain"), name));
        dataMap.put("email", RequestBody.create(MediaType.parse("text/plain"), email));
        dataMap.put("phone_number", RequestBody.create(MediaType.parse("text/plain"), number));
        dataMap.put("ssn", RequestBody.create(MediaType.parse("text/plain"), ssn));
        dataMap.put("_method", RequestBody.create(MediaType.parse("text/plain"), "PUT"));
        return dataMap;
    }

    private void callGetProfileApi() {
        ServiceManager.callServerApi(getActivity(), ServiceManager.API_TECHNICIAN_GET_PROFILE, new ApiCallBack<>(new ApiResponseListener<TechnicianProfileResponse>() {
            @Override
            public void onApiSuccess(TechnicianProfileResponse response) {
                TechnicianProfileResponse.CustomerData data = response.getData();
                PrefManager.putBoolean(PrefManager.IS_LOGGED, true);
                PrefManager.putInt(PrefManager.USER_ID, data.getId());
                PrefManager.putString(PrefManager.USER_NAME, data.getName());
                PrefManager.putString(PrefManager.USER_EMAIL, data.getEmail());
                PrefManager.putString(PrefManager.USER_PHONE, data.getPhone_number());
                PrefManager.putString(PrefManager.USER_IMAGE, data.getAvatar());
                PrefManager.putString(PrefManager.USER_SSN, data.getSsn());
                PrefManager.putString(PrefManager.REQUISITION_ID, data.getRequisition_id());
                PrefManager.putString(PrefManager.USER_DOB, data.getDob());
                PrefManager.putString(PrefManager.TECHNICIAN_CITY, data.getCity());
                PrefManager.putString(PrefManager.TECHNICIAN_ADDRESS, data.getAddress());
                PrefManager.putString(PrefManager.TECHNICIAN_REGION, data.getRegion());
                PrefManager.putString(PrefManager.TECHNICIAN_COUNTRY, data.getCountry());
                PrefManager.putString(PrefManager.POSTAL_CODE, data.getPostal_code());

                if(getActivity() instanceof TechnicianMainActivity) {
                    ((TechnicianMainActivity) getActivity()).updateUserImage();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {

            }
        }), false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_submit:
                checkValidationAndProceed();
                break;

            case R.id.register_number:
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.ACTIVITY_TYPE, PhoneNumberActivity.TYPE_EDIT_PROFILE);
                ActivityController.startActivityForResult(this, PhoneNumberActivity.class, bundle, false, false, 1212);
                break;

            case R.id.text_image_upload:
                selectedImageDialog = 0;
                Utils.startImagePicker(this, 500);
                break;

            case R.id.text_upload_driver_license:
                selectedImageDialog = 1;
                Utils.startImagePicker(this);
                break;

            case R.id.text_upload_insurance_information:
                selectedImageDialog = 2;
                Utils.startImagePicker(this);
                break;

            case R.id.text_upload_vehicle_registration:
                selectedImageDialog = 3;
                Utils.startImagePicker(this);
                break;

            case R.id.no_vehicle_section:
                ActivityController.startActivityForResult(this, AddTechnicianVehicle.class, null, false, false, 121);
                break;

            case R.id.row_vehicle:
                Bundle bundle2 = new Bundle();
                bundle2.putInt(Constants.ACTIVITY_TYPE, AddVehicleActivity.EDIT_VEHICLE);
                bundle2.putSerializable("vehicle_model", vehicleModel);
                ActivityController.startActivityForResult(this, AddTechnicianVehicle.class, bundle2, false, false, 122);
                break;
        }
    }

    private void callUpdateProfileApi(Map<String, RequestBody> dataMap) {
        ServiceManager.callTechnicianRegisterApi(getActivity(), ServiceManager.API_UPDATE_TECHNICIAN_PROFILE, new ApiCallBack<>(new ApiResponseListener<RegisterResponse>() {
            @Override
            public void onApiSuccess(RegisterResponse response) {
                switch (response.getStatus()) {
                    case 200:
                        callGetProfileApi();
                        Toast.makeText(getActivity(), R.string.profile_saved_sucecssfully, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(getActivity(), failureMessage);
            }
        }), dataMap, userImage, driverLicenseImage, insuranceImage, vehicleRegistrationImage, true);
    }

    private void callGetTechnicianVehiclesApi(boolean showProgress) {
        ServiceManager.callServerApi(getActivity(), ServiceManager.API_GET_ALL_TECHNICIAN_VEHICLE, new ApiCallBack<>(new ApiResponseListener<UserAllVehiclesResponse>() {
            @Override
            public void onApiSuccess(UserAllVehiclesResponse response) {
                if(response.getData() != null) {
                    if(response.getData().size() > 0) {
                        binding.noVehicleSection.setVisibility(View.GONE);
                        binding.rowVehicle.getRoot().setVisibility(View.VISIBLE);
                        vehicleModel = response.getData().get(0);
                        binding.rowVehicle.vehicleName.setText(vehicleModel.getMake());
                        String desc = vehicleModel.getYear() + " | " + vehicleModel.getModel();
                        binding.rowVehicle.vehicleDesc.setText(desc);
                        Utils.loadPicassoImage(vehicleModel.getVehicle_image(), binding.rowVehicle.vehicleImage, binding.rowVehicle.progressBar, R.drawable.car_demo);
                    } else {
                        binding.noVehicleSection.setVisibility(View.VISIBLE);
                        binding.rowVehicle.getRoot().setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), showProgress);
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
        } else if(requestCode == 121 || requestCode == 122) {
            if(resultCode == 2 || resultCode == Activity.RESULT_OK) {
                callGetTechnicianVehiclesApi(true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
