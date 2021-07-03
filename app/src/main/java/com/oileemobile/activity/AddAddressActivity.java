package com.oileemobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.transition.TransitionManager;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.oileemobile.R;
import com.oileemobile.databinding.ActivityAddAddressBinding;
import com.oileemobile.dialogs.ConfirmationDialog;
import com.oileemobile.dialogs.InformationDialog;
import com.oileemobile.helpers.HideErrorLine;
import com.oileemobile.models.AddressModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.UserAddAddressResponse;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.Utils;

import java.util.Arrays;
import java.util.List;

public class AddAddressActivity extends BaseActivity implements View.OnClickListener, OnMapReadyCallback,
        GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnMyLocationButtonClickListener {

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    private Context context;
    private ActivityAddAddressBinding binding;
    public static final int ADD_ADDRESS = 1;
    public static final int EDIT_ADDRESS = 2;

    private int activityType;
    private AddressModel editAddressModel;
    private boolean isDefault, isFirstItem;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_address);
        context=AddAddressActivity.this;
        Bundle bundle = getIntent().getExtras();
        activityType = ADD_ADDRESS;
        if(bundle != null) {
            activityType = bundle.getInt(Constants.ACTIVITY_TYPE, ADD_ADDRESS);
            isFirstItem = bundle.getBoolean(Constants.IS_FIRST_ITEM, false);
        }
        setupToolbar();

        if(activityType == EDIT_ADDRESS) {
            binding.myToolbar.toolbarEndText.setVisibility(View.VISIBLE);
            binding.myToolbar.toolbarEndText.setText(R.string.delete);
            binding.myToolbar.toolbarEndText.setOnClickListener(this);
            editAddressModel = (AddressModel) getIntent().getSerializableExtra("address_model");
            setupAddressDetails();
        }
        setupMaps();

        binding.addressText.addTextChangedListener(new HideErrorLine(binding.addressLayout));
        binding.secondaryAddressText.addTextChangedListener(new HideErrorLine(binding.secondaryAddressLayout));
        binding.cityText.addTextChangedListener(new HideErrorLine(binding.cityLayout));
        binding.stateText.addTextChangedListener(new HideErrorLine(binding.stateLayout));
        binding.zipText.addTextChangedListener(new HideErrorLine(binding.zipLayout));
        binding.tagText.addTextChangedListener(new HideErrorLine(binding.tagLayout));

        binding.addAddressSubmit.setOnClickListener(this);
        if(isFirstItem) {
            setDefaultStatus(true);
            binding.linearDefaultAddress.setEnabled(false);
        } else {
            binding.linearDefaultAddress.setOnClickListener(this);
        }


//        binding.addressText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                    locationPlacesMethod();
//            }
//        });
    }


    private void locationPlacesMethod()
    {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(context);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
//                Log.e(context, "Place: " + place.getName() + ", " + place.getId());
                

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(activityType == ADD_ADDRESS ? R.string.add_address : R.string.edit_address);
        binding.myToolbar.getRoot().setBackgroundResource(R.drawable.main_background_gradient);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        if(Utils.isLocationPermissionGranted(this)) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setCompassEnabled(false);
        }
        if(activityType == EDIT_ADDRESS) {
            setCurrentLocation(false);
            binding.locationSelectMarker.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCameraMoveStarted(int i) {
        if(i == REASON_GESTURE) {
            binding.locationSelectMarker.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        binding.locationSelectMarker.setVisibility(View.INVISIBLE);
        return false;
    }

    private void setupMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null)
                    return;

                try {
                    if(activityType != EDIT_ADDRESS) {
                        Location location = locationResult.getLocations().get(0);
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                        setCurrentLocation(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        if(Utils.isLocationPermissionGranted(this))
            startLocationUpdates();
        else
            Utils.requestLocationPermission(this);
    }

    private void startLocationUpdates() {
        if(Utils.isLocationPermissionGranted(this)) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
        }
    }

    private void setCurrentLocation(boolean animate) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 18.0f);
        if (animate) {
            mMap.animateCamera(cameraUpdate);
        } else {
            mMap.moveCamera(cameraUpdate);
        }
    }

    private void setupLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setNumUpdates(1);
    }

    private void setupAddressDetails() {
        if(editAddressModel != null) {
            binding.addressText.setText(editAddressModel.getAddressOne());
            binding.secondaryAddressText.setText(editAddressModel.getAddressTwo());
            binding.cityText.setText(editAddressModel.getCity());
            binding.stateText.setText(editAddressModel.getState());
            binding.zipText.setText(editAddressModel.getZip());
            binding.tagText.setText(editAddressModel.getTag());
            isFirstItem = editAddressModel.isIsDefault();
            lat = editAddressModel.getLat();
            lng = editAddressModel.getLng();
        }
    }

    private void setDefaultStatus(boolean isDefault) {
        this.isDefault = isDefault;
        TransitionManager.beginDelayedTransition(binding.mainView);
        binding.imageTick.setImageResource(isDefault ? R.drawable.ic_tick : R.drawable.ic_untick);
    }

    private void toggleDefaultStatus() {
        setDefaultStatus(!isDefault);
    }

    private void checkValidationAndProceed() {
        String fieldBlankError = getString(R.string.field_blank_error);
        String address = binding.addressText.getText().toString().trim();
        if(address.isEmpty()) {
            binding.addressLayout.setError(fieldBlankError);
            return;
        }

        String secondaryAddress = binding.secondaryAddressText.getText().toString().trim();

        String city = binding.cityText.getText().toString().trim();
        if(city.isEmpty()) {
            binding.cityLayout.setError(fieldBlankError);
            return;
        }

        String state = binding.stateText.getText().toString().trim();
        if(state.isEmpty()) {
            binding.stateLayout.setError(fieldBlankError);
            return;
        }

        String zipcode = binding.zipText.getText().toString().trim();
        if(zipcode.isEmpty()) {
            binding.zipLayout.setError(fieldBlankError);
            return;
        }

        String tag = binding.tagText.getText().toString().trim();
        if(tag.isEmpty()) {
            binding.tagLayout.setError(fieldBlankError);
            return;
        }

        LatLng latLng = mMap.getCameraPosition().target;
        lat = latLng.latitude;
        lng = latLng.longitude;
        if(activityType == EDIT_ADDRESS) {
            AddressModel addressModel = new AddressModel(editAddressModel.getId(), address, secondaryAddress, city,
                    state, zipcode, tag, isDefault, lat, lng);
            callEditAddressApi(addressModel);
        } else {
            AddressModel addressModel = new AddressModel(0, address, secondaryAddress, city,
                    state, zipcode, tag, isDefault, lat, lng);
            callAddAddressApi(addressModel);
        }
    }

    private void callAddAddressApi(AddressModel addressModel) {
        ServiceManager.callServerApi(this, ServiceManager.API_ADD_USER_ADDRESS, new ApiCallBack<>(new ApiResponseListener<UserAddAddressResponse>() {
            @Override
            public void onApiSuccess(UserAddAddressResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    Toast.makeText(AddAddressActivity.this, R.string.address_added_successfully, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("address", addressModel);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(AddAddressActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(AddAddressActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), addressModel);
    }

    private void callEditAddressApi(AddressModel addressModel) {
        ServiceManager.callServerApi(this, ServiceManager.API_UPDATE_USER_ADDRESS, new ApiCallBack<>(new ApiResponseListener<UserAddAddressResponse>() {
            @Override
            public void onApiSuccess(UserAddAddressResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    Toast.makeText(AddAddressActivity.this, R.string.address_updated_successfully, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("address", addressModel);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(AddAddressActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(AddAddressActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), editAddressModel.getId(), addressModel);
    }

    private void callDeleteAddressApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_DELETE_USER_ADDRESS, new ApiCallBack<>(new ApiResponseListener<UserAddAddressResponse>() {
            @Override
            public void onApiSuccess(UserAddAddressResponse response) {
                if(response.getMessage().toLowerCase().contains("successfully")) {
                    Toast.makeText(AddAddressActivity.this, R.string.address_deleted_successfully, Toast.LENGTH_SHORT).show();
                    setResult(2);
                    finish();
                } else {
                    Toast.makeText(AddAddressActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(AddAddressActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), editAddressModel.getId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_address_submit:
                checkValidationAndProceed();
                break;

            case R.id.toolbar_end_text:
                if(isFirstItem) {
                    new InformationDialog(R.string.delete, R.string.cant_delete_default_address, null).show(getSupportFragmentManager(), "");
                } else {
                    new ConfirmationDialog(R.string.delete, R.string.address_delete_confirmation_text, R.string.delete, R.string.cancel, which -> {
                        if(which == DialogInterface.BUTTON_POSITIVE) {
                            callDeleteAddressApi();
                        }
                    }).show(getSupportFragmentManager(), "");
                }
                break;

            case R.id.linear_default_address:
                toggleDefaultStatus();
                break;
        }
    }
}
