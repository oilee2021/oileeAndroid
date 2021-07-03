package com.oileemobile.network.response.technician;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.oileemobile.models.technician.TechnicianOrderHistoryModel;
import com.oileemobile.network.response.ApiResponse;

public class TechnicianOrderHistoryResponse extends ApiResponse {
    @SerializedName("data")
    @Expose
    private List<TechnicianOrderHistoryModel> data;

    public List<TechnicianOrderHistoryModel> getData() {
        return data;
    }
}
