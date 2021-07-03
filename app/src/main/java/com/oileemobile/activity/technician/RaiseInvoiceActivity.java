package com.oileemobile.activity.technician;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.transition.TransitionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.oileemobile.R;
import com.oileemobile.activity.BaseActivity;
import com.oileemobile.adapters.CouponListAdapter;
import com.oileemobile.databinding.ActivityRaiseInvoiceBinding;
import com.oileemobile.helpers.HideErrorLine;
import com.oileemobile.interfaces.OnCouponSelectedListener;
import com.oileemobile.models.technician.CouponModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiRequest;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.ApiResponse;
import com.oileemobile.network.response.technician.CouponResponse;
import com.oileemobile.network.response.technician.InvoiceDetailsResponse;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.Utils;

public class RaiseInvoiceActivity extends BaseActivity implements OnCouponSelectedListener {
    private ActivityRaiseInvoiceBinding binding;
    private int jobId;
    private List<CouponModel> couponList;
    private CouponListAdapter mAdapter;
    private double itemTotal, bookingCharge, discountPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_raise_invoice);
        setupToolbar();

        jobId = getIntent().getIntExtra(Constants.JOB_ID, -1);
        binding.subTotalText.addTextChangedListener(new HideErrorLine(binding.subTotalLayout));
        binding.buttonSubmit.setOnClickListener(v -> checkValidationAndProceed());
        setupRecyclerView();
        callGetInvoiceDetails();
        callGetCouponApi();
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.complete_payment);
    }

    private void setupRecyclerView() {
        couponList = new ArrayList<>();
        binding.recyclerCoupons.setItemAnimator(null);
        mAdapter = new CouponListAdapter(couponList, this);
        binding.recyclerCoupons.setAdapter(mAdapter);
    }

    private void checkValidationAndProceed() {
        String subTotal = binding.subTotalText.getText().toString().trim();
        if(TextUtils.isEmpty(subTotal)) {
            binding.subTotalLayout.setError(getString(R.string.field_blank_error));
            return;
        }

        callRaiseInvoiceApi();
    }

    @Override
    public void onCouponSelected(int position) {
        CouponModel coupon = couponList.get(position);
        discountPrice = getDiscountPrice(coupon.getDiscount());
        updateItemTotalDetails();
        binding.textDiscountAppliedTitle.setVisibility(View.VISIBLE);
        binding.textDiscountApplied.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCouponRemoved() {
        discountPrice = 0;
        updateItemTotalDetails();
        binding.textDiscountApplied.setVisibility(View.GONE);
        binding.textDiscountAppliedTitle.setVisibility(View.GONE);
    }

    private double getDiscountPrice(String discount) {
        double price = 0;
        if(!TextUtils.isEmpty(discount)) {
            if(discount.contains("%")) {
                int percentage = Integer.parseInt(discount.substring(0, discount.length() - 1));
                price = itemTotal * percentage / 100;
            } else if(discount.contains("$")) price = Double.parseDouble(discount.substring(1));
        }
        return price;
    }

    private void updateItemTotalDetails() {
        double total = itemTotal - bookingCharge - discountPrice;
        binding.textDiscountApplied.setText(String.format(Locale.getDefault(), "$%.2f", discountPrice));
        binding.textTotal.setText(String.format(Locale.getDefault(), "$%.2f", total));
    }

    private void callGetCouponApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_COUPONS, new ApiCallBack<>(new ApiResponseListener<CouponResponse>() {
            @Override
            public void onApiSuccess(CouponResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    TransitionManager.beginDelayedTransition(binding.mainView);
                    couponList.addAll(response.getData());
                    if(couponList.size() > 0) mAdapter.notifyDataSetChanged();
                    else {
                        binding.recyclerCoupons.setVisibility(View.GONE);
                        binding.textCouponTitle.setVisibility(View.GONE);
                    }
                } else {
                    Utils.showSnackbar(RaiseInvoiceActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(RaiseInvoiceActivity.this, failureMessage);
            }
        }), false);
    }

    private void callGetInvoiceDetails() {
        ServiceManager.callServerApi(this, ServiceManager.API_INVOICE_DETAILS, new ApiCallBack<>(new ApiResponseListener<InvoiceDetailsResponse>() {
            @Override
            public void onApiSuccess(InvoiceDetailsResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    binding.mainView.setVisibility(View.VISIBLE);
                    InvoiceDetailsResponse.InvoiceDetail invoiceDetail = response.getData();
                    itemTotal = Double.parseDouble(invoiceDetail.getSub_total());
                    bookingCharge = Double.parseDouble(invoiceDetail.getBooking_charge());
                    binding.subTotalText.setText(String.format(Locale.getDefault(), "$%.2f", itemTotal));
                    binding.textInvoiceInfo.setText(getString(R.string.raise_invoice_info, bookingCharge));

                    binding.textItemTotal.setText(String.format(Locale.getDefault(), "$%.2f", itemTotal));
                    binding.textBookingCharge.setText(String.format(Locale.getDefault(), "$%.2f", bookingCharge));
                    updateItemTotalDetails();
                } else {
                    Toast.makeText(RaiseInvoiceActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(RaiseInvoiceActivity.this, failureMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        }), jobId);
    }

    private void callRaiseInvoiceApi() {
        ApiRequest apiRequest = new ApiRequest();
        if (mAdapter.getSelectedCouponId() != -1) apiRequest.setCoupon_id(String.valueOf(mAdapter.getSelectedCouponId()));

        ServiceManager.callServerApi(this, ServiceManager.API_CREATE_INVOICE, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    setResult(RESULT_OK);
                    finish();
                    Toast.makeText(RaiseInvoiceActivity.this, "Invoice created!", Toast.LENGTH_SHORT).show();
                } else {
                    Utils.showSnackbar(RaiseInvoiceActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(RaiseInvoiceActivity.this, failureMessage);
            }
        }), jobId, apiRequest);
    }
}
