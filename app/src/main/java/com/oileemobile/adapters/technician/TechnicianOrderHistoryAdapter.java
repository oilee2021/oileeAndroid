package com.oileemobile.adapters.technician;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.databinding.ItemTechnicianJobsBinding;
import com.oileemobile.interfaces.RecyclerViewItemClickListener;
import com.oileemobile.models.technician.TechnicianOrderHistoryModel;
import com.oileemobile.utils.Utils;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-19 00:54
 **/
public class TechnicianOrderHistoryAdapter extends RecyclerView.Adapter<TechnicianOrderHistoryAdapter.MyViewHolder> {
    private List<TechnicianOrderHistoryModel> items;
    private RecyclerViewItemClickListener mListener;

    public TechnicianOrderHistoryAdapter(List<TechnicianOrderHistoryModel> items, RecyclerViewItemClickListener clickListener) {
        this.items = items;
        this.mListener = clickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemTechnicianJobsBinding binding;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_technician_jobs, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TechnicianOrderHistoryModel item = items.get(position);

        holder.binding.textUserName.setText(item.getCustomer().getName());
        Utils.loadPicassoImage(item.getCustomer().getAvatar(), holder.binding.imageUser, null);
        holder.binding.textAppointmentTime.setText(item.getAppointment_time());
        holder.binding.textCarDetails.setText(item.getVehicle().getYear() + " - " + item.getVehicle().getMake());
        holder.binding.textJobLocation.setText(item.getCustomer().getAddress());
    }

    private void setText(TextView textView, String text) {
        textView.setText(text);
        textView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<TechnicianOrderHistoryModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}
