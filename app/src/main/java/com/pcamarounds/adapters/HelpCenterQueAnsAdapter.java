package com.pcamarounds.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pcamarounds.R;
import com.pcamarounds.databinding.HelpcenterqueansCellBinding;
import com.pcamarounds.models.HelpCenterQueAnsModel;

import java.util.List;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 07,August,2020
 */
public class HelpCenterQueAnsAdapter extends RecyclerView.Adapter<HelpCenterQueAnsAdapter.ViewHolder> {


    private final Context mContext;
    private List<HelpCenterQueAnsModel> mHelpCenterQueAnsModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public HelpCenterQueAnsAdapter(Context mContext, List<HelpCenterQueAnsModel> mHelpCenterQueAnsModelList) {
        this.mHelpCenterQueAnsModelList = mHelpCenterQueAnsModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final HelpcenterqueansCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.helpcenterqueans_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final HelpCenterQueAnsModel mHelpCenterQueAnsModel = this.mHelpCenterQueAnsModelList.get(position);
        holder.bind(mHelpCenterQueAnsModel);

        holder.binding.tvQue.setText(mHelpCenterQueAnsModel.getQuestion());
       // holder.binding.tvAns.setText(Utility.capitalize(mHelpCenterQueAnsModel.getAnswer()));

    }

    @Override
    public int getItemCount() {
        return mHelpCenterQueAnsModelList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<HelpCenterQueAnsModel> getHelpCenterQueAnsModel() {
        return mHelpCenterQueAnsModelList;
    }

    public void addChatMassgeModel(HelpCenterQueAnsModel mHelpCenterQueAnsModel) {
        try {
            this.mHelpCenterQueAnsModelList.add(mHelpCenterQueAnsModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setHelpCenterQueAnsModelList(List<HelpCenterQueAnsModel> mHelpCenterQueAnsModelList) {
        this.mHelpCenterQueAnsModelList = mHelpCenterQueAnsModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final HelpcenterqueansCellBinding binding;

        public ViewHolder(final View view, final HelpcenterqueansCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mHelpCenterQueAnsModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final HelpCenterQueAnsModel mHelpCenterQueAnsModel) {
            // this.binding.setHelpCenterQueAns(mHelpCenterQueAnsModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mHelpCenterQueAnsModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mHelpCenterQueAnsModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mHelpCenterQueAnsModelList.size();
        mHelpCenterQueAnsModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

}