package com.oileemobile.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.oileemobile.models.OrderHistoryModel;

public class OrderHistoryResponse extends ApiResponse {
    @SerializedName("data")
    @Expose
    private List<OrderHistoryModel> data;

    public List<OrderHistoryModel> getData() {
        return data;
    }
}
