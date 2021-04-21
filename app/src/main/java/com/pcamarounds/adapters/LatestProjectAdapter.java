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
import com.pcamarounds.databinding.LatestprojectCellBinding;
import com.pcamarounds.models.dashboard.InprogressList;
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
public class LatestProjectAdapter extends RecyclerView.Adapter<LatestProjectAdapter.ViewHolder> {


    private final Context mContext;
    private List<InprogressList> mLatestProjectModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public LatestProjectAdapter(Context mContext, List<InprogressList> mLatestProjectModelList) {
        this.mLatestProjectModelList = mLatestProjectModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final LatestprojectCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.latestproject_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final InprogressList mLatestProjectModel = this.mLatestProjectModelList.get(position);
        holder.bind(mLatestProjectModel);
        holder.binding.tvServiceName.setText(mLatestProjectModel.getBidData());
        if (Utility.isCheckEmptyOrNull(mLatestProjectModel.getBidData())) {
            if (Utility.isCheckEmptyOrNull(mLatestProjectModel.getBidData())) {
                double amount = Double.parseDouble(mLatestProjectModel.getJobAmount());
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                DecimalFormat formatter = (DecimalFormat) nf;
                formatter.setRoundingMode(RoundingMode.HALF_EVEN);
                formatter.applyPattern("#,###0.00");

                if (Utility.isCheckEmptyOrNull(mLatestProjectModel.getLastName())) {
                    holder.binding.tvTechNamePrice.setText(Utility.capitalize(mLatestProjectModel.getFirstName() + " " + Utility.printInitials(mLatestProjectModel.getLastName())) + " - $" + formatter.format(amount));
                }else {
                    holder.binding.tvTechNamePrice.setText(Utility.capitalize(mLatestProjectModel.getFirstName()) + " - $" + formatter.format(amount));
                }
            }

        } else {
            holder.binding.tvTechNamePrice.setText("");
        }

        // holder.binding.tvDate.setText(Utility.change_format(mLatestProjectModel.getCreatedOn(),"yyyy-MM-dd","dd MMM"));

        for (int i = 0; i < AppConstants.bookingStatus.length; i++) {
            if (mLatestProjectModel.getBidStatus().equals(AppConstants.bookingStatus[i])) {
                holder.binding.btnStatus.setText(AppConstants.bookingStatusTitle[i]);
                holder.binding.btnStatus.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources()
                        .getColor(AppConstants.bookingStatusColor[i])));
            }
        }

    }

    @Override
    public int getItemCount() {
        return mLatestProjectModelList.size();

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<InprogressList> getLatestProjectModel() {
        return mLatestProjectModelList;
    }

    public void addChatMassgeModel(InprogressList mLatestProjectModel) {
        try {
            this.mLatestProjectModelList.add(mLatestProjectModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLatestProjectModelList(List<InprogressList> mLatestProjectModelList) {
        this.mLatestProjectModelList = mLatestProjectModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final LatestprojectCellBinding binding;

        public ViewHolder(final View view, final LatestprojectCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mLatestProjectModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final InprogressList mLatestProjectModel) {
            // this.binding.setLatestProject(mLatestProjectModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mLatestProjectModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mLatestProjectModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mLatestProjectModelList.size();
        mLatestProjectModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

}