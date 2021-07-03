package com.oileemobile.models;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-09-13 00:01
 **/
public class AddressModel implements Serializable {

    private int id;
    private String address_one, address_two, city, state, zip, tag;
    private boolean is_default;
    private double lat, lng;

    public AddressModel(int id, String address_one, String address_two, String city, String state, String zip, String tag, boolean is_default, double lat, double lng) {
        this.id = id;
        this.address_one = address_one;
        this.address_two = address_two;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.tag = tag;
        this.is_default = is_default;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public String getAddressOne() {
        return address_one;
    }

    public String getAddressTwo() {
        return address_two;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getTag() {
        return tag;
    }

    public boolean isIsDefault() {
        return is_default;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getFullAddress() {
        StringBuilder builder = new StringBuilder();
        builder.append(getAddressOne()).append(", ");
        if(!TextUtils.isEmpty(getAddressTwo())) {
            builder.append(getAddressTwo()).append(", ");
        }
        builder.append(getCity()).append(", ");
        builder.append(getState()).append("  ");
        builder.append(getZip());
        return builder.toString();
    }
}
