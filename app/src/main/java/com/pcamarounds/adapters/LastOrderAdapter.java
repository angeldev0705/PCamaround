package com.pcamarounds.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.pcamarounds.R;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.databinding.LastorderCellBinding;
import com.pcamarounds.models.helpsupport.LastOrderModel;
import com.pcamarounds.utils.Utility;

import java.util.List;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 26,May,2020
 */
public class LastOrderAdapter extends RecyclerView.Adapter<LastOrderAdapter.ViewHolder> {


    private final Context mContext;
    private List<LastOrderModel> mLastOrderModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public LastOrderAdapter(Context mContext, List<LastOrderModel> mLastOrderModelList) {
        this.mLastOrderModelList = mLastOrderModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final LastorderCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.lastorder_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final LastOrderModel mLastOrderModel = this.mLastOrderModelList.get(position);
        holder.bind(mLastOrderModel);

        holder.binding.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.rb.performClick();
            }
        });

        holder.binding.tvServicename.setText(mLastOrderModel.getServiceName());

        holder.binding.rb.setOnCheckedChangeListener(null);
        holder.binding.rb.setChecked(position == currentPos);
        holder.binding.rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentPos = position;
                notifyDataSetChanged();
            }
        });

        for (int i = 0; i < AppConstants.bookingStatus.length; i++) {
            if (mLastOrderModel.getBookingStatus().equals(AppConstants.bookingStatus[i])) {
                holder.binding.tvStatus.setText(AppConstants.bookingStatusTitle[i]);
                holder.binding.tvStatus.setTextColor(ColorStateList.valueOf(mContext.getResources()
                        .getColor(AppConstants.bookingStatusColor[i])));
            }
        }
        holder.binding.tvDate.setText(Utility.capitalize(Utility.change_format(mLastOrderModel.getCreated(), "yyyy-MM-dd", "MMM dd")));

    }

    @Override
    public int getItemCount() {
        return mLastOrderModelList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<LastOrderModel> getLastOrderModel() {
        return mLastOrderModelList;
    }

    public void addChatMassgeModel(LastOrderModel mLastOrderModel) {
        try {
            this.mLastOrderModelList.add(mLastOrderModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLastOrderModelList(List<LastOrderModel> mLastOrderModelList) {
        this.mLastOrderModelList = mLastOrderModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final LastorderCellBinding binding;

        public ViewHolder(final View view, final LastorderCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mLastOrderModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final LastOrderModel mLastOrderModel) {
            // this.binding.setLastOrder(mLastOrderModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mLastOrderModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mLastOrderModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mLastOrderModelList.size();
        mLastOrderModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

}