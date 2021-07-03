package com.oileemobile.activity;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.oileemobile.R;
import com.oileemobile.databinding.ActivitySpecialRequestBinding;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Utils;

public class SpecialRequestActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySpecialRequestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_special_request);

        setupToolbar();
        binding.specialRequestSubmit.setOnClickListener(this);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.special_request);
    }

    private void checkValidationAndProceed() {
        String request = binding.specialRequestText.getText().toString().trim();
        if(request.isEmpty()) {
            binding.specialRequestText.setError(getString(R.string.field_blank_error));
            return;
        }

        Bundle bundle = getIntent().getExtras();
        bundle.putString("special_request", request);
        ActivityController.startActivity(this, SpecialInstructionActivity.class, bundle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.special_request_submit:
                checkValidationAndProceed();
                break;
        }
    }
}
