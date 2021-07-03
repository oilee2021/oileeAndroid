package com.oileemobile.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import com.oileemobile.R;
import com.oileemobile.databinding.ActivityOrderDetailBinding;
import com.oileemobile.dialogs.InformationDialog;
import com.oileemobile.dialogs.RateDialogFragment;
import com.oileemobile.dialogs.TipDialogFragment;
import com.oileemobile.interfaces.OnRatingSubmittedListener;
import com.oileemobile.models.OrderDetailResponse;
import com.oileemobile.models.OrderHistoryModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiRequest;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.ApiResponse;
import com.oileemobile.network.response.CustomerOrderTrackResponse;
import com.oileemobile.network.response.technician.TechnicianJobStatusResponse;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.Utils;

public class OrderDetailActivity extends BaseActivity implements View.OnClickListener,
        OnRatingSubmittedListener, TipDialogFragment.OnTechnicianTipListener {
    private ActivityOrderDetailBinding binding;
    private OrderHistoryModel orderModel;
    private CustomerOrderTrackResponse.DataBean trackOrder;
    private BottomSheetBehavior bottomSheetBehavior;
    private TechnicianJobStatusResponse.TechnicianJobStatusModel jobStatusModel;
    private int activityType;
    private Handler mHandler;
    private Runnable jobStatusRunnable;
    private boolean isBookingClosed;
    private RateDialogFragment rateDialogFragment;
    private TipDialogFragment tipDialogFragment;
    /*private LatestInvoiceModel latestInvoiceModel;
    private InvoiceDialogFragment invoiceDialogFragment;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail);
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.oilee_detail);

        int orderId = getIntent().getIntExtra(Constants.JOB_ID, -1);
        callOrderDetailsApi(orderId);
        activityType = getIntent().getIntExtra(Constants.ACTIVITY_TYPE, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startJobStatusUpdates();
    }

    @Override
    protected void onStop() {
        if (jobStatusRunnable != null)
            mHandler.removeCallbacks(jobStatusRunnable);

        super.onStop();
    }

    private void startJobStatusUpdates() {
        if (!isBookingClosed && activityType == 0 && jobStatusRunnable != null) {
            mHandler.removeCallbacks(jobStatusRunnable);
            mHandler.postDelayed(jobStatusRunnable, Constants.MAX_INTERVAL);
        }
    }

    private void setupBottomSheet() {
        callCustomerTrackOrderApi(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(Utils.dpToPx(this, 70));

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        binding.bottomSheet.textSwipeAction.setText(R.string.swipe_down_text);
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        binding.bottomSheet.textSwipeAction.setText(R.string.swipe_up_text);
                        binding.bottomSheet.scrollView.scrollTo(0, 0);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                binding.bottomSheet.imageDownArrow.setRotation(slideOffset * 180);
            }
        });

        binding.bottomSheet.linearSheetHeader.setOnClickListener(v -> {
            switch (bottomSheetBehavior.getState()) {
                case BottomSheetBehavior.STATE_COLLAPSED:
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    break;

                case BottomSheetBehavior.STATE_EXPANDED:
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    break;
            }
        });
    }

    private void setupBottomSheetData() {
        setText(binding.bottomSheet.itemRequestAccepted.textStatus, R.string.request_accepted);
        setText(binding.bottomSheet.itemReachedWarehouse.textStatus, R.string.reached_warehouse);
        //setText(binding.bottomSheet.itemLeavingWarehouse.textStatus, R.string.leaving_warehouse);
        setText(binding.bottomSheet.itemInRoute.textStatus, R.string.in_route);
        setText(binding.bottomSheet.itemArrivedLocation.textStatus, R.string.arrived_location);
        setText(binding.bottomSheet.itemStartingWork.textStatus, R.string.starting_work);
        setText(binding.bottomSheet.itemWorkCompleted.textStatus, R.string.work_completed);
        //setText(binding.bottomSheet.itemPaymentSubmitted.textStatus, R.string.payment_submitted);
        setText(binding.bottomSheet.itemBookingClosed.textStatus, R.string.booking_closed);
        setJobStatusData();
    }

    private void setJobStatusData() {
        setImageTick(binding.bottomSheet.itemRequestAccepted.imageTick, jobStatusModel.isRequest_accepted());
        setImageTick(binding.bottomSheet.itemReachedWarehouse.imageTick, jobStatusModel.isReached_at_warehouse());
        //setImageTick(binding.bottomSheet.itemLeavingWarehouse.imageTick, jobStatusModel.isLeaving_warehouse());
        setImageTick(binding.bottomSheet.itemInRoute.imageTick, jobStatusModel.isIn_route());
        setImageTick(binding.bottomSheet.itemArrivedLocation.imageTick, jobStatusModel.isArrived_at_location());
        setImageTick(binding.bottomSheet.itemStartingWork.imageTick, jobStatusModel.isStarting_work());
        setImageTick(binding.bottomSheet.itemWorkCompleted.imageTick, jobStatusModel.isWork_completed());
        //setImageTick(binding.bottomSheet.itemPaymentSubmitted.imageTick, jobStatusModel.isPayment_submitted());
        setImageTick(binding.bottomSheet.itemBookingClosed.imageTick, jobStatusModel.isBooking_closed());

        setText(binding.bottomSheet.itemRequestAccepted.textStatusDate, jobStatusModel.getRequest_accepted_date(), jobStatusModel.isRequest_accepted());
        setText(binding.bottomSheet.itemReachedWarehouse.textStatusDate, jobStatusModel.getReached_at_warehouse_date(), jobStatusModel.isReached_at_warehouse());
        /*setText(binding.bottomSheet.itemLeavingWarehouse.textStatusDate,
                jobStatusModel.getLeaving_warehouse_date(), jobStatusModel.isLeaving_warehouse());*/
        setText(binding.bottomSheet.itemInRoute.textStatusDate, jobStatusModel.getIn_route_date(), jobStatusModel.isIn_route());
        setText(binding.bottomSheet.itemArrivedLocation.textStatusDate, jobStatusModel.getArrived_at_location_date(), jobStatusModel.isArrived_at_location());
        setText(binding.bottomSheet.itemStartingWork.textStatusDate, jobStatusModel.getStarting_work_date(), jobStatusModel.isStarting_work());
        setText(binding.bottomSheet.itemWorkCompleted.textStatusDate, jobStatusModel.getWork_completed_date(), jobStatusModel.isWork_completed());
        /*setText(binding.bottomSheet.itemPaymentSubmitted.textStatusDate,
                jobStatusModel.getPayment_submitted_date(), jobStatusModel.isPayment_submitted());*/
        setText(binding.bottomSheet.itemBookingClosed.textStatusDate, jobStatusModel.getBooking_closed_date(), jobStatusModel.isBooking_closed());

        isBookingClosed = jobStatusModel.isBooking_closed();
        if(isBookingClosed) {
            //hideInvoiceDialog();
            showRateTechnicianDialog();
        } else {
            /*if (jobStatusModel.isWork_completed() && !jobStatusModel.isPayment_submitted()) {
                callGetLatestInvoiceApi();
            } else
                hideInvoiceDialog();*/

            startJobStatusUpdates();
        }

        if(activityType == 0) {
            binding.textOrderCancel.setVisibility(jobStatusModel.isArrived_at_location() ? View.GONE : View.VISIBLE);
        } else {
            binding.textOrderCancel.setVisibility(View.GONE);
        }
        binding.textTrackOrder.setVisibility(activityType == 0 ? View.VISIBLE : View.GONE);
        binding.bottomSheet.getRoot().setVisibility(activityType == 2 ? View.GONE : View.VISIBLE);
    }

    /*private void hideInvoiceDialog() {
        if(invoiceDialogFragment != null && invoiceDialogFragment.isVisible())
            invoiceDialogFragment.dismiss();
    }*/

    private void setImageTick(ImageView imageTick, boolean setSelected) {
        imageTick.setImageResource(setSelected ? R.drawable.ic_tick : R.drawable.ic_untick);
    }

    private void setText(TextView textView, String text, boolean isTicked) {
        if(TextUtils.isEmpty(text) || !isTicked) textView.setVisibility(View.GONE);
        else {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void showRateTechnicianDialog() {
        if (!trackOrder.isTip_gave_to_technician() && orderModel.isShow_tip()) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (tipDialogFragment == null || tipDialogFragment.isHidden()) {
                tipDialogFragment = TipDialogFragment.newInstance(String.valueOf(trackOrder.getJob_id()), orderModel.getId(), orderModel.getTechnician());
                tipDialogFragment.show(getSupportFragmentManager(), "");
            }
        } else if(!trackOrder.isRated_for_technician()) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (rateDialogFragment == null || rateDialogFragment.isHidden()) {
                rateDialogFragment = RateDialogFragment.newInstance(orderModel.getTechnician().getName(), orderModel.getTechnician().getImage());
                rateDialogFragment.show(getSupportFragmentManager(), "");
            }
        }
    }

    @Override
    public void onTechnicianTip(double amount) {
        trackOrder.setTip_gave_to_technician(true);
        orderModel.setTip_amount(amount);
        showRateTechnicianDialog();
    }

    @Override
    public void onRatingSubmitted(String rating, String reviewText) {
        callRatingTechnicianApi(rating, reviewText);
    }

    private void setText(TextView textView, @StringRes int text) {
        setText(textView, getString(text));
    }

    private void setText(TextView textView, String text) {
        textView.setText(text);
        textView.setVisibility(View.VISIBLE);
    }

    /*private void callGetLatestInvoiceApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_LATEST_INVOICE, new ApiCallBack<>(new ApiResponseListener<LatestInvoiceResponse>() {
            @Override
            public void onApiSuccess(LatestInvoiceResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    latestInvoiceModel = response.getData();
                    boolean showNewDialog = true;
                    if(invoiceDialogFragment != null && invoiceDialogFragment.isVisible()) {
                        if(invoiceDialogFragment.getInvoiceId() == latestInvoiceModel.getId()) {
                            showNewDialog = false;
                        }
                    }

                    if(showNewDialog) {
                        hideInvoiceDialog();
                        invoiceDialogFragment = InvoiceDialogFragment.newInstance(latestInvoiceModel);
                        invoiceDialogFragment.show(getSupportFragmentManager(), "");
                    }
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {

            }
        }), false);
    }*/

    private void callOrderDetailsApi(int orderId) {
        ServiceManager.callServerApi(this, ServiceManager.API_ORDER_DETAILS, new ApiCallBack<>(new ApiResponseListener<OrderDetailResponse>() {
            @Override
            public void onApiSuccess(OrderDetailResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    orderModel = response.getDetailData();
                    Utils.loadPicassoImage(orderModel.getTechnician().getImage(), binding.imageUser, null);
                    binding.textOrderId.setText(String.valueOf(orderModel.getId()));
                    binding.textUserName.setText(orderModel.getTechnician().getName());
                    binding.textOrderDate.setText(orderModel.getDate());
                    binding.summaryDate.setText(orderModel.getDate());
                    binding.summaryTime.setText(orderModel.getTime());
                    binding.summaryAddress.setText(orderModel.getService().getAddress());

                    if(orderModel.getTechnician().getId() == 0) {
                        binding.ratingUser.setVisibility(View.GONE);
                        binding.textRatingAverage.setVisibility(View.GONE);
                        binding.textRatingTotal.setVisibility(View.GONE);
                        binding.textTechnicianVehicle.setVisibility(View.GONE);
                    } else {
                        binding.ratingUser.setVisibility(View.VISIBLE);
                        binding.ratingUser.setRating(orderModel.getTechnician().getStar());
                        setText(binding.textRatingAverage, String.valueOf(orderModel.getTechnician().getRating()));
                        setText(binding.textRatingTotal, "(" + orderModel.getTechnician().getRating_label() + ")");
                        setText(binding.textTechnicianVehicle, orderModel.getTechnician().getVehicle().toString());
                    }

                    binding.selectedVehicleRow.vehicleArrow.setVisibility(View.INVISIBLE);
                    binding.selectedVehicleRow.vehicleName.setText(orderModel.getCustomer().getVehicle().getMake());
                    binding.selectedVehicleRow.vehicleDesc.setText(orderModel.getCustomer().getVehicle().getYear() + " | "
                            + orderModel.getCustomer().getVehicle().getModel());
                    Utils.loadPicassoImage(orderModel.getCustomer().getVehicle().getVehicle_image(), binding.selectedVehicleRow.vehicleImage,
                            binding.selectedVehicleRow.progressBar, R.drawable.car_demo);
                    binding.textBookingFee.setText(getString(R.string.item_price, orderModel.getPrice()));
                    binding.textServiceTotal.setText(getString(R.string.item_price, (orderModel.getPaid_amount() + orderModel.getTip_amount())));
                    if (orderModel.getTip_amount() != 0) {
                        binding.rlTip.setVisibility(View.VISIBLE);
                        binding.tvTip.setText(getString(R.string.item_price, orderModel.getTip_amount()));
                    } else
                        binding.rlTip.setVisibility(View.GONE);

                    if (orderModel.getPrice() - orderModel.getPaid_amount() > 0) {
                        binding.rlDiscount.setVisibility(View.VISIBLE);
                        binding.textDiscount.setText(getString(R.string.item_discount, (orderModel.getPrice() - orderModel.getPaid_amount())));
                        binding.textSubTotal.setText(getString(R.string.item_price, orderModel.getPaid_amount()));
                    } else
                        binding.rlDiscount.setVisibility(View.GONE);

                    bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.getRoot());
                    setupBottomSheet();
                    binding.textTrackOrder.setOnClickListener(OrderDetailActivity.this);
                    binding.textOrderCancel.setOnClickListener(OrderDetailActivity.this);

                    mHandler = new Handler();
                    jobStatusRunnable = () -> callCustomerTrackOrderApi(false);
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Error fetching order details!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(OrderDetailActivity.this, "Error fetching order details!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }), orderId);
    }

    private void callRatingTechnicianApi(String rating, String reviewText) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setRatings(rating);
        apiRequest.setReview_text(reviewText);
        apiRequest.setJob_id(String.valueOf(trackOrder.getJob_id()));

        ServiceManager.callServerApi(this, ServiceManager.API_RATE_TECHNICIAN, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    trackOrder.setRated_for_technician(true);
                    new InformationDialog(R.string.success, R.string.rating_submitted, new InformationDialog.OnClickListener() {
                        @Override
                        public void onInformationDialogButtonClick() {
                            recreate();
                        }
                    }).show(getSupportFragmentManager(), "");
                } else {
                    Utils.showSnackbar(OrderDetailActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(OrderDetailActivity.this, failureMessage);
            }
        }), apiRequest);
    }

    private void callCustomerTrackOrderApi(boolean showProgress) {
        ServiceManager.callServerApi(this, ServiceManager.API_CUSTOMER_TRACK_ORDER, new ApiCallBack<>(new ApiResponseListener<CustomerOrderTrackResponse>() {
            @Override
            public void onApiSuccess(CustomerOrderTrackResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    binding.mainView.setVisibility(View.VISIBLE);
                    trackOrder = response.getData();
                    jobStatusModel = trackOrder.getStatus();
                    setupBottomSheetData();
                } else {
                    Utils.showSnackbar(OrderDetailActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(OrderDetailActivity.this, failureMessage);
            }
        }), orderModel.getId(), showProgress);
    }

    private void callCancelOrderApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_CANCEL_ORDER, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    Toast.makeText(OrderDetailActivity.this, R.string.order_cancelled_successfully, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(OrderDetailActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(OrderDetailActivity.this, failureMessage, Toast.LENGTH_SHORT).show();
            }
        }), orderModel.getId());
    }

    private void callSkipTipApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_CUSTOMER_SKIP_TIP, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {

            }

            @Override
            public void onApiFailure(String failureMessage) {

            }
        }), orderModel.getId(), false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_track_order:
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                break;

            case R.id.text_order_cancel:
                callCancelOrderApi();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(bottomSheetBehavior != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
        super.onBackPressed();
    }
}
