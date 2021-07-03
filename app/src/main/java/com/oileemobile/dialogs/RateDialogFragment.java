package com.oileemobile.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.oileemobile.R;
import com.oileemobile.databinding.FragmentRateDialogBinding;
import com.oileemobile.interfaces.OnRatingSubmittedListener;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.PrefManager;
import com.oileemobile.utils.Utils;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-12-13 00:26
 **/
public class RateDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private FragmentRateDialogBinding binding;
    private String technicianName, technicianImage;
    private OnRatingSubmittedListener mListener;

    private RateDialogFragment() {}

    public static RateDialogFragment newInstance(String name, String image) {

        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("image", image);
        RateDialogFragment fragment = new RateDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rate_dialog, container, false);

        if(getArguments() != null) {
            technicianName = getArguments().getString("name");
            technicianImage = getArguments().getString("image");
        }
        binding.textTechnicianName.setText(technicianName);
        Utils.loadPicassoImage(technicianImage, binding.imageTechnician, null, R.drawable.app_logo);

        if(PrefManager.getInt(PrefManager.USER_TYPE, Constants.TYPE_CUSTOMER) == Constants.TYPE_CUSTOMER) {
            binding.textTitle.setText(R.string.rate_your_technician);
            binding.textSubtitle.setText(R.string.tell_us_about_experience);
        }

        binding.buttonSubmit.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_submit) {
            if(mListener != null) mListener.onRatingSubmitted(String.valueOf(binding.ratingTechnician.getRating()),
                    binding.editMoreDetails.getText().toString().trim());
            dismiss();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnRatingSubmittedListener) {
            mListener = (OnRatingSubmittedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
