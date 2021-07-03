package com.oileemobile.network.response.technician;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import com.oileemobile.models.technician.TechnicianJobModel;
import com.oileemobile.network.response.ApiResponse;

public class TechnicianJobsResponse extends ApiResponse implements Serializable {
    @SerializedName("data")
    @Expose
    private List<TechnicianJobModel> data;

    public List<TechnicianJobModel> getData() {
        return data;
    }
}
