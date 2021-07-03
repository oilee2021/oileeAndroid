package com.oileemobile.activity;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import java.util.Date;

import com.oileemobile.R;
import com.oileemobile.databinding.ActivityOrderConfirmationBinding;
import com.oileemobile.models.AddressModel;
import com.oileemobile.models.TimeSlot;
import com.oileemobile.models.VehicleModel;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.Utils;

public class OrderConfirmationActivity extends BaseActivity implements View.OnClickListener {

    private ActivityOrderConfirmationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_confirmation);
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);

        AddressModel addressModel = (AddressModel) getIntent().getSerializableExtra("address");
        VehicleModel vehicleModel = (VehicleModel) getIntent().getSerializableExtra("vehicle");
        TimeSlot time = (TimeSlot) getIntent().getSerializableExtra("time");
        Date date = (Date) getIntent().getSerializableExtra("date");
        int bookingId = getIntent().getIntExtra("id", 0);

        setupToolbar();
        setupSelectedVehicle(vehicleModel);
        binding.summaryAddress.setText(addressModel.getFullAddress());
        binding.summaryTime.setText(time.getTime12Hr());
        binding.summaryDate.setText(Utils.getTimeFromDate("MMMM dd, yyyy", date));

        binding.tvUserName.setText(PrefManager.getString(PrefManager.USER_NAME));
        Utils.loadPicassoImage(PrefManager.getString(PrefManager.USER_IMAGE), binding.ivUserImage, null);
        binding.orderDate.setText(Utils.getTimeFromDate("MMMM dd, yyyy", new Date()));
        binding.bookingId.setText(String.valueOf(bookingId));
        binding.textOrderConfirmation.setText(getString(R.string.order_confirmation_text_new, Utils.getTimeFromDate("MM/dd/yyyy", date)));

        binding.oileeHistory.setOnClickListener(this);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.confirmation);
    }

    private void setupSelectedVehicle(VehicleModel vehicleModel) {
        if(vehicleModel != null) {
            binding.summaryVehicle.setText(vehicleModel.getVehicleDetails());
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.oilee_history) {
            ActivityController.startActivity(this, MyOrderHistory.class, true);
        }
    }
}