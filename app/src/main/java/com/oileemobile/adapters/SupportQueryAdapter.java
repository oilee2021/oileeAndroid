package com.oileemobile.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.databinding.ItemQueryListingBinding;
import com.oileemobile.interfaces.RecyclerViewItemClickListener;
import com.oileemobile.models.SupportQueryModel;
import com.oileemobile.utils.Utils;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-31 21:05
 **/
public class SupportQueryAdapter extends RecyclerView.Adapter<SupportQueryAdapter.MyViewHolder> {
    private List<SupportQueryModel> items;
    private RecyclerViewItemClickListener mListener;

    public SupportQueryAdapter(List<SupportQueryModel> items, RecyclerViewItemClickListener clickListener) {
        this.items = items;
        this.mListener = clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemQueryListingBinding binding;

        public MyViewHolder(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                mListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_query_listing, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SupportQueryModel item = items.get(position);

        holder.binding.textSupportQuery.setText(item.getBody());
        holder.binding.textSupportDate.setText(Utils.getDateFromFormat(item.getQuery_date(), "MM-dd-yyyy"));
        setImage(item.getFile1(), holder.binding.image1, holder.binding.linearImages);
        setImage(item.getFile2(), holder.binding.image2, holder.binding.linearImages);
        setImage(item.getFile3(), holder.binding.image3, holder.binding.linearImages);

        if(item.getStatus().equalsIgnoreCase("resolved")) {
            holder.binding.textSupportStatus.setText(R.string.resolved);
            holder.binding.textSupportStatus.setBackgroundResource(R.drawable.background_support_resolved);
        } else {
            holder.binding.textSupportStatus.setText(R.string.pending);
            holder.binding.textSupportStatus.setBackgroundResource(R.drawable.background_support_pending);
        }
    }

    private void setImage(String image, ImageView imageView, View view) {
        if(!TextUtils.isEmpty(image)) {
            view.setVisibility(View.VISIBLE);
            Utils.loadPicassoImage(image, imageView, null);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
