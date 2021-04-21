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
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.databinding.MycamaroundsCellBinding;
import com.pcamarounds.models.MyCamaroundsModel;
import com.pcamarounds.utils.Utility;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 21,March,2020
 */
public class MyCamaroundsAdapter extends RecyclerView.Adapter<MyCamaroundsAdapter.ViewHolder> {
    private static final String TAG = "MyCamaroundsAdapter";
    private final Context mContext;
    private List<MyCamaroundsModel> mMyCamaroundsModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public MyCamaroundsAdapter(Context mContext, List<MyCamaroundsModel> mMyCamaroundsModelList) {
        this.mMyCamaroundsModelList = mMyCamaroundsModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final MycamaroundsCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.mycamarounds_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MyCamaroundsModel mMyCamaroundsModel = this.mMyCamaroundsModelList.get(position);
        holder.bind(mMyCamaroundsModel);

        holder.binding.tvServiceName.setText(mMyCamaroundsModel.getServiceName());

        if (Utility.isCheckEmptyOrNull(mMyCamaroundsModel.getUserName())) {
            if (Utility.isCheckEmptyOrNull(mMyCamaroundsModel.getBidData().getUserDiscountAmount())) {
                double amount = Double.parseDouble(mMyCamaroundsModel.getBidData().getUserDiscountAmount());
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                DecimalFormat formatter = (DecimalFormat) nf;
                formatter.setRoundingMode(RoundingMode.HALF_EVEN);
                formatter.applyPattern("#,###0.00");
                if (Utility.isCheckEmptyOrNull(mMyCamaroundsModel.getBidData().getLastName())) {
                    holder.binding.tvCamarounds.setText(Utility.capitalize(mMyCamaroundsModel.getBidData().getFirstName() + " " + Utility.printInitials(mMyCamaroundsModel.getBidData().getLastName())) + " - $" + formatter.format(amount));
                } else {
                    holder.binding.tvCamarounds.setText(Utility.capitalize(mMyCamaroundsModel.getBidData().getFirstName()) + " - $" + formatter.format(amount));
                }
            }

        } else {
            holder.binding.tvCamarounds.setText("");
        }

        holder.binding.tvDate.setText(Utility.capitalize(Utility.change_format(mMyCamaroundsModel.getCreated(), "yyyy-MM-dd", "dd MMM")));

        for (int i = 0; i < AppConstants.bookingStatus.length; i++) {
            if (mMyCamaroundsModel.getBookingStatus().equals(AppConstants.bookingStatus[i])) {
                holder.binding.btnStatus.setText(AppConstants.bookingStatusTitle[i]);
                holder.binding.btnStatus.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources()
                        .getColor(AppConstants.bookingStatusColor[i])));
            }
        }


    }

    @Override
    public int getItemCount() {
        return mMyCamaroundsModelList.size();

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<MyCamaroundsModel> getMyCamaroundsModel() {
        return mMyCamaroundsModelList;
    }

    public void addChatMassgeModel(MyCamaroundsModel mMyCamaroundsModel) {
        try {
            this.mMyCamaroundsModelList.add(mMyCamaroundsModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMyCamaroundsModelList(List<MyCamaroundsModel> mMyCamaroundsModelList) {
        this.mMyCamaroundsModelList = mMyCamaroundsModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MycamaroundsCellBinding binding;

        public ViewHolder(final View view, final MycamaroundsCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mMyCamaroundsModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final MyCamaroundsModel mMyCamaroundsModel) {
            // this.binding.setMyCamarounds(mMyCamaroundsModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mMyCamaroundsModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mMyCamaroundsModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mMyCamaroundsModelList.size();
        mMyCamaroundsModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

}