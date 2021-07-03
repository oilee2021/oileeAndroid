package com.oileemobile.utils;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-12-07 23:40
 **/
public class Constants {

    public static final String COUNTRY_CODE = "+91";
    public static final String ACTIVITY_TYPE = "activity_type";
    public static final String IS_FIRST_ITEM = "is_first_item";
    public static final String JOB_DETAIL = "job_detail";
    public static final String INVOICE_MODEL = "invoice_model";
    public static final String JOB_ID = "job_id";
    public static final String ORDER_ID = "order_id";
    public static final String IS_TERMS_AGREED = "is_terms_agreed";

    public static final int MAX_INTERVAL = 5000;
    public static final int MIN_MOBILE_NUMBER = 7;
    public static final int MAX_MOBILE_NUMBER = 12;

    public static final int TYPE_CUSTOMER = 0;
    public static final int TYPE_TECHNICIAN = 1;

    public static final int REQUEST_CODE_RAISE_INVOICE = 111;
    public static final int REQUEST_CODE_CHECK_OUT = 112;
    public static final int REQUEST_CODE_CAMERA_PICTURE = 113;

    public interface NotificationType {
        String TECHNICIAN_ACCEPTED_JOB_NOTIFICATION = "technician_accepted_job_notification";
        String TECHNICIAN_REACHED_AT_WAREHOUSE_NOTIFICATION = "technician_reached_at_warehouse_notification";
        String TECHNICIAN_IS_LEAVING_WAREHOUSE_NOTIFICATION = "technician_is_leaving_warehouse_notification";
        String TECHNICIAN_IS_IN_ROUTE_NOTIFICATION = "technician_is_in_route_notification";
        String TECHNICIAN_ARRIVED_AT_LOCATION_NOTIFICATION = "technician_arrived_at_location_notification";
        String TECHNICIAN_STARTING_WORK_NOTIFICATION = "technician_starting_work_notification";
        String TECHNICIAN_WORK_COMPLETED_NOTIFICATION = "technician_work_completed_notification";
        String TECHNICIAN_PAYMENT_SUBMITTED_NOTIFICATION = "technician_payment_submitted_notification";
        String TECHNICIAN_BOOKING_CLOSED_NOTIFICATION = "technician_booking_closed_notification";
    }

    public interface JobStatus {
        String REQUEST_ACCEPTED = "Request Accepted";
        String REQUEST_NOT_ACCEPTED = "Request Not Accepted";
        String REQUEST_DECLINED = "Request Declined";
        String REQUEST_CANCELLED = "Request Cancelled";
    }
}
