package com.pcamarounds.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.pcamarounds.R;
import com.pcamarounds.databinding.CategoryassignCellBinding;
import com.pcamarounds.models.CategoryAssignModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 17,September,2020
 */
public class CategoryAssignAdapter extends RecyclerView.Adapter<CategoryAssignAdapter.ViewHolder> {

    private static final String TAG = "CategoryAssignAdapter";
    private final Context mContext;
    private List<CategoryAssignModel> mCategoryAssignModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public CategoryAssignAdapter(Context mContext, List<CategoryAssignModel> mCategoryAssignModelList) {
        this.mCategoryAssignModelList = mCategoryAssignModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final CategoryassignCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.categoryassign_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CategoryAssignModel mCategoryAssignModel = this.mCategoryAssignModelList.get(position);
        holder.bind(mCategoryAssignModel);

        holder.binding.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.checkbox.performClick();
            }
        });

        holder.binding.tvCategory.setText(mCategoryAssignModel.getServiceName());
        holder.binding.checkbox.setChecked(mCategoryAssignModel.isChecked());
        holder.binding.checkbox.setTag(position);
        holder.binding.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                int clickedPos = ((Integer) cb.getTag()).intValue();
                if (!cb.isChecked()) {
                    mCategoryAssignModelList.get(clickedPos).setChecked(cb.isChecked());

                }
                mCategoryAssignModelList.get(clickedPos).setChecked(cb.isChecked());
                notifyItemChanged(position);
            }
        });

    }
    public List<CategoryAssignModel> getSelectedItemlist() {
        List<CategoryAssignModel> newList = new ArrayList<>();
        for (int i = 0; i < mCategoryAssignModelList.size(); i++) {
            CategoryAssignModel itemModel = mCategoryAssignModelList.get(i);
            if (itemModel.isChecked()) {
                newList.add(itemModel);
            }
        }
        return newList;
    }

    @Override
    public int getItemCount() {
        return mCategoryAssignModelList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<CategoryAssignModel> getCategoryAssignModel() {
        return mCategoryAssignModelList;
    }

    public void addChatMassgeModel(CategoryAssignModel mCategoryAssignModel) {
        try {
            this.mCategoryAssignModelList.add(mCategoryAssignModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCategoryAssignModelList(List<CategoryAssignModel> mCategoryAssignModelList) {
        this.mCategoryAssignModelList = mCategoryAssignModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final CategoryassignCellBinding binding;

        public ViewHolder(final View view, final CategoryassignCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mCategoryAssignModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final CategoryAssignModel mCategoryAssignModel) {
            // this.binding.setCategoryAssign(mCategoryAssignModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mCategoryAssignModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mCategoryAssignModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mCategoryAssignModelList.size();
        mCategoryAssignModelList.clear();
        notifyItemRangeRemoved(0, size);
    }
    public void setSelected(String data) {
        String[] dataarray = data.split(",");
        for (int i = 0; i < dataarray.length; i++) {
            for (int j = 0; j < mCategoryAssignModelList.size(); j++) {
                if (dataarray[i].equalsIgnoreCase(mCategoryAssignModelList.get(j).getId())) {
                    mCategoryAssignModelList.get(j).setChecked(true);
                }
            }
        }
        notifyDataSetChanged();
    }
    public void setSelectedClear(String data){
        for (int j = 0; j < mCategoryAssignModelList.size(); j++) {
            mCategoryAssignModelList.get(j).setChecked(false);
        }
        notifyDataSetChanged();
    }
}