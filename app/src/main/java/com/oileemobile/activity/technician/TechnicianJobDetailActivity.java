package com.oileemobile.activity.technician;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.transition.TransitionManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.oileemobile.R;
import com.oileemobile.activity.BaseActivity;
import com.oileemobile.databinding.ActivityTechnicianJobDetailBinding;
import com.oileemobile.dialogs.InformationDialog;
import com.oileemobile.dialogs.RateDialogFragment;
import com.oileemobile.dialogs.UploadCarImagesDialogFragment;
import com.oileemobile.interfaces.OnRatingSubmittedListener;
import com.oileemobile.interfaces.OnUploadCarImages;
import com.oileemobile.models.technician.TechnicianJobModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiRequest;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.ApiResponse;
import com.oileemobile.network.response.technician.TechnicianJobStatusResponse;
import com.oileemobile.network.response.technician.TechnicianOrderDetailResponse;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.ProgressDialog;
import com.oileemobile.utils.Utils;

import java.io.File;

public class TechnicianJobDetailActivity extends BaseActivity implements View.OnClickListener, OnUploadCarImages, OnRatingSubmittedListener {

    private ActivityTechnicianJobDetailBinding binding;
    private TechnicianJobModel jobDetail;
    private TechnicianJobStatusResponse.TechnicianJobStatusModel jobStatusModel;
    private BottomSheetBehavior bottomSheetBehavior;
    public static final int GOOGLE_MAPS_DIRECTIONS_REQUEST = 124;
    private UploadCarImagesDialogFragment uploadImagesFragment, uploadImagesFragment2;
    /*private Handler mHandler;
    private Runnable mRunnable;
    private boolean isWaitingForPayment;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_technician_job_detail);

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.getRoot());
        uploadImagesFragment = UploadCarImagesDialogFragment.newInstance(4, getString(R.string.upload_vehicle_images_text));
        uploadImagesFragment2 = UploadCarImagesDialogFragment.newInstance(2, getString(R.string.technician_work_complete_message));

        /*mHandler = new Handler();
        mRunnable = () -> {
            callTechnicianJobStatusApi(false);
        };*/

        if(jobDetail != null) setupJobDetail();
        else callGetOrderDetailApi();

        binding.textPositive.setOnClickListener(this);
        binding.textNegative.setOnClickListener(this);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(getString(R.string.order_id, jobDetail.getOrder_id()));
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        startJobStatusUpdates();
    }

    @Override
    protected void onStop() {
        mHandler.removeCallbacks(mRunnable);
        super.onStop();
    }*/

    private void setupJobDetail() {
        setupToolbar();
        if(jobDetail.getJob_status().equals(Constants.JobStatus.REQUEST_NOT_ACCEPTED)) {
            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            binding.mainView.setVisibility(View.VISIBLE);
        } else {
            setupBottomSheet();
        }
        Utils.loadPicassoImage(jobDetail.getCustomer_avatar(), binding.ivUserImage, null);
        binding.tvUserName.setText(jobDetail.getCustomer_name());
        binding.ratingBar.setRating(jobDetail.getRating());
        binding.textRatingAverage.setText(String.valueOf(jobDetail.getRating()));
        binding.textRatingCount.setText("(" + jobDetail.getRating_label() + ")");
        binding.textTime.setText(jobDetail.getCreated_date());
        binding.bookingId.setText(String.valueOf(jobDetail.getOrder_id()));
        binding.orderDate.setText(jobDetail.getOrder_date());
        binding.summaryDate.setText(jobDetail.getService().getDate());
        binding.summaryTime.setText(jobDetail.getService().getTime());
        binding.summaryAddress.setText(jobDetail.getService().getAddress());
        binding.vehicleRow.vehicleArrow.setVisibility(View.INVISIBLE);
        binding.vehicleRow.vehicleName.setText(jobDetail.getVehicle().getMake());
        binding.vehicleRow.vehicleDesc.setText(jobDetail.getVehicle().getYear() + " | " + jobDetail.getVehicle().getModel());
        Utils.loadPicassoImage(jobDetail.getVehicle().getImage(), binding.vehicleRow.vehicleImage,
                binding.vehicleRow.progressBar, R.drawable.car_demo);
        TechnicianJobModel.OilBean oilBean = jobDetail.getOil();
        binding.textOilNeeded.setText(String.format("%s Qts | %s | %s", oilBean.getQuarts(), oilBean.getPackage_name(), oilBean.getType()));

        binding.textOilNeeded.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if(binding.textOilNeeded.getLineCount() == 1) binding.textOilNeeded.setGravity(Gravity.END);
        });

        if(jobDetail.getParking_instructions() != null) {
            binding.parkingInstructionsSection.setVisibility(View.VISIBLE);
            binding.textParkingInstructions.setText(jobDetail.getParking_instructions());
        }

        if(jobDetail.getSpecial_notes() != null) {
            binding.specialNotesSection.setVisibility(View.VISIBLE);
            binding.textSpecialNotes.setText(jobDetail.getSpecial_notes());
        }
    }

    private void setupBottomSheet() {
        callTechnicianJobStatusApi(true);
        binding.textNegative.setVisibility(View.GONE);
        binding.textPositive.setText(R.string.navigate_to_job);
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
        if(jobStatusModel != null) {
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

            binding.bottomSheet.itemReachedWarehouse.getRoot().setOnClickListener(this);
            //binding.bottomSheet.itemLeavingWarehouse.getRoot().setOnClickListener(this);
            binding.bottomSheet.itemInRoute.getRoot().setOnClickListener(this);
            binding.bottomSheet.itemArrivedLocation.getRoot().setOnClickListener(this);
            binding.bottomSheet.itemStartingWork.getRoot().setOnClickListener(this);
            binding.bottomSheet.itemWorkCompleted.getRoot().setOnClickListener(this);
            //binding.bottomSheet.itemPaymentSubmitted.getRoot().setOnClickListener(this);
            binding.bottomSheet.itemBookingClosed.getRoot().setOnClickListener(this);
        }
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

        setText(binding.bottomSheet.itemRequestAccepted.textStatusDate,
                jobStatusModel.getRequest_accepted_date(), jobStatusModel.isRequest_accepted());
        setText(binding.bottomSheet.itemReachedWarehouse.textStatusDate,
                jobStatusModel.getReached_at_warehouse_date(), jobStatusModel.isReached_at_warehouse());
        /*setText(binding.bottomSheet.itemLeavingWarehouse.textStatusDate,
                jobStatusModel.getLeaving_warehouse_date(), jobStatusModel.isLeaving_warehouse());*/
        setText(binding.bottomSheet.itemInRoute.textStatusDate,
                jobStatusModel.getIn_route_date(), jobStatusModel.isIn_route());
        setText(binding.bottomSheet.itemArrivedLocation.textStatusDate,
                jobStatusModel.getArrived_at_location_date(), jobStatusModel.isArrived_at_location());
        setText(binding.bottomSheet.itemStartingWork.textStatusDate,
                jobStatusModel.getStarting_work_date(), jobStatusModel.isStarting_work());
        setText(binding.bottomSheet.itemWorkCompleted.textStatusDate,
                jobStatusModel.getWork_completed_date(), jobStatusModel.isWork_completed());
        /*setText(binding.bottomSheet.itemPaymentSubmitted.textStatusDate,
                jobStatusModel.getPayment_submitted_date(), jobStatusModel.isPayment_submitted());*/
        setText(binding.bottomSheet.itemBookingClosed.textStatusDate,
                jobStatusModel.getBooking_closed_date(), jobStatusModel.isBooking_closed());

        binding.textPositive.setVisibility((jobStatusModel.isArrived_at_location()
                || jobDetail.getJob_status().equals(Constants.JobStatus.REQUEST_DECLINED)
                || jobDetail.getJob_status().equals(Constants.JobStatus.REQUEST_CANCELLED)) ? View.GONE : View.VISIBLE);
        /*if(isWaitingForPayment && jobStatusModel.isPayment_submitted()) {
            if(jobStatusModel.getPayment() != null && jobStatusModel.getPayment().size() > 0) {
                TechnicianJobStatusResponse.TechnicianJobStatusModel.PaymentModel paymentModel = jobStatusModel.getPayment().get(0);
                PaymentSuccessDialogFragment.newInstance(paymentModel.getPaid_date(), paymentModel.getPaid_time(), paymentModel.getPaid_amount())
                        .show(getSupportFragmentManager(), "");
            }
            isWaitingForPayment = false;
        }
        isWaitingForPayment = jobStatusModel.isWork_completed() && !jobStatusModel.isPayment_submitted();
        startJobStatusUpdates();*/

        if(jobStatusModel.isBooking_closed()) {
            showRateCustomerDialog();
        }
    }

    /*private void startJobStatusUpdates() {
        if (isWaitingForPayment) {
            mHandler.postDelayed(mRunnable, Constants.MAX_INTERVAL);
        }
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

    private void setText(TextView textView, @StringRes int text) {
        textView.setText(text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_reached_warehouse:
                callJobStatusUpdateApi(jobStatusModel.isReached_at_warehouse(), jobStatusModel.isRequest_accepted(), ServiceManager.API_JOB_REACHED_WAREHOUSE);
                break;

            /*case R.id.item_leaving_warehouse:
                if(jobStatusModel.isChecked_out()) {
                    callJobStatusUpdateApi(jobStatusModel.isLeaving_warehouse(), jobStatusModel.isReached_at_warehouse(), ServiceManager.API_JOB_LEAVING_WAREHOUSE);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.JOB_ID, jobDetail.getId());
                    ActivityController.startActivityForResult(this, CheckInOutActivity.class, bundle, Constants.REQUEST_CODE_CHECK_OUT);
                    Toast.makeText(this, R.string.check_out_for_this_job, Toast.LENGTH_SHORT).show();
                }
                break;*/

            case R.id.item_in_route:
                callJobStatusUpdateApi(jobStatusModel.isIn_route(), /*jobStatusModel.isLeaving_warehouse()*/jobStatusModel.isReached_at_warehouse(), ServiceManager.API_JOB_IN_ROUTE);
                break;

            case R.id.item_arrived_location:
                callJobStatusUpdateApi(jobStatusModel.isArrived_at_location(), jobStatusModel.isIn_route(), ServiceManager.API_JOB_ARRIVED_LOCATION);
                break;

            case R.id.item_starting_work:
                if(!jobStatusModel.isStarting_work() && jobStatusModel.isArrived_at_location()) {
                    openStartWorkImageUploadDialog();
                }
                break;

            case R.id.item_work_completed:
                if(!jobStatusModel.isWork_completed() && jobStatusModel.isStarting_work()) {
                    openCompleteWorkImageUploadDialog();
                }
                break;

            case R.id.item_booking_closed:
                if(!jobStatusModel.isBooking_closed()) {
                    callJobStatusUpdateApi(jobStatusModel.isBooking_closed(), jobStatusModel.isWork_completed(), ServiceManager.API_JOB_BOOKING_CLOSED);
                } else {
                    showRateCustomerDialog();
                }
                break;

            case R.id.text_positive:
                if(!jobDetail.getJob_status().equals(Constants.JobStatus.REQUEST_NOT_ACCEPTED)) startMapNavigation();
                else callAcceptJobApi();
                break;

            case R.id.text_negative:
                callDeclineJobApi();
                break;
        }
    }

    private void showRateCustomerDialog() {
        if(!jobStatusModel.isIs_rated()) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            RateDialogFragment.newInstance(jobDetail.getCustomer_name(), jobDetail.getCustomer_avatar())
                    .show(getSupportFragmentManager(), "");
        }
    }

    @Override
    public void onRatingSubmitted(String rating, String reviewText) {
        callRatingCustomerApi(rating, reviewText);
    }

    private void openStartWorkImageUploadDialog() {
        uploadImagesFragment.show(getSupportFragmentManager(), "");
    }

    private void openCompleteWorkImageUploadDialog() {
        uploadImagesFragment2.show(getSupportFragmentManager(), "");
    }

    private void callGetOrderDetailApi() {
        int jobId = getIntent().getIntExtra(Constants.JOB_ID, 0);
        ServiceManager.callServerApi(this, ServiceManager.API_TECHNICIAN_JOB_DETAIL, new ApiCallBack<>(new ApiResponseListener<TechnicianOrderDetailResponse>() {
            @Override
            public void onApiSuccess(TechnicianOrderDetailResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    jobDetail = response.getData();
                    setupJobDetail();
                } else {
                    Toast.makeText(TechnicianJobDetailActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(TechnicianJobDetailActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                finish();
            }
        }), jobId);
    }

    private void callJobStatusUpdateApi(boolean isTicked, boolean isPreviousTicked, int api) {
        if(!isTicked && isPreviousTicked) {
            ProgressDialog.showProgressDialog(this);
            ServiceManager.callServerApi(this, api, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
                @Override
                public void onApiSuccess(ApiResponse response) {
                    if(response.getStatus() == StatusCodes.SUCCESS) {
                        callTechnicianJobStatusApi(false);
                    } else {
                        ProgressDialog.hideProgressDialog();
                        Utils.showSnackbar(TechnicianJobDetailActivity.this, response.getMessage());
                    }
                }

                @Override
                public void onApiFailure(String failureMessage) {
                    ProgressDialog.hideProgressDialog();
                    Utils.showSnackbar(TechnicianJobDetailActivity.this, failureMessage);
                }
            }), jobDetail.getId(), false);
        }
    }

    private void startMapNavigation() {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + jobDetail.getCustomer_lat() + "," + jobDetail.getCustomer_lng() + "&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        try {
            startActivityForResult(mapIntent, GOOGLE_MAPS_DIRECTIONS_REQUEST);
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(this, R.string.google_maps_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    private void callTechnicianJobStatusApi(boolean showProgress) {
        ServiceManager.callServerApi(this, ServiceManager.API_TECHNICIAN_JOB_STATUS, new ApiCallBack<>(new ApiResponseListener<TechnicianJobStatusResponse>() {
            @Override
            public void onApiSuccess(TechnicianJobStatusResponse response) {
                if(!showProgress) ProgressDialog.hideProgressDialog();
                else binding.mainView.setVisibility(View.VISIBLE);
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    jobStatusModel = response.getData();
                    if(showProgress) setupBottomSheetData();
                    else setJobStatusData();
                } else {
                    Utils.showSnackbar(TechnicianJobDetailActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                if(!showProgress) ProgressDialog.hideProgressDialog();
                Utils.showSnackbar(TechnicianJobDetailActivity.this, failureMessage);
            }
        }), jobDetail.getId(), showProgress);
    }

    private void callAcceptJobApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_TECHNICIAN_JOB_ACCEPT, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    TransitionManager.beginDelayedTransition(binding.mainView);
                    jobDetail.setJob_status(Constants.JobStatus.REQUEST_ACCEPTED);
                    setupBottomSheet();
                    Toast.makeText(TechnicianJobDetailActivity.this, R.string.request_accepted, Toast.LENGTH_SHORT).show();
                } else {
                    Utils.showSnackbar(TechnicianJobDetailActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(TechnicianJobDetailActivity.this, failureMessage);
            }
        }), jobDetail.getId());
    }

    private void callDeclineJobApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_TECHNICIAN_JOB_DECLINE, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    setResult(RESULT_OK);
                    finish();
                    Toast.makeText(TechnicianJobDetailActivity.this, R.string.request_declined, Toast.LENGTH_SHORT).show();
                } else {
                    Utils.showSnackbar(TechnicianJobDetailActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(TechnicianJobDetailActivity.this, failureMessage);
            }
        }), jobDetail.getId());
    }

    private void callStartWorkApi(File file1, File file2, File file3, File file4) {
        ProgressDialog.showProgressDialog(this);
        ServiceManager.callTechnicianStartWorkApi(this, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    if(uploadImagesFragment != null) uploadImagesFragment.dismiss();
                    callTechnicianJobStatusApi(false);
                } else {
                    ProgressDialog.hideProgressDialog();
                    Utils.showSnackbar(TechnicianJobDetailActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                ProgressDialog.hideProgressDialog();
                Utils.showSnackbar(TechnicianJobDetailActivity.this, failureMessage);
            }
        }), jobDetail.getId(), file1, file2, file3, file4);
    }

    private void callCompleteWorkApi(File file1, File file2) {
        ProgressDialog.showProgressDialog(this);
        ServiceManager.callTechnicianCompleteWorkApi(this, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    if(uploadImagesFragment2 != null) uploadImagesFragment2.dismiss();
                    callTechnicianJobStatusApi(false);
                } else {
                    ProgressDialog.hideProgressDialog();
                    Utils.showSnackbar(TechnicianJobDetailActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                ProgressDialog.hideProgressDialog();
                Utils.showSnackbar(TechnicianJobDetailActivity.this, failureMessage);
            }
        }), jobDetail.getId(), file1, file2);
    }

    private void callRatingCustomerApi(String rating, String reviewText) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setRatings(rating);
        apiRequest.setReview_text(reviewText);
        apiRequest.setJob_id(String.valueOf(jobDetail.getId()));

        ServiceManager.callServerApi(this, ServiceManager.API_RATE_CUSTOMER, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    jobStatusModel.setIs_rated(true);
                    new InformationDialog(R.string.success, R.string.rating_submitted, null)
                            .show(getSupportFragmentManager(), "");
                } else {
                    Utils.showSnackbar(TechnicianJobDetailActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(TechnicianJobDetailActivity.this, failureMessage);
            }
        }), apiRequest);
    }

    @Override
    public void onImagesSelected(File[] files) {
        if (files.length == 4)
            callStartWorkApi(files[0], files[1], files[2], files[3]);
        else if (files.length == 2)
            callCompleteWorkApi(files[0], files[1]);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.REQUEST_CODE_RAISE_INVOICE
                && resultCode == RESULT_OK) {
            ProgressDialog.showProgressDialog(this);
            callTechnicianJobStatusApi(false);
        } else if(requestCode == Constants.REQUEST_CODE_CHECK_OUT) {
            callTechnicianJobStatusApi(true);
        }
    }

    @Override
    public void onBackPressed() {
        if(!jobDetail.getJob_status().equals(Constants.JobStatus.REQUEST_NOT_ACCEPTED)) {
            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return;
            }
            if(jobStatusModel != null && jobStatusModel.isBooking_closed()) setResult(RESULT_FIRST_USER);
            else setResult(RESULT_OK);
        }
        super.onBackPressed();
    }
}
