package com.oileemobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.databinding.VehicleRowScheduleBinding;
import com.oileemobile.models.VehicleModel;
import com.oileemobile.utils.Utils;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-09-02 02:02
 **/
public class ReminderVehicleAdapter extends RecyclerView.Adapter<ReminderVehicleAdapter.MyViewHolder> {

    private List<VehicleModel> dataList;
    private OnVehicleSelectedListener mListener;

    public ReminderVehicleAdapter(List<VehicleModel> dataList, OnVehicleSelectedListener listener) {
        this.dataList = dataList;
        this.mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public VehicleRowScheduleBinding binding;

        MyViewHolder(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v == itemView) {
                TransitionManager.beginDelayedTransition((ViewGroup) itemView);
                binding.vehicleTick.setImageResource(R.drawable.ic_tick);
                if(mListener != null) {
                    mListener.onVehicleSelected(getAdapterPosition());
                }
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_row_schedule, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        VehicleModel vehicleModel = dataList.get(position);
        holder.binding.vehicleName.setText(vehicleModel.getMake());

        String desc = vehicleModel.getYear() + " | " + vehicleModel.getModel();
        holder.binding.vehicleDesc.setText(desc);
        Utils.loadPicassoImage(vehicleModel.getVehicle_image(), holder.binding.vehicleImage, holder.binding.progressBar, R.drawable.car_demo);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface OnVehicleSelectedListener {
        void onVehicleSelected(int position);
    }
}
