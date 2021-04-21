package com.pcamarounds.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pcamarounds.R;
import com.pcamarounds.databinding.HelptypeCellBinding;
import com.pcamarounds.models.helpsupport.Helptype;
import com.pcamarounds.utils.Utility;

import java.util.List;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 26,May,2020
 */
public class HelptypeAdapter extends RecyclerView.Adapter<HelptypeAdapter.ViewHolder> {


    private final Context mContext;
    private List<Helptype> mHelptypeList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public HelptypeAdapter(Context mContext, List<Helptype> mHelptypeList) {
        this.mHelptypeList = mHelptypeList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final HelptypeCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.helptype_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Helptype mHelptype = this.mHelptypeList.get(position);
        holder.bind(mHelptype);
        holder.binding.tvSub.setText(Utility.capitalize(mHelptype.getQuestion()));

    }

    @Override
    public int getItemCount() {
        return mHelptypeList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<Helptype> getHelptype() {
        return mHelptypeList;
    }

    public void addChatMassgeModel(Helptype mHelptype) {
        try {
            this.mHelptypeList.add(mHelptype);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setHelptypeList(List<Helptype> mHelptypeList) {
        this.mHelptypeList = mHelptypeList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final HelptypeCellBinding binding;

        public ViewHolder(final View view, final HelptypeCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mHelptypeList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final Helptype mHelptype) {
            // this.binding.setHelptype(mHelptype);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mHelptypeList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mHelptypeList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mHelptypeList.size();
        mHelptypeList.clear();
        notifyItemRangeRemoved(0, size);
    }

}