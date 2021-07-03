package com.oileemobile.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CustomerProfileResponse implements Serializable {
    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private CustomerData data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public CustomerData getData() {
        return data;
    }

    public class CustomerData implements Serializable {
        @SerializedName("id")
        @Expose
        private int id;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("email")
        @Expose
        private String email;

        @SerializedName("phone_number")
        @Expose
        private String phone_number;

        @SerializedName("dob")
        @Expose
        private String dob;

        @SerializedName("avatar")
        @Expose
        private String avatar;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public String getDob() {
            return dob;
        }

        public String getAvatar() {
            return avatar;
        }
    }
}
