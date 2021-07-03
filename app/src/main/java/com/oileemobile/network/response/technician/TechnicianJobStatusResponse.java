package com.oileemobile.network.response.technician;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import com.oileemobile.network.response.ApiResponse;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-12-07 12:27
 **/
public class TechnicianJobStatusResponse extends ApiResponse implements Serializable {
    @SerializedName("data")
    @Expose
    private TechnicianJobStatusModel data;

    public TechnicianJobStatusModel getData() {
        return data;
    }
    
    public class TechnicianJobStatusModel implements Serializable {

        /**
         * request_accepted : true
         * request_accepted_date : 19 October 2019, 4:24:00 PM
         * reached_at_warehouse : false
         * reached_at_warehouse_date : null
         * leaving_warehouse : false
         * leaving_warehouse_date : null
         * in_route : false
         * in_route_date : null
         * arrived_at_location : false
         * arrived_at_location_date : null
         * starting_work : false
         * starting_work_date : null
         * work_completed : false
         * work_completed_date : null
         * payment_submitted : false
         * payment_submitted_date : null
         * booking_closed : false
         * booking_closed_date : null
         * is_rated : false
         * checked_in : false
         * checked_out : false
         */

        private boolean request_accepted;
        private String request_accepted_date;
        private boolean reached_at_warehouse;
        private String reached_at_warehouse_date;
        private boolean leaving_warehouse;
        private String leaving_warehouse_date;
        private boolean in_route;
        private String in_route_date;
        private boolean arrived_at_location;
        private String arrived_at_location_date;
        private boolean starting_work;
        private String starting_work_date;
        private boolean work_completed;
        private String work_completed_date;
        private boolean payment_submitted;
        private String payment_submitted_date;
        private boolean booking_closed;
        private String booking_closed_date;
        private boolean is_rated;
        private boolean checked_in;
        private boolean checked_out;
        private List<PaymentModel> payment;

        public boolean isRequest_accepted() {
            return request_accepted;
        }

        public String getRequest_accepted_date() {
            return request_accepted_date;
        }

        public boolean isReached_at_warehouse() {
            return reached_at_warehouse;
        }

        public String getReached_at_warehouse_date() {
            return reached_at_warehouse_date;
        }

        public boolean isLeaving_warehouse() {
            return leaving_warehouse;
        }

        public String getLeaving_warehouse_date() {
            return leaving_warehouse_date;
        }

        public boolean isIn_route() {
            return in_route;
        }

        public String getIn_route_date() {
            return in_route_date;
        }

        public boolean isArrived_at_location() {
            return arrived_at_location;
        }

        public String getArrived_at_location_date() {
            return arrived_at_location_date;
        }

        public boolean isStarting_work() {
            return starting_work;
        }

        public String getStarting_work_date() {
            return starting_work_date;
        }

        public boolean isWork_completed() {
            return work_completed;
        }

        public String getWork_completed_date() {
            return work_completed_date;
        }

        public boolean isPayment_submitted() {
            return payment_submitted;
        }

        public String getPayment_submitted_date() {
            return payment_submitted_date;
        }

        public boolean isBooking_closed() {
            return booking_closed;
        }

        public String getBooking_closed_date() {
            return booking_closed_date;
        }

        public boolean isIs_rated() {
            return is_rated;
        }

        public void setIs_rated(boolean is_rated) {
            this.is_rated = is_rated;
        }

        public boolean isChecked_in() {
            return checked_in;
        }

        public boolean isChecked_out() {
            return checked_out;
        }

        public List<PaymentModel> getPayment() {
            return payment;
        }

        public class PaymentModel implements Serializable {

            /**
             * paid_amount : 420.00
             * paid_date : 1/20/2020
             * paid_time : 07:25 PM
             */

            private String paid_amount;
            private String paid_date;
            private String paid_time;

            public String getPaid_amount() {
                return paid_amount;
            }

            public String getPaid_date() {
                return paid_date;
            }

            public String getPaid_time() {
                return paid_time;
            }
        }
    }
}
