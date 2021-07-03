package com.oileemobile;

import android.app.Application;

import com.google.android.libraries.places.api.Places;
import com.oileemobile.utils.MyLogger;
import com.oileemobile.utils.PrefManager;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-08-28 19:36
 **/
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Places.initialize(getApplicationContext(), "AIzaSyCqoVC17UDk0bxWVLhh2aXDPVoPYzEeio4");
        PrefManager.initialize(getApplicationContext());
        MyLogger.error("My Token", "Token: " + PrefManager.getString(PrefManager.TOKEN));
        MyLogger.error("My Fcm Token", "Fcm Token: " + PrefManager.getString(PrefManager.FCM_TOKEN));
    }
}