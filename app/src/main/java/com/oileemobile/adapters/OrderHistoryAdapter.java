package com.oileemobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.databinding.ItemOrderHistoryBinding;
import com.oileemobile.interfaces.RecyclerViewItemClickListener;
import com.oileemobile.models.OrderHistoryModel;
import com.oileemobile.utils.Utils;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-19 00:54
 **/
public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolder> {
    private List<OrderHistoryModel> items;
    private RecyclerViewItemClickListener mListener;

    public OrderHistoryAdapter(List<OrderHistoryModel> items, RecyclerViewItemClickListener clickListener) {
        this.items = items;
        this.mListener = clickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemOrderHistoryBinding binding;

        MyViewHolder(View itemView) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OrderHistoryModel item = items.get(position);
        ItemOrderHistoryBinding binding = holder.binding;
        Utils.loadPicassoImage(item.getTechnician().getImage(), binding.imageUser, null);
        binding.textOrderId.setText(String.valueOf(item.getId()));
        binding.textUserName.setText(item.getTechnician().getName());
        binding.textPrice.setText(holder.itemView.getContext().getString(R.string.item_price, item.getPaid_amount()));
        binding.textOrderDate.setText(item.getDate());
        binding.textOrderServices.setText("Oil Change: " + item.getPackageX().getCategory_name());
        String serviceDate = item.getService().getDate() + " | " + item.getService().getTime();
        binding.textOrderServiceDate.setText(serviceDate);
        if(item.getTechnician().getId() == 0) {
            binding.ratingUser.setVisibility(View.GONE);
            binding.textRatingAverage.setVisibility(View.GONE);
            binding.textRatingTotal.setVisibility(View.GONE);
            binding.textTechnicianVehicle.setVisibility(View.GONE);
        } else {
            binding.ratingUser.setVisibility(View.VISIBLE);
            binding.ratingUser.setRating(item.getTechnician().getStar());
            setText(binding.textRatingAverage, String.valueOf(item.getTechnician().getRating()));
            setText(binding.textRatingTotal, "(" + item.getTechnician().getRating_label() + ")");
            setText(binding.textTechnicianVehicle, item.getTechnician().getVehicle().toString());
        }
    }

    private void setText(TextView textView, String text) {
        textView.setText(text);
        textView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<OrderHistoryModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}
