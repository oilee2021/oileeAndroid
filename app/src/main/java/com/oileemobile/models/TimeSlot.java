package com.oileemobile.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-18 19:01
 **/
public class TimeSlot implements Serializable {
    private Date date;

    public TimeSlot(Date date) {
        this.date = date;
    }

    public String getTime12Hr() {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date);
    }

    public String getTime24Hr() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
    }

    public Date getDate() {
        return date;
    }
}
