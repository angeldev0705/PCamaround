package com.pcamarounds.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.pcamarounds.R;
import com.pcamarounds.databinding.CardlistCellBinding;
import com.pcamarounds.models.CardListModel;
import com.pcamarounds.utils.Utility;

import java.util.List;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 11,June,2020
 */
public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {


    private final Context mContext;
    private List<CardListModel> mCardListModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public CardListAdapter(Context mContext, List<CardListModel> mCardListModelList) {
        this.mCardListModelList = mCardListModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final CardlistCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.cardlist_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CardListModel mCardListModel = this.mCardListModelList.get(position);
        holder.bind(mCardListModel);

        holder.binding.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.rb.performClick();
            }
        });
        if (position == 0) {
            holder.binding.tvMonthYear.setVisibility(View.GONE);
            holder.binding.tvAddCard.setText(mCardListModel.getCcnumber());
        }else {
            holder.binding.tvMonthYear.setVisibility(View.VISIBLE);
            String number = mCardListModel.getCcnumber();
            String mask = number.replaceAll("\\w(?=\\w{4})", "*");
            holder.binding.tvAddCard.setText(mask);
        }
        holder.binding.rb.setOnCheckedChangeListener(null);
        holder.binding.rb.setChecked(position == currentPos);
      /*  holder.binding.rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentPos = position;
                notifyDataSetChanged();
            }
        });*/
        changeIcon(holder.binding.tvAddCard,mCardListModel.getCardType());
        if (Utility.isCheckEmptyOrNull(mCardListModel.getCcexp()) && mCardListModel.getCcexp().length()>3)
        {
            String mm = mCardListModel.getCcexp().charAt(0)+""+mCardListModel.getCcexp().charAt(1);
            String yy = mCardListModel.getCcexp().charAt(2)+""+mCardListModel.getCcexp().charAt(3);
            holder.binding.tvMonthYear.setText(mm+"/"+yy);
        }else {
            holder.binding.tvMonthYear.setText(mCardListModel.getCcexp());
        }
    }
    private void changeIcon(TextView textView, String s) {
        if (Utility.isCheckEmptyOrNull(s)) {
            if (s.equals("Visa")) {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vi, 0, 0, 0);
                //type = "Visa";
            } else if (s.equals("MasterCard")) {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mc, 0, 0, 0);
                //type = "MasterCard";
            } else if (s.equals("American_Express")) {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.am, 0, 0, 0);
                // type = "American_Express";
            } else if (s.equals("Discover")) {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ds, 0, 0, 0);
                // type = "Discover";
            } else if (s.equals("JCB")) {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.jcb, 0, 0, 0);
                // type = "JCB";
            } else if (s.equals("Diners_Club")) {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dc, 0, 0, 0);
                // type = "Diners_Club";
            } else if (s.equals("Maestro_Card")) {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.maestro, 0, 0, 0);
                // type = "Maestro_Card";
            } else if (s.equals("unknown")) {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.card, 0, 0, 0);
                // type = "Maestro_Card";
            } else {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.card, 0, 0, 0);
                // type = "UNKNOWN";
            }
        }else {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.card, 0, 0, 0);

        }
    }

    @Override
    public int getItemCount() {
        return mCardListModelList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<CardListModel> getCardListModel() {
        return mCardListModelList;
    }

    public void addChatMassgeModel(CardListModel mCardListModel) {
        try {
            this.mCardListModelList.add(mCardListModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCardListModelList(List<CardListModel> mCardListModelList) {
        this.mCardListModelList = mCardListModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final CardlistCellBinding binding;

        public ViewHolder(final View view, final CardlistCellBinding binding) {
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
                    notifyItemRangeChanged(0, mCardListModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final CardListModel mCardListModel) {
            // this.binding.setCardList(mCardListModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mCardListModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mCardListModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mCardListModelList.size();
        mCardListModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

}