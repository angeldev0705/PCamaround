package com.pcamarounds.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pcamarounds.R;
import com.pcamarounds.databinding.MessagesCellBinding;
import com.pcamarounds.models.MessagesModel;

import java.util.List;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 30,March,2020
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {


    private final Context mContext;
    private List<MessagesModel> mMessagesModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public MessagesAdapter(Context mContext, List<MessagesModel> mMessagesModelList) {
        this.mMessagesModelList = mMessagesModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final MessagesCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.messages_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
     /*   final MessagesModel mMessagesModel = this.mMessagesModelList.get(position);
        holder.bind(mMessagesModel);

        if (position == currentPos) {
            // selected true
        } else {
            // selected false
        }*/

    }

    @Override
    public int getItemCount() {
        //return mMessagesModelList.size();
        return 10;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<MessagesModel> getMessagesModel() {
        return mMessagesModelList;
    }

    public void addChatMassgeModel(MessagesModel mMessagesModel) {
        try {
            this.mMessagesModelList.add(mMessagesModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMessagesModelList(List<MessagesModel> mMessagesModelList) {
        this.mMessagesModelList = mMessagesModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MessagesCellBinding binding;

        public ViewHolder(final View view, final MessagesCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mMessagesModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final MessagesModel mMessagesModel) {
            // this.binding.setMessages(mMessagesModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mMessagesModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mMessagesModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mMessagesModelList.size();
        mMessagesModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

}