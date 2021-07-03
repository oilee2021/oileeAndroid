package com.oileemobile.activity.technician;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.transition.TransitionManager;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.oileemobile.R;
import com.oileemobile.activity.BaseActivity;
import com.oileemobile.databinding.ActivityAddVehicleBinding;
import com.oileemobile.dialogs.ConfirmationDialog;
import com.oileemobile.helpers.HideErrorLine;
import com.oileemobile.models.VehicleModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiRequest;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.UserAddVehicleResponse;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.Utils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.oileemobile.activity.AddVehicleActivity.ADD_VEHICLE;
import static com.oileemobile.activity.AddVehicleActivity.EDIT_VEHICLE;

public class AddTechnicianVehicle extends BaseActivity implements View.OnClickListener {
    private ActivityAddVehicleBinding binding;
    private int activityType;
    private VehicleModel editVehicleModel;
    private File selectedImageFile;
    public static final int ADD_VEHICLE_REGISTER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_vehicle);

        activityType = getIntent().getIntExtra(Constants.ACTIVITY_TYPE, ADD_VEHICLE);
        setupToolbar();
        binding.subModelLayout.setVisibility(View.GONE);
        binding.currentMileageLayout.setVisibility(View.GONE);
        binding.linearDefaultVehicle.setVisibility(View.GONE);

        if(activityType == EDIT_VEHICLE) {
            /*binding.myToolbar.toolbarEndText.setVisibility(View.VISIBLE);
            binding.myToolbar.toolbarEndText.setText(R.string.delete);
            binding.myToolbar.toolbarEndText.setOnClickListener(this);*/
            binding.addVehicleSubmit.setText(R.string.save_changes);
        }
        editVehicleModel = (VehicleModel) getIntent().getSerializableExtra("vehicle_model");
        setupVehicleDetails();

        binding.yearText.addTextChangedListener(new HideErrorLine(binding.yearLayout));
        binding.makeText.addTextChangedListener(new HideErrorLine(binding.makeLayout));
        binding.modelText.addTextChangedListener(new HideErrorLine(binding.modelLayout));
        binding.colorText.addTextChangedListener(new HideErrorLine(binding.colorLayout));
        binding.licensePlateText.addTextChangedListener(new HideErrorLine(binding.licensePlateLayout));

        binding.addVehicleSubmit.setOnClickListener(this);
        binding.imageVehicle.setOnClickListener(this);
        binding.textUploadPhoto.setOnClickListener(this);
        binding.textRemovePhoto.setOnClickListener(this);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(activityType == EDIT_VEHICLE ? R.string.edit_vehicle : R.string.add_vehicle);
        binding.myToolbar.getRoot().setBackgroundResource(R.drawable.main_background_gradient);
    }

    private void setupVehicleDetails() {
        if(editVehicleModel != null) {
            binding.yearText.setText(editVehicleModel.getYear());
            binding.makeText.setText(editVehicleModel.getMake());
            binding.modelText.setText(editVehicleModel.getModel());
            binding.colorText.setText(editVehicleModel.getColor());
            binding.licensePlateText.setText(editVehicleModel.getYear());
            setVehicleImage();
        }
    }

    private void setVehicleImage() {
        String vehicleImage = editVehicleModel.getVehicle_image();
        if(vehicleImage != null) {
            if(activityType == ADD_VEHICLE_REGISTER) {
                selectedImageFile = new File(vehicleImage);
                Utils.loadPicassoImage(selectedImageFile, binding.imageVehicle, null, R.drawable.car_demo);
            } else {
                Utils.loadPicassoImage(vehicleImage, binding.imageVehicle, null);
            }
        } else {
            Utils.loadPicassoImage(R.drawable.ic_camera, binding.imageVehicle, null);
        }
    }

    public static Map<String, RequestBody> getAddTechnicianVehicleMultipartData(VehicleModel vehicleModel, boolean isUpdate) {
        Map<String, RequestBody> dataMap = new HashMap<>();
        dataMap.put("year", RequestBody.create(MediaType.parse("text/plain"), vehicleModel.getYear()));
        dataMap.put("make", RequestBody.create(MediaType.parse("text/plain"), vehicleModel.getMake()));
        dataMap.put("model", RequestBody.create(MediaType.parse("text/plain"), vehicleModel.getModel()));
        dataMap.put("color", RequestBody.create(MediaType.parse("text/plain"), vehicleModel.getColor()));
        dataMap.put("licence_plate", RequestBody.create(MediaType.parse("text/plain"), vehicleModel.getLicencePlate()));
        if(isUpdate) {
            dataMap.put("_method", RequestBody.create(MediaType.parse("text/plain"), "PUT"));
        }
        return dataMap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_vehicle_submit:
                checkValidationAndProceed();
                break;

            case R.id.toolbar_end_text:
                new ConfirmationDialog(R.string.delete, R.string.vehicle_delete_confirmation_text, R.string.delete, R.string.cancel, which -> {
                    if(which == DialogInterface.BUTTON_POSITIVE) {
                        callDeleteVehicleApi();
                    }
                }).show(getSupportFragmentManager(), "");
                break;

            case R.id.image_vehicle:
            case R.id.text_upload_photo:
                Utils.startImagePicker(this, 1000);
                break;

            case R.id.text_remove_photo:
                removeVehiclePhoto();
                break;
        }
    }

    public void onImageSelected(File file) {
        Utils.loadPicassoImage(file, binding.imageVehicle, null);
        selectedImageFile = file;
        TransitionManager.beginDelayedTransition(binding.mainView);
        binding.textRemovePhoto.setVisibility(View.VISIBLE);
    }

    private void removeVehiclePhoto() {
        if(selectedImageFile != null) {
            selectedImageFile = null;
            if(editVehicleModel != null) setVehicleImage();
            else Utils.loadPicassoImage(R.drawable.ic_camera, binding.imageVehicle, null);
            TransitionManager.beginDelayedTransition(binding.mainView);
            binding.textRemovePhoto.setVisibility(View.GONE);
        }
    }

    private void checkValidationAndProceed() {
        String fieldBlankError = getString(R.string.field_blank_error);
        String year = binding.yearText.getText().toString().trim();
        if(year.isEmpty()) {
            binding.yearLayout.setError(fieldBlankError);
            return;
        }

        String make = binding.makeText.getText().toString().trim();
        if(make.isEmpty()) {
            binding.makeLayout.setError(fieldBlankError);
            return;
        }

        String model = binding.modelText.getText().toString().trim();
        if(model.isEmpty()) {
            binding.modelLayout.setError(fieldBlankError);
            return;
        }

        String color = binding.colorText.getText().toString().trim();
        if(color.isEmpty()) {
            binding.colorLayout.setError(fieldBlankError);
            return;
        }

        String licensePlate = binding.licensePlateText.getText().toString().trim();
        if(licensePlate.isEmpty()) {
            binding.licensePlateLayout.setError(fieldBlankError);
            return;
        }

        VehicleModel vehicleModel = new VehicleModel(0, year, make, model, color, licensePlate, false);
        if(activityType == EDIT_VEHICLE) {
            vehicleModel.setId(editVehicleModel.getId());
            callEditVehicleApi(vehicleModel);
        } else if(activityType == ADD_VEHICLE) {
            callAddVehicleApi(vehicleModel);
        } else {
            Intent intent = new Intent();
            if(selectedImageFile != null) {
                vehicleModel.setVehicle_image(selectedImageFile.getPath());
            }
            intent.putExtra("vehicle", vehicleModel);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void callAddVehicleApi(VehicleModel vehicleModel) {
        ServiceManager.callMultipartServerApi(this, ServiceManager.API_ADD_TECHNICIAN_VEHICLE, new ApiCallBack<>(new ApiResponseListener<UserAddVehicleResponse>() {
            @Override
            public void onApiSuccess(UserAddVehicleResponse response) {
                if(response.getStatus() == 200) {
                    Toast.makeText(AddTechnicianVehicle.this, R.string.vehicle_added_successfully, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("vehicle", vehicleModel);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(AddTechnicianVehicle.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(AddTechnicianVehicle.this, failureMessage);
            }
        }), getAddTechnicianVehicleMultipartData(vehicleModel, false), selectedImageFile, "image");
    }

    private void callEditVehicleApi(VehicleModel vehicleModel) {
        ServiceManager.callMultipartServerApi(this, ServiceManager.API_UPDATE_TECHNICIAN_VEHICLE, new ApiCallBack<>(new ApiResponseListener<UserAddVehicleResponse>() {
            @Override
            public void onApiSuccess(UserAddVehicleResponse response) {
                if(response.getStatus() == 200) {
                    Toast.makeText(AddTechnicianVehicle.this, R.string.vehicle_updated_successfully, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("vehicle", vehicleModel);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(AddTechnicianVehicle.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(AddTechnicianVehicle.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), editVehicleModel.getId(), getAddTechnicianVehicleMultipartData(vehicleModel, true), selectedImageFile, "image");
    }

    private void callDeleteVehicleApi() {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.set_method("DELETE");

        ServiceManager.callServerApi(this, ServiceManager.API_DELETE_TECHNICIAN_VEHICLE, new ApiCallBack<>(new ApiResponseListener<UserAddVehicleResponse>() {
            @Override
            public void onApiSuccess(UserAddVehicleResponse response) {
                if(response.getMessage().toLowerCase().contains("successfully")) {
                    Toast.makeText(AddTechnicianVehicle.this, R.string.vehicle_deleted_successfully, Toast.LENGTH_SHORT).show();
                    setResult(2);
                    finish();
                } else {
                    Toast.makeText(AddTechnicianVehicle.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(AddTechnicianVehicle.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), editVehicleModel.getId(), apiRequest);
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
