package com.oileemobile.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CreateOrderResponse implements Serializable {
    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private OrderResponseData data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public OrderResponseData getData() {
        return data;
    }

    public class OrderResponseData implements Serializable {
        int id;

        public int getId() {
            return id;
        }
    }
}
