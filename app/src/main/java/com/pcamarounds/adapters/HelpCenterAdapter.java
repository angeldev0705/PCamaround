package com.pcamarounds.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.pcamarounds.R;
import com.pcamarounds.databinding.HelpcenterCellBinding;
import com.pcamarounds.models.HelpCategoryModel;

import java.util.List;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 30,March,2020
 */
public class HelpCenterAdapter extends RecyclerView.Adapter<HelpCenterAdapter.ViewHolder> {


    private final Context mContext;
    private List<HelpCategoryModel> mHelpCenterModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public HelpCenterAdapter(Context mContext, List<HelpCategoryModel> mHelpCenterModelList) {
        this.mHelpCenterModelList = mHelpCenterModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final HelpcenterCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.helpcenter_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final HelpCategoryModel mHelpCenterModel = this.mHelpCenterModelList.get(position);
        holder.bind(mHelpCenterModel);
        holder.binding.tvMain.setText(mHelpCenterModel.getCategory());
    }

    @Override
    public int getItemCount() {
        return mHelpCenterModelList.size();

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<HelpCategoryModel> getHelpCenterModel() {
        return mHelpCenterModelList;
    }

    public void addChatMassgeModel(HelpCategoryModel mHelpCenterModel) {
        try {
            this.mHelpCenterModelList.add(mHelpCenterModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setHelpCenterModelList(List<HelpCategoryModel> mHelpCenterModelList) {
        this.mHelpCenterModelList = mHelpCenterModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final HelpcenterCellBinding binding;

        public ViewHolder(final View view, final HelpcenterCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mHelpCenterModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final HelpCategoryModel mHelpCenterModel) {
            // this.binding.setHelpCenter(mHelpCenterModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mHelpCenterModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mHelpCenterModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mHelpCenterModelList.size();
        mHelpCenterModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

}