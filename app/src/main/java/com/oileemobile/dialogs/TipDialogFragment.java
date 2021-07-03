package com.oileemobile.dialogs;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.oileemobile.R;
import com.oileemobile.databinding.FragmentTipDialogBinding;
import com.oileemobile.interfaces.OnCardSelectListener;
import com.oileemobile.models.OrderHistoryModel;
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
public class TipDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private FragmentTipDialogBinding binding;
    private OrderHistoryModel.TechnicianBean technicianDetail;
    private OnTechnicianTipListener mListener;
    private TextView selectedTipTextView;
    private String jobId;
    private int orderId;

    public TipDialogFragment() {}

    public static TipDialogFragment newInstance(String jobId, int orderId, OrderHistoryModel.TechnicianBean technicianDetail) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.JOB_ID, jobId);
        args.putSerializable(Constants.ORDER_ID, orderId);
        args.putSerializable(Constants.INVOICE_MODEL, technicianDetail);
        TipDialogFragment fragment = new TipDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tip_dialog, container, false);

        if(getArguments() != null) {
            technicianDetail = (OrderHistoryModel.TechnicianBean) getArguments().getSerializable(Constants.INVOICE_MODEL);
            jobId = getArguments().getString(Constants.JOB_ID, "");
            orderId = getArguments().getInt(Constants.ORDER_ID, -1);
        }

        if(technicianDetail != null) {
            Utils.loadPicassoImage(technicianDetail.getImage(), binding.imageUser, null, R.drawable.app_logo);
            binding.textUserName.setText(technicianDetail.getName());
            binding.ratingUser.setRating(technicianDetail.getStar());
            binding.textRatingAverage.setText(String.valueOf(technicianDetail.getRating()));
            binding.textRatingTotal.setText("(" + technicianDetail.getRating_label() + ")");
        }

        binding.tvDollar10.setOnClickListener(this);
        binding.tvDollar20.setOnClickListener(this);
        binding.tvDollar30.setOnClickListener(this);
        binding.buttonPay.setOnClickListener(this);
        binding.tvSkip.setOnClickListener(this);
        binding.etCustom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (!s.toString().contains("$"))
                        setText(binding.etCustom, "$" + s);
                }

                if (s.length() == 1) {
                    if (s.toString().contains("$"))
                        setText(binding.etCustom, "");
                } else if (s.length() > 1) {
                    setSelected(binding.etCustom);
                } else {
                    if (selectedTipTextView != null && selectedTipTextView instanceof EditText) {
                        selectedTipTextView.setSelected(false);
                        binding.buttonPay.setEnabled(false);
                    }
                }
            }
        });
        return binding.getRoot();
    }

    private void updatePayButton() {
        binding.buttonPay.setEnabled(!getTipAmount().isEmpty() && Integer.parseInt(getTipAmount()) > 0);
    }

    private void setText(EditText editText, String text) {
        editText.setText(text);
        editText.setSelection(text.length());
    }

    private OnCardSelectListener onCardSelectListener = this::callTechnicianTipApi;

    private String getTipAmount() {
        if (selectedTipTextView != null) {
            return selectedTipTextView.getText().toString().replace("$", "");
        }

        return "";
    }

    private void callTechnicianTipApi(String cardId) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setJob_id(jobId);
        apiRequest.setTip_amount(getTipAmount());
        apiRequest.setUser_card_id(cardId);

        ServiceManager.callServerApi(getActivity(), ServiceManager.API_TIP_TECHNICIAN, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    Toast.makeText(getActivity(), "Thank You!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();
                }

                if (mListener != null)
                    mListener.onTechnicianTip(Double.parseDouble(getTipAmount()));

                dismiss();
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(getActivity(), failureMessage, Toast.LENGTH_SHORT).show();
            }
        }), apiRequest);
    }

    private void callSkipTipApi() {
        ServiceManager.callServerApi(getActivity(), ServiceManager.API_CUSTOMER_SKIP_TIP, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
//                Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onApiFailure(String failureMessage) {

            }
        }), orderId, false);

        dismiss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (mListener != null)
            mListener.onTechnicianTip(0);

        super.onDismiss(dialog);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_pay:
                new PaymentInvoiceDialogFragment(onCardSelectListener).show(getChildFragmentManager(), "");
                break;

            case R.id.tv_dollar_10:
                setSelected((TextView) v);
                binding.etCustom.setText("");
                binding.etCustom.clearFocus();
                Utils.hideKeyboard(getActivity(), binding.etCustom);
                break;
            case R.id.tv_dollar_20:
                setSelected((TextView) v);
                binding.etCustom.setText("");
                binding.etCustom.clearFocus();
                Utils.hideKeyboard(getActivity(), binding.etCustom);
                break;
            case R.id.tv_dollar_30:
                setSelected((TextView) v);
                binding.etCustom.setText("");
                binding.etCustom.clearFocus();
                Utils.hideKeyboard(getActivity(), binding.etCustom);
                break;
            case R.id.tv_skip:
                callSkipTipApi();
                break;
        }
    }

    private void setSelected(TextView textView) {
        if (selectedTipTextView != null)
            selectedTipTextView.setSelected(false);

        selectedTipTextView = textView;
        selectedTipTextView.setSelected(true);
        updatePayButton();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnTechnicianTipListener) {
            mListener = (OnTechnicianTipListener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface OnTechnicianTipListener {
        void onTechnicianTip(double amount);
    }
}
