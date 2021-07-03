package com.oileemobile.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;

import com.oileemobile.R;
import com.oileemobile.databinding.ActivityQueryDetailBinding;
import com.oileemobile.models.SupportQueryModel;
import com.oileemobile.utils.Utils;

public class QueryDetailActivity extends BaseActivity {
    private ActivityQueryDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_query_detail);
        setupToolbar();

        SupportQueryModel model = (SupportQueryModel) getIntent().getSerializableExtra("support_model");

        binding.viewQueryDetail.textSupportDate.setText(Utils.getDateFromFormat(model.getQuery_date(), "MM-dd-yyyy"));
        binding.viewQueryDetail.textSupportQuery.setText(model.getBody());

        if(model.getStatus().equalsIgnoreCase("resolved")) {
            binding.viewQueryDetail.textSupportStatus.setText(R.string.resolved);
            binding.viewQueryDetail.textSupportStatus.setBackgroundResource(R.drawable.background_support_resolved);
        } else {
            binding.viewQueryDetail.textSupportStatus.setText(R.string.pending);
            binding.viewQueryDetail.textSupportStatus.setBackgroundResource(R.drawable.background_support_pending);
        }

        setImage(model.getFile1(), binding.viewQueryDetail.image1, binding.viewQueryDetail.linearImages);
        setImage(model.getFile2(), binding.viewQueryDetail.image2, binding.viewQueryDetail.linearImages);
        setImage(model.getFile3(), binding.viewQueryDetail.image3, binding.viewQueryDetail.linearImages);

        if(!TextUtils.isEmpty(model.getResponse())) {
            binding.linearReplySection.setVisibility(View.VISIBLE);
            binding.textQueryReply.setText(model.getResponse());
        }
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.query_detail);
    }

    private void setImage(String image, ImageView imageView, View view) {
        if(!TextUtils.isEmpty(image)) {
            view.setVisibility(View.VISIBLE);
            Utils.loadPicassoImage(image, imageView, null);
        }
    }
}
