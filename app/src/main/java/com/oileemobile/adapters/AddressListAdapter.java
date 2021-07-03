package com.oileemobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.databinding.AddressRowBinding;
import com.oileemobile.models.AddressModel;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-09-02 02:02
 **/
public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.MyViewHolder> {

    private List<AddressModel> dataList;
    private OnAddressSelectedListener mListener;

    public AddressListAdapter(List<AddressModel> dataList, OnAddressSelectedListener listener) {
        this.dataList = dataList;
        this.mListener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AddressRowBinding binding;

        MyViewHolder(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v == itemView) {
                if(mListener != null)
                    mListener.onAddressSelected(getAdapterPosition());
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AddressModel addressModel = dataList.get(position);
        holder.binding.addressType.setText(addressModel.getTag());
        holder.binding.addressDesc.setText(addressModel.getFullAddress());
        if(addressModel.isIsDefault()) {
            holder.itemView.setBackgroundResource(R.drawable.background_default_item);
            holder.binding.textDefault.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setBackgroundResource(0);
            holder.binding.textDefault.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface OnAddressSelectedListener {
        void onAddressSelected(int position);
    }
}
