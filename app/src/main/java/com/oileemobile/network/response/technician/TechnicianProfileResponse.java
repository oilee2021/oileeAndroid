package com.oileemobile.network.response.technician;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import com.oileemobile.network.response.ApiResponse;

public class TechnicianProfileResponse extends ApiResponse implements Serializable {
    @SerializedName("data")
    @Expose
    private CustomerData data;

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

        @SerializedName("ssn")
        @Expose
        private String ssn;

        @SerializedName("avatar")
        @Expose
        private String avatar;

        @SerializedName("driver_license")
        @Expose
        private String driver_license;

        @SerializedName("insurance_information")
        @Expose
        private String insurance_information;

        @SerializedName("vehicle_registration")
        @Expose
        private String vehicle_registration;

        @SerializedName("online")
        @Expose
        private boolean online;

        @SerializedName("is_verified")
        @Expose
        private boolean isVerified;

        @SerializedName("is_activated")
        @Expose
        private boolean isActivated;

        @SerializedName("requisition_id")
        @Expose
        private String requisition_id;

        @SerializedName("dob")
        @Expose
        private String dob;

        @SerializedName("city")
        @Expose
        private String city;

        @SerializedName("address")
        @Expose
        private String address;

        @SerializedName("region")
        @Expose
        private String region;

        @SerializedName("country")
        @Expose
        private String country;

        @SerializedName("postal_code")
        @Expose
        private String postal_code;

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

        public String getSsn() {
            return ssn;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getDriver_license() {
            return driver_license;
        }

        public String getInsurance_information() {
            return insurance_information;
        }

        public String getVehicle_registration() {
            return vehicle_registration;
        }

        public boolean isOnline() {
            return online;
        }

        public boolean isVerified() {
            return isVerified;
        }

        public boolean isActivated() {
            return isActivated;
        }

        public String getRequisition_id() {
            return requisition_id;
        }

        public String getDob() {
            return dob;
        }

        public String getCity() {
            return city;
        }

        public String getAddress() {
            return address;
        }

        public String getRegion() {
            return region;
        }

        public String getCountry() {
            return country;
        }

        public String getPostal_code() {
            return postal_code;
        }
    }
}
