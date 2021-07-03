package com.oileemobile.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import com.oileemobile.models.CustomerNotificationModel;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-11-19 00:34
 **/
public class CustomerNotificationResponse implements Serializable {
    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("message")
    @Expose
    private String message;

    private List<CustomerNotificationModel> data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<CustomerNotificationModel> getData() {
        return data;
    }
}
