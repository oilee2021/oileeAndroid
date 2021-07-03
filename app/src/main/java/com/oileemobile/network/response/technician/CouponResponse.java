package com.oileemobile.network.response.technician;

import java.io.Serializable;
import java.util.List;

import com.oileemobile.models.technician.CouponModel;
import com.oileemobile.network.response.ApiResponse;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-12-14 00:47
 **/
public class CouponResponse extends ApiResponse implements Serializable {

    private List<CouponModel> data;

    public List<CouponModel> getData() {
        return data;
    }
}
