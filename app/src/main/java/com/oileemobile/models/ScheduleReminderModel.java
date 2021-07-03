package com.oileemobile.models;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-28 22:34
 **/
public class ScheduleReminderModel implements Serializable {
    private int id;
    private String customer_id, reminder_date, year, make, model, sub_model, color, current_mileage, licence_plate, image;

    public int getId() {
        return id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public String getReminder_date() {
        return reminder_date;
    }

    public String getYear() {
        return year;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getSub_model() {
        return sub_model;
    }

    public String getColor() {
        return color;
    }

    public String getCurrent_mileage() {
        return current_mileage;
    }

    public String getLicence_plate() {
        return licence_plate;
    }

    public String getImage() {
        return image;
    }

    public VehicleModel getCustomer_vehicle() {
        return new VehicleModel(0, image, year, make, model, sub_model, color, current_mileage, licence_plate, false);
    }
}
