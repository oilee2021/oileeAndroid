package com.oileemobile.models;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-09-12 22:50
 **/
public class VehicleModel implements Serializable {
    private int id;
    private String vehicle_image, year, make, model, sub_model, color, current_mileage, licence_plate;
    private boolean is_default;

    public VehicleModel(int id, String vehicle_image, String year, String make, String model, String sub_model,
                        String color, String current_mileage, String licence_plate, boolean is_default) {
        this.id = id;
        this.vehicle_image = vehicle_image;
        this.year = year;
        this.make = make;
        this.model = model;
        this.sub_model = sub_model;
        this.color = color;
        this.current_mileage = current_mileage;
        this.licence_plate = licence_plate;
        this.is_default = is_default;
    }

    public VehicleModel(int id, String year, String make, String model, String color, String licence_plate, boolean is_default) {
        this.id = id;
        this.year = year;
        this.make = make;
        this.model = model;
        this.color = color;
        this.licence_plate = licence_plate;
        this.is_default = is_default;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVehicle_image() {
        return vehicle_image;
    }

    public void setVehicle_image(String vehicle_image) {
        this.vehicle_image = vehicle_image;
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

    public String getSubModel() {
        return sub_model;
    }

    public String getColor() {
        return color;
    }

    public String getCurrentMileage() {
        return current_mileage;
    }

    public String getLicencePlate() {
        return licence_plate;
    }

    public boolean isIsDefault() {
        return is_default;
    }

    public String getVehicleDetails() {
        return String.format("%s %s %s (%s, %s)", getMake(), getModel(), getSubModel(), getColor(), getYear());
    }
}
