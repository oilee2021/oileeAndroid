package com.oileemobile.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;

import com.oileemobile.R;
import com.oileemobile.databinding.ActivityTermsBinding;
import com.oileemobile.models.TermsModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.TermsResponse;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.Utils;

public class TermsActivity extends BaseActivity implements View.OnClickListener {
    private ActivityTermsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_terms);
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);

        binding.buttonAgree.setOnClickListener(this);
        boolean isTermsAgreed = getIntent().getBooleanExtra(Constants.IS_TERMS_AGREED, false);
        binding.buttonAgree.setEnabled(isTermsAgreed);

        binding.scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            View view = binding.scrollView.getChildAt(0);
            int diff = (view.getBottom() + binding.scrollView.getPaddingBottom() - (binding.scrollView.getHeight() + binding.scrollView.getScrollY()));
            if (diff == 0) {
                binding.buttonAgree.setEnabled(true);
            }
        });
        callTermsApi();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_agree) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void callTermsApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_TERMS, new ApiCallBack<>(new ApiResponseListener<TermsResponse>() {
            @Override
            public void onApiSuccess(TermsResponse response) {
                if(response.getStatus() == 200) {
                    if (response.getData().size() > 0) {
                        TermsModel termsModel = response.getData().get(0);
                        if (termsModel != null) {
                            if (PrefManager.getInt(PrefManager.USER_TYPE, Constants.TYPE_CUSTOMER) == Constants.TYPE_CUSTOMER) {
                                binding.textTerms.setText(Html.fromHtml(termsModel.getCustomerDescription()));
                            } else {
                                binding.textTerms.setText(Html.fromHtml(termsModel.getTechnicianDescription()));
                            }
                        }
                    }
                }

                View child = binding.scrollView.getChildAt(0);
                child.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                    int childHeight = child.getHeight();
                    if(binding.scrollView.getHeight() >= childHeight + binding.scrollView.getPaddingTop()) {
                        binding.buttonAgree.setEnabled(true);
                    }
                });
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(TermsActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        }), true);
    }
}
