package com.oileemobile.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import com.oileemobile.R;
import com.oileemobile.adapters.FaqListAdapter;
import com.oileemobile.databinding.ActivityFaqBinding;
import com.oileemobile.models.FaqModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.response.FaqResponse;
import com.oileemobile.utils.Utils;

public class FaqActivity extends BaseActivity {

    private ActivityFaqBinding binding;
    private List<FaqModel> popularList, searchList;
    private FaqListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_faq);

        setupToolbar();
        setupRecyclerViews();
        callGetFaqApi();

        binding.faqSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean hasData;
                if(s.length() == 0) {
                    adapter.setDataList(popularList);
                    hasData = popularList.size() != 0;
                } else {
                    searchList.clear();
                    for(FaqModel faqModel : popularList) {
                        if(faqModel.getTitle().toLowerCase().contains(s.toString().toLowerCase())
                                || faqModel.getDescription().toLowerCase().contains(s.toString().toLowerCase())) {
                            searchList.add(faqModel);
                        }
                    }
                    adapter.setDataList(searchList);
                    hasData = searchList.size() != 0;
                }

                binding.tvNoResults.setVisibility(hasData ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }

    private void setupToolbar() {
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.faqs);
    }

    private void setupRecyclerViews() {
        popularList = new ArrayList<>();
        searchList = new ArrayList<>();
        adapter = new FaqListAdapter(popularList, binding.mainView);
        binding.faqPopularList.setLayoutManager(new LinearLayoutManager(this));
        binding.faqPopularList.setAdapter(adapter);
    }

    private void callGetFaqApi() {
        ServiceManager.callServerApi(this, ServiceManager.API_GET_FAQ, new ApiCallBack<>(new ApiResponseListener<FaqResponse>() {
            @Override
            public void onApiSuccess(FaqResponse response) {
                popularList.clear();
                if(response.getData() != null) {
                    popularList.addAll(response.getData());
                    if(popularList.size() != 0) {
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(FaqActivity.this, "No FAQ found!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FaqActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Toast.makeText(FaqActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                finish();
            }
        }), true);
    }
}
