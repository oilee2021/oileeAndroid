package com.oileemobile.network.response;

import java.io.Serializable;

import com.oileemobile.network.response.technician.TechnicianJobStatusResponse;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-12-13 02:05
 **/
public class CustomerOrderTrackResponse implements Serializable {

    /**
     * data : {"technician_name":"Shubham Mathur","star":4,"rating":4.8,"rating_label":"1K+ Reviews","rated_for_technician":false,"status":{"request_accepted":true,"request_accepted_date":"12 December 2019, 8:32:23 PM","reached_at_warehouse":false,"reached_at_warehouse_date":"12 December 2019, 8:32:59 PM","leaving_warehouse":false,"leaving_warehouse_date":"12 December 2019, 8:32:59 PM","in_route":false,"in_route_date":"12 December 2019, 8:32:59 PM","arrived_at_location":false,"arrived_at_location_date":"12 December 2019, 8:32:59 PM","starting_work":false,"starting_work_date":"12 December 2019, 8:32:59 PM","work_completed":false,"work_completed_date":"12 December 2019, 8:32:59 PM","payment_submitted":false,"payment_submitted_date":"12 December 2019, 8:32:59 PM","booking_closed":false,"booking_closed_date":"12 December 2019, 8:32:59 PM"}}
     * message : null
     * status : 200
     */

    private DataBean data;
    private String message;
    private int status;

    public DataBean getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public static class DataBean implements Serializable {
        /**
         * technician_name : Shubham Mathur
         * star : 4
         * rating : 4.8
         * rating_label : 1K+ Reviews
         * rated_for_technician : false
         * status : {"request_accepted":true,"request_accepted_date":"12 December 2019, 8:32:23 PM","reached_at_warehouse":false,"reached_at_warehouse_date":"12 December 2019, 8:32:59 PM","leaving_warehouse":false,"leaving_warehouse_date":"12 December 2019, 8:32:59 PM","in_route":false,"in_route_date":"12 December 2019, 8:32:59 PM","arrived_at_location":false,"arrived_at_location_date":"12 December 2019, 8:32:59 PM","starting_work":false,"starting_work_date":"12 December 2019, 8:32:59 PM","work_completed":false,"work_completed_date":"12 December 2019, 8:32:59 PM","payment_submitted":false,"payment_submitted_date":"12 December 2019, 8:32:59 PM","booking_closed":false,"booking_closed_date":"12 December 2019, 8:32:59 PM"}
         */

        private String technician_name;
        private float star;
        private double rating;
        private String rating_label;
        private String job_id;
        private String order_id;
        private boolean rated_for_technician;
        private boolean tip_gave_to_technician;
        private TechnicianJobStatusResponse.TechnicianJobStatusModel status;

        public String getTechnician_name() {
            return technician_name;
        }

        public float getStar() {
            return star;
        }

        public double getRating() {
            return rating;
        }

        public String getRating_label() {
            return rating_label;
        }

        public boolean isRated_for_technician() {
            return rated_for_technician;
        }

        public String getJob_id() {
            return job_id;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setRated_for_technician(boolean rated_for_technician) {
            this.rated_for_technician = rated_for_technician;
        }

        public boolean isTip_gave_to_technician() {
            return tip_gave_to_technician;
        }

        public void setTip_gave_to_technician(boolean tip_gave_to_technician) {
            this.tip_gave_to_technician = tip_gave_to_technician;
        }

        public TechnicianJobStatusResponse.TechnicianJobStatusModel getStatus() {
            return status;
        }
    }
}
