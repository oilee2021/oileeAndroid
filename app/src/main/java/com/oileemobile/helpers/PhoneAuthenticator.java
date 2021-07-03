package com.oileemobile.helpers;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import com.oileemobile.R;
import com.oileemobile.interfaces.OtpSentListener;
import com.oileemobile.interfaces.OtpVerificationListener;
import com.oileemobile.utils.MyLogger;
import com.oileemobile.utils.ProgressDialog;
import com.oileemobile.utils.Utils;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-13 15:28
 **/

public class PhoneAuthenticator implements Parcelable {
    private Context context;
    private String countryCode, mobileNumber, verificationId;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private OtpSentListener mListener;

    public PhoneAuthenticator(String countryCode, String mobileNumber) {
        this.countryCode = countryCode;
        this.mobileNumber = mobileNumber;
    }

    protected PhoneAuthenticator(Parcel in) {
        countryCode = in.readString();
        mobileNumber = in.readString();
        verificationId = in.readString();
        forceResendingToken = in.readParcelable(PhoneAuthProvider.ForceResendingToken.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(countryCode);
        dest.writeString(mobileNumber);
        dest.writeString(verificationId);
        dest.writeParcelable(forceResendingToken, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhoneAuthenticator> CREATOR = new Creator<PhoneAuthenticator>() {
        @Override
        public PhoneAuthenticator createFromParcel(Parcel in) {
            return new PhoneAuthenticator(in);
        }

        @Override
        public PhoneAuthenticator[] newArray(int size) {
            return new PhoneAuthenticator[size];
        }
    };

    public void sendOtp(Activity activity, OtpSentListener mListener) {
        ProgressDialog.showProgressDialog(activity);
        context = activity;
        this.mListener = mListener;
        Utils.sendOtpToPhone(activity, countryCode, mobileNumber, verificationStateChangedCallbacks);
    }

    public void resendOtp(Activity activity, OtpSentListener mListener) {
        ProgressDialog.showProgressDialog(activity);
        context = activity;
        this.mListener = mListener;
        Utils.sendOtpToPhone(activity, countryCode, mobileNumber, verificationStateChangedCallbacks, forceResendingToken);
    }

    public void verifyOtp(FirebaseAuth firebaseAuth, String otp, OtpVerificationListener otpVerificationListener) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, otp);
        firebaseAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        otpVerificationListener.onOtpVerificationSuccess();
                    } else {
                        otpVerificationListener.onOtpVerificationFail();
                    }
                });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken resendingToken) {
            ProgressDialog.hideProgressDialog();
            verificationId = s;
            forceResendingToken = resendingToken;
            if(mListener != null) {
                mListener.onOtpSent();
            }
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            ProgressDialog.hideProgressDialog();
            if(mListener != null) {
                mListener.onNumberVerified();
            }
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            ProgressDialog.hideProgressDialog();
            MyLogger.error("onCodeAutoTimeOut", s);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            ProgressDialog.hideProgressDialog();
            e.printStackTrace();
            int error = R.string.error_sending_otp;
            if (e instanceof FirebaseAuthInvalidCredentialsException)
                error = R.string.invalid_mobile_number;

            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
        }
    };

    public String getCountryCode() {
        return countryCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
}
