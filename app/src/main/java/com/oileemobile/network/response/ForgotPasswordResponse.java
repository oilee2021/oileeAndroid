package com.oileemobile.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ForgotPasswordResponse implements Serializable {
    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private ForgotPasswordData data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ForgotPasswordData getData() {
        return data;
    }

    public class ForgotPasswordData implements Serializable {
        private String email, token;

        public String getEmail() {
            return email;
        }

        public String getToken() {
            return token;
        }
    }
}
