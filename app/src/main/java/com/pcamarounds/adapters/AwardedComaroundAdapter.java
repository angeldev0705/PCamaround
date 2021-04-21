package com.pcamarounds.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import com.pcamarounds.R;
import com.pcamarounds.activities.SearchDetailsCamaroundActivity;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.databinding.AwardedcomaroundCellBinding;
import com.pcamarounds.models.dashboard.AwardedList;
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
 * Created on : 20,March,2020
 */
public class AwardedComaroundAdapter extends RecyclerView.Adapter<AwardedComaroundAdapter.ViewHolder> {


    private final Context mContext;
    private List<AwardedList> mAwardedComaroundModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public AwardedComaroundAdapter(Context mContext, List<AwardedList> mAwardedComaroundModelList) {
        this.mAwardedComaroundModelList = mAwardedComaroundModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final AwardedcomaroundCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.awardedcomaround_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AwardedList mAwardedComaroundModel = this.mAwardedComaroundModelList.get(position);
        holder.bind(mAwardedComaroundModel);
        holder.binding.tvServiceName.setText(mAwardedComaroundModel.getServiceName());
        if (Utility.isCheckEmptyOrNull(mAwardedComaroundModel.getServiceName())) {
            holder.binding.tvTechNamePrice.setVisibility(View.VISIBLE);

            if (Utility.isCheckEmptyOrNull(mAwardedComaroundModel.getJobAmount())) {
                if (Utility.isCheckEmptyOrNull(mAwardedComaroundModel.getDiscount())) {
                    double dis = ((Double.parseDouble(mAwardedComaroundModel.getJobAmount()) * Double.parseDouble(mAwardedComaroundModel.getDiscount()) / 100));
                    double vc = Double.parseDouble(mAwardedComaroundModel.getJobAmount()) - dis;
                    NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                    DecimalFormat formatter = (DecimalFormat) nf;
                    formatter.setRoundingMode(RoundingMode.HALF_EVEN);
                    formatter.applyPattern("#,###0.00");
                    if (Utility.isCheckEmptyOrNull(mAwardedComaroundModel.getTech_name_last())) {
                        holder.binding.tvTechNamePrice.setText(Utility.capitalize(mAwardedComaroundModel.getTechName() + " " + Utility.printInitials(mAwardedComaroundModel.getTech_name_last())) + " - $" + formatter.format(vc));
                    } else {
                        holder.binding.tvTechNamePrice.setText(Utility.capitalize(mAwardedComaroundModel.getTechName()) + " - $" + formatter.format(vc));
                    }
                } else {
                    double vc = Double.parseDouble(mAwardedComaroundModel.getJobAmount());
                    NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                    DecimalFormat formatter = (DecimalFormat) nf;
                    formatter.setRoundingMode(RoundingMode.HALF_EVEN);
                    formatter.applyPattern("#,###0.00");
                    if (Utility.isCheckEmptyOrNull(mAwardedComaroundModel.getTech_name_last())) {
                        holder.binding.tvTechNamePrice.setText(Utility.capitalize(mAwardedComaroundModel.getTechName() + " " + Utility.printInitials(mAwardedComaroundModel.getTech_name_last())) + " - $" + formatter.format(vc));
                    } else {
                        holder.binding.tvTechNamePrice.setText(Utility.capitalize(mAwardedComaroundModel.getTechName()) + " - $" + formatter.format(vc));
                    }
                }
            } else {
                holder.binding.tvTechNamePrice.setText(Utility.capitalize(mAwardedComaroundModel.getTechName() + " " + Utility.printInitials(mAwardedComaroundModel.getTech_name_last())));
            }

        } else {
            holder.binding.tvTechNamePrice.setText("");
        }

        holder.binding.tvDate.setText(Utility.change_format(mAwardedComaroundModel.getCreated(), "yyyy-MM-dd", "dd MMM"));

        holder.binding.btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchDetailsCamaroundActivity.class);
                intent.putExtra(AppConstants.JOB_ID, mAwardedComaroundModel.getId());
                intent.putExtra(AppConstants.USER_ID, mAwardedComaroundModel.getTechId());
                intent.putExtra(AppConstants.SERVICE_NAME, mAwardedComaroundModel.getServiceName());
                intent.putExtra(AppConstants.BID_STATUS, mAwardedComaroundModel.getBookingStatus());
                mContext.startActivity(intent);
            }
        });

/*
        for (int i = 0; i < AppConstants.bookingStatus.length; i++) {
            if (mAwardedComaroundModel.getBookingStatus().equals(AppConstants.bookingStatus[i])) {
                holder.binding.btnStatus.setText(AppConstants.bookingStatusTitle[i]);
                holder.binding.btnStatus.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources()
                        .getColor(AppConstants.bookingStatusColor[i])));
            }
        }
*/

    }

    @Override
    public int getItemCount() {
        return mAwardedComaroundModelList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<AwardedList> getAwardedComaroundModel() {
        return mAwardedComaroundModelList;
    }

    public void addChatMassgeModel(AwardedList mAwardedComaroundModel) {
        try {
            this.mAwardedComaroundModelList.add(mAwardedComaroundModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAwardedComaroundModelList(List<AwardedList> mAwardedComaroundModelList) {
        this.mAwardedComaroundModelList = mAwardedComaroundModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AwardedcomaroundCellBinding binding;

        public ViewHolder(final View view, final AwardedcomaroundCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mAwardedComaroundModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final AwardedList mAwardedComaroundModel) {
            //this.binding.setAwardedComaroundModel(mAwardedComaroundModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mAwardedComaroundModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mAwardedComaroundModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mAwardedComaroundModelList.size();
        mAwardedComaroundModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

}