package com.oileemobile.network.response.technician;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.oileemobile.models.technician.TechnicianJobModel;
import com.oileemobile.network.response.ApiResponse;

public class TechnicianOrderDetailResponse extends ApiResponse {
    @SerializedName("data")
    @Expose
    private TechnicianJobModel data;

    public TechnicianJobModel getData() {
        return data;
    }
}
