package com.oileemobile.network.response.technician;

import com.oileemobile.models.technician.LatestInvoiceModel;
import com.oileemobile.network.response.ApiResponse;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-12-18 01:07
 **/
public class LatestInvoiceResponse extends ApiResponse implements Serializable {
    private LatestInvoiceModel data;

    public LatestInvoiceModel getData() {
        return data;
    }
}
