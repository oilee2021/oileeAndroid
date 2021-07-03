package com.oileemobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.databinding.ItemNotificationListBinding;
import com.oileemobile.interfaces.RecyclerViewItemClickListener;
import com.oileemobile.models.CustomerNotificationModel;
import com.oileemobile.utils.Utils;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-16 01:59
 **/
public class CustomerNotificationAdapter extends RecyclerView.Adapter<CustomerNotificationAdapter.MyViewHolder> {

    private List<CustomerNotificationModel> dataList;
    private RecyclerViewItemClickListener mListener;

    public CustomerNotificationAdapter(List<CustomerNotificationModel> dataList, RecyclerViewItemClickListener listener) {
        this.dataList = dataList;
        this.mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemNotificationListBinding binding;

        public MyViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CustomerNotificationModel item = dataList.get(position);

        holder.binding.textNotification.setText(item.getTitle());
        holder.binding.textNotificationDate.setText(Utils.getDateFromFormat(item.getTimestamp(), "yyyy-MM-dd hh:mm:ss"));
        holder.binding.textNotificationDescription.setText(item.getDescription());
        holder.binding.textNotificationDescription.setVisibility(View.VISIBLE);

        holder.binding.mainView.setCardBackgroundColor(item.isIs_read() ? getColor(holder.itemView.getContext(), R.color.lightGrey)
                : Color.WHITE);
        holder.binding.newNotificationDot.setVisibility(item.isIs_read() ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private int getColor(Context context, @ColorRes int colorId) {
        return ContextCompat.getColor(context, colorId);
    }
}
