package com.oileemobile.network.response.technician;

import com.oileemobile.network.response.ApiResponse;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-12-20 01:24
 **/
public class InvoiceDetailsResponse extends ApiResponse implements Serializable {
    private InvoiceDetail data;

    public InvoiceDetail getData() {
        return data;
    }

    public class InvoiceDetail implements Serializable {
        private String sub_total;
        private String booking_charge;

        public String getSub_total() {
            return sub_total;
        }

        public String getBooking_charge() {
            return booking_charge;
        }
    }
}
