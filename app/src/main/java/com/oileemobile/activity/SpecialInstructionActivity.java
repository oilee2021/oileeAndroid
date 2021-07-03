package com.oileemobile.activity;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.oileemobile.R;
import com.oileemobile.databinding.ActivitySpecialInstructionBinding;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Utils;

public class SpecialInstructionActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySpecialInstructionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_special_instruction);

        setupToolbar();
        binding.specialInstructionSubmit.setOnClickListener(this);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.notes_for_technician);
    }

    private void checkValidationAndProceed() {
        String note = binding.specialNote.getText().toString().trim();
        String instruction = binding.specialInstruction.getText().toString().trim();
        Bundle bundle = getIntent().getExtras();
        bundle.putString("special_note", note);
        bundle.putString("special_instruction", instruction);
        ActivityController.startActivity(this, SelectPaymentMethodActivity.class, bundle, false, false, false);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.special_instruction_submit) {
            checkValidationAndProceed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
