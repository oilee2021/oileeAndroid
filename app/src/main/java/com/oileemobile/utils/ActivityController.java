package com.oileemobile.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-07-15 23:11
 **/
public class ActivityController {

    public static void startActivity(Activity activity, Class klass, Bundle bundle, boolean finishPrevious, boolean finishAffinity, boolean animate) {
        Intent intent = new Intent(activity, klass);
        if(bundle != null)
            intent.putExtras(bundle);

        if (!animate)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        activity.startActivity(intent);
        if(finishAffinity)
            activity.finishAffinity();
        else if(finishPrevious)
            activity.finish();
    }

    public static void startActivity(Activity activity, Class klass, Bundle bundle, boolean finishPrevious, boolean finishAffinity) {
        startActivity(activity, klass, bundle, finishPrevious, finishAffinity, true);
    }

    public static void startActivity(Activity activity, Class klass) {
        startActivity(activity, klass, null, false, false);
    }

    public static void startActivity(Activity activity, Class klass, Bundle bundle) {
        startActivity(activity, klass, bundle, false, false);
    }

    public static void startActivity(Activity activity, Class klass, boolean finishPrevious) {
        startActivity(activity, klass, null, finishPrevious, false);
    }

    public static void startActivity(Activity activity, Class klass, Bundle bundle, boolean finishPrevious) {
        startActivity(activity, klass, bundle, finishPrevious, false);
    }

    public static void startActivity(Activity activity, Class klass, boolean finishPrevious, boolean finishAffinity) {
        startActivity(activity, klass, null, finishPrevious, finishAffinity);
    }

    public static void startActivityForResult(Activity activity, Class klass, Bundle bundle, boolean finishPrevious, boolean finishAffinity, int requestCode) {
        Intent intent = new Intent(activity, klass);
        if(bundle != null)
            intent.putExtras(bundle);

        activity.startActivityForResult(intent, requestCode);
        if(finishAffinity)
            activity.finishAffinity();
        else if(finishPrevious)
            activity.finish();
    }

    public static void startActivityForResult(Activity activity, Class klass, int requestCode) {
        startActivityForResult(activity, klass, null, false, false, requestCode);
    }

    public static void startActivityForResult(Activity activity, Class klass, Bundle bundle, int requestCode) {
        startActivityForResult(activity, klass, bundle, false, false, requestCode);
    }

    public static void startActivityForResult(Activity activity, Class klass, boolean finishPrevious, int requestCode) {
        startActivityForResult(activity, klass, null, finishPrevious, false, requestCode);
    }

    public static void startActivityForResult(Activity activity, Class klass, Bundle bundle, boolean finishPrevious, int requestCode) {
        startActivityForResult(activity, klass, bundle, finishPrevious, false, requestCode);
    }

    public static void startActivityForResult(Activity activity, Class klass, boolean finishPrevious, boolean finishAffinity, int requestCode) {
        startActivityForResult(activity, klass, null, finishPrevious, finishAffinity, requestCode);
    }

    public static void startActivity(Fragment fragment, Class klass, Bundle bundle, boolean finishPrevious, boolean finishAffinity) {
        Intent intent = new Intent(fragment.getActivity(), klass);
        if(bundle != null)
            intent.putExtras(bundle);

        fragment.startActivity(intent);
        if(finishAffinity)
            fragment.getActivity().finishAffinity();
        else if(finishPrevious)
            fragment.getActivity().finish();
    }

    public static void startActivityForResult(Fragment fragment, Class klass, Bundle bundle, boolean finishPrevious, boolean finishAffinity, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), klass);
        if(bundle != null)
            intent.putExtras(bundle);

        fragment.startActivityForResult(intent, requestCode);
        if(finishAffinity)
            fragment.getActivity().finishAffinity();
        else if(finishPrevious)
            fragment.getActivity().finish();
    }
}
