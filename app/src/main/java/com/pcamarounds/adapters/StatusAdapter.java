package com.pcamarounds.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.pcamarounds.R;
import com.pcamarounds.databinding.StatusCellBinding;
import com.pcamarounds.models.StatusModel;

import java.util.List;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 21,March,2020
 */
public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {


    private final Context mContext;
    private List<StatusModel> mStatusModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos;

    public StatusAdapter(Context mContext, List<StatusModel> mStatusModelList) {
        this.mStatusModelList = mStatusModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final StatusCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.status_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final StatusModel mStatusModel = this.mStatusModelList.get(position);
        holder.bind(mStatusModel);
        holder.binding.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.rb.performClick();
            }
        });

        holder.binding.rb.setText(mStatusModel.getStatusName());
        holder.binding.rb.setOnCheckedChangeListener(null);
        holder.binding.rb.setChecked(position == currentPos);
        holder.binding.rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentPos = position;
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mStatusModelList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<StatusModel> getStatusModel() {
        return mStatusModelList;
    }

    public void addChatMassgeModel(StatusModel mStatusModel) {
        try {
            this.mStatusModelList.add(mStatusModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStatusModelList(List<StatusModel> mStatusModelList) {
        this.mStatusModelList = mStatusModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final StatusCellBinding binding;

        public ViewHolder(final View view, final StatusCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
            //binding.ll.setOnClickListener(this);
            //binding.rb.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mStatusModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final StatusModel mStatusModel) {
            //this.binding.setStatus(mStatusModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mStatusModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mStatusModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mStatusModelList.size();
        mStatusModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

}