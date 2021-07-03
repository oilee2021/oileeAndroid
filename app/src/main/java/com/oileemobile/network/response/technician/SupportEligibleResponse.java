package com.oileemobile.network.response.technician;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import com.oileemobile.network.response.ApiResponse;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-12-08 00:10
 **/
public class SupportEligibleResponse extends ApiResponse implements Serializable {
    @SerializedName("data")
    @Expose
    private EligibleModel eligibleModel;

    public EligibleModel getEligibleModel() {
        return eligibleModel;
    }
    public class EligibleModel implements Serializable {
        private boolean is_eligible;

        public boolean isEligible() {
            return is_eligible;
        }
    }
}
