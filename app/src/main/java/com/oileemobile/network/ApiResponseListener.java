package com.oileemobile.network;

public interface ApiResponseListener<T> {
    void onApiSuccess(T response);

    void onApiFailure(String failureMessage);
}
