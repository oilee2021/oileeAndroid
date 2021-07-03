package com.oileemobile.models.technician;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-11-16 19:02
 **/
public class TechnicianJobModel implements Serializable {

    /**
     * id : 11
     * order_id : 112
     * customer_name : Shyam
     * customer_avatar : http://localhost:8000/default.png
     * appointment_time : 4:02 PM
     * vehicle_year : 2020
     * vehicle_make : make
     * customer_address : one city state
     * star : 4
     * customer_lat : "28.49388626"
     * customer_lng : "77.09526245"
     * rating : 4.8
     * rating_label : 1K+ Reviews
     * created_date : 1 week ago
     * order_date : Oct 4, 2019
     * service : {"date":"Oct 4, 2019","time":"4:02 PM","address":"one city state"}
     * vehicle : {"make":"make","model":"model","year":"2020","image":"default.png"}
     * oil_needed : 5w-30
     * parking_instructions : null
     * special_notes : note
     */

    private int id;
    private String order_id;
    private String customer_name;
    private String customer_avatar;
    private String appointment_time;
    private String vehicle_year;
    private String vehicle_make;
    private String customer_address;
    private float star;
    private String customer_lat;
    private String customer_lng;
    private float rating;
    private String rating_label;
    private String created_date;
    private String order_date;
    private ServiceBean service;
    private VehicleBean vehicle;
    private OilBean oil;
    private String parking_instructions;
    private String special_notes;
    private String job_status;

    public int getId() {
        return id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public String getCustomer_avatar() {
        return customer_avatar;
    }

    public String getAppointment_time() {
        return appointment_time;
    }

    public String getVehicle_year() {
        return vehicle_year;
    }

    public String getVehicle_make() {
        return vehicle_make;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public float getStar() {
        return star;
    }

    public String getCustomer_lat() {
        return customer_lat;
    }

    public String getCustomer_lng() {
        return customer_lng;
    }

    public float getRating() {
        return rating;
    }

    public String getRating_label() {
        return rating_label;
    }

    public String getCreated_date() {
        return created_date;
    }

    public String getOrder_date() {
        return order_date;
    }

    public ServiceBean getService() {
        return service;
    }

    public VehicleBean getVehicle() {
        return vehicle;
    }

    public OilBean getOil() {
        return oil;
    }

    public String getParking_instructions() {
        return parking_instructions;
    }

    public String getSpecial_notes() {
        return special_notes;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getJob_status() {
        return job_status;
    }

    public void setJob_status(String job_status) {
        this.job_status = job_status;
    }

    public static class ServiceBean implements Serializable {
        /**
         * date : Oct 4, 2019
         * time : 4:02 PM
         * address : one city state
         */

        private String date;
        private String time;
        private String address;

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        public String getAddress() {
            return address;
        }
    }

    public static class VehicleBean implements Serializable {
        /**
         * make : make
         * model : model
         * year : 2020
         * image : default.png
         */

        private String make;
        private String model;
        private String year;
        private String image;

        public String getMake() {
            return make;
        }

        public String getModel() {
            return model;
        }

        public String getYear() {
            return year;
        }

        public String getImage() {
            return image;
        }
    }

    public static class OilBean implements Serializable {

        /**
         * quarts : null
         * package_name : null
         * type : Synthetic
         */

        private String quarts;
        private String package_name;
        private String type;

        public String getQuarts() {
            return quarts;
        }

        public String getPackage_name() {
            return package_name;
        }

        public String getType() {
            return type;
        }
    }
}
