package com.oileemobile.network.response.technician;

import java.io.Serializable;

import com.oileemobile.network.response.ApiResponse;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-11-17 20:17
 **/
public class OnlineStatusResponse extends ApiResponse implements Serializable {
    private OnlineStatus data;

    public OnlineStatus getData() {
        return data;
    }

    public class OnlineStatus implements Serializable {
        private boolean online;

        public boolean isOnline() {
            return online;
        }
    }
}
