package com.oileemobile.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;

import com.oileemobile.R;
import com.oileemobile.databinding.FragmentInformationDialogBinding;

public class InformationDialog extends BaseDialogFragment {

    private int title, description, positiveButtonText;
    private OnClickListener mListener;

    public InformationDialog(@StringRes int title, @StringRes int description, OnClickListener onClickListener) {
        this(title, description, R.string.okay, onClickListener);
    }

    public InformationDialog(@StringRes int title, @StringRes int description, @StringRes int positiveButtonText,
                             OnClickListener onClickListener) {
        this.title = title;
        this.description = description;
        this.positiveButtonText = positiveButtonText;
        this.mListener = onClickListener;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
            if(mListener != null) {
                if(v.getId() == R.id.text_positive) {
                    mListener.onInformationDialogButtonClick();
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_information_dialog, null);
        FragmentInformationDialogBinding binding = DataBindingUtil.bind(view);
        binding.textTitle.setText(title);
        binding.textDescription.setText(description);
        binding.textPositive.setText(positiveButtonText);
        binding.textPositive.setOnClickListener(onClickListener);

        if(getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
        }
        return binding.getRoot();
    }

    public interface OnClickListener {
        void onInformationDialogButtonClick();
    }
}
