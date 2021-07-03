package com.oileemobile.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;

import com.oileemobile.R;
import com.oileemobile.databinding.FragmentConfirmationDialogBinding;

public class ConfirmationDialog extends BaseDialogFragment {

    private int title, description, positiveButtonText, negativeButtonText;
    private OnClickListener mListener;

    public ConfirmationDialog(@StringRes int title, @StringRes int description, OnClickListener onClickListener) {
        this(title, description, R.string.yes, R.string.no, onClickListener);
    }

    public ConfirmationDialog(@StringRes int title, @StringRes int description, @StringRes int positiveButtonText,
                              @StringRes int negativeButtonText, OnClickListener onClickListener) {
        this.title = title;
        this.description = description;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.mListener = onClickListener;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
            if(mListener != null) {
                if(v.getId() == R.id.text_positive) {
                    mListener.onConfirmationDialogButtonClick(DialogInterface.BUTTON_POSITIVE);
                } else if(v.getId() == R.id.text_negative) {
                    mListener.onConfirmationDialogButtonClick(DialogInterface.BUTTON_NEGATIVE);
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_confirmation_dialog, null);
        FragmentConfirmationDialogBinding binding = DataBindingUtil.bind(view);
        binding.textTitle.setText(title);
        binding.textDescription.setText(description);

        binding.textPositive.setText(positiveButtonText);
        binding.textNegative.setText(negativeButtonText);

        binding.textPositive.setOnClickListener(onClickListener);
        binding.textNegative.setOnClickListener(onClickListener);


        if(getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
        }
        return binding.getRoot();
    }

    public interface OnClickListener {
        void onConfirmationDialogButtonClick(int which);
    }
}
