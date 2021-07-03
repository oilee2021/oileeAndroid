package com.oileemobile.models.technician;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-16 02:05
 **/
public class NotificationModel implements Serializable {
    private String id;
    private String title, date, url;
    private boolean is_read;
    private int job_id;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    public int getJob_id() {
        return job_id;
    }
}
