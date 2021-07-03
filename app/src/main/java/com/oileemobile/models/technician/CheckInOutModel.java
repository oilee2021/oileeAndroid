package com.oileemobile.models.technician;

import java.io.Serializable;
import java.util.List;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-12-10 01:06
 **/
public class CheckInOutModel implements Serializable {

    /**
     * total_jobs : 16
     * total_quarts : 20
     * job_list : [{"id":75,"booking_id":86,"order_date":"Dec 9, 2019","customer_name":"Test Account","package_category":"5w-30","oil_type":"Blended","oil_quarts":"20"}]
     */

    private int total_jobs;
    private int total_quarts;
    private List<CheckInOutBean> job_list;

    public int getTotal_jobs() {
        return total_jobs;
    }

    public int getTotal_quarts() {
        return total_quarts;
    }

    public List<CheckInOutBean> getJob_list() {
        return job_list;
    }

    public static class CheckInOutBean implements Serializable {
        /**
         * id : 75
         * booking_id : 86
         * order_id : 9
         * order_date : Dec 9, 2019
         * customer_name : Test Account
         * package_category : 5w-30
         * oil_type : Blended
         * oil_quarts : 20
         */

        private int id;
        private int booking_id;
        private String order_id;
        private String order_date;
        private String customer_name;
        private String package_category;
        private String oil_type;
        private String oil_quarts;
        private boolean checked_in;
        private boolean checked_out;

        public int getId() {
            return id;
        }

        public int getBooking_id() {
            return booking_id;
        }

        public String getOrder_id() {
            return order_id;
        }

        public String getOrder_date() {
            return order_date;
        }

        public String getCustomer_name() {
            return customer_name;
        }

        public String getPackage_category() {
            return package_category;
        }

        public String getOil_type() {
            return oil_type;
        }

        public String getOil_quarts() {
            return oil_quarts;
        }

        public boolean isChecked_in() {
            return checked_in;
        }

        public boolean isChecked_out() {
            return checked_out;
        }

        public void setChecked_in(boolean checked_in) {
            this.checked_in = checked_in;
        }

        public void setChecked_out(boolean checked_out) {
            this.checked_out = checked_out;
        }
    }
}
