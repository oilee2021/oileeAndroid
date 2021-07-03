package com.oileemobile.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.oileemobile.R;
import com.oileemobile.databinding.FragmentIntroBinding;
import com.oileemobile.models.IntroSliderModel;
import com.oileemobile.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntroFragment extends Fragment {

    public IntroFragment() {
        // Required empty public constructor
    }

    public static IntroFragment newInstance(IntroSliderModel introSliderModel) {
        Bundle args = new Bundle();
        args.putSerializable("model", introSliderModel);
        IntroFragment fragment = new IntroFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentIntroBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_intro, container, false);

        Bundle bundle = getArguments();
        if(bundle != null) {
            IntroSliderModel model = (IntroSliderModel) bundle.getSerializable("model");
            if(model != null) {
                Utils.loadPicassoImage(model.getImageId(), binding.imageIntro, null);
                binding.textIntroHeader.setText(model.getTitle());
                binding.textIntroText.setText(model.getDescription());
            }
        }

        return binding.getRoot();
    }

}
