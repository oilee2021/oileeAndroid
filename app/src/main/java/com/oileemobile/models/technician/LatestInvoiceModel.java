package com.oileemobile.models.technician;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-12-18 01:06
 **/
public class LatestInvoiceModel implements Serializable {

    /**
     * id : 13
     * technician_name : Shubham Mathur
     * technician_avatar : http://oilapp.ilogix.co.in/avatar/default.png
     * star : 4
     * rating : 4.8
     * rating_label : 1K+ Reviews
     * description : Shubham Mathur requested to get paid $67.00 for the oil change service
     */

    private int id;
    private int job_id;
    private String technician_name;
    private String technician_avatar;
    private float star;
    private double rating;
    private String rating_label;
    private String description;

    public int getId() {
        return id;
    }

    public int getJob_id() {
        return job_id;
    }

    public String getTechnician_name() {
        return technician_name;
    }

    public String getTechnician_avatar() {
        return technician_avatar;
    }

    public float getStar() {
        return star;
    }

    public double getRating() {
        return rating;
    }

    public String getRating_label() {
        return rating_label;
    }

    public String getDescription() {
        return description;
    }
}
