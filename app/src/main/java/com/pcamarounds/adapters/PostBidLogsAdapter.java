package com.pcamarounds.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.pcamarounds.R;
import com.pcamarounds.databinding.PostbidlogsCellBinding;
import com.pcamarounds.models.postdetail.PostBidLogs;
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
 * Created on : 25,July,2020
 */
public class PostBidLogsAdapter extends RecyclerView.Adapter<PostBidLogsAdapter.ViewHolder> {


    private final Context mContext;
    private List<PostBidLogs> mPostBidLogsList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public PostBidLogsAdapter(Context mContext, List<PostBidLogs> mPostBidLogsList) {
        this.mPostBidLogsList = mPostBidLogsList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final PostbidlogsCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.postbidlogs_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PostBidLogs mPostBidLogs = this.mPostBidLogsList.get(position);
        holder.bind(mPostBidLogs);

        if (Utility.isCheckEmptyOrNull(mPostBidLogs.getJobAmounts())) {
            double amount = Double.parseDouble(mPostBidLogs.getJobAmounts());
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat formatter = (DecimalFormat) nf;
            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            formatter.applyPattern("#,###0.00");
            holder.binding.tvTotalPagado.setText("$" + formatter.format(amount));
        }
        holder.binding.tvDate.setText(Utility.capitalize(Utility.change_format(mPostBidLogs.getCreatedOn(), "yyyy-MM-dd", "MMM dd")));

    }

    @Override
    public int getItemCount() {
        return mPostBidLogsList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<PostBidLogs> getPostBidLogs() {
        return mPostBidLogsList;
    }

    public void addChatMassgeModel(PostBidLogs mPostBidLogs) {
        try {
            this.mPostBidLogsList.add(mPostBidLogs);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPostBidLogsList(List<PostBidLogs> mPostBidLogsList) {
        this.mPostBidLogsList = mPostBidLogsList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final PostbidlogsCellBinding binding;

        public ViewHolder(final View view, final PostbidlogsCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mPostBidLogsList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final PostBidLogs mPostBidLogs) {
            // this.binding.setPostBidLogs(mPostBidLogs);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mPostBidLogsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mPostBidLogsList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mPostBidLogsList.size();
        mPostBidLogsList.clear();
        notifyItemRangeRemoved(0, size);
    }

}