package com.pcamarounds.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.pcamarounds.R;
import com.pcamarounds.activities.ImageSliderJobActivity;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.databinding.ImagesCellBinding;
import com.pcamarounds.models.postdetail.ImagesModel;
import com.pcamarounds.utils.Utility;

import java.io.Serializable;
import java.util.List;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 21,March,2020
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private static final String TAG = "ImagesAdapter";
    private final Context mContext;
    private List<ImagesModel> mImagesModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;
    private String user_id = "";

    public ImagesAdapter(Context mContext, List<ImagesModel> mImagesModelList) {
        this.mImagesModelList = mImagesModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ImagesCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.images_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ImagesModel mImagesModel = this.mImagesModelList.get(position);
        holder.bind(mImagesModel);

        Picasso.get()
                .load(AppConstants.DEVICES_IMG+user_id+"/" + mImagesModel.getImage())
                .placeholder(mContext.getResources().getDrawable(R.color.colorShimmer))
                .into(holder.binding.ivImage);

        Log.e(TAG, "onBindViewHolder: " + AppConstants.DEVICES_IMG+user_id+"/" + mImagesModel.getImage());

        if (Utility.isCheckEmptyOrNull(mImagesModel.getImage())) {
            holder.binding.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // String url = AppConstants.DEVICES_IMG +user_id+"/"+ mImagesModel.getImage();
                   // new PhotoFullPopupWindow(mContext, R.layout.popup_photo_full_two, v, url, null);
                    Intent intent = new Intent(mContext, ImageSliderJobActivity.class);
                    intent.putExtra(AppConstants.LIST, (Serializable) mImagesModelList);
                    intent.putExtra(AppConstants.USER_ID, user_id);
                    intent.putExtra(AppConstants.POSITION, position);
                    mContext.startActivity(intent);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mImagesModelList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<ImagesModel> getImagesModel() {
        return mImagesModelList;
    }

    public void addChatMassgeModel(ImagesModel mImagesModel) {
        try {
            this.mImagesModelList.add(mImagesModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setImagesModelList(List<ImagesModel> mImagesModelList,String user_id) {
        this.mImagesModelList = mImagesModelList;
        this.user_id = user_id;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImagesCellBinding binding;

        public ViewHolder(final View view, final ImagesCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mImagesModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final ImagesModel mImagesModel) {
            // this.binding.setImages(mImagesModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mImagesModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mImagesModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mImagesModelList.size();
        mImagesModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

}