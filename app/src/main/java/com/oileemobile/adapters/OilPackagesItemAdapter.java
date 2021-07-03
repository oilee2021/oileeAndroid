package com.oileemobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.databinding.ItemOilPackageLayoutBinding;
import com.oileemobile.interfaces.OnOilPackageSelectListener;
import com.oileemobile.models.OilPackageModel;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 01/11/20 10:45 PM
 **/
public class OilPackagesItemAdapter extends RecyclerView.Adapter<OilPackagesItemAdapter.MyViewHolder> {
    private List<OilPackageModel.PackagesBean> oilPackages;
    private int selectedItemPosition;
    private OnOilPackageSelectListener mListener;

    public OilPackagesItemAdapter(int selectedItemPosition, List<OilPackageModel.PackagesBean> oilPackages, OnOilPackageSelectListener mListener) {
        this.selectedItemPosition = selectedItemPosition;
        this.oilPackages = oilPackages;
        this.mListener = mListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ItemOilPackageLayoutBinding binding;

        MyViewHolder(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                OilPackageModel.PackagesBean packagesBean = oilPackages.get(getAdapterPosition());
                if (!packagesBean.isSelected())
                    mListener.onOilPackageSelected(selectedItemPosition, getAdapterPosition());
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_oil_package_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OilPackageModel.PackagesBean packageBean = oilPackages.get(position);

        holder.binding.oilPackPrice.setText(packageBean.getPrice());
        holder.binding.oilPackType.setText(packageBean.getOil_type());

        holder.binding.oilPackTick.setImageResource(packageBean.isSelected() ? R.drawable.ic_tick : R.drawable.ic_untick);
    }

    @Override
    public int getItemCount() {
        return oilPackages.size();
    }
}
