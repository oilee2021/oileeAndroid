package com.oileemobile.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import com.oileemobile.models.VehicleModel;

public class UserAddVehicleResponse implements Serializable {
    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private VehicleModel data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public VehicleModel getData() {
        return data;
    }
}
