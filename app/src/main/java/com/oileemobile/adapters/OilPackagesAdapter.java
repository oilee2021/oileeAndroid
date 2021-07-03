package com.oileemobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.databinding.ItemOilPackageBinding;
import com.oileemobile.interfaces.OnOilPackageSelectListener;
import com.oileemobile.models.OilPackageModel;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-17 23:14
 **/
public class OilPackagesAdapter extends RecyclerView.Adapter<OilPackagesAdapter.MyViewHolder> {
    private List<OilPackageModel> items;
    private OnOilPackageSelectListener mListener;

    public OilPackagesAdapter(List<OilPackageModel> items, OnOilPackageSelectListener tickChangeListener) {
        this.items = items;
        this.mListener = tickChangeListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ItemOilPackageBinding binding;

        MyViewHolder(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_oil_package, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OilPackageModel item = items.get(position);
        holder.binding.oilPackName.setText(item.getPackage_category());

        OilPackagesItemAdapter itemAdapter = new OilPackagesItemAdapter(position, item.getPackages(), mListener);
        holder.binding.rvPackages.setItemAnimator(null);
        holder.binding.rvPackages.setAdapter(itemAdapter);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItem(int position) {
        notifyItemChanged(position);
    }
}
