package com.oileemobile.network;

import com.oileemobile.models.OrderDetailResponse;
import com.oileemobile.network.response.AboutUsResponse;
import com.oileemobile.network.response.ApiResponse;
import com.oileemobile.network.response.CardListResponse;
import com.oileemobile.network.response.CreateOrderResponse;
import com.oileemobile.network.response.CustomerNotificationResponse;
import com.oileemobile.network.response.CustomerOrderTrackResponse;
import com.oileemobile.network.response.CustomerProfileResponse;
import com.oileemobile.network.response.FaqResponse;
import com.oileemobile.network.response.ForgotPasswordResponse;
import com.oileemobile.network.response.LoginResponse;
import com.oileemobile.network.response.OrderHistoryResponse;
import com.oileemobile.network.response.PackageResponse;
import com.oileemobile.network.response.RegisterResponse;
import com.oileemobile.network.response.ReminderListResponse;
import com.oileemobile.network.response.ReportIssueResponse;
import com.oileemobile.network.response.ResetPasswordResponse;
import com.oileemobile.network.response.SupportQueryResponse;
import com.oileemobile.network.response.TermsResponse;
import com.oileemobile.network.response.UserAddAddressResponse;
import com.oileemobile.network.response.UserAddVehicleResponse;
import com.oileemobile.network.response.UserAllAddressResponse;
import com.oileemobile.network.response.UserAllVehiclesResponse;
import com.oileemobile.network.response.technician.BackgroundCheckResponse;
import com.oileemobile.network.response.technician.CheckInOutResponse;
import com.oileemobile.network.response.technician.CouponResponse;
import com.oileemobile.network.response.technician.InvoiceDetailsResponse;
import com.oileemobile.network.response.technician.LatestInvoiceResponse;
import com.oileemobile.network.response.technician.OnlineStatusResponse;
import com.oileemobile.network.response.technician.SupportEligibleResponse;
import com.oileemobile.network.response.technician.TechnicianJobStatusResponse;
import com.oileemobile.network.response.technician.TechnicianJobsResponse;
import com.oileemobile.network.response.technician.TechnicianNotificationResponse;
import com.oileemobile.network.response.technician.TechnicianOrderDetailResponse;
import com.oileemobile.network.response.technician.TechnicianOrderHistoryResponse;
import com.oileemobile.network.response.technician.TechnicianProfileResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface Api {

    //Customers Api
    @POST("customers/checkMobile")
    Call<ApiResponse> getCheckMobileApi(@Body Object apiRequest);

    @POST("customers/login")
    Call<LoginResponse> getLoginApi(@Body Object apiRequest);

    @Multipart
    @POST("customers/signup")
    Call<RegisterResponse> getRegisterApi(@Part MultipartBody.Part file, @PartMap Map<String, RequestBody> map);

    @GET("customers/profile")
    Call<CustomerProfileResponse> getCustomerProfileApi();

    @Multipart
    @POST("customers/profile")
    Call<CustomerProfileResponse> getUpdateCustomerProfileApi(@Part MultipartBody.Part file, @PartMap Map<String, RequestBody> map);

    @GET("faq")
    Call<FaqResponse> getFaqApi();

    @GET("about-us")
    Call<AboutUsResponse> getAboutUsApi();

    @Multipart
    @POST("customers/vehicle/create")
    Call<UserAddVehicleResponse> getAddUserVehicleApi(@Part MultipartBody.Part file, @PartMap Map<String, RequestBody> map);

    @GET("customer/vehicles")
    Call<UserAllVehiclesResponse> getAllUserVehiclesApi();

    @POST("customers/address/create")
    Call<UserAddAddressResponse> getAddUserAddressApi(@Body Object apiRequest);

    @GET("customer/addresses")
    Call<UserAllAddressResponse> getAllUserAddressApi();

    @Multipart
    @POST("customers/vehicle/{id}/update")
    Call<UserAddVehicleResponse> getUpdateVehicleApi(@Path("id") int id, @Part MultipartBody.Part file, @PartMap Map<String, RequestBody> map);

    @DELETE("customers/vehicle/{id}")
    Call<UserAddVehicleResponse> getDeleteVehicleApi(@Path("id") int id);

    @DELETE("customers/address/{id}")
    Call<UserAddAddressResponse> getDeleteAddressApi(@Path("id") int id);

    @PUT("customers/address/{id}/update")
    Call<UserAddAddressResponse> getUpdateAddressApi(@Path("id") int id, @Body Object apiRequest);

    @GET("packages")
    Call<PackageResponse> getPackagesApi();

    @GET("customer/cardlist")
    Call<CardListResponse> getCustomerCardsApi();

    @POST("customer/order/create")
    Call<CreateOrderResponse> getCreateOrderApi(@Body Object apiRequest);

    @GET("customers/orders/pending")
    Call<OrderHistoryResponse> getPendingOrdersApi();

    @GET("customers/orders/completed")
    Call<OrderHistoryResponse> getCompletedOrdersApi();

    @GET("customers/orders/cancelled")
    Call<OrderHistoryResponse> getCancelledOrdersApi();

    @POST("password/phone_number")
    Call<ForgotPasswordResponse> getForgotPasswordApi(@Body Object apiRequest);

    @POST("password/reset")
    Call<ResetPasswordResponse> getPasswordResetApi(@Body Object apiRequest);

    @GET("term")
    Call<TermsResponse> getTermsApi();

    @Multipart
    @POST("customer/support-query/create")
    Call<ReportIssueResponse> getReportIssueApi(@PartMap Map<String, RequestBody> map, @Part MultipartBody.Part file1,
                                                @Part MultipartBody.Part file2, @Part MultipartBody.Part file3);

    @GET("customer/getReminder")
    Call<ReminderListResponse> getScheduleReminderListApi();

    @POST("customer/saveReminder")
    Call<ReportIssueResponse> getAddScheduleApi(@Body Object apiRequest);

    @POST("customer/reminder/{id}")
    Call<ApiResponse> getDeleteReminderApi(@Path("id") int id, @Body Object apiRequest);

    @GET("customer/support-queries")
    Call<SupportQueryResponse> getSupportQueriesApi();

    @POST("customers/save-card")
    Call<ApiResponse> getAddCustomerCardApi(@Body Object apiRequest);

    @DELETE("customer/card/{id}")
    Call<ApiResponse> deleteCardApi(@Path("id") String id);

    @GET("customer/support-query/is-eligible")
    Call<SupportEligibleResponse> getSupportEligibleApi();

    @GET("customers/invoices/latest")
    Call<LatestInvoiceResponse> getLatestInvoiceApi();

    @POST("customers/invoices/{id}/pay")
    Call<ApiResponse> getPayInvoiceApi(@Path("id") int id, @Body Object apiRequest);

    @GET("customer/notifications")
    Call<CustomerNotificationResponse> getCustomerNotificationsApi();

    @POST("customer/notifications/mark-as-read")
    Call<ApiResponse> getCustomerNotificationsReadApi();

    @DELETE("customer/notifications/delete")
    Call<ApiResponse> getCustomerNotificationsClearApi();

    @POST("customer/notifications/{id}/read")
    Call<ApiResponse> getCustomerNotificationReadApi(@Path("id") String id);

    @POST("customers/orders/{id}/cancel")
    Call<ApiResponse> getCancelOrderApi(@Path("id") int id);

    @GET("customers/orders/{id}/show")
    Call<OrderDetailResponse> getOrderDetailsApi(@Path("id") int id);

    @POST("tip-technician")
    Call<ApiResponse> getTipTechnicianApi(@Body Object apiRequest);

    @POST("customers/orders/{orderId}/skip-tip")
    Call<ApiResponse> getSkipTipApi(@Path("orderId") int id);


    //Technicians Api
    @POST("technicians/checkMobile")
    Call<ApiResponse> getCheckTechnicianMobileApi(@Body Object apiRequest);

    @Multipart
    @POST("technicians/signup")
    Call<RegisterResponse> getTechnicianRegisterApi(@PartMap Map<String, RequestBody> map, @Part MultipartBody.Part file1, @Part MultipartBody.Part file2, @Part MultipartBody.Part file3,
                                                    @Part MultipartBody.Part file4);

    @GET("technicians/profile")
    Call<TechnicianProfileResponse> getTechnicianProfileApi();

    @POST("technicians/login")
    Call<LoginResponse> getTechnicianLoginApi(@Body Object apiRequest);

    @Multipart
    @POST("technicians/vehicle/create")
    Call<UserAddVehicleResponse> getAddTechnicianVehicleApi(@Part MultipartBody.Part file, @PartMap Map<String, RequestBody> map);

    @POST("technicians/password-reset-token")
    Call<ForgotPasswordResponse> getTechnicianForgotPasswordApi(@Body Object apiRequest);

    @POST("technicians/reset-password")
    Call<ResetPasswordResponse> getTechnicianPasswordResetApi(@Body Object apiRequest);

    @GET("technician/upcoming-jobs")
    Call<TechnicianJobsResponse> getTechnicianJobsApi();

    @POST("technician/mark-online")
    Call<ApiResponse> getMarkTechnicianOnlineApi();

    @POST("technician/mark-offline")
    Call<ApiResponse> getMarkTechnicianOfflineApi();

    @GET("technician/online-status")
    Call<OnlineStatusResponse> getTechnicianOnlineStatusApi();

    @Multipart
    @POST("technicians/profile")
    Call<RegisterResponse> getTechnicianUpdateProfileApi(@PartMap Map<String, RequestBody> map, @Part MultipartBody.Part file1, @Part MultipartBody.Part file2, @Part MultipartBody.Part file3,
                                                    @Part MultipartBody.Part file4);

    @GET("technician/notifications")
    Call<TechnicianNotificationResponse> getTechnicianNotificationsApi();

    @POST("technician/notifications/mark-as-read")
    Call<ApiResponse> getTechnicianMarkNotificationsApi();

    @GET("technician/vehicles")
    Call<UserAllVehiclesResponse> getAllTechnicianVehiclesApi();

    @GET("technician/job/{id}/status")
    Call<TechnicianJobStatusResponse> getTechnicianJobStatusApi(@Path("id") int id);

    @POST("technician/jobs/{id}/accept")
    Call<ApiResponse> getTechnicianJobAcceptApi(@Path("id") int id);

    @POST("technician/jobs/{id}/decline")
    Call<ApiResponse> getTechnicianJobDeclineApi(@Path("id") int id);

    @Multipart
    @POST("technicians/vehicle/{id}/update")
    Call<UserAddVehicleResponse> getUpdateTechnicianVehicleApi(@Path("id") int id, @Part MultipartBody.Part file, @PartMap Map<String, RequestBody> map);

    @POST("technicians/vehicle/{id}")
    Call<UserAddVehicleResponse> getDeleteTechnicianVehicleApi(@Path("id") int id, @Body Object apiRequest);

    @GET("technician/support-queries")
    Call<SupportQueryResponse> getTechnicianSupportQueriesApi();

    @Multipart
    @POST("technician/support-query/create")
    Call<ReportIssueResponse> getTechnicianReportIssueApi(@PartMap Map<String, RequestBody> map, @Part MultipartBody.Part file1,
                                                @Part MultipartBody.Part file2, @Part MultipartBody.Part file3);

    @GET("technician/support-query/is-eligible")
    Call<SupportEligibleResponse> getTechnicianSupportEligibleApi();

    @GET("technician/accepted-jobs")
    Call<TechnicianJobsResponse> getTechnicianAcceptedJobsApi();

    @POST("technician/jobs/{id}/reached-at-warehouse")
    Call<ApiResponse> getReachedWarehouseApi(@Path("id") int id);

    @POST("technician/jobs/{id}/leaving-warehouse")
    Call<ApiResponse> getLeavingWarehouseApi(@Path("id") int id);

    @POST("technician/jobs/{id}/in-route")
    Call<ApiResponse> getInRouteApi(@Path("id") int id);

    @POST("technician/jobs/{id}/arrived-at-location")
    Call<ApiResponse> getArrivedAtLocationApi(@Path("id") int id);

    @POST("technician/jobs/{id}/booking-closed")
    Call<ApiResponse> getBookingClosedApi(@Path("id") int id);

    @Multipart
    @POST("technician/jobs/{id}/upload-vehicle-images")
    Call<ApiResponse> getTechnicianStartWorkApi(@Path("id") int id, @Part MultipartBody.Part file1,
                                                          @Part MultipartBody.Part file2, @Part MultipartBody.Part file3, @Part MultipartBody.Part file4);

    @POST("technician/jobs/{id}/invoice/create")
    Call<ApiResponse> getTechnicianInvoiceCreateApi(@Path("id") int id, @Body Object apiRequest);

    @GET("technician/oil-management/checkin")
    Call<CheckInOutResponse> getTechnicianCheckInDataApi();

    @GET("technician/oil-management/checkout")
    Call<CheckInOutResponse> getTechnicianCheckOutDataApi();

    @POST("technician/oil-management/checkin")
    Call<CheckInOutResponse> getTechnicianCheckInApi(@Body Object apiRequest);

    @POST("technician/oil-management/checkout")
    Call<CheckInOutResponse> getTechnicianCheckOutApi(@Body Object apiRequest);

    @POST("technician/update-current-location")
    Call<ApiResponse> getUpdateTechnicianLocationApi(@Body Object apiRequest);

    @GET("customers/orders/{id}/track")
    Call<CustomerOrderTrackResponse> getCustomerOrderTrackApi(@Path("id") int id);

    @POST("technician/rate-customer")
    Call<ApiResponse> getRateCustomerApi(@Body Object apiRequest);

    @POST("customer/rate-technician")
    Call<ApiResponse> getRateTechnicianApi(@Body Object apiRequest);

    @GET("coupons")
    Call<CouponResponse> getCouponApi();

    @POST("coupons/apply")
    Call<CouponResponse> getApplyCouponApi(@Body Object apiRequest);

    @GET("technicians/jobs/{id}/invoice")
    Call<InvoiceDetailsResponse> getInvoiceDetailsApi(@Path("id") int id);

    @GET("technicians/activation-status")
    Call<ApiResponse> getTechnicianActivatedStatusApi();

    @GET("technicians/jobs/pending")
    Call<TechnicianOrderHistoryResponse> getTechnicianPendingOrdersApi();

    @GET("technicians/jobs/completed")
    Call<TechnicianOrderHistoryResponse> getTechnicianCompletedOrdersApi();

    @GET("technicians/jobs/cancelled")
    Call<TechnicianOrderHistoryResponse> getTechnicianCancelledOrdersApi();

    @GET("technician/jobs/{id}")
    Call<TechnicianOrderDetailResponse> getTechnicianJobDetailApi(@Path("id") int id);

    @Multipart
    @POST("technician/jobs/{id}/work-completed")
    Call<ApiResponse> getWorkCompletedApi(@Path("id") int id, @Part MultipartBody.Part file1,
                                                @Part MultipartBody.Part file2);

    @DELETE("technician/notifications/delete")
    Call<ApiResponse> getTechnicianNotificationsClearApi();

    @POST("technician/notifications/{id}/read")
    Call<ApiResponse> getTechnicianNotificationReadApi(@Path("id") String id);

    @GET("background-check-status/{requisitionId}")
    Call<BackgroundCheckResponse> getTechnicianBackgroundVerificationApi(@Path("requisitionId") String id);
}