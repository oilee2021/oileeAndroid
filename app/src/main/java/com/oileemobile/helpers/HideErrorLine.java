package com.oileemobile.helpers;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

/**
 * Created by Shubham Mathur on 12-04-17.
 */

public class HideErrorLine implements TextWatcher {

    private TextInputLayout layout;
    private TextView errorTextView;

    public HideErrorLine(TextInputLayout layout) {
        this.layout = layout;
    }

    public HideErrorLine(TextView errorTextView) {
        this.errorTextView = errorTextView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(layout != null) {
            if (layout.isErrorEnabled())
                layout.setErrorEnabled(false);
        } else if(errorTextView != null) {
            errorTextView.setVisibility(View.GONE);
        }
    }
}
