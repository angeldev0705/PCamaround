package com.pcamarounds.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pcamarounds.R;
import com.pcamarounds.databinding.BidtypeCellBinding;
import com.pcamarounds.models.BidType;

import java.util.List;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 17,September,2020
 */
public class BidTypeAdapter extends RecyclerView.Adapter<BidTypeAdapter.ViewHolder> {

    private final Context mContext;
    private List<BidType> mBidTypeList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;
    private int lastCheckedPosition = 0;
    public BidTypeAdapter(Context mContext, List<BidType> mBidTypeList) {
        this.mBidTypeList = mBidTypeList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final BidtypeCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.bidtype_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final BidType mBidType = this.mBidTypeList.get(position);
        holder.bind(mBidType);

        holder.binding.btnstatus.setText(mBidType.getStatusName());

        if (lastCheckedPosition == position) {
            holder.binding.btnstatus.setTextColor(mContext.getResources().getColor(R.color.color_primary));
            holder.binding.btnstatus.setStrokeColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.color_primary)));
        } else {
            holder.binding.btnstatus.setTextColor(mContext.getResources().getColor(R.color.colorGray));
            holder.binding.btnstatus.setStrokeColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGray)));
           // holder.binding.btnstatus.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGray)));

        }

    }

    @Override
    public int getItemCount() {
        return mBidTypeList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<BidType> getBidType() {
        return mBidTypeList;
    }

    public void addChatMassgeModel(BidType mBidType) {
        try {
            this.mBidTypeList.add(mBidType);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBidTypeList(List<BidType> mBidTypeList) {
        this.mBidTypeList = mBidTypeList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final BidtypeCellBinding binding;

        public ViewHolder(final View view, final BidtypeCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
            binding.btnstatus.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    lastCheckedPosition = getAdapterPosition();
                    notifyItemRangeChanged(0, mBidTypeList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final BidType mBidType) {
            // this.binding.setBidType(mBidType);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mBidTypeList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mBidTypeList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mBidTypeList.size();
        mBidTypeList.clear();
        notifyItemRangeRemoved(0, size);
    }
}