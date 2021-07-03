package com.oileemobile.models.technician;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2020-01-15 00:08
 **/
public class TechnicianOrderHistoryModel implements Serializable {

    /**
     * id : 309
     * customer : {"name":"Manish Chandra","avatar":"http://oilapp.ilogix.co.in/storage/avatars/uhHWK7aIe6186PYKBBUYSkHBzGKSIx76hY8S9tKa.jpeg","address":"flat 101 noida up"}
     * appointment_time : 5:00 AM
     * appointment_date : Jan 12, 2020
     * vehicle : {"make":"maruti","model":"xt","year":"2014","image":"default.png"}
     */

    private int id;
    private CustomerBean customer;
    private String appointment_time;
    private String appointment_date;
    private VehicleBean vehicle;

    public int getId() {
        return id;
    }

    public CustomerBean getCustomer() {
        return customer;
    }

    public String getAppointment_time() {
        return appointment_time;
    }

    public String getAppointment_date() {
        return appointment_date;
    }

    public VehicleBean getVehicle() {
        return vehicle;
    }

    public static class CustomerBean {
        /**
         * name : Manish Chandra
         * avatar : http://oilapp.ilogix.co.in/storage/avatars/uhHWK7aIe6186PYKBBUYSkHBzGKSIx76hY8S9tKa.jpeg
         * address : flat 101 noida up
         */

        private String name;
        private String avatar;
        private String address;

        public String getName() {
            return name;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getAddress() {
            return address;
        }
    }

    public static class VehicleBean {
        /**
         * make : maruti
         * model : xt
         * year : 2014
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
}
