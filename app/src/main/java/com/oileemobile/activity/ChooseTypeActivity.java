package com.oileemobile.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.oileemobile.R;
import com.oileemobile.databinding.ActivityChooseTypeBinding;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.PrefManager;

import static com.oileemobile.utils.Constants.TYPE_CUSTOMER;
import static com.oileemobile.utils.Constants.TYPE_TECHNICIAN;

public class ChooseTypeActivity extends BaseActivity implements View.OnClickListener {

    private ActivityChooseTypeBinding binding;
    private int selectedOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_type);
        binding.chooseCustomerArea.setOnClickListener(this);
        binding.chooseTechnicianArea.setOnClickListener(this);
        binding.chooseSubmit.setOnClickListener(this);

        selectedOption = 0;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.choose_customer_area:
                showCustomerSelected(true);
                break;

            case R.id.choose_technician_area:
                showTechnicianSelected(true);
                break;

            case R.id.choose_submit:
                if(selectedOption == 1) {
                    PrefManager.putInt(PrefManager.USER_TYPE, TYPE_CUSTOMER);
                } else if (selectedOption == 2) {
                    PrefManager.putInt(PrefManager.USER_TYPE, TYPE_TECHNICIAN);
                }

                if(PrefManager.getBoolean(PrefManager.INTRO_SKIPPED)) {
                    ActivityController.startActivity(this, PhoneNumberActivity.class);
                } else {
                    ActivityController.startActivity(this, GettingStartedActivity.class);
                }
                break;
        }
    }

    private void showCustomerSelected(boolean show) {
        showSelected(show, binding.chooseTitle1, binding.chooseCustomer, binding.chooseCustomerArea);
        if(show) {
            binding.chooseSubmit.setEnabled(true);
            selectedOption = 1;
            showTechnicianSelected(false);
        }
    }

    private void showTechnicianSelected(boolean show) {
        showSelected(show, binding.chooseTitle2, binding.chooseTechnician, binding.chooseTechnicianArea);
        if(show) {
            binding.chooseSubmit.setEnabled(true);
            selectedOption = 2;
            showCustomerSelected(false);
        }
    }

    private void showSelected(boolean show, TextView textView, ImageView imageView, View view) {
        if(show) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            view.setBackgroundResource(R.drawable.round_red_stroke_background);
            imageView.setVisibility(View.VISIBLE);
        } else {
            textView.setTextColor(ContextCompat.getColor(this, R.color.lightBlack));
            view.setBackground(null);
            imageView.setVisibility(View.INVISIBLE);
        }
    }
}
