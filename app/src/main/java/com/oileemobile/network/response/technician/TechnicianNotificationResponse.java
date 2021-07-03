package com.oileemobile.network.response.technician;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import com.oileemobile.models.technician.NotificationModel;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-11-19 00:34
 **/
public class TechnicianNotificationResponse implements Serializable {
    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("message")
    @Expose
    private String message;

    private List<NotificationModel> data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<NotificationModel> getData() {
        return data;
    }
}
