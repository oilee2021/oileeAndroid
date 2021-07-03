package com.oileemobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.databinding.ItemCardListBinding;
import com.oileemobile.interfaces.RecyclerViewItemClickDeletListener;
import com.oileemobile.interfaces.RecyclerViewItemClickListener;
import com.oileemobile.models.CardModel;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-18 03:17
 **/
public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.MyViewHolder> {
    private List<CardModel> items;
    private RecyclerViewItemClickListener mListener;
    private RecyclerViewItemClickDeletListener itemClickDeletListener;

    public CardListAdapter(List<CardModel> items, RecyclerViewItemClickListener clickListener,RecyclerViewItemClickDeletListener itemClickDeletListener) {
        this.items = items;
        this.mListener = clickListener;
        this.itemClickDeletListener = itemClickDeletListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemCardListBinding binding;

        MyViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(this);
            binding.imageDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickDeletListener.onItemDeleteClick(getAdapterPosition());
                }
            });
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CardModel item = items.get(position);
        holder.binding.textCardNumber.setText("XXXX-XXXX-XXXX-" + item.getLast4());
        holder.binding.textCardExpirationn.setText(item.getExp_month() + "/" + item.getExp_year());
        holder.binding.textCardType.setText(item.getBrand());


        holder.binding.imageTick.setVisibility(mListener != null ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
