package com.oileemobile.network.response.technician;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 10/4/21 2:04 AM
 **/
public class BackgroundCheckResponse implements Serializable {
    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private DataBean data;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public DataBean getData() {
        return data;
    }

    public static class DataBean implements Serializable {
        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("result")
        @Expose
        private String result;

        public String getStatus() {
            return status;
        }

        public String getResult() {
            return result;
        }
    }
}
