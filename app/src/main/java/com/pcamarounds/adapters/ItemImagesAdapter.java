package com.pcamarounds.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pcamarounds.R;
import com.pcamarounds.activities.ImageSliderActivity;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.ItemimagesCellBinding;
import com.pcamarounds.models.ItemImagesModel;

import com.squareup.picasso.Picasso;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ItemImagesAdapter extends RecyclerView.Adapter<ItemImagesAdapter.ViewHolder> {

    private static final String TAG = "ItemImagesAdapter";
    private final Context mContext;
    private List<ItemImagesModel> mItemImagesModelList;
    private OnItemClickListener onItemClickListener;
    private String action = "";

    public ItemImagesAdapter(Context mContext, List<ItemImagesModel> mItemImagesModelList) {
        this.mItemImagesModelList = mItemImagesModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ItemimagesCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.itemimages_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ItemImagesModel mItemImagesModel = this.mItemImagesModelList.get(position);
        holder.bind(mItemImagesModel);
        if (mItemImagesModel.getmType() != null && !mItemImagesModel.getmType().isEmpty()) {
           // Log.e(TAG, "onBindViewHolder: ->>>>> " + mItemImagesModel.getImage());
            Picasso.get()
                    .load("file://" + mItemImagesModel.getImage())
                    .placeholder(mContext.getResources().getDrawable(R.color.colorShimmer))
                    .into(holder.binding.ivimage);
            holder.binding.ivdelete.setVisibility(View.VISIBLE);
            Log.e(TAG, "onBindViewHolder: iff " + "file://" + mItemImagesModel.getImage() );
        } else {
            Log.e(TAG, "onBindViewHolder: else " + AppConstants.PROFILE_WORK + mItemImagesModel.getImage() );
           // Log.e(TAG, "onBindViewHolder: ->>>>> " +AppConstants.PROFILE_WORK + mItemImagesModel.getImage());
            holder.binding.ivdelete.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(AppConstants.PROFILE_WORK + mItemImagesModel.getImage())
                    .placeholder(mContext.getResources().getDrawable(R.color.colorShimmer))
                    .into(holder.binding.ivimage);
        }
        holder.binding.ivimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, ImageSliderActivity.class);
                intent.putExtra(AppConstants.LIST, (Serializable) mItemImagesModelList);
                intent.putExtra(AppConstants.POSITION, position);
                mContext.startActivity(intent);

               /* if (Utility.isCheckEmptyOrNull(mItemImagesModel.getmType())) {
                    String url = "file://" + mItemImagesModel.getImage();
                    new PhotoFullPopupWindow(mContext, R.layout.popup_photo_full_two, v, url, null);
                } else {
                    String url = AppConstants.PROFILE_WORK + mItemImagesModel.getImage();
                    new PhotoFullPopupWindow(mContext, R.layout.popup_photo_full_two, v, url, null);
                }*/
            }
        });

        holder.binding.ivdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isCheckEmptyOrNull(mItemImagesModel.getmType())) {
                    deleteitem(position);
                    if (mItemImagesModelList.size() == 0)
                    {
                        Intent intent = new Intent(AppConstants.BROADCAST);
                        intent.putExtra(AppConstants.KEY, "size");
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    }
                } else {
                    delete_images(mItemImagesModel.getId(), mItemImagesModel.getImage(), position);
                }
            }
        });

        if (Utility.isCheckEmptyOrNull(action) && action.equals("view"))
        {
            holder.binding.ivdelete.setVisibility(View.GONE);
        }else {
            holder.binding.ivdelete.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mItemImagesModelList.size();

    }

    private void delete_images(String id, String image, int position1) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(mContext));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        hashMap.put(AppConstants.IMAGE_ID, id);
        hashMap.put(AppConstants.OLD_FILE, image);
        Log.e(TAG, "delete_images: " + hashMap);

        RetrofitClient.getContentData(new Dialog(mContext), RetrofitClient.service(mContext).delete_images(Utility.getHeaderAuthentication(mContext),hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        deleteitem(position1);
                        if (mItemImagesModelList.size() == 0)
                        {
                            Intent intent = new Intent(AppConstants.BROADCAST);
                            intent.putExtra(AppConstants.KEY, "size");
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                        }

                    } else if (jsonObject.getInt("status") == 0) {
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(mContext, jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(String error) {
                Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<ItemImagesModel> getItemImagesModel() {
        return mItemImagesModelList;
    }

    public void addChatMassgeModel(ItemImagesModel mItemImagesModel) {
        try {
            this.mItemImagesModelList.add(mItemImagesModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setItemImagesModelList(List<ItemImagesModel> mItemImagesModelList,String action) {
        this.mItemImagesModelList = mItemImagesModelList;
        this.action = action;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemimagesCellBinding binding;

        public ViewHolder(final View view, final ItemimagesCellBinding binding) {
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
        public void bind(final ItemImagesModel mItemImagesModel) {
            //this.binding.setItemImagesModel(mItemImagesModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    private void deleteitem(int position) {
        mItemImagesModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mItemImagesModelList.size());
    }
}