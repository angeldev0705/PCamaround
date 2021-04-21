package com.pcamarounds.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.pcamarounds.R;
import com.pcamarounds.databinding.CheckboxselectedCellBinding;
import com.pcamarounds.models.CheckBoxModel;

import java.util.List;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 07,July,2020
 */
public class CheckBoxSelectedAdapter extends RecyclerView.Adapter<CheckBoxSelectedAdapter.ViewHolder> {


    private final Context mContext;
    private List<CheckBoxModel> mCheckBoxModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public CheckBoxSelectedAdapter(Context mContext, List<CheckBoxModel> mCheckBoxModelList) {
        this.mCheckBoxModelList = mCheckBoxModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final CheckboxselectedCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.checkboxselected_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CheckBoxModel mCheckBoxModel = this.mCheckBoxModelList.get(position);
        holder.bind(mCheckBoxModel);
        holder.binding.checkbox.setText(mCheckBoxModel.getOption());
    }

    @Override
    public int getItemCount() {
        return mCheckBoxModelList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<CheckBoxModel> getCheckBoxModel() {
        return mCheckBoxModelList;
    }

    public void addChatMassgeModel(CheckBoxModel mCheckBoxModel) {
        try {
            this.mCheckBoxModelList.add(mCheckBoxModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCheckBoxModelList(List<CheckBoxModel> mCheckBoxModelList) {
        this.mCheckBoxModelList = mCheckBoxModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final CheckboxselectedCellBinding binding;

        public ViewHolder(final View view, final CheckboxselectedCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mCheckBoxModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final CheckBoxModel mCheckBoxModel) {
            // this.binding.setCheckBoxSelected(mCheckBoxModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mCheckBoxModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mCheckBoxModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mCheckBoxModelList.size();
        mCheckBoxModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

}