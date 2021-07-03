package com.oileemobile.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.transition.TransitionManager;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.oileemobile.R;
import com.oileemobile.adapters.TimeSlotsAdapter;
import com.oileemobile.databinding.ActivitySelectDateTimeBinding;
import com.oileemobile.models.AddressModel;
import com.oileemobile.models.TimeSlot;
import com.oileemobile.models.VehicleModel;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Utils;

public class SelectDateTimeActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySelectDateTimeBinding binding;
    private int selectedTimeSlot;
    private List<TimeSlot> timeSlotList;
    private Calendar calendar;
    private VehicleModel vehicleModel;
    private AddressModel addressModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_date_time);

        vehicleModel = (VehicleModel) getIntent().getSerializableExtra("vehicle");
        addressModel = (AddressModel) getIntent().getSerializableExtra("address");
        setupToolbar();

        calendar = Calendar.getInstance();
        setupTimeSlotRecyclerView();
        binding.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            setupTimeSlotRecyclerView();
        });
        binding.calendarView.setMinDate(System.currentTimeMillis());
        binding.dateSubmit.setOnClickListener(this);
    }

    private void setupTimeSlotRecyclerView() {
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        timeSlotList = new ArrayList<>();
        timeSlotList.add(new TimeSlot(calendar.getTime()));
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        timeSlotList.add(new TimeSlot(calendar.getTime()));
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        timeSlotList.add(new TimeSlot(calendar.getTime()));
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        timeSlotList.add(new TimeSlot(calendar.getTime()));
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        timeSlotList.add(new TimeSlot(calendar.getTime()));
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        timeSlotList.add(new TimeSlot(calendar.getTime()));
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        timeSlotList.add(new TimeSlot(calendar.getTime()));

        TransitionManager.beginDelayedTransition(binding.mainView);
        binding.recyclerTimeSlots.setLayoutManager(new FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP));
        binding.recyclerTimeSlots.setAdapter(new TimeSlotsAdapter(timeSlotList, (position, view) -> setSlotSelected(position)));
        selectedTimeSlot = -1;
        binding.dateSubmit.setEnabled(false);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.select_date_time);
    }

    private void setSlotSelected(int position) {
        if(selectedTimeSlot != position) {
            TimeSlotsAdapter.MyViewHolder viewHolder = (TimeSlotsAdapter.MyViewHolder) binding.recyclerTimeSlots.findViewHolderForAdapterPosition(position);
            if(viewHolder != null) {
                TextView selectedText = (TextView) viewHolder.itemView;
                selectedText.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                selectedText.setBackgroundResource(R.drawable.round_red_solid_background);
            }

            setSlotUnselected(selectedTimeSlot);
            selectedTimeSlot = position;
            if(!binding.dateSubmit.isEnabled()) {
                binding.dateSubmit.setEnabled(true);
            }
        }
    }

    private void setSlotUnselected(int position) {
        if(position != -1) {
            TimeSlotsAdapter.MyViewHolder viewHolder = (TimeSlotsAdapter.MyViewHolder) binding.recyclerTimeSlots.findViewHolderForAdapterPosition(position);
            if(viewHolder != null) {
                TextView selectedText = (TextView) viewHolder.itemView;
                selectedText.setTextColor(ContextCompat.getColor(this, R.color.lightBlack));
                selectedText.setBackgroundResource(R.drawable.round_red_stroke_background);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_submit:
                Bundle bundle = new Bundle();
                bundle.putSerializable("address", addressModel);
                bundle.putSerializable("vehicle", vehicleModel);
                bundle.putSerializable("date", calendar.getTime());
                bundle.putSerializable("time", timeSlotList.get(selectedTimeSlot));
                ActivityController.startActivity(this, SelectOilPackagesActivity.class, bundle, false, false, false);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
