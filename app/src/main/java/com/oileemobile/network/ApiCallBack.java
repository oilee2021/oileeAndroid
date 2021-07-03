package com.oileemobile.network;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.oileemobile.R;
import com.oileemobile.utils.MyLogger;
import com.oileemobile.utils.ProgressDialog;
import com.oileemobile.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCallBack<T> implements Callback<T> {
    private ApiResponseListener<T> apiListener;
    private Activity activity;
    private boolean showProgress;

    public ApiCallBack(ApiResponseListener<T> apiListener) {
        this.apiListener = apiListener;
    }

    void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if(showProgress) {
            ProgressDialog.hideProgressDialog();
        }

        if(response.code() == 200 || response.code() == 201) {
            if (response.body() != null) {
                apiListener.onApiSuccess(response.body());
            } else {
                apiListener.onApiFailure(activity.getString(R.string.something_went_wrong));
            }
        } else {
            ResponseBody responseBody = response.errorBody();
            try {
                if(responseBody != null) {
                    String error = responseBody.string();
                    if(error.contains("Unauthenticated")) {
                        Utils.logoutUser(activity, true);
                    }
                    MyLogger.error("ErrorResponse", error);
                    apiListener.onApiFailure(error);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, Throwable t) {
        if(showProgress) {
            ProgressDialog.hideProgressDialog();
        }
        MyLogger.error("ApiCallBack", "" + t.getMessage());
        if(activity != null && t.getMessage() != null && t.getMessage().toLowerCase().contains("timeout")) {
            apiListener.onApiFailure(activity.getString(R.string.server_timeout));
        } else {
            apiListener.onApiFailure(t.getMessage());
        }
    }
}
