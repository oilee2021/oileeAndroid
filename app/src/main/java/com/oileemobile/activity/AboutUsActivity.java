package com.oileemobile.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.transition.TransitionManager;

import com.oileemobile.R;
import com.oileemobile.databinding.AboutUsRowBinding;
import com.oileemobile.databinding.ActivityAboutUsBinding;
import com.oileemobile.models.AboutUsModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.AboutUsResponse;
import com.oileemobile.utils.Utils;

public class AboutUsActivity extends BaseActivity {

    private ActivityAboutUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_us);

        setupToolbar();
        callAboutUsApi();
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.about_us);
    }

    private void callAboutUsApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_ABOUT_US, new ApiCallBack<>(new ApiResponseListener<AboutUsResponse>() {
            @Override
            public void onApiSuccess(AboutUsResponse response) {
                if(response.getData() != null) {
                    if (response.getData().size() > 0) {
                        for (AboutUsModel aboutUs : response.getData()) {
                            addAboutUsSection(aboutUs.getTitle(), aboutUs.getDescription());
                            /*Utils.loadPicassoImage(aboutUsModel.getAboutImage(), binding.ivAboutus, binding.progressBar);
                            binding.tvAboutus.setText(aboutUsModel.getAboutDescription());
                            addAboutUsSection("How We Started", aboutUsModel.getHowWeStarted());
                            addAboutUsSection("Our Mission", aboutUsModel.getOurMission());
                            addAboutUsSection("Our Vision", aboutUsModel.getOurVision());*/
                        }
                    }
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(AboutUsActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }), true);
    }

    private void addAboutUsSection(String title, String description) {
        View view = LayoutInflater.from(this).inflate(R.layout.about_us_row, null, false);
        AboutUsRowBinding rowBinding = DataBindingUtil.bind(view);

        rowBinding.tvAboutUsTitle.setText(title);
        rowBinding.tvAboutUsDescription.setText(HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT));

        rowBinding.rlAboutUsHeader.setOnClickListener(v -> {
            if(rowBinding.tvAboutUsDescription.getVisibility() != View.VISIBLE) {
                rowBinding.ivSeeMore.animate().rotation(45).start();
                TransitionManager.beginDelayedTransition(binding.mainView);
                rowBinding.tvAboutUsDescription.setVisibility(View.VISIBLE);
            } else {
                rowBinding.ivSeeMore.animate().rotation(0).start();
                TransitionManager.beginDelayedTransition(binding.mainView);
                rowBinding.tvAboutUsDescription.setVisibility(View.GONE);
            }
        });

        binding.llAboutUsSection.addView(view);
    }
}
