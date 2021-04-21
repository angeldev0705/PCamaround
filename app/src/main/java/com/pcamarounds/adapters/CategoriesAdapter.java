package com.pcamarounds.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;

import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pcamarounds.R;
import com.pcamarounds.databinding.CategoriesCellBinding;
import com.pcamarounds.models.SearchPostListModel;
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
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private final Context mContext;
    private List<SearchPostListModel> mCategoriesModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;
    private static final String TAG = "CategoriesAdapter";

    public CategoriesAdapter(Context mContext, List<SearchPostListModel> mCategoriesModelList) {
        this.mCategoriesModelList = mCategoriesModelList;
        this.mContext = mContext;
    }

    public List<SearchPostListModel> getmCategoriesModelList() {
        return mCategoriesModelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final CategoriesCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.categories_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SearchPostListModel mCategoriesModel = this.mCategoriesModelList.get(position);
        holder.bind(mCategoriesModel);
        holder.binding.tvServiceName.setText(mCategoriesModel.getServiceName());
        if (Utility.isCheckEmptyOrNull(mCategoriesModel.getAddress())) {
            holder.binding.tvDestance.setVisibility(View.VISIBLE);
            holder.binding.tvDestance.setText(Utility.roundOff(mCategoriesModel.getDiatamnce()) + "km");
        }else {
            holder.binding.tvDestance.setVisibility(View.GONE);
        }
        holder.binding.tvCotizaciones.setText(+mCategoriesModel.getBidCount() + "");

        if (mCategoriesModel.getBidCount() > 0) {
            if (Utility.isCheckEmptyOrNull(mCategoriesModel.getTechJobAmounts())) {
                holder.binding.llSend.setVisibility(View.VISIBLE);
                double amount = Double.parseDouble(mCategoriesModel.getTechJobAmounts());
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                DecimalFormat formatter = (DecimalFormat) nf;
                formatter.setRoundingMode(RoundingMode.HALF_EVEN);
                formatter.applyPattern("#,###0.00");
                holder.binding.tvAmount.setText("$" + formatter.format(amount));
                //holder.binding.tvAmount.setText("$" + formatter.format(amount) + " USD");
            }else {
                holder.binding.llSend.setVisibility(View.GONE);
            }
            if (Utility.isCheckEmptyOrNull(mCategoriesModel.getBid_avg_price())) {
                double amount = Double.parseDouble(mCategoriesModel.getBid_avg_price());
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                DecimalFormat formatter = (DecimalFormat) nf;
                formatter.setRoundingMode(RoundingMode.HALF_EVEN);
                formatter.applyPattern("#,###0.00");
                holder.binding.tvAvarage.setText("$" + formatter.format(amount));
            }

        } else {
            Log.e(TAG, "onBindViewHolder elseeee: " + mCategoriesModel.getBidCount());
            holder.binding.llSend.setVisibility(View.GONE);
            holder.binding.tvAvarage.setText("$0.00");
            holder.binding.tvAmount.setText("$0.00");
        }
    }

    @Override
    public int getItemCount() {
        return mCategoriesModelList.size();

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<SearchPostListModel> getCategoriesModel() {
        return mCategoriesModelList;
    }

    public void addChatMassgeModel(SearchPostListModel mCategoriesModel) {
        try {
            this.mCategoriesModelList.add(mCategoriesModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCategoriesModelList(List<SearchPostListModel> mCategoriesModelList) {
        this.mCategoriesModelList = mCategoriesModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final CategoriesCellBinding binding;

        public ViewHolder(final View view, final CategoriesCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mCategoriesModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final SearchPostListModel mCategoriesModel) {
            //this.binding.setCategories(mCategoriesModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mCategoriesModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mCategoriesModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mCategoriesModelList.size();
        mCategoriesModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

}