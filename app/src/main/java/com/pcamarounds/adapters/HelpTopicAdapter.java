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
import com.pcamarounds.databinding.HelptopicCellBinding;
import com.pcamarounds.models.helpsupport.HelpTopic;

import java.util.List;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 26,May,2020
 */
public class HelpTopicAdapter extends RecyclerView.Adapter<HelpTopicAdapter.ViewHolder> {


    private final Context mContext;
    private List<HelpTopic> mHelpTopicList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public HelpTopicAdapter(Context mContext, List<HelpTopic> mHelpTopicList) {
        this.mHelpTopicList = mHelpTopicList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final HelptopicCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.helptopic_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final HelpTopic mHelpTopic = this.mHelpTopicList.get(position);
        holder.bind(mHelpTopic);

        holder.binding.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.rb.performClick();
            }
        });

        holder.binding.rb.setText(mHelpTopic.getType());
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
        return mHelpTopicList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<HelpTopic> getHelpTopic() {
        return mHelpTopicList;
    }

    public void addChatMassgeModel(HelpTopic mHelpTopic) {
        try {
            this.mHelpTopicList.add(mHelpTopic);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setHelpTopicList(List<HelpTopic> mHelpTopicList) {
        this.mHelpTopicList = mHelpTopicList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final HelptopicCellBinding binding;

        public ViewHolder(final View view, final HelptopicCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mHelpTopicList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final HelpTopic mHelpTopic) {
            // this.binding.setHelpTopic(mHelpTopic);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mHelpTopicList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mHelpTopicList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mHelpTopicList.size();
        mHelpTopicList.clear();
        notifyItemRangeRemoved(0, size);
    }

}