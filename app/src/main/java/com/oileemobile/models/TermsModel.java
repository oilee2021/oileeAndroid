package com.oileemobile.models;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-26 09:33
 **/
public class TermsModel implements Serializable {
    private String customer_description, technician_description;

    public String getCustomerDescription() {
        return customer_description;
    }

    public String getTechnicianDescription() {
        return technician_description;
    }
}
