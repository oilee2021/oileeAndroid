package com.oileemobile.adapters.technician;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.databinding.ItemTechnicianJobsBinding;
import com.oileemobile.interfaces.RecyclerViewItemClickListener;
import com.oileemobile.models.technician.TechnicianJobModel;
import com.oileemobile.utils.Utils;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-11-17 14:54
 **/
public class TechnicianJobAdapter extends RecyclerView.Adapter<TechnicianJobAdapter.MyViewHolder> {
    private List<TechnicianJobModel> data;
    private RecyclerViewItemClickListener itemClickListener;

    public TechnicianJobAdapter(List<TechnicianJobModel> data, RecyclerViewItemClickListener itemClickListener) {
        this.data = data;
        this.itemClickListener = itemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemTechnicianJobsBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(itemClickListener != null) {
                itemClickListener.onItemClick(getAdapterPosition(), v);
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
        TechnicianJobModel item = data.get(position);

        holder.binding.textUserName.setText(item.getCustomer_name());
        Utils.loadPicassoImage(item.getCustomer_avatar(), holder.binding.imageUser, null);
        holder.binding.textAppointmentTime.setText(holder.itemView.getContext().getString(R.string.appointment_time, item.getService().getDate(),
                item.getAppointment_time()));
        holder.binding.textCarDetails.setText(item.getVehicle_year() + " - " + item.getVehicle_make());
        holder.binding.textJobLocation.setText(item.getCustomer_address());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
