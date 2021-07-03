package com.oileemobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.databinding.ItemCouponLlistBinding;
import com.oileemobile.interfaces.OnCouponSelectedListener;
import com.oileemobile.models.technician.CouponModel;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-12-14 04:36
 **/
public class CouponListAdapter extends RecyclerView.Adapter<CouponListAdapter.MyViewHolder> {

    private List<CouponModel> couponList;
    private int selectedItem;
    private OnCouponSelectedListener mListener;

    public CouponListAdapter(List<CouponModel> couponList, OnCouponSelectedListener listener) {
        this.couponList = couponList;
        selectedItem = -1;
        this.mListener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemCouponLlistBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
            binding.textApply.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.text_apply) {
                CouponModel item = couponList.get(getAdapterPosition());
                if(!item.isSelected()) {
                    if(mListener != null) mListener.onCouponSelected(getAdapterPosition());
                } else {
                    if(mListener != null) mListener.onCouponRemoved();
                }
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon_llist, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CouponModel item = couponList.get(position);
        holder.binding.textCouponCode.setText(item.getCode());
        holder.binding.textCouponDetail.setText(item.getDiscount());
        if(item.isSelected()) {
            holder.binding.textApply.setText(R.string.remove);
            holder.binding.mainView.setBackgroundResource(R.drawable.round_red_stroke_background);
        } else {
            holder.binding.textApply.setText(R.string.apply);
            holder.binding.mainView.setBackgroundResource(0);
        }
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public void selectItem(int position) {
        if (selectedItem != -1) {
            CouponModel couponModel = couponList.get(selectedItem);
            couponModel.setSelected(false);
            notifyItemChanged(selectedItem);
            selectedItem = -1;
        }

        if (position != -1) {
            CouponModel couponModel = couponList.get(position);
            couponModel.setSelected(true);
            notifyItemChanged(position);
            selectedItem = position;
        }
    }

    public int getSelectedCouponId() {
        return selectedItem != -1 ? couponList.get(selectedItem).getId() : -1;
    }

    public CouponModel getSelectedCoupon() {
        if (selectedItem != -1) {
            return couponList.get(selectedItem);
        }

        return null;
    }
}
