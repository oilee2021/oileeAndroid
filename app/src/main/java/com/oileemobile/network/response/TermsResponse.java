package com.oileemobile.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import com.oileemobile.models.TermsModel;

public class TermsResponse implements Serializable {
    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private List<TermsModel> data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<TermsModel> getData() {
        return data;
    }
}
