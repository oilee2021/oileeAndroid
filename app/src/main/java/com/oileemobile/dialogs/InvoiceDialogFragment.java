package com.oileemobile.dialogs;


import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.oileemobile.R;
import com.oileemobile.databinding.FragmentInvoiceDialogBinding;
import com.oileemobile.interfaces.OnCardSelectListener;
import com.oileemobile.models.technician.LatestInvoiceModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiRequest;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.ApiResponse;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class InvoiceDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private FragmentInvoiceDialogBinding binding;
    private LatestInvoiceModel invoiceModel;

    public InvoiceDialogFragment() {}

    public static InvoiceDialogFragment newInstance(LatestInvoiceModel invoiceModel) {

        Bundle args = new Bundle();
        args.putSerializable(Constants.INVOICE_MODEL, invoiceModel);
        InvoiceDialogFragment fragment = new InvoiceDialogFragment();
        fragment.setArguments(args);
        fragment.setCancelable(false);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invoice_dialog, container, false);

        if(getArguments() != null) {
            invoiceModel = (LatestInvoiceModel) getArguments().getSerializable(Constants.INVOICE_MODEL);
        }

        if(invoiceModel != null) {
            Utils.loadPicassoImage(invoiceModel.getTechnician_avatar(), binding.imageUser, null, R.drawable.app_logo);
            binding.textUserName.setText(invoiceModel.getTechnician_name());
            binding.textInvoiceDetail.setText(invoiceModel.getDescription());
            binding.ratingUser.setRating(invoiceModel.getStar());
            binding.textRatingAverage.setText(String.valueOf(invoiceModel.getRating()));
            binding.textRatingTotal.setText("(" + invoiceModel.getRating_label() + ")");
        }

        binding.buttonPay.setOnClickListener(this);
        return binding.getRoot();
    }

    public int getInvoiceId() {
        return invoiceModel != null ? invoiceModel.getId() : -1;
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    private OnCardSelectListener onCardSelectListener = this::callPayInvoiceApi;

    private void callPayInvoiceApi(String cardId) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setCard_id(cardId);

        ServiceManager.callServerApi(getActivity(), ServiceManager.API_PAY_INVOICE, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    if(!response.getMessage().contains("already paid")) {
                        Toast.makeText(getActivity(), "Payment success!", Toast.LENGTH_SHORT).show();
                        RateDialogFragment.newInstance(invoiceModel.getTechnician_name(), invoiceModel.getTechnician_avatar())
                                .show(getChildFragmentManager(), "");
                    } else {
                        Toast.makeText(getActivity(), "Invoice already paid!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                } else {
                    Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(getActivity(), failureMessage, Toast.LENGTH_SHORT).show();
            }
        }), invoiceModel.getId(), apiRequest);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_pay) {
            new PaymentInvoiceDialogFragment(onCardSelectListener).show(getChildFragmentManager(), "");
        }
    }
}
