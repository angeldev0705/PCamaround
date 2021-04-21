package com.pcamarounds.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pcamarounds.R;
import com.pcamarounds.databinding.BanklistCellBinding;
import com.pcamarounds.models.BankListModel;
import com.pcamarounds.utils.Utility;

import java.util.List;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 10,August,2020
 */
public class BankListAdapter extends RecyclerView.Adapter<BankListAdapter.ViewHolder> {


    private final Context mContext;
    private List<BankListModel> mBankListModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public BankListAdapter(Context mContext, List<BankListModel> mBankListModelList) {
        this.mBankListModelList = mBankListModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final BanklistCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.banklist_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final BankListModel mBankListModel = this.mBankListModelList.get(position);
        holder.bind(mBankListModel);

        holder.binding.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.rb.performClick();
            }
        });
        holder.binding.tvBank.setText(Utility.capitalize(mBankListModel.getBankName()));
        holder.binding.rb.setOnCheckedChangeListener(null);
        holder.binding.rb.setChecked(position == currentPos);

    }

    @Override
    public int getItemCount() {
        return mBankListModelList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<BankListModel> getBankListModel() {
        return mBankListModelList;
    }

    public void addChatMassgeModel(BankListModel mBankListModel) {
        try {
            this.mBankListModelList.add(mBankListModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBankListModelList(List<BankListModel> mBankListModelList) {
        this.mBankListModelList = mBankListModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final BanklistCellBinding binding;

        public ViewHolder(final View view, final BanklistCellBinding binding) {
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
                    notifyItemRangeChanged(0, mBankListModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final BankListModel mBankListModel) {
            // this.binding.setBankList(mBankListModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mBankListModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mBankListModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mBankListModelList.size();
        mBankListModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

}