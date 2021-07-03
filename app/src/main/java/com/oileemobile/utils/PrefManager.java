package com.oileemobile.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private static PrefManager prefManager;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static final String PREF_NAME = "user_details";

    public static final String IS_LOGGED = "is_logged";
    public static final String INTRO_SKIPPED = "intro_skipped";
    public static final String USER_TYPE = "user_type";
    public static final String TOKEN = "token";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_PHONE = "user_phone";
    public static final String USER_DOB = "user_dob";
    public static final String USER_IMAGE = "user_image";
    public static final String FCM_TOKEN = "fcm_token";

    //Technician
    public static final String USER_SSN = "user_ssn";
    public static final String USER_DRIVER_LICENSE = "user_driver_license";
    public static final String USER_INSURANCE_INFORMATION = "user_insurance_information";
    public static final String USER_VEHICLE_REGISTRATION = "user_vehicle_registration";
    public static final String USER_ONLINE_STATUS = "is_user_online";
    public static final String IS_VERIFIED = "is_user_verified";
    public static final String IS_ACTIVATED = "is_user_activated";
    public static final String REQUISITION_ID = "requisition_id";
    public static final String TECHNICIAN_CITY = "technician_city";
    public static final String TECHNICIAN_ADDRESS = "technician_address";
    public static final String TECHNICIAN_REGION = "technician_region";
    public static final String TECHNICIAN_COUNTRY = "technician_country";
    public static final String POSTAL_CODE = "technician_postal_code";

    @SuppressLint("CommitPrefEdits")
    private PrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void initialize(Context context) {
        if(prefManager == null) {
            prefManager = new PrefManager(context);
        }
    }

    public static void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public static void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public static void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public static void clearPrefs() {
        editor.clear();
        editor.apply();
    }
}
