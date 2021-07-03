package com.oileemobile.utils;

import android.util.Log;

import com.oileemobile.BuildConfig;

public class MyLogger {
    private static final boolean IS_DEBUG = BuildConfig.DEBUG;

    public static void error(String TAG, String message) {
        if (IS_DEBUG) {
            Log.e(TAG, message);
        }
    }

    public static void debug(String TAG, String message) {
        if (IS_DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void warn(String TAG, String message) {
        if (IS_DEBUG) {
            Log.w(TAG, message);
        }
    }

    public static void verbose(String TAG, String message) {
        if (IS_DEBUG) {
            Log.v(TAG, message);
        }
    }

    public static void info(String TAG, String message) {
        if (IS_DEBUG) {
            Log.i(TAG, message);
        }
    }
}
