package com.oileemobile.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.oileemobile.dialogs.ConfirmationDialog;
import com.oileemobile.models.AddressModel;
import com.oileemobile.network.response.UserAddAddressResponse;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.adapters.CardListAdapter;
import com.oileemobile.databinding.ActivityMySavedPaymentBinding;
import com.oileemobile.databinding.DialogAddNewCardBinding;
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

public class MySavedPaymentActivity extends BaseActivity implements View.OnClickListener {
    private ActivityMySavedPaymentBinding binding;
    List<CardModel> cardList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_saved_payment);
        setupToolbar();
        callGetCardsApi();

        binding.paymentAddMore.getRoot().setOnClickListener(this);
        binding.paymentAddMore.addMoreText.setText(R.string.add_new_payment_method);
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.saved_payments);
        binding.myToolbar.getRoot().setBackgroundResource(R.drawable.main_background_gradient);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payment_add_more:
                getAddCardDialog().show();
                break;
        }
    }

    private void setDeleteCard(int position) {

        new ConfirmationDialog(R.string.delete, R.string.delete_confirmation, R.string.delete, R.string.cancel, which -> {
            if(which == DialogInterface.BUTTON_POSITIVE) {
                callDeleteCardApi(position);
            }
        }).show(getSupportFragmentManager(), "");
    }


    private void callDeleteCardApi(int positon) {

        ServiceManager.callServerApiTwo(MySavedPaymentActivity.this, ServiceManager.API_CUSTOMER_DELETE_CARD, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getMessage().contains("Card deleted successfully")) {
                    Toast.makeText(MySavedPaymentActivity.this,response.getMessage(), Toast.LENGTH_SHORT).show();
                    callGetCardsApi();
                } else {
                    Toast.makeText(MySavedPaymentActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), cardList.get(positon).getId());
    }

    private void callGetCardsApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_CARD_LIST, new ApiCallBack<>(new ApiResponseListener<CardListResponse>() {
            @Override
            public void onApiSuccess(CardListResponse response) {
                if(response.getData() != null) {
                    cardList = response.getData();
                    if(cardList.size() > 0) {
                        binding.rvCardList.setAdapter(new CardListAdapter(cardList, null,MySavedPaymentActivity.this::setDeleteCard));
                        binding.textNoResults.setVisibility(View.INVISIBLE);
                    } else {
                        binding.rvCardList.setVisibility(View.GONE);
                        binding.textNoResults.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(MySavedPaymentActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), true);
    }

    private void callAddCardApi(String token) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setToken_id(token);

        ServiceManager.callServerApi(this, ServiceManager.API_ADD_CUSTOMER_CARD, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    callGetCardsApi();
                    Toast.makeText(MySavedPaymentActivity.this, R.string.card_added, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(MySavedPaymentActivity.this, failureMessage);
            }
        }), apiRequest);
    }

    private AlertDialog getAddCardDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_new_card, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();

        DialogAddNewCardBinding cardBinding = DataBindingUtil.bind(view);
        cardBinding.buttonAddCard.setOnClickListener(v -> {
            Utils.hideKeyboard(this, view);
            Card card = cardBinding.cardWidget.getCard();
            if(card != null) {
                if(card.validateCard()) {
                    ProgressDialog.showProgressDialog(this);
                    Stripe stripe = new Stripe(this, getString(R.string.stripe_publishable_key));
                    stripe.createCardToken(card, new ApiResultCallback<Token>() {
                        @Override
                        public void onSuccess(Token token) {
                            callAddCardApi(token.getId());
                        }

                        @Override
                        public void onError(@NotNull Exception e) {
                            ProgressDialog.hideProgressDialog();
                            e.printStackTrace();
                            Toast.makeText(MySavedPaymentActivity.this, "Exception occurred!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(MySavedPaymentActivity.this, "Validation failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return alertDialog;
    }
}
