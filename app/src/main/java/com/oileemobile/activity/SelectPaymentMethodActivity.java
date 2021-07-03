package com.oileemobile.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.transition.TransitionManager;

import com.oileemobile.dialogs.ConfirmationDialog;
import com.oileemobile.network.response.UserAddAddressResponse;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.oileemobile.R;
import com.oileemobile.adapters.CardListAdapter;
import com.oileemobile.adapters.CouponListAdapter;
import com.oileemobile.databinding.ActivitySelectPaymentMethodBinding;
import com.oileemobile.databinding.DialogAddNewCardBinding;
import com.oileemobile.interfaces.OnCouponSelectedListener;
import com.oileemobile.models.AddressModel;
import com.oileemobile.models.CardModel;
import com.oileemobile.models.TimeSlot;
import com.oileemobile.models.VehicleModel;
import com.oileemobile.models.technician.CouponModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiRequest;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.ApiResponse;
import com.oileemobile.network.response.CardListResponse;
import com.oileemobile.network.response.technician.CouponResponse;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.ProgressDialog;
import com.oileemobile.utils.Utils;

public class SelectPaymentMethodActivity extends BaseActivity
        implements View.OnClickListener, OnCouponSelectedListener {

    private ActivitySelectPaymentMethodBinding binding;
    private int selectedCard;
    private List<CardModel> cardList;
    private CardListAdapter cardListAdapter;
    private List<CouponModel> couponList;
    private CouponListAdapter mCouponAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_payment_method);

        AddressModel addressModel = (AddressModel) getIntent().getSerializableExtra("address");
        TimeSlot time = (TimeSlot) getIntent().getSerializableExtra("time");
        Date date = (Date) getIntent().getSerializableExtra("date");
        double packagePrice = getIntent().getDoubleExtra("package_price", 0.0);
        VehicleModel vehicleModel = (VehicleModel) getIntent().getSerializableExtra("vehicle");
        selectedCard = -1;

        setupToolbar();
        setupSelectedVehicle(vehicleModel);
        binding.summaryAddress.setText(addressModel.getFullAddress());
        binding.summaryTime.setText(time.getTime12Hr());
        binding.summaryDate.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date));
        binding.paySubmit.setOnClickListener(this);
        binding.paymentAddMore.getRoot().setOnClickListener(this);

        binding.paymentAddMore.addMoreText.setText(R.string.add_new_payment_method);
        binding.summaryEditDetails.setText(getString(R.string.item_price, packagePrice));

        cardList = new ArrayList<>();
        cardListAdapter = new CardListAdapter(cardList, this::setCardSelected,this::setDeleteCard);
        binding.recyclerCardList.setAdapter(cardListAdapter);
        setupDiscountRecyclerView();
        callGetCouponApi();
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.service_confirmation);
    }

    private void setupSelectedVehicle(VehicleModel vehicleModel) {
        if(vehicleModel != null) {
            binding.summaryVehicle.setText(vehicleModel.getVehicleDetails());
        }
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

    private void setCardUnselected(int position) {
        CardListAdapter.MyViewHolder viewHolder = (CardListAdapter.MyViewHolder) binding.recyclerCardList.findViewHolderForAdapterPosition(position);
        if(viewHolder != null) {
            View view = viewHolder.itemView;
            TransitionManager.beginDelayedTransition(binding.mainView);
            view.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
            ((ImageView) view.findViewById(R.id.image_tick)).setImageResource(R.drawable.ic_untick);
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

        ServiceManager.callServerApiTwo(SelectPaymentMethodActivity.this, ServiceManager.API_CUSTOMER_DELETE_CARD, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getMessage().contains("Card deleted successfully")) {
                    Toast.makeText(SelectPaymentMethodActivity.this,response.getMessage(), Toast.LENGTH_SHORT).show();
                    callGetCardsApi();
                } else {
                    Toast.makeText(SelectPaymentMethodActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), cardList.get(positon).getId());
    }


    private void setupDiscountRecyclerView() {
        couponList = new ArrayList<>();
        binding.recyclerCoupons.setItemAnimator(null);
        mCouponAdapter = new CouponListAdapter(couponList, this);
        binding.recyclerCoupons.setAdapter(mCouponAdapter);
    }

    @Override
    public void onCouponSelected(int position) {
        mCouponAdapter.selectItem(position);
    }

    @Override
    public void onCouponRemoved() {
        mCouponAdapter.selectItem(-1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_submit:
                openOrderSummary(cardList.get(selectedCard));
                break;

            case R.id.payment_add_more:
                getAddCardDialog().show();
                break;
        }
    }

    private void openOrderSummary(CardModel cardModel) {
        Bundle bundle = getIntent().getExtras();
        bundle.putParcelable("discount_coupon", mCouponAdapter.getSelectedCoupon());
        bundle.putString("card_token", cardModel.getId());
        bundle.putBoolean("is_new_card", cardModel.isNewCard());
        ActivityController.startActivity(this, OrderSummaryActivity.class, bundle);
    }

    /*private void callApplyCouponApi(int position) {
        CouponModel couponModel = couponList.get(position);
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setCoupon_code(couponModel.getCode());

        ServiceManager.callServerApi(this, ServiceManager.API_APPLY_COUPON, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    mCouponAdapter.selectItem(position);
                } else {
                    Utils.showSnackbar(SelectPaymentMethodActivity.this, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(SelectPaymentMethodActivity.this, failureMessage);
            }
        }), apiRequest);
    }*/

    private void callGetCouponApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_COUPONS, new ApiCallBack<>(new ApiResponseListener<CouponResponse>() {
            @Override
            public void onApiSuccess(CouponResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    TransitionManager.beginDelayedTransition(binding.mainView);
                    couponList.addAll(response.getData());
                    if(couponList.size() > 0) {
                        binding.textCouponTitle.setVisibility(View.VISIBLE);
                        binding.recyclerCoupons.setVisibility(View.VISIBLE);
                        mCouponAdapter.notifyDataSetChanged();
                    } else {
                        binding.recyclerCoupons.setVisibility(View.GONE);
                        binding.textCouponTitle.setVisibility(View.GONE);
                    }
                    callGetCardsApi();
                } else {
                    callGetCardsApi();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(SelectPaymentMethodActivity.this, failureMessage);
            }
        }), true);
    }

    private void callGetCardsApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_CARD_LIST, new ApiCallBack<>(new ApiResponseListener<CardListResponse>() {
            @Override
            public void onApiSuccess(CardListResponse response) {
                ProgressDialog.hideProgressDialog();
                if(response.getData() != null) {
                    cardList = response.getData();
                    if(cardList.size() > 0) {
                        TransitionManager.beginDelayedTransition(binding.mainView);
                        cardListAdapter = new CardListAdapter(cardList, SelectPaymentMethodActivity.this::setCardSelected,SelectPaymentMethodActivity.this::setDeleteCard);
                        binding.recyclerCardList.setAdapter(cardListAdapter);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.recyclerCardList.getLayoutParams();
                        if(cardList.size() > 2) {
                            layoutParams.height = Utils.dpToPx(SelectPaymentMethodActivity.this, 200);
                        } else {
                            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        }
                        binding.recyclerCardList.setLayoutParams(layoutParams);
                    }
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                ProgressDialog.hideProgressDialog();
                Toast.makeText(SelectPaymentMethodActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SelectPaymentMethodActivity.this, R.string.card_added, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(SelectPaymentMethodActivity.this, failureMessage);
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
            Utils.hideKeyboard(SelectPaymentMethodActivity.this, view);
            Card card = cardBinding.cardWidget.getCard();
            if(card != null) {
                if(card.validateCard()) {
                    ProgressDialog.showProgressDialog(SelectPaymentMethodActivity.this);
                    Stripe stripe = new Stripe(SelectPaymentMethodActivity.this, getString(R.string.stripe_publishable_key));
                    stripe.createCardToken(card, new ApiResultCallback<Token>() {
                        @Override
                        public void onSuccess(Token token) {
                            binding.paySubmit.setEnabled(false);
                            callAddCardApi(token.getId());
                        }

                        @Override
                        public void onError(@NotNull Exception e) {
                            ProgressDialog.hideProgressDialog();
                            e.printStackTrace();
                            Toast.makeText(SelectPaymentMethodActivity.this, "Exception occurred!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(SelectPaymentMethodActivity.this, "Validation failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return alertDialog;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
