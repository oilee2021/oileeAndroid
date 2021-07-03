package com.oileemobile.models;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import com.oileemobile.network.response.ApiResponse;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-19 00:57
 **/
public class OrderHistoryModel extends ApiResponse implements Serializable {


    /**
     * id : 188
     * date : Jan 30, 2020
     * time : 1:00 PM
     * card_type : mastercard
     * price : 65.00
     * transaction_id : txn_1Fz3ySKoFaEy5ulInb62l2Ce
     * technician : {"id":0,"name":"Technician Not Assigned","image":"http://oilapp.ilogix.co.in/avatar/default.png","star":0,"rating":0,"rating_label":"0 Reviews","vehicle":{"make":"Suzuki","model":"Swift","licence_plate":"GAHJS755"}}
     * customer : {"vehicle":{"make":"Suzuki","model":"Swift","licence_plate":"GAHJS755"}}
     * service : {"date":"Jan 30, 2020","time":"1:00 PM","address":"Flat No 1303, Tower C2 Noida Extension Uttar Pradesh"}
     * package : {"category_name":"5w-30","oil_type":"Synthetic","price":"65.00"}
     */

    private int id;
    private String date;
    private String time;
    private String card_type;
    private String price;
    private String paid_amount;
    private String transaction_id;
    private double tip_amount;
    private TechnicianBean technician;
    private CustomerBean customer;
    private ServiceBean service;
    @SerializedName("package")
    private PackageBean packageX;
    private boolean show_tip;

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getCard_type() {
        return card_type;
    }

    public double getPrice() {
        return TextUtils.isEmpty(price) ? 0.0 : Double.parseDouble(price);
    }

    public double getPaid_amount() {
        return TextUtils.isEmpty(paid_amount) ? 0.0 : Double.parseDouble(paid_amount);
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public TechnicianBean getTechnician() {
        return technician;
    }

    public CustomerBean getCustomer() {
        return customer;
    }

    public ServiceBean getService() {
        return service;
    }

    public PackageBean getPackageX() {
        return packageX;
    }

    public double getTip_amount() {
        return tip_amount;
    }

    public void setTip_amount(double tip_amount) {
        this.tip_amount = tip_amount;
    }

    public boolean isShow_tip() {
        return show_tip;
    }

    public static class TechnicianBean implements Serializable {
        /**
         * id : 0
         * name : Technician Not Assigned
         * image : http://oilapp.ilogix.co.in/avatar/default.png
         * star : 0
         * rating : 0
         * rating_label : 0 Reviews
         * vehicle : {"make":"Suzuki","model":"Swift","licence_plate":"GAHJS755"}
         */

        private int id;
        private String name;
        private String image;
        private float star;
        private float rating;
        private String rating_label;
        private VehicleBean vehicle;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getImage() {
            return image;
        }

        public float getStar() {
            return star;
        }

        public float getRating() {
            return rating;
        }

        public String getRating_label() {
            return rating_label;
        }

        public VehicleBean getVehicle() {
            return vehicle;
        }
    }

    public static class CustomerBean implements Serializable {
        /**
         * vehicle : {"make":"Suzuki","model":"Swift","licence_plate":"GAHJS755"}
         */

        private VehicleModel vehicle;

        public VehicleModel getVehicle() {
            return vehicle;
        }
    }

    public static class VehicleBean implements Serializable {
        /**
         * make : Suzuki
         * model : Swift
         * licence_plate : GAHJS755
         */

        private String make;
        private String model;
        private String licence_plate;

        public String getMake() {
            return make;
        }

        public String getModel() {
            return model;
        }

        public String getLicence_plate() {
            return licence_plate;
        }

        @NonNull
        @Override
        public String toString() {
            return getMake() + " " + getModel() + " | " + getLicence_plate();
        }
    }

    public static class ServiceBean implements Serializable {
        /**
         * date : Jan 30, 2020
         * time : 1:00 PM
         * address : Flat No 1303, Tower C2 Noida Extension Uttar Pradesh
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

    public static class PackageBean implements Serializable {
        /**
         * category_name : 5w-30
         * oil_type : Synthetic
         * price : 65.00
         */

        private String category_name;
        private String oil_type;
        private String price;

        public String getCategory_name() {
            return category_name;
        }

        public String getOil_type() {
            return oil_type;
        }

        public String getPrice() {
            return price;
        }
    }
}
