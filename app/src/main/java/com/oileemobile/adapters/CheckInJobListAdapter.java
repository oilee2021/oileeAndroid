package com.oileemobile.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import java.util.List;

import com.oileemobile.R;
import com.oileemobile.databinding.ItemCheckInOutJobListBinding;
import com.oileemobile.models.technician.CheckInOutModel;
import com.oileemobile.network.ApiCallBack;
import com.oileemobile.network.ApiRequest;
import com.oileemobile.network.ApiResponseListener;
import com.oileemobile.network.ServiceManager;
import com.oileemobile.network.StatusCodes;
import com.oileemobile.network.response.ApiResponse;
import com.oileemobile.utils.Utils;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-12-10 02:28
 **/
public class CheckInJobListAdapter extends RecyclerView.Adapter<CheckInJobListAdapter.MyViewHolder> {
    private List<CheckInOutModel.CheckInOutBean> data;
    private ViewGroup mainView;
    private int type;
    private Activity activity;

    public CheckInJobListAdapter(Activity activity, List<CheckInOutModel.CheckInOutBean> data, ViewGroup mainView, int type) {
        this.activity = activity;
        this.data = data;
        this.mainView = mainView;
        this.type = type;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ItemCheckInOutJobListBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
            binding.jobHeader.setOnClickListener(this);
            binding.buttonMark.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.button_mark) {
                int jobId = data.get(getAdapterPosition()).getId();
                if(type == 1) callCheckInApi(jobId, binding);
                else callCheckOutApi(jobId, binding);
            } else {
                TransitionManager.beginDelayedTransition(mainView);
                if (binding.jobDetail.getVisibility() != View.VISIBLE) {
                    binding.jobDetail.setVisibility(View.VISIBLE);
                    binding.imageDownArrow.animate().rotation(0);
                } else {
                    binding.jobDetail.setVisibility(View.GONE);
                    binding.imageDownArrow.animate().rotation(180);
                }
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_in_out_job_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CheckInOutModel.CheckInOutBean item = data.get(position);
        holder.binding.textJobTitle.setText("Job " + (position + 1));
        holder.binding.textBookingId.setText(item.getOrder_id());
        holder.binding.textOrderDate.setText(item.getOrder_date());
        holder.binding.textCustomerName.setText(item.getCustomer_name());
        holder.binding.textOilQuantity.setText(item.getOil_quarts() + " Qts");
        holder.binding.textOilNeeded.setText(String.format("%s %s", item.getPackage_category(), item.getOil_type()));
        if(type == 1) {
            if (item.isChecked_in()) {
                holder.binding.buttonMark.setVisibility(View.GONE);
                holder.binding.linearChecked.setVisibility(View.VISIBLE);

                holder.binding.textMarkedText.setText("Marked as Checked-In.");
            } else {
                holder.binding.buttonMark.setVisibility(View.VISIBLE);
                holder.binding.linearChecked.setVisibility(View.GONE);

                holder.binding.buttonMark.setText("Mark Checked-In");
            }
        } else {
            if (item.isChecked_out()) {
                holder.binding.buttonMark.setVisibility(View.GONE);
                holder.binding.linearChecked.setVisibility(View.VISIBLE);

                holder.binding.textMarkedText.setText("Marked as Checked-Out.");
            } else {
                holder.binding.buttonMark.setVisibility(View.VISIBLE);
                holder.binding.linearChecked.setVisibility(View.GONE);

                holder.binding.buttonMark.setText("Mark Checked-Out");
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void callCheckInApi(int jobId, ItemCheckInOutJobListBinding binding) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setTechnician_job_id(String.valueOf(jobId));

        ServiceManager.callServerApi(activity, ServiceManager.API_CHECK_IN, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    Toast.makeText(activity, "Checked-in successfully!", Toast.LENGTH_SHORT).show();

                    binding.buttonMark.setVisibility(View.GONE);
                    binding.linearChecked.setVisibility(View.VISIBLE);
                    binding.textMarkedText.setText("Marked as Checked-In.");
                } else {
                    Utils.showSnackbar(activity, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(activity, failureMessage);
            }
        }), apiRequest);
    }

    private void callCheckOutApi(int jobId, ItemCheckInOutJobListBinding binding) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setTechnician_job_id(String.valueOf(jobId));

        ServiceManager.callServerApi(activity, ServiceManager.API_CHECK_OUT, new ApiCallBack<>(new ApiResponseListener<ApiResponse>() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                if(response.getStatus() == StatusCodes.SUCCESS) {
                    Toast.makeText(activity, "Checked-out successfully!", Toast.LENGTH_SHORT).show();
                    binding.buttonMark.setVisibility(View.GONE);
                    binding.linearChecked.setVisibility(View.VISIBLE);
                    binding.textMarkedText.setText("Marked as Checked-Out.");
                } else {
                    Utils.showSnackbar(activity, response.getMessage());
                }
            }

            @Override
            public void onApiFailure(String failureMessage) {
                Utils.showSnackbar(activity, failureMessage);
            }
        }), apiRequest);
    }
}
