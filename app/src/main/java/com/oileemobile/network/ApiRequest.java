package com.oileemobile.network;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-08 23:32
 **/
public class ApiRequest implements Serializable {
    //Check mobile
    private String mobile_number;

    //Login
    private String phone_number;
    private String password;
    private String fcm_token;

    //Register
    private String name;
    private String email;
    private String dob;
    private String avatar;

    //Create order
    private String customer_address_id;
    private String customer_vehicle_id;
    private String package_id;
    private String date;
    private String time;
    private String user_card_id;
    private String note;
    private String instruction;
    private String special_request;
    private String coupon_code;

    //Reset password
    private String password_confirmation;
    private String token;

    //Schedule reminder
    private String customer_id;
    private String vehicle_id;
    private String reminder_date;

    //Add Customer Card
    private String token_id;

    //Update
    private String _method;

    //Raise invoice
    private String technician_job_id;
    private String coupon_id;
    private String discount;

    //Update technician location
    private String lat;
    private String lng;

    //Rating customer
    private String job_id;
    private String ratings;
    private String review_text;

    //Pay invoice
    private String card_id;

    //Tip Technician
    private String tip_amount;

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setCustomer_address_id(String customer_address_id) {
        this.customer_address_id = customer_address_id;
    }

    public void setCustomer_vehicle_id(String customer_vehicle_id) {
        this.customer_vehicle_id = customer_vehicle_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUser_card_id(String user_card_id) {
        this.user_card_id = user_card_id;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public void setSpecial_request(String special_request) {
        this.special_request = special_request;
    }

    public void setPassword_confirmation(String password_confirmation) {
        this.password_confirmation = password_confirmation;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public void setReminder_date(String reminder_date) {
        this.reminder_date = reminder_date;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public void set_method(String _method) {
        this._method = _method;
    }

    public void setTechnician_job_id(String technician_job_id) {
        this.technician_job_id = technician_job_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public void setReview_text(String review_text) {
        this.review_text = review_text;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public void setTip_amount(String tip_amount) {
        this.tip_amount = tip_amount;
    }
}
