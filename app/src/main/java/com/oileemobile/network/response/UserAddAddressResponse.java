package com.oileemobile.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import com.oileemobile.models.AddressModel;

public class UserAddAddressResponse implements Serializable {
    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private AddressModel data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public AddressModel getData() {
        return data;
    }
}
