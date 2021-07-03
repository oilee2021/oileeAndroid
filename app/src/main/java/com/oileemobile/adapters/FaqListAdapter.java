package com.oileemobile.adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.databinding.FaqRowBinding;
import com.oileemobile.models.FaqModel;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-09-02 02:02
 **/
public class FaqListAdapter extends RecyclerView.Adapter<FaqListAdapter.MyViewHolder> {

    private List<FaqModel> dataList;
    private ViewGroup parentView;
    private FaqRowBinding itemOpen;

    public FaqListAdapter(List<FaqModel> dataList, ViewGroup parentView) {
        this.dataList = dataList;
        this.parentView = parentView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private FaqRowBinding binding;

        MyViewHolder(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);

            binding.faqHeaderSection.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.faq_header_section) {
                if(binding.faqDescriptionSection.getVisibility() != View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(parentView);
                    binding.faqIcon.animate().rotation(90);
                    binding.faqDescriptionSection.setVisibility(View.VISIBLE);
                    if(itemOpen != null) {
                        toggleItemOpen(false, itemOpen);
                    }
                    itemOpen = binding;
                    toggleItemOpen(true, binding);
                } else {
                    TransitionManager.beginDelayedTransition(parentView);
                    binding.faqIcon.animate().rotation(0);
                    binding.faqDescriptionSection.setVisibility(View.GONE);
                    itemOpen = null;
                }
            }
        }
    }

    private void toggleItemOpen(boolean open, FaqRowBinding binding) {
        if(open) {
            TransitionManager.beginDelayedTransition(parentView);
            binding.faqIcon.animate().rotation(90);
            binding.faqDescriptionSection.setVisibility(View.VISIBLE);
        } else {
            TransitionManager.beginDelayedTransition(parentView);
            binding.faqIcon.animate().rotation(0);
            binding.faqDescriptionSection.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FaqModel item = dataList.get(position);
        holder.binding.faqTitle.setText(item.getTitle());
        holder.binding.faqDescription.setText(Html.fromHtml(item.getDescription()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setDataList(List<FaqModel> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }
}
