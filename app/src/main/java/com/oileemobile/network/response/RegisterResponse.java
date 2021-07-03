package com.oileemobile.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RegisterResponse implements Serializable {
    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private RegisterData data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public RegisterData getData() {
        return data;
    }

    public class RegisterData implements Serializable {
        @SerializedName("id")
        @Expose
        private int id;

        @SerializedName("token")
        @Expose
        private String token;

        public int getId() {
            return id;
        }

        public String getToken() {
            return token;
        }
    }
}
