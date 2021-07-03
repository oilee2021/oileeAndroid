package com.oileemobile.activity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import com.oileemobile.R;
import com.oileemobile.databinding.ActivityGettingStartedBinding;
import com.oileemobile.fragment.IntroFragment;
import com.oileemobile.models.IntroSliderModel;
import com.oileemobile.utils.ActivityController;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.Utils;

public class GettingStartedActivity extends BaseActivity implements View.OnClickListener {
    private ActivityGettingStartedBinding binding;
    private View[] indicators;
    private int selectedPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_getting_started);

        List<IntroSliderModel> dataList = new ArrayList<>();
        if(PrefManager.getInt(PrefManager.USER_TYPE, Constants.TYPE_CUSTOMER) == Constants.TYPE_CUSTOMER) {
            dataList.add(new IntroSliderModel(R.drawable.customer_intro_image_1, R.string.customer_intro_title_1, R.string.customer_intro_description_1));
            dataList.add(new IntroSliderModel(R.drawable.customer_intro_image_2, R.string.customer_intro_title_2, R.string.customer_intro_description_2));
            dataList.add(new IntroSliderModel(R.drawable.customer_intro_image_3, R.string.customer_intro_title_3, R.string.customer_intro_description_3));
            dataList.add(new IntroSliderModel(R.drawable.customer_intro_image_4, R.string.customer_intro_title_4, R.string.customer_intro_description_4));
            dataList.add(new IntroSliderModel(R.drawable.customer_intro_image_5, R.string.customer_intro_title_5, R.string.customer_intro_description_5));
        } else {
            dataList.add(new IntroSliderModel(R.drawable.technician_intro_image_1, R.string.technician_intro_title_1, R.string.technician_intro_description_1));
            dataList.add(new IntroSliderModel(R.drawable.technician_intro_image_2, R.string.technician_intro_title_2, R.string.technician_intro_description_2));
            dataList.add(new IntroSliderModel(R.drawable.technician_intro_image_3, R.string.technician_intro_title_3, R.string.technician_intro_description_3));
            dataList.add(new IntroSliderModel(R.drawable.technician_intro_image_4, R.string.technician_intro_title_4, R.string.technician_intro_description_4));
        }

        setupIndicators(dataList.size());

        binding.startedSubmit.setOnClickListener(this);
        binding.textIntroNext.setOnClickListener(this);
        binding.textIntroSkip.setOnClickListener(this);
        Utils.enableBackButton(this, binding.myToolbar.toolbarBack);
        binding.myToolbar.toolbarTitle.setText(R.string.back);
        binding.myToolbar.toolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.lightBlack));
        binding.myToolbar.toolbarBack.setColorFilter(ContextCompat.getColor(this, R.color.lightBlack), PorterDuff.Mode.SRC_IN);

        binding.pagerIntro.setAdapter(new IntroPagerAdapter(getSupportFragmentManager(), dataList));

        binding.pagerIntro.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if(position == dataList.size() - 1) {
                    binding.textIntroSkip.setVisibility(View.INVISIBLE);
                    binding.textIntroNext.setVisibility(View.INVISIBLE);
                    binding.startedSubmit.setVisibility(View.VISIBLE);
                } else {
                    binding.textIntroSkip.setVisibility(View.VISIBLE);
                    binding.textIntroNext.setVisibility(View.VISIBLE);
                    binding.startedSubmit.setVisibility(View.INVISIBLE);
                }
                setIndicatorSelected(position, true);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.pagerIntro.setCurrentItem(0);
    }

    private void setupIndicators(int size) {
        selectedPage = 0;
        indicators = new View[size];
        for(int i = 0; i < size; i++) {
            indicators[i] = LayoutInflater.from(this).inflate(R.layout.item_indicator_view_pager, null);
            LinearLayout.LayoutParams params;
            if(i == 0) {
                indicators[i].setBackgroundResource(R.drawable.background_indicator_pager_active);
                params = new LinearLayout.LayoutParams(Utils.dpToPx(this, 5), Utils.dpToPx(this, 15));
            } else {
                indicators[i].setBackgroundResource(R.drawable.background_indicator_pager_inactive);
                params = new LinearLayout.LayoutParams(Utils.dpToPx(this, 5), Utils.dpToPx(this, 7));
            }
            params.setMarginStart(Utils.dpToPx(this, 7));
            binding.indicatorViewPager.addView(indicators[i], params);
        }
    }

    private void setIndicatorSelected(int position, boolean setSelected) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicators[position].getLayoutParams();
        TransitionManager.beginDelayedTransition(binding.mainView);
        if(setSelected) {
            if(selectedPage != -1) {
                setIndicatorSelected(selectedPage, false);
            }
            indicators[position].setBackgroundResource(R.drawable.background_indicator_pager_active);
            params.height = Utils.dpToPx(this, 15);
            selectedPage = position;
        } else {
            indicators[position].setBackgroundResource(R.drawable.background_indicator_pager_inactive);
            params.height = Utils.dpToPx(this, 7);
        }
        indicators[position].setLayoutParams(params);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.started_submit:
            case R.id.text_intro_skip:
                PrefManager.putBoolean(PrefManager.INTRO_SKIPPED, true);
                ActivityController.startActivity(this, PhoneNumberActivity.class, true);
                break;

            case R.id.text_intro_next:
                binding.pagerIntro.setCurrentItem(binding.pagerIntro.getCurrentItem() + 1);
                break;
        }
    }

    private class IntroPagerAdapter extends FragmentPagerAdapter {
        private List<IntroSliderModel> dataList;

        IntroPagerAdapter(FragmentManager fragmentManager, List<IntroSliderModel> dataList) {
            super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return IntroFragment.newInstance(dataList.get(position));
        }

        @Override
        public int getCount() {
            return dataList.size();
        }
    }
}
