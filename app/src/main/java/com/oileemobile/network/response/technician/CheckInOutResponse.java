package com.oileemobile.network.response.technician;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import com.oileemobile.models.technician.CheckInOutModel;
import com.oileemobile.network.response.ApiResponse;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-12-10 01:09
 **/
public class CheckInOutResponse extends ApiResponse implements Serializable {
    @SerializedName("data")
    @Expose
    private CheckInOutModel data;

    public CheckInOutModel getData() {
        return data;
    }
}
