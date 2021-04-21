package com.pcamarounds.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.pcamarounds.R;
import com.pcamarounds.databinding.PlaceautocompletetwoCellBinding;
import com.pcamarounds.models.PlaceAutoCompleteModel;

import java.util.List;

public class PlaceAutoCompletetwoAdapter extends RecyclerView.Adapter<PlaceAutoCompletetwoAdapter.ViewHolder> {


    private final Context mContext;
    private List<PlaceAutoCompleteModel> mPlaceAutoCompleteModelList;
    private OnItemClickListener onItemClickListener;

    public PlaceAutoCompletetwoAdapter(Context mContext, List<PlaceAutoCompleteModel> mPlaceAutoCompleteModelList) {
        this.mPlaceAutoCompleteModelList = mPlaceAutoCompleteModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final PlaceautocompletetwoCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.placeautocompletetwo_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PlaceAutoCompleteModel mPlaceAutoCompleteModel = this.mPlaceAutoCompleteModelList.get(position);
        holder.bind(mPlaceAutoCompleteModel);
    }

    @Override
    public int getItemCount() {
        return mPlaceAutoCompleteModelList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<PlaceAutoCompleteModel> getPlaceAutoCompleteModel() {
        return mPlaceAutoCompleteModelList;
    }

    public void addChatMassgeModel(PlaceAutoCompleteModel mPlaceAutoCompleteModel) {
        try {
            this.mPlaceAutoCompleteModelList.add(mPlaceAutoCompleteModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPlaceAutoCompleteModelList(List<PlaceAutoCompleteModel> mPlaceAutoCompleteModelList) {
        this.mPlaceAutoCompleteModelList = mPlaceAutoCompleteModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final PlaceautocompletetwoCellBinding binding;

        public ViewHolder(final View view, final PlaceautocompletetwoCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final PlaceAutoCompleteModel mPlaceAutoCompleteModel) {
            this.binding.setPlaceAutoCompleteModel(mPlaceAutoCompleteModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mPlaceAutoCompleteModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mPlaceAutoCompleteModelList.size());
    }

    public void clearRecyclerView() {
        int size = mPlaceAutoCompleteModelList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                mPlaceAutoCompleteModelList.remove(0);
            }
            notifyItemRangeRemoved(0, size);
        }
    }
}