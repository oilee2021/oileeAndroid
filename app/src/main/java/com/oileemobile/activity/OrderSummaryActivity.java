package com.oileemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.oileemobile.R;
import com.oileemobile.databinding.ActivityOrderSummaryBinding;
import com.oileemobile.models.AddressModel;
import com.oileemobile.models.TimeSlot;
import com.oileemobile.models.VehicleModel;
import com.oileemobile.models.technician.CouponModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiRequest;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.CreateOrderResponse;
import com.oileemobile.utils.Utils;

public class OrderSummaryActivity extends BaseActivity implements View.OnClickListener {

    private ActivityOrderSummaryBinding binding;
    private VehicleModel vehicleModel;
    private int packageId;
    private AddressModel addressModel;
    private TimeSlot timeSlot;
    private Date date;
    private CouponModel couponModel;
    private String couponCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_summary);

        vehicleModel = (VehicleModel) getIntent().getSerializableExtra("vehicle");
        addressModel = (AddressModel) getIntent().getSerializableExtra("address");
        timeSlot = (TimeSlot) getIntent().getSerializableExtra("time");
        date = (Date) getIntent().getSerializableExtra("date");
        packageId = getIntent().getIntExtra("package_id", 0);
        double totalPrice = getIntent().getDoubleExtra("package_price", 0.0);
        couponModel = getIntent().getParcelableExtra("discount_coupon");

        setupToolbar();
        setupSelectedVehicle();
        binding.summaryAddress.setText(addressModel.getFullAddress());
        binding.summaryTime.setText(timeSlot.getTime12Hr());
        binding.summaryDate.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date));
        binding.tvSubtotalAmount.setText(getString(R.string.item_price, totalPrice));

        if (couponModel != null) {
            double discountPrice = getDiscountPrice(totalPrice, couponModel.getDiscount());
            binding.tvDiscountAmount.setText(getString(R.string.item_discount, discountPrice));
            totalPrice -= discountPrice;
            couponCode = couponModel.getCode();
        } else {
            binding.groupDiscount.setVisibility(View.GONE);
        }

        binding.tvTotalAmount.setText(getString(R.string.item_price, Math.max(0.0, totalPrice)));
        binding.summarySubmit.setOnClickListener(this);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.oilee_summary);
    }

    private void setupSelectedVehicle() {
        if(vehicleModel != null) {
            binding.summaryVehicle.setText(vehicleModel.getVehicleDetails());
        }
    }

    private double getDiscountPrice(double totalPrice, String discount) {
        double price = 0;
        if(!TextUtils.isEmpty(discount)) {
            if(discount.contains("%")) {
                int percentage = Integer.parseInt(discount.substring(0, discount.length() - 1));
                price = totalPrice * percentage / 100;
            } else if(discount.contains("$")) price = Double.parseDouble(discount.substring(1));
        }
        return price;
    }

    private void callOrderCreateApi() {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setCustomer_address_id(String.valueOf(addressModel.getId()));
        apiRequest.setCustomer_vehicle_id(String.valueOf(vehicleModel.getId()));
        apiRequest.setPackage_id(String.valueOf(packageId));
        apiRequest.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date));
        apiRequest.setTime(timeSlot.getTime24Hr());
        apiRequest.setNote(getIntent().getStringExtra("special_note"));
        apiRequest.setInstruction(getIntent().getStringExtra("special_instruction"));
        apiRequest.setSpecial_request(getIntent().getStringExtra("special_request"));
        apiRequest.setUser_card_id(getIntent().getStringExtra("card_token"));
        apiRequest.setCoupon_code(couponCode);

        ServiceManager.callServerApi(this, ServiceManager.API_CREATE_ORDER, new ApiCallBack<>(new ApiResponseListener<CreateOrderResponse>() {
            @Override
            public void onApiSuccess(CreateOrderResponse response) {
                if(response.getStatus() == 201) {
                    Intent intent = new Intent(OrderSummaryActivity.this, OrderConfirmationActivity.class);
                    Bundle bundle = getIntent().getExtras();
                    bundle.putInt("id", response.getData().getId());
                    intent.putExtras(bundle);
                    startActivities(new Intent[]{new Intent(OrderSummaryActivity.this, CustomerMainActivity.class), intent});
                    finishAffinity();
                } else {
                    Utils.showSnackbar(OrderSummaryActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(OrderSummaryActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), apiRequest, true);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.summary_submit) {
            Utils.showConfirmationDialog(this, getString(R.string.confirmation_text_new),
                    getString(R.string.i_understand), v1 -> callOrderCreateApi());
        }
    }
}
