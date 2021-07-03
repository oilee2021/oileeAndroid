package com.oileemobile.models;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2020-01-09 21:16
 **/
public class CustomerNotificationModel implements Serializable {

    /**
     * status : 200
     * key : technician_reached_at_warehouse_notification
     * message : Data Received.
     * title : Technician reached at warehouse.
     * description : Technician Name Nitin Kumar
     * timestamp : 2020-01-04 03:52:53
     * data : {"job_id":179}
     * is_read : false
     */

    private int status;
    private String key;
    private String message;
    private String title;
    private String description;
    private String timestamp;
    private DataBean data;
    private boolean is_read;
    private String id;

    public int getStatus() {
        return status;
    }

    public String getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public DataBean getData() {
        return data;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    public String getId() {
        return id;
    }

    public static class DataBean {
        /**
         * job_id : 179
         */

        private int order_id;

        public int getOrder_id() {
            return order_id;
        }
    }
}
