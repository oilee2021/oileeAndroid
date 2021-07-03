package com.oileemobile.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.oileemobile.R;
import com.oileemobile.databinding.FragmentPaymentSuccessDialogBinding;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2020-01-21 22:46
 **/
public class PaymentSuccessDialogFragment extends BaseDialogFragment {
    private FragmentPaymentSuccessDialogBinding binding;

    public static PaymentSuccessDialogFragment newInstance(String date, String time, String amount) {
        Bundle args = new Bundle();
        args.putString("date", date);
        args.putString("time", time);
        args.putString("amount", amount);
        PaymentSuccessDialogFragment fragment = new PaymentSuccessDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_success_dialog, container, false);

        Bundle bundle = getArguments();
        if(bundle != null) {
            binding.buttonOkay.setOnClickListener(v -> dismiss());
            binding.textAmount.setText("$" + bundle.getString("amount"));
            binding.textDate.setText(bundle.getString("date"));
            binding.textTime.setText(bundle.getString("time"));
        }

        return binding.getRoot();
    }
}
