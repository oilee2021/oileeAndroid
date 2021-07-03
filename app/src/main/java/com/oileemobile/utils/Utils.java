package com.oileemobile.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.oileemobile.R;
import com.oileemobile.activity.ChooseTypeActivity;
import com.oileemobile.activity.CustomerMainActivity;
import com.oileemobile.activity.technician.TechnicianMainActivity;
import com.oileemobile.helpers.PicassoCallback;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.CustomerProfileResponse;
import com.oileemobile.network.response.technician.TechnicianProfileResponse;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-07-12 00:55
 **/
public class Utils {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 124;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 125;
    public static final int STORAGE_PERMISSION_REQUEST_CODE = 126;

    public static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo info = connectivityManager.getActiveNetworkInfo();

            return (info != null && info.isConnected());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void enableBackButton(Activity activity, View backButton) {
        backButton.setOnClickListener(view -> {
            Utils.hideKeyboard(activity);
            activity.onBackPressed();
        });
    }

    public static void hideKeyboard(Activity activity) {
        if(activity != null) {
            View view = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            hideKeyboard(activity, view);
        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showSnackbar(Activity activity, @StringRes int message) {
        showSnackbar(activity, activity.getString(message));
    }

    public static void showSnackbar(Activity activity, String message) {
        View view = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static int dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static boolean isLocationPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestLocationPermission(Fragment fragment) {
        if(!isLocationPermissionGranted(fragment.getActivity())) {
            String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
            fragment.requestPermissions(permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public static void requestLocationPermission(Activity activity) {
        if(!isLocationPermissionGranted(activity)) {
            String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(activity, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public static void sendOtpToPhone(Activity activity, String countryCode, String mobileNumber,
                                      PhoneAuthProvider.OnVerificationStateChangedCallbacks callback, PhoneAuthProvider.ForceResendingToken resendToken) {
        if(resendToken != null) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(countryCode + mobileNumber, 60, TimeUnit.SECONDS, activity, callback, resendToken);
        } else {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(countryCode + mobileNumber, 60, TimeUnit.SECONDS, activity, callback);
        }
    }

    public static void sendOtpToPhone(Activity activity, String countryCode, String mobileNumber, PhoneAuthProvider.OnVerificationStateChangedCallbacks callback) {
        sendOtpToPhone(activity, countryCode, mobileNumber, callback, null);
    }

    public static void setCustomerLoggedIn(Activity activity, String token) {
        PrefManager.putString(PrefManager.TOKEN, token);
        ServiceManager.callServerApi(activity, ServiceManager.API_GET_CUSTOMER_PROFILE, new ApiCallBack<>(new ApiResponseListener<CustomerProfileResponse>() {
            @Override
            public void onApiSuccess(CustomerProfileResponse response) {
                ProgressDialog.hideProgressDialog();
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    CustomerProfileResponse.CustomerData data = response.getData();
                    PrefManager.putBoolean(PrefManager.IS_LOGGED, true);
                    PrefManager.putInt(PrefManager.USER_ID, data.getId());
                    PrefManager.putString(PrefManager.USER_NAME, data.getName());
                    PrefManager.putString(PrefManager.USER_EMAIL, data.getEmail());
                    PrefManager.putString(PrefManager.USER_PHONE, data.getPhone_number());
                    PrefManager.putString(PrefManager.USER_DOB, data.getDob());
                    PrefManager.putString(PrefManager.USER_IMAGE, data.getAvatar());

                    ActivityController.startActivity(activity, CustomerMainActivity.class, true, true);
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                ProgressDialog.hideProgressDialog();
                Toast.makeText(activity, "Error getting your profile!", Toast.LENGTH_SHORT).show();
            }
        }), false);
    }

    public static void setTechnicianLoggedIn(Activity activity, String token) {
        PrefManager.putString(PrefManager.TOKEN, token);
        setTechnicianLoggedIn(activity);
    }

    public static void setTechnicianLoggedIn(Activity activity) {
        ServiceManager.callServerApi(activity, ServiceManager.API_TECHNICIAN_GET_PROFILE, new ApiCallBack<>(new ApiResponseListener<TechnicianProfileResponse>() {
            @Override
            public void onApiSuccess(TechnicianProfileResponse response) {
                ProgressDialog.hideProgressDialog();
                TechnicianProfileResponse.CustomerData data = response.getData();
                PrefManager.putBoolean(PrefManager.IS_LOGGED, true);
                PrefManager.putInt(PrefManager.USER_ID, data.getId());
                PrefManager.putString(PrefManager.USER_NAME, data.getName());
                PrefManager.putString(PrefManager.USER_EMAIL, data.getEmail());
                PrefManager.putString(PrefManager.USER_PHONE, data.getPhone_number());
                PrefManager.putString(PrefManager.USER_SSN, data.getSsn());
                PrefManager.putString(PrefManager.USER_IMAGE, data.getAvatar());
                PrefManager.putString(PrefManager.USER_DRIVER_LICENSE, data.getDriver_license());
                PrefManager.putString(PrefManager.USER_INSURANCE_INFORMATION, data.getInsurance_information());
                PrefManager.putString(PrefManager.USER_VEHICLE_REGISTRATION, data.getVehicle_registration());
                PrefManager.putBoolean(PrefManager.USER_ONLINE_STATUS, data.isOnline());
                PrefManager.putBoolean(PrefManager.IS_VERIFIED, data.isVerified());
                PrefManager.putBoolean(PrefManager.IS_ACTIVATED, data.isActivated());
                PrefManager.putString(PrefManager.REQUISITION_ID, data.getRequisition_id());
                PrefManager.putString(PrefManager.USER_DOB, data.getDob());
                PrefManager.putString(PrefManager.TECHNICIAN_CITY, data.getCity());
                PrefManager.putString(PrefManager.TECHNICIAN_ADDRESS, data.getAddress());
                PrefManager.putString(PrefManager.TECHNICIAN_REGION, data.getRegion());
                PrefManager.putString(PrefManager.TECHNICIAN_COUNTRY, data.getCountry());
                PrefManager.putString(PrefManager.POSTAL_CODE, data.getPostal_code());

                ActivityController.startActivity(activity, TechnicianMainActivity.class, true, true);
            }

            @Override
            public void onApiFailure(String failureMessage) {
                ProgressDialog.hideProgressDialog();
                Toast.makeText(activity, "Error getting your profile!", Toast.LENGTH_SHORT).show();
            }
        }), false);
    }

    public static void logoutUser(Activity activity, boolean isForced) {
        if(isForced) {
            Toast.makeText(activity, R.string.invalid_token, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(activity, R.string.logged_out_successfully, Toast.LENGTH_SHORT).show();
        }

        deleteFirebaseToken();
        ActivityController.startActivity(activity, ChooseTypeActivity.class, true, true);
        PrefManager.clearPrefs();
    }

    private static void deleteFirebaseToken() {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public static void loadPicassoImage(String image, ImageView imageView, ProgressBar progressBar) {
        if(!TextUtils.isEmpty(image)) {
            Picasso.get().load(image).error(R.drawable.app_logo).into(imageView, new PicassoCallback(progressBar));
        } else {
            Picasso.get().load(R.drawable.app_logo).into(imageView, new PicassoCallback(progressBar));
        }
    }

    public static void loadPicassoImage(String image, ImageView imageView, ProgressBar progressBar, @DrawableRes int errorImg) {
        if(!TextUtils.isEmpty(image)) {
            Picasso.get().load(image).error(errorImg).into(imageView, new PicassoCallback(progressBar));
        } else {
            Picasso.get().load(errorImg).into(imageView, new PicassoCallback(progressBar));
        }
    }

    public static void loadPicassoImage(File image, ImageView imageView, ProgressBar progressBar, @DrawableRes int errorImg) {
        if(image != null) {
            Picasso.get().load(image).error(errorImg).into(imageView, new PicassoCallback(progressBar));
        } else {
            Picasso.get().load(errorImg).into(imageView, new PicassoCallback(progressBar));
        }
    }

    public static void loadPicassoImage(@DrawableRes int imageId, ImageView imageView, ProgressBar progressBar) {
        if(imageId != 0) {
            Picasso.get().load(imageId).error(R.drawable.app_logo).into(imageView, new PicassoCallback(progressBar));
        } else {
            Picasso.get().load(R.drawable.app_logo).into(imageView, new PicassoCallback(progressBar));
        }
    }

    public static void loadPicassoImage(File file, ImageView imageView, ProgressBar progressBar) {
        if(file != null) {
            Picasso.get().load(file).error(R.drawable.app_logo).into(imageView, new PicassoCallback(progressBar));
        } else {
            Picasso.get().load(R.drawable.app_logo).into(imageView, new PicassoCallback(progressBar));
        }
    }

    public static boolean checkCameraPermission(Context context) {
        String[] permissions = { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };
        for(String permission : permissions) {
            if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static void requestCameraPermission(Activity activity) {
        if(!checkCameraPermission(activity)) {
            ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    public static void requestCameraPermission(Fragment fragment) {
        fragment.requestPermissions(new String[] { Manifest.permission.CAMERA },
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    public static boolean checkStoragePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestStoragePermission(Activity activity) {
        if(!checkStoragePermission(activity))
            ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    STORAGE_PERMISSION_REQUEST_CODE);
    }

    public static String getDateFromFormat(String date, String format) {
        String returnDate = "";
        Date myDate = null;
        try {
            myDate = new SimpleDateFormat(format, Locale.getDefault()).parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(myDate != null) {
            returnDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(myDate);
        }

        return returnDate;
    }

    public static void startImagePicker(Activity activity, int maxSize) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setAspectRatio(1, 1)
                .setRequestedSize(maxSize, maxSize, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .setAllowFlipping(false)
                .start(activity);
    }

    public static void startImagePicker(Activity activity) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setRequestedSize(1000, 1000, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .setAllowFlipping(false)
                .start(activity);
    }

    public static void startImagePicker(Fragment fragment, int maxSize) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setAspectRatio(1, 1)
                .setRequestedSize(maxSize, maxSize, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .setAllowFlipping(false)
                .start(fragment.getContext(), fragment);
    }

    public static void startImagePicker(Fragment fragment) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setRequestedSize(1000, 1000, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .setAllowFlipping(false)
                .start(fragment.getContext(), fragment);
    }

    public static void startImagePicker(Fragment fragment, Uri uri) {
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setRequestedSize(1000, 1000, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .setAllowFlipping(false)
                .start(fragment.getContext(), fragment);
    }

    public static boolean isTechnicianActivated() {
        return PrefManager.getInt(PrefManager.USER_TYPE, Constants.TYPE_CUSTOMER) == Constants.TYPE_TECHNICIAN
                && PrefManager.getBoolean(PrefManager.IS_ACTIVATED);
    }

    public static void showConfirmationDialog(Activity activity, String confirmationText,
                                              String buttonText, View.OnClickListener onButtonClickListener) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_confirmation, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        TextView tvConfirmation = view.findViewById(R.id.tv_confirmation_text);
        TextView tvButton = view.findViewById(R.id.tv_button);
        tvConfirmation.setText(confirmationText);
        tvButton.setText(buttonText);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        tvButton.setOnClickListener(v -> {
            onButtonClickListener.onClick(v);
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    public static void updateFirebaseToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            PrefManager.putString(PrefManager.FCM_TOKEN, token);
            MyLogger.error("FCM UPDATED", PrefManager.getString(PrefManager.FCM_TOKEN));
        });
    }

    public static String getTimeFromDate(String format, Date date) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }
}
