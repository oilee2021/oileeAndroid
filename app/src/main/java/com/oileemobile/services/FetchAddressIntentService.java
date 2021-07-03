package com.oileemobile.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.oileemobile.BuildConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FetchAddressIntentService extends IntentService {

    protected ResultReceiver mResultReceiver;
    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_FAILURE = 1;
    public static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    public FetchAddressIntentService() {
        super("FetchAddressFromLocation");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent == null)
            return;

        String errorMessage = "";
        Location location = intent.getParcelableExtra(LOCATION_DATA_EXTRA);
        mResultReceiver = intent.getParcelableExtra(RECEIVER);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(addresses == null || addresses.size() == 0) {
            if(errorMessage.isEmpty())
                errorMessage = "No address found!";

            deliverResultToReceiver(RESULT_FAILURE, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++)
                addressFragments.add(address.getAddressLine(i));

            deliverResultToReceiver(RESULT_SUCCESS, TextUtils.join(System.getProperty("line.separator"), addressFragments));
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(RESULT_DATA_KEY, message);
        mResultReceiver.send(resultCode, bundle);
    }
}
