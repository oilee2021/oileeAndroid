package com.oileemobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.databinding.ItemScheduleReminderBinding;
import com.oileemobile.interfaces.RecyclerViewItemClickListener;
import com.oileemobile.models.ScheduleReminderModel;
import com.oileemobile.models.VehicleModel;
import com.oileemobile.utils.Utils;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-28 22:31
 **/
public class ScheduleReminderAdapter extends RecyclerView.Adapter<ScheduleReminderAdapter.MyViewHolder> {
    private List<ScheduleReminderModel> data;
    private RecyclerViewItemClickListener itemClickListener;

    public ScheduleReminderAdapter(List<ScheduleReminderModel> data, RecyclerViewItemClickListener itemClickListener) {
        this.data = data;
        this.itemClickListener = itemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemScheduleReminderBinding binding;

        public MyViewHolder(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
            binding.scheduleDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(itemClickListener != null) itemClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_reminder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ScheduleReminderModel item = data.get(position);
        VehicleModel vehicleModel = item.getCustomer_vehicle();
        Utils.loadPicassoImage(vehicleModel.getVehicle_image(), holder.binding.scheduleCarImage, holder.binding.progressBar, R.drawable.car_demo);
        holder.binding.scheduleCarName.setText(vehicleModel.getMake());
        holder.binding.scheduleReminderDate.setText((item.getReminder_date()));
        holder.binding.scheduleCarDesc.setText(vehicleModel.getYear() + " | " + vehicleModel.getModel());
        if(position == 0) holder.binding.scheduleTitle.setVisibility(View.VISIBLE);
        else holder.binding.scheduleTitle.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
