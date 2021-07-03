package com.oileemobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

import com.oileemobile.R;
import com.oileemobile.interfaces.RecyclerViewItemClickListener;
import com.oileemobile.models.TimeSlot;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-18 19:08
 **/
public class TimeSlotsAdapter extends RecyclerView.Adapter<TimeSlotsAdapter.MyViewHolder> {
    private List<TimeSlot> items;
    private RecyclerViewItemClickListener mListener;

    public TimeSlotsAdapter(List<TimeSlot> items, RecyclerViewItemClickListener clickListener) {
        this.items = items;
        this.mListener = clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MyViewHolder(View itemView) {
            super(itemView);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slots_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TimeSlot item = items.get(position);
        ((TextView) holder.itemView).setText(item.getTime12Hr());
        holder.itemView.setEnabled(!item.getDate().before(new Date()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
