package com.oileemobile.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.oileemobile.R;
import com.oileemobile.databinding.FragmentOilChangeBinding;
import com.oileemobile.services.FetchAddressIntentService;
import com.oileemobile.utils.Utils;

public class OilChangeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentOilChangeBinding binding;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    private AddressResultReceiver resultReceiver;
    private String currentAddress;

    public OilChangeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_oil_change, container, false);
        setupMaps();
        return binding.getRoot();
    }

    private void setupMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        resultReceiver = new AddressResultReceiver(new Handler());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        setupLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null)
                    return;

                setCurrentLocation(locationResult.getLocations().get(0));
            }
        };

        if(Utils.isLocationPermissionGranted(getActivity()))
            startLocationUpdates();
        else
            Utils.requestLocationPermission(this);
    }

    private void startLocationUpdates() {
        if(Utils.isLocationPermissionGranted(getActivity())) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
        }
    }

    private void setCurrentLocation(Location location) {
        mCurrentLocation = location;
        setMapLocation();
        startAddressIntentService(mCurrentLocation);
    }

    private void startAddressIntentService(Location location) {
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.RECEIVER, resultReceiver);
        intent.putExtra(FetchAddressIntentService.LOCATION_DATA_EXTRA, location);
        getActivity().startService(intent);
    }

    private void animateCamera() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 18.0f));
    }

    private void setMapLocation() {
        if(mMap != null) {
            if(Utils.isLocationPermissionGranted(getActivity())) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setCompassEnabled(false);
            }

            animateCamera();
        }
    }

    private void setupLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setNumUpdates(1);
    }

    class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if(resultData == null)
                return;

            String result = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY);
            currentAddress = getString(R.string.address_unavailable);
            if(result != null) {
                if(resultCode != FetchAddressIntentService.RESULT_FAILURE)
                    currentAddress = result;
            }

            binding.oilChangeAddress.setText(currentAddress);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Utils.LOCATION_PERMISSION_REQUEST_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startLocationUpdates();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
