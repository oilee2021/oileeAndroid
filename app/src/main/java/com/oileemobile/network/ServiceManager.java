package com.oileemobile.network;

import android.app.Activity;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.oileemobile.R;
import com.oileemobile.utils.ProgressDialog;
import com.oileemobile.utils.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class ServiceManager {

    //Customer Api
    public static final int API_CUSTOMER_CHECK_MOBILE = 1;
    public static final int API_CUSTOMER_LOGIN = 2;
    public static final int API_CUSTOMER_REGISTER = 3;
    public static final int API_GET_CUSTOMER_PROFILE = 4;
    public static final int API_UPDATE_CUSTOMER_PROFILE = 5;
    public static final int API_ADD_USER_VEHICLE = 6;
    public static final int API_GET_ALL_USER_VEHICLE = 7;
    public static final int API_ADD_USER_ADDRESS = 8;
    public static final int API_GET_ALL_USER_ADDRESS = 9;
    public static final int API_GET_FAQ = 10;
    public static final int API_GET_ABOUT_US = 11;
    public static final int API_UPDATE_USER_VEHICLE = 12;
    public static final int API_DELETE_USER_VEHICLE = 13;
    public static final int API_UPDATE_USER_ADDRESS = 14;
    public static final int API_DELETE_USER_ADDRESS = 15;
    public static final int API_GET_PACKAGES = 16;
    public static final int API_GET_CARD_LIST = 17;
    public static final int API_CREATE_ORDER = 18;
    public static final int API_GET_PENDING_ORDERS = 19;
    public static final int API_FORGOT_PASSWORD = 20;
    public static final int API_RESET_PASSWORD = 21;
    public static final int API_GET_TERMS = 22;
    public static final int API_GET_SCHEDULE_REMINDER = 23;
    public static final int API_ADD_SCHEDULE_REMINDER = 24;
    public static final int API_GET_SUPPORT_QUERY = 25;
    public static final int API_ADD_CUSTOMER_CARD = 40;
    public static final int API_CREATE_SUPPORT_QUERY = 48;
    public static final int API_SUPPORT_ELIGIBLE = 50;
    public static final int API_CUSTOMER_TRACK_ORDER = 66;
    public static final int API_RATE_TECHNICIAN = 69;
    public static final int API_GET_LATEST_INVOICE = 70;
    public static final int API_PAY_INVOICE = 71;
    public static final int API_USER_NOTIFICATIONS = 72;
    public static final int API_DELETE_REMINDER = 74;
    public static final int API_GET_COMPLETED_ORDERS = 75;
    public static final int API_GET_CANCELLED_ORDERS = 76;
    public static final int API_USER_NOTIFICATIONS_READ = 77;
    public static final int API_CANCEL_ORDER = 79;
    public static final int API_APPLY_COUPON = 84;
    public static final int API_TIP_TECHNICIAN = 57;
    public static final int API_ORDER_DETAILS = 83;
    public static final int API_CUSTOMER_NOTIFICATION_CLEAR = 85;
    public static final int API_CUSTOMER_NOTIFICATION_READ = 86;
    public static final int API_CUSTOMER_SKIP_TIP = 89;
    public static final int API_CUSTOMER_DELETE_CARD = 190;

    //Technician Api
    public static final int API_TECHNICIAN_CHECK_MOBILE = 26;
    public static final int API_TECHNICIAN_LOGIN = 27;
    public static final int API_TECHNICIAN_GET_PROFILE = 28;
    public static final int API_ADD_TECHNICIAN_VEHICLE = 29;
    public static final int API_TECHNICIAN_FORGOT_PASSWORD = 30;
    public static final int API_TECHNICIAN_RESET_PASSWORD = 31;
    public static final int API_TECHNICIAN_JOBS = 32;
    public static final int API_TECHNICIAN_ONLINE = 33;
    public static final int API_TECHNICIAN_OFFLINE = 34;
    public static final int API_CHECK_ONLINE_STATUS = 35;
    public static final int API_UPDATE_TECHNICIAN_PROFILE = 36;
    public static final int API_TECHNICIAN_REGISTER = 37;
    public static final int API_GET_NOTIFICATIONS = 38;
    public static final int API_MARK_NOTIFICATIONS = 39;
    public static final int API_GET_ALL_TECHNICIAN_VEHICLE = 41;
    public static final int API_TECHNICIAN_JOB_STATUS = 42;
    public static final int API_TECHNICIAN_JOB_ACCEPT = 43;
    public static final int API_TECHNICIAN_JOB_DECLINE = 44;
    public static final int API_UPDATE_TECHNICIAN_VEHICLE = 45;
    public static final int API_DELETE_TECHNICIAN_VEHICLE = 46;
    public static final int API_GET_TECHNICIAN_SUPPORT_QUERY = 47;
    public static final int API_CREATE_TECHNICIAN_SUPPORT_QUERY = 49;
    public static final int API_TECHNICIAN_SUPPORT_ELIGIBLE = 51;
    public static final int API_TECHNICIAN_ACCEPTED_JOBS = 52;
    public static final int API_JOB_REACHED_WAREHOUSE = 53;
    public static final int API_JOB_LEAVING_WAREHOUSE = 54;
    public static final int API_JOB_IN_ROUTE = 55;
    public static final int API_JOB_ARRIVED_LOCATION = 56;
    public static final int API_JOB_BOOKING_CLOSED = 59;
    public static final int API_CREATE_INVOICE = 60;
    public static final int API_CHECK_IN_DATA = 61;
    public static final int API_CHECK_OUT_DATA = 62;
    public static final int API_CHECK_IN = 63;
    public static final int API_CHECK_OUT = 64;
    public static final int API_UPDATE_TECHNICIAN_LOCATION = 65;
    public static final int API_RATE_CUSTOMER = 67;
    public static final int API_GET_COUPONS = 68;
    public static final int API_INVOICE_DETAILS = 73;
    public static final int API_TECHNICIAN_VERIFICATION_API = 78;
    public static final int API_TECHNICIAN_PENDING_ORDERS = 80;
    public static final int API_TECHNICIAN_COMPLETED_ORDERS = 81;
    public static final int API_TECHNICIAN_CANCELLED_ORDERS = 83;
    public static final int API_TECHNICIAN_JOB_DETAIL = 82;
    public static final int API_TECHNICIAN_NOTIFICATION_CLEAR = 87;
    public static final int API_TECHNICIAN_NOTIFICATION_READ = 88;
    public static final int API_TECHNICIAN_BACKGROUND_CHECK = 90;

    private static <T> void callApi(Activity activity, Call<T> call, ApiCallBack apiCallBack, boolean showProgress) {
        Utils.hideKeyboard(activity);
        apiCallBack.setActivity(activity);
        apiCallBack.setShowProgress(showProgress);
        call.enqueue(apiCallBack);
        if(showProgress) {
            ProgressDialog.showProgressDialog(activity);
        }
    }

    public static void callServerApi(Activity activity, int apiType, ApiCallBack apiCallBack,
                                     Object apiRequest) {
        callServerApi(activity, apiType, apiCallBack, apiRequest, true);
    }

    public static Call callServerApi(Activity activity, int apiType, ApiCallBack apiCallBack,
                                     boolean showProgress) {
        return callServerApi(activity, apiType, apiCallBack, null, showProgress);
    }

    public static Call callServerApi(Activity activity, int apiType, ApiCallBack apiCallBack,
                                     Object apiRequest, boolean showProgress) {
        Call call = null;
        if (Utils.isNetworkConnected(activity)) {
            switch (apiType) {
                //Customer Api
                case API_CUSTOMER_CHECK_MOBILE:
                    call = ApiClient.current().getCheckMobileApi(apiRequest);
                    break;

                case API_CUSTOMER_LOGIN:
                    call = ApiClient.current().getLoginApi(apiRequest);
                    break;

                case API_GET_CUSTOMER_PROFILE:
                    call = ApiClient.current().getCustomerProfileApi();
                    break;

                case API_GET_ALL_USER_VEHICLE:
                    call = ApiClient.current().getAllUserVehiclesApi();
                    break;

                case API_ADD_USER_ADDRESS:
                    call = ApiClient.current().getAddUserAddressApi(apiRequest);
                    break;

                case API_GET_ALL_USER_ADDRESS:
                    call = ApiClient.current().getAllUserAddressApi();
                    break;

                case API_GET_FAQ:
                    call = ApiClient.current().getFaqApi();
                    break;

                case API_GET_ABOUT_US:
                    call = ApiClient.current().getAboutUsApi();
                    break;

                case API_GET_PACKAGES:
                    call = ApiClient.current().getPackagesApi();
                    break;

                case API_GET_CARD_LIST:
                    call = ApiClient.current().getCustomerCardsApi();
                    break;

                case API_CREATE_ORDER:
                    call = ApiClient.current().getCreateOrderApi(apiRequest);
                    break;

                case API_GET_PENDING_ORDERS:
                    call = ApiClient.current().getPendingOrdersApi();
                    break;

                case API_GET_COMPLETED_ORDERS:
                    call = ApiClient.current().getCompletedOrdersApi();
                    break;

                case API_GET_CANCELLED_ORDERS:
                    call = ApiClient.current().getCancelledOrdersApi();
                    break;

                case API_FORGOT_PASSWORD:
                    call = ApiClient.current().getForgotPasswordApi(apiRequest);
                    break;

                case API_RESET_PASSWORD:
                    call = ApiClient.current().getPasswordResetApi(apiRequest);
                    break;

                case API_GET_TERMS:
                    call = ApiClient.current().getTermsApi();
                    break;

                case API_GET_SCHEDULE_REMINDER:
                    call = ApiClient.current().getScheduleReminderListApi();
                    break;

                case API_ADD_SCHEDULE_REMINDER:
                    call = ApiClient.current().getAddScheduleApi(apiRequest);
                    break;

                case API_GET_SUPPORT_QUERY:
                    call = ApiClient.current().getSupportQueriesApi();
                    break;

                case API_ADD_CUSTOMER_CARD:
                    call = ApiClient.current().getAddCustomerCardApi(apiRequest);
                    break;


                case API_SUPPORT_ELIGIBLE:
                    call = ApiClient.current().getSupportEligibleApi();
                    break;

                case API_GET_LATEST_INVOICE:
                    call = ApiClient.current().getLatestInvoiceApi();
                    break;

                case API_USER_NOTIFICATIONS:
                    call = ApiClient.current().getCustomerNotificationsApi();
                    break;

                case API_USER_NOTIFICATIONS_READ:
                    call = ApiClient.current().getCustomerNotificationsReadApi();
                    break;

                case API_TIP_TECHNICIAN:
                    call = ApiClient.current().getTipTechnicianApi(apiRequest);
                    break;

                case API_CUSTOMER_NOTIFICATION_CLEAR:
                    call = ApiClient.current().getCustomerNotificationsClearApi();
                    break;


                //Technician Api
                case API_TECHNICIAN_CHECK_MOBILE:
                    call = ApiClient.current().getCheckTechnicianMobileApi(apiRequest);
                    break;

                case API_TECHNICIAN_GET_PROFILE:
                    call = ApiClient.current().getTechnicianProfileApi();
                    break;

                case API_TECHNICIAN_LOGIN:
                    call = ApiClient.current().getTechnicianLoginApi(apiRequest);
                    break;

                case API_TECHNICIAN_FORGOT_PASSWORD:
                    call = ApiClient.current().getTechnicianForgotPasswordApi(apiRequest);
                    break;

                case API_TECHNICIAN_RESET_PASSWORD:
                    call = ApiClient.current().getTechnicianPasswordResetApi(apiRequest);
                    break;

                case API_TECHNICIAN_JOBS:
                    call = ApiClient.current().getTechnicianJobsApi();
                    break;

                case API_TECHNICIAN_ONLINE:
                    call = ApiClient.current().getMarkTechnicianOnlineApi();
                    break;

                case API_TECHNICIAN_OFFLINE:
                    call = ApiClient.current().getMarkTechnicianOfflineApi();
                    break;

                case API_CHECK_ONLINE_STATUS:
                    call = ApiClient.current().getTechnicianOnlineStatusApi();
                    break;

                case API_GET_NOTIFICATIONS:
                    call = ApiClient.current().getTechnicianNotificationsApi();
                    break;

                case API_MARK_NOTIFICATIONS:
                    call = ApiClient.current().getTechnicianMarkNotificationsApi();
                    break;

                case API_GET_ALL_TECHNICIAN_VEHICLE:
                    call = ApiClient.current().getAllTechnicianVehiclesApi();
                    break;

                case API_GET_TECHNICIAN_SUPPORT_QUERY:
                    call = ApiClient.current().getTechnicianSupportQueriesApi();
                    break;

                case API_TECHNICIAN_SUPPORT_ELIGIBLE:
                    call = ApiClient.current().getTechnicianSupportEligibleApi();
                    break;

                case API_TECHNICIAN_ACCEPTED_JOBS:
                    call = ApiClient.current().getTechnicianAcceptedJobsApi();
                    break;

                case API_CHECK_IN_DATA:
                    call = ApiClient.current().getTechnicianCheckInDataApi();
                    break;

                case API_CHECK_OUT_DATA:
                    call = ApiClient.current().getTechnicianCheckOutDataApi();
                    break;

                case API_CHECK_IN:
                    call = ApiClient.current().getTechnicianCheckInApi(apiRequest);
                    break;

                case API_CHECK_OUT:
                    call = ApiClient.current().getTechnicianCheckOutApi(apiRequest);
                    break;

                case API_UPDATE_TECHNICIAN_LOCATION:
                    call = ApiClient.current().getUpdateTechnicianLocationApi(apiRequest);
                    break;

                case API_RATE_CUSTOMER:
                    call = ApiClient.current().getRateCustomerApi(apiRequest);
                    break;

                case API_RATE_TECHNICIAN:
                    call = ApiClient.current().getRateTechnicianApi(apiRequest);
                    break;

                case API_GET_COUPONS:
                    call = ApiClient.current().getCouponApi();
                    break;

                case API_APPLY_COUPON:
                    call = ApiClient.current().getApplyCouponApi(apiRequest);
                    break;

                case API_TECHNICIAN_VERIFICATION_API:
                    call = ApiClient.current().getTechnicianActivatedStatusApi();
                    break;

                case API_TECHNICIAN_PENDING_ORDERS:
                    call = ApiClient.current().getTechnicianPendingOrdersApi();
                    break;

                case API_TECHNICIAN_COMPLETED_ORDERS:
                    call = ApiClient.current().getTechnicianCompletedOrdersApi();
                    break;

                case API_TECHNICIAN_CANCELLED_ORDERS:
                    call = ApiClient.current().getTechnicianCancelledOrdersApi();
                    break;

                case API_TECHNICIAN_NOTIFICATION_CLEAR:
                    call = ApiClient.current().getTechnicianNotificationsClearApi();
                    break;
            }

            if(call != null) {
                callApi(activity, call, apiCallBack, showProgress);
            }
        } else {
            try {
                apiCallBack.onFailure(call, new Throwable(activity.getString(R.string.internet_not_connected)));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            /*if(showProgress) {
                Utils.showSnackbar(activity, R.string.internet_not_connected);
            }*/
        }

        return call;
    }

    public static void callServerApi(Activity activity, int apiType, ApiCallBack apiCallBack, int id, Object apiRequest) {
        callServerApi(activity, apiType, apiCallBack, id, apiRequest, true);
    }

    public static void callServerApi(Activity activity, int apiType, ApiCallBack apiCallBack, int id, Object apiRequest, boolean showProgress) {
        if (Utils.isNetworkConnected(activity)) {
            Call call = null;

            switch (apiType) {
                case API_DELETE_USER_VEHICLE:
                    call = ApiClient.current().getDeleteVehicleApi(id);
                    break;

                case API_UPDATE_USER_ADDRESS:
                    call = ApiClient.current().getUpdateAddressApi(id, apiRequest);
                    break;

                case API_DELETE_USER_ADDRESS:
                    call = ApiClient.current().getDeleteAddressApi(id);
                    break;


                case API_TECHNICIAN_JOB_STATUS:
                    call = ApiClient.current().getTechnicianJobStatusApi(id);
                    break;

                case API_DELETE_TECHNICIAN_VEHICLE:
                    call = ApiClient.current().getDeleteTechnicianVehicleApi(id, apiRequest);
                    break;

                case API_TECHNICIAN_JOB_ACCEPT:
                    call = ApiClient.current().getTechnicianJobAcceptApi(id);
                    break;

                case API_TECHNICIAN_JOB_DECLINE:
                    call = ApiClient.current().getTechnicianJobDeclineApi(id);
                    break;

                case API_JOB_REACHED_WAREHOUSE:
                    call = ApiClient.current().getReachedWarehouseApi(id);
                    break;

                case API_JOB_LEAVING_WAREHOUSE:
                    call = ApiClient.current().getLeavingWarehouseApi(id);
                    break;

                case API_JOB_IN_ROUTE:
                    call = ApiClient.current().getInRouteApi(id);
                    break;

                case API_JOB_ARRIVED_LOCATION:
                    call = ApiClient.current().getArrivedAtLocationApi(id);
                    break;

                case API_JOB_BOOKING_CLOSED:
                    call = ApiClient.current().getBookingClosedApi(id);
                    break;

                case API_CREATE_INVOICE:
                    call = ApiClient.current().getTechnicianInvoiceCreateApi(id, apiRequest);
                    break;

                case API_CUSTOMER_TRACK_ORDER:
                    call = ApiClient.current().getCustomerOrderTrackApi(id);
                    break;

                case API_PAY_INVOICE:
                    call = ApiClient.current().getPayInvoiceApi(id, apiRequest);
                    break;

                case API_INVOICE_DETAILS:
                    call = ApiClient.current().getInvoiceDetailsApi(id);
                    break;

                case API_DELETE_REMINDER:
                    call = ApiClient.current().getDeleteReminderApi(id, apiRequest);
                    break;

                case API_CANCEL_ORDER:
                    call = ApiClient.current().getCancelOrderApi(id);
                    break;

                case API_TECHNICIAN_JOB_DETAIL:
                    call = ApiClient.current().getTechnicianJobDetailApi(id);
                    break;

                case API_ORDER_DETAILS:
                    call = ApiClient.current().getOrderDetailsApi(id);
                    break;

                case API_CUSTOMER_SKIP_TIP:
                    call = ApiClient.current().getSkipTipApi(id);
                    break;
            }

            if(call != null) {
                callApi(activity, call, apiCallBack, showProgress);
            }
        } else {
            Utils.showSnackbar(activity, R.string.internet_not_connected);
        }
    }

    public static void callServerApiTwo(Activity activity, int apiType, ApiCallBack apiCallBack, String id, Object apiRequest, boolean showProgress) {
        if (Utils.isNetworkConnected(activity)) {
            Call call = null;

            switch (apiType) {

                case API_CUSTOMER_DELETE_CARD:
                    call = ApiClient.current().deleteCardApi(id);
                    break;
            }

            if(call != null) {
                callApi(activity, call, apiCallBack, showProgress);
            }
        } else {
            Utils.showSnackbar(activity, R.string.internet_not_connected);
        }
    }

    public static void callServerApi(Activity activity, int apiType, ApiCallBack apiCallBack, String id) {
        if (Utils.isNetworkConnected(activity)) {
            Call call = null;

            switch (apiType) {
                case API_CUSTOMER_NOTIFICATION_READ:
                    call = ApiClient.current().getCustomerNotificationReadApi(id);
                    break;

                case API_TECHNICIAN_NOTIFICATION_READ:
                    call = ApiClient.current().getTechnicianNotificationReadApi(id);
                    break;

                case API_TECHNICIAN_BACKGROUND_CHECK:
                    call = ApiClient.current().getTechnicianBackgroundVerificationApi(id);
                    break;
            }

            if(call != null) {
                callApi(activity, call, apiCallBack, false);
            }
        } else {
            Utils.showSnackbar(activity, R.string.internet_not_connected);
        }
    }

    public static void callServerApi(Activity activity, int apiType, ApiCallBack apiCallBack, int id, boolean showProgress) {
        callServerApi(activity, apiType, apiCallBack, id, null, showProgress);
    }

    public static void callServerApi(Activity activity, int apiType, ApiCallBack apiCallBack, int id) {
        callServerApi(activity, apiType, apiCallBack, id, null, true);
    }

    public static void callServerApiTwo(Activity activity, int apiType, ApiCallBack apiCallBack, String id) {
        callServerApiTwo(activity, apiType, apiCallBack, id, null, true);
    }



    public static void callMultipartServerApi(Activity activity, int apiType, ApiCallBack apiCallBack,
                                              Map<String, RequestBody> dataList, File imageFile, String imageParamName) {
        callMultipartServerApi(activity, apiType, apiCallBack, -1, dataList, imageFile, imageParamName);
    }

    public static void callMultipartServerApi(Activity activity, int apiType, ApiCallBack apiCallBack, int id,
                                              Map<String, RequestBody> dataList, File imageFile, String imageParamName) {
        callMultipartServerApi(activity, apiType, apiCallBack, id, dataList, imageFile, imageParamName, true);
    }

    public static void callMultipartServerApi(Activity activity, int apiType, ApiCallBack apiCallBack, int id,
                                              Map<String, RequestBody> dataList, File imageFile, String imageParamName, boolean showProgress) {
        if (Utils.isNetworkConnected(activity)) {
            Call call = null;
            MultipartBody.Part imagePart = null;
            if (imageFile != null) {
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpg"), imageFile);
                imagePart = MultipartBody.Part.createFormData(imageParamName, imageFile.getName(), imageBody);
            }

            switch (apiType) {
                case API_CUSTOMER_REGISTER:
                    call = ApiClient.current().getRegisterApi(imagePart, dataList);
                    break;

                case API_UPDATE_CUSTOMER_PROFILE:
                    call = ApiClient.current().getUpdateCustomerProfileApi(imagePart, dataList);
                    break;

                case API_ADD_USER_VEHICLE:
                    call = ApiClient.current().getAddUserVehicleApi(imagePart, dataList);
                    break;

                case API_UPDATE_USER_VEHICLE:
                    call = ApiClient.current().getUpdateVehicleApi(id, imagePart, dataList);
                    break;

                case API_ADD_TECHNICIAN_VEHICLE:
                    call = ApiClient.current().getAddTechnicianVehicleApi(imagePart, dataList);
                    break;

                case API_UPDATE_TECHNICIAN_VEHICLE:
                    call = ApiClient.current().getUpdateTechnicianVehicleApi(id, imagePart, dataList);
                    break;
            }

            if(call != null)
                callApi(activity, call, apiCallBack, showProgress);
        } else {
            if(showProgress) {
                Utils.showSnackbar(activity, R.string.internet_not_connected);
            }
        }
    }

    public static void callReportIssueApi(Activity activity, int apiType, ApiCallBack apiCallBack,
                                          Map<String, RequestBody> dataList, List<File> fileList) {
        if (Utils.isNetworkConnected(activity)) {
            MultipartBody.Part imagePart1 = null;
            MultipartBody.Part imagePart2 = null;
            MultipartBody.Part imagePart3 = null;

            for (int i = 0; i < fileList.size(); i++) {
                File file = fileList.get(i);
                if (i == 0) {
                    RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpg"), file);
                    imagePart1 = MultipartBody.Part.createFormData("file1", file.getName(), imageBody);
                } else if (i == 1) {
                    RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpg"), file);
                    imagePart2 = MultipartBody.Part.createFormData("file2", file.getName(), imageBody);
                } else if (i == 2) {
                    RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpg"), file);
                    imagePart3 = MultipartBody.Part.createFormData("file3", file.getName(), imageBody);
                }
            }

            Call call = null;
            switch (apiType) {
                case API_CREATE_SUPPORT_QUERY:
                    call = ApiClient.current().getReportIssueApi(dataList, imagePart1, imagePart2, imagePart3);
                    break;

                case API_CREATE_TECHNICIAN_SUPPORT_QUERY:
                    call = ApiClient.current().getTechnicianReportIssueApi(dataList, imagePart1, imagePart2, imagePart3);
                    break;
            }
            if(call != null) {
                callApi(activity, call, apiCallBack, true);
            }
        } else {
            Utils.showSnackbar(activity, R.string.internet_not_connected);
        }
    }

    public static void callTechnicianRegisterApi(Activity activity, int apiType, ApiCallBack apiCallBack,
                                                 Map<String, RequestBody> dataList, File userImage, File licenseImage, File insuranceImage, File vehicleRegistrationImage,
                                                 boolean showProgress) {
        if (Utils.isNetworkConnected(activity)) {
            MultipartBody.Part imagePart1 = null;
            MultipartBody.Part imagePart2 = null;
            MultipartBody.Part imagePart3 = null;
            MultipartBody.Part imagePart4 = null;

            if(userImage != null) {
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpg"), userImage);
                imagePart1 = MultipartBody.Part.createFormData("avatar", userImage.getName(), imageBody);
            }

            if(licenseImage != null) {
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpg"), licenseImage);
                imagePart2 = MultipartBody.Part.createFormData("driver_license", licenseImage.getName(), imageBody);
            }

            if(insuranceImage != null) {
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpg"), insuranceImage);
                imagePart3 = MultipartBody.Part.createFormData("insurance_information", insuranceImage.getName(), imageBody);
            }

            if(vehicleRegistrationImage != null) {
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpg"), vehicleRegistrationImage);
                imagePart4 = MultipartBody.Part.createFormData("vehicle_registration", vehicleRegistrationImage.getName(), imageBody);
            }

            Call call = null;
            switch (apiType) {
                case API_TECHNICIAN_REGISTER:
                    call = ApiClient.current().getTechnicianRegisterApi(dataList, imagePart1, imagePart2, imagePart3, imagePart4);
                    break;

                case API_UPDATE_TECHNICIAN_PROFILE:
                    call = ApiClient.current().getTechnicianUpdateProfileApi(dataList, imagePart1, imagePart2, imagePart3, imagePart4);
                    break;
            }

            if(call != null) {
                callApi(activity, call, apiCallBack, showProgress);
            }
        } else {
            Utils.showSnackbar(activity, R.string.internet_not_connected);
        }
    }

    public static void callTechnicianStartWorkApi(Activity activity, ApiCallBack apiCallBack,
                                                  int id, File image1, File image2, File image3, File image4) {
        if (Utils.isNetworkConnected(activity)) {
            MultipartBody.Part imagePart1 = null;
            MultipartBody.Part imagePart2 = null;
            MultipartBody.Part imagePart3 = null;
            MultipartBody.Part imagePart4 = null;

            if(image1 != null) {
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpg"), image1);
                imagePart1 = MultipartBody.Part.createFormData("vehicle_image1", image1.getName(), imageBody);
            }

            if(image2 != null) {
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpg"), image2);
                imagePart2 = MultipartBody.Part.createFormData("vehicle_image2", image2.getName(), imageBody);
            }

            if(image3 != null) {
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpg"), image3);
                imagePart3 = MultipartBody.Part.createFormData("vehicle_image3", image3.getName(), imageBody);
            }

            if(image4 != null) {
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpg"), image4);
                imagePart4 = MultipartBody.Part.createFormData("vehicle_image4", image4.getName(), imageBody);
            }

            Call call = ApiClient.current().getTechnicianStartWorkApi(id, imagePart1, imagePart2, imagePart3, imagePart4);

            if(call != null) {
                callApi(activity, call, apiCallBack, false);
            }
        } else {
            Utils.showSnackbar(activity, R.string.internet_not_connected);
        }
    }

    public static void callTechnicianCompleteWorkApi(Activity activity, ApiCallBack apiCallBack,
                                                  int id, File image1, File image2) {
        if (Utils.isNetworkConnected(activity)) {
            MultipartBody.Part imagePart1 = null;
            MultipartBody.Part imagePart2 = null;

            if(image1 != null) {
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpg"), image1);
                imagePart1 = MultipartBody.Part.createFormData("image1", image1.getName(), imageBody);
            }

            if(image2 != null) {
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpg"), image2);
                imagePart2 = MultipartBody.Part.createFormData("image2", image2.getName(), imageBody);
            }

            Call call = ApiClient.current().getWorkCompletedApi(id, imagePart1, imagePart2);

            if(call != null) {
                callApi(activity, call, apiCallBack, false);
            }
        } else {
            Utils.showSnackbar(activity, R.string.internet_not_connected);
        }
    }
}

