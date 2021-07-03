package com.oileemobile.dialogs;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.oileemobile.activity.MySavedPaymentActivity;
import com.oileemobile.network.response.UserAddAddressResponse;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.adapters.CardListAdapter;
import com.oileemobile.databinding.DialogAddNewCardBinding;
import com.oileemobile.databinding.FragmentPaymentInvoiceDialogBinding;
import com.oileemobile.interfaces.OnCardSelectListener;
import com.oileemobile.models.CardModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiRequest;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.ApiResponse;
import com.oileemobile.network.response.CardListResponse;
import com.oileemobile.utils.ProgressDialog;
import com.oileemobile.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentInvoiceDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private FragmentPaymentInvoiceDialogBinding binding;
    private int selectedCard;
    private List<CardModel> cardList;
    private CardListAdapter cardListAdapter;
    private OnCardSelectListener mListener;

    public PaymentInvoiceDialogFragment(OnCardSelectListener onCardSelectListener) {
        this.mListener = onCardSelectListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_invoice_dialog, container, false);

        selectedCard = -1;
        binding.paySubmit.setOnClickListener(this);
        binding.paymentAddMore.getRoot().setOnClickListener(this);
        callGetCardsApi();

        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_submit:
                dismiss();
                if(mListener != null) mListener.onCardSelect(cardList.get(selectedCard).getId());
                break;

            case R.id.payment_add_more:
                getAddCardDialog().show();
                break;
        }
    }

    private AlertDialog getAddCardDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_new_card, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        AlertDialog alertDialog = builder.create();

        DialogAddNewCardBinding cardBinding = DataBindingUtil.bind(view);
        cardBinding.buttonAddCard.setOnClickListener(v -> {
            Utils.hideKeyboard(getActivity(), view);
            Card card = cardBinding.cardWidget.getCard();
            if(card != null) {
                if(card.validateCard()) {
                    ProgressDialog.showProgressDialog(getActivity());
                    Stripe stripe = new Stripe(getActivity(), getString(R.string.stripe_publishable_key));
                    stripe.createToken(card, new ApiResultCallback<Token>() {
                        @Override
                        public void onSuccess(Token token) {
                            binding.paySubmit.setEnabled(false);
                            callAddCardApi(token.getId());
                        }

                        @Override
                        public void onError(@NotNull Exception e) {
                            ProgressDialog.hideProgressDialog();
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Exception occurred!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Validation failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return alertDialog;
    }

    private void setCardSelected(int position, View view) {
        if(!binding.paySubmit.isEnabled())
            binding.paySubmit.setEnabled(true);

        if (selectedCard != position) {
            TransitionManager.beginDelayedTransition((ViewGroup) view);
            view.setBackgroundResource(R.drawable.payment_method_select_background);
            ((ImageView) view.findViewById(R.id.image_tick)).setImageResource(R.drawable.ic_tick);
            if(selectedCard != -1) setCardUnselected(selectedCard);
            selectedCard = position;
        }
    }

    private void setDeleteCard(int position) {

        new ConfirmationDialog(R.string.delete, R.string.delete_confirmation, R.string.delete, R.string.cancel, which -> {
            if(which == DialogInterface.BUTTON_POSITIVE) {
                callDeleteCardApi(position);
            }
        }).show(getChildFragmentManager(), "");
    }

    private void callDeleteCardApi(int positon) {

        ServiceManager.callServerApiTwo(getActivity(), ServiceManager.API_CUSTOMER_DELETE_CARD, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getMessage().contains("Card deleted successfully")) {
                    Toast.makeText(getActivity(),response.getMessage(), Toast.LENGTH_SHORT).show();
                    callGetCardsApi();
                } else {
                    Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), cardList.get(positon).getId());
    }


    private void setCardUnselected(int position) {
        CardListAdapter.MyViewHolder viewHolder = (CardListAdapter.MyViewHolder) binding.recyclerCardList.findViewHolderForAdapterPosition(position);
        if(viewHolder != null) {
            View view = viewHolder.itemView;
            TransitionManager.beginDelayedTransition(binding.mainView);
            view.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.white));
            ((ImageView) view.findViewById(R.id.image_tick)).setImageResource(R.drawable.ic_untick);
        }
    }

    private void callAddCardApi(String token) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setToken_id(token);

        ServiceManager.callServerApi(getActivity(), ServiceManager.API_ADD_CUSTOMER_CARD, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    callGetCardsApi();
                    Toast.makeText(getActivity(), R.string.card_added, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(getActivity(), failureMessage);
            }
        }), apiRequest);
    }

    private void callGetCardsApi() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerCardList.setVisibility(View.GONE);
        binding.addCardSection.setVisibility(View.GONE);
        ServiceManager.callServerApi(getActivity(), ServiceManager.API_GET_CARD_LIST, new ApiCallBack<>(new ApiResponseListener<CardListResponse>() {
            @Override
            public void onApiSuccess(CardListResponse response) {
                binding.addCardSection.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                if(response.getData() != null) {
                    cardList = response.getData();
                    if(cardList.size() > 0) {
                        binding.recyclerCardList.setVisibility(View.VISIBLE);
                        TransitionManager.beginDelayedTransition(binding.mainView);
                        cardListAdapter = new CardListAdapter(cardList, PaymentInvoiceDialogFragment.this::setCardSelected,PaymentInvoiceDialogFragment.this::setDeleteCard);
                        binding.recyclerCardList.setAdapter(cardListAdapter);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.recyclerCardList.getLayoutParams();
                        if(cardList.size() > 2) {
                            layoutParams.height = Utils.dpToPx(getActivity(), 200);
                        } else {
                            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        }
                        binding.recyclerCardList.setLayoutParams(layoutParams);
                    }
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), false);
    }
}
