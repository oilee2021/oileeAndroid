package com.oileemobile.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import com.oileemobile.network.response.ApiResponse;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 13/3/21 11:00 PM
 **/
public class OrderDetailResponse extends ApiResponse implements Serializable {
    @SerializedName("data")
    private OrderHistoryModel data;

    public OrderHistoryModel getDetailData() {
        return data;
    }
}
