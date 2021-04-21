package com.pcamarounds.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pcamarounds.R;
import com.pcamarounds.databinding.IfscCellBinding;
import com.pcamarounds.models.IFSCModel;
import com.pcamarounds.utils.Utility;

import java.util.List;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 11,August,2020
 */
public class IFSCAdapter extends RecyclerView.Adapter<IFSCAdapter.ViewHolder> {


    private final Context mContext;
    private List<IFSCModel> mIFSCModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public IFSCAdapter(Context mContext, List<IFSCModel> mIFSCModelList) {
        this.mIFSCModelList = mIFSCModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final IfscCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.ifsc_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final IFSCModel mIFSCModel = this.mIFSCModelList.get(position);
        holder.bind(mIFSCModel);

        holder.binding.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.rb.performClick();
            }
        });
        holder.binding.tvBank.setText(Utility.capitalize(mIFSCModel.getRoutes()));
        holder.binding.rb.setOnCheckedChangeListener(null);
        holder.binding.rb.setChecked(position == currentPos);

    }

    @Override
    public int getItemCount() {
        return mIFSCModelList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<IFSCModel> getIFSCModel() {
        return mIFSCModelList;
    }

    public void addChatMassgeModel(IFSCModel mIFSCModel) {
        try {
            this.mIFSCModelList.add(mIFSCModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIFSCModelList(List<IFSCModel> mIFSCModelList) {
        this.mIFSCModelList = mIFSCModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final IfscCellBinding binding;

        public ViewHolder(final View view, final IfscCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
            binding.rb.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mIFSCModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final IFSCModel mIFSCModel) {
            // this.binding.setIFSC(mIFSCModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mIFSCModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mIFSCModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mIFSCModelList.size();
        mIFSCModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

}