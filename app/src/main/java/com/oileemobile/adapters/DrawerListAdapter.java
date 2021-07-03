package com.oileemobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.databinding.DrawerListItemBinding;
import com.oileemobile.models.DrawerListItemModel;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-08-30 00:57
 **/
public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListAdapter.MyViewHolder> {

    private List<DrawerListItemModel> dataList;
    private OnDrawerItemSelectedListener mListener;

    public DrawerListAdapter(List<DrawerListItemModel> dataList, OnDrawerItemSelectedListener mListener) {
        this.dataList = dataList;
        this.mListener = mListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private DrawerListItemBinding binding;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null)
                mListener.onDrawerItemSelected(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DrawerListItemModel item = dataList.get(position);

        holder.binding.drawerItemIcon.setImageResource(item.getIcon());
        holder.binding.drawerItemTitle.setText(item.getTitle());
        holder.binding.drawerItemSelected.setVisibility(item.isSelected() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface OnDrawerItemSelectedListener {
        void onDrawerItemSelected(int position);
    }
}
