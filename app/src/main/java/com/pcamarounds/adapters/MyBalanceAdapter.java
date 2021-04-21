package com.pcamarounds.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pcamarounds.R;
import com.pcamarounds.databinding.MybalanceCellBinding;
import com.pcamarounds.models.MyBalanceModel;
import com.pcamarounds.utils.Utility;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 21,March,2020
 */
public class MyBalanceAdapter extends RecyclerView.Adapter<MyBalanceAdapter.ViewHolder> {
    private static final String TAG = "MyBalanceAdapter";
    private final Context mContext;
    private List<MyBalanceModel> mMyBalanceModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public MyBalanceAdapter(Context mContext, List<MyBalanceModel> mMyBalanceModelList) {
        this.mMyBalanceModelList = mMyBalanceModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final MybalanceCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.mybalance_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MyBalanceModel mMyBalanceModel = this.mMyBalanceModelList.get(position);
        holder.bind(mMyBalanceModel);

        if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getTransType()) && mMyBalanceModel.getTransType().equals("debit")) {
            if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getTransAmount())) {
                double amount = Double.parseDouble(mMyBalanceModel.getTransAmount());
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                DecimalFormat formatter = (DecimalFormat) nf;
                formatter.applyPattern("#,###0.00");
                holder.binding.tvBal.setText("-$" + formatter.format(amount).replace("-", ""));

                if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getPayType()) && mMyBalanceModel.getPayType().equals("weekly")) {
                    holder.binding.tvType.setText("Depósito Realizado");
                    holder.binding.tvName.setText("Pago de Camarounds");
                    holder.binding.tvBal.setTextColor(mContext.getResources().getColor(R.color.colorDarkBlack));
                } else if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getPayType()) && mMyBalanceModel.getPayType().equals("adjustment")) {
                    holder.binding.tvType.setText("Cargo de Camarounds");
                    holder.binding.tvName.setText(Utility.capitalize(mMyBalanceModel.getDescription()));
                    holder.binding.tvBal.setTextColor(mContext.getResources().getColor(R.color.red));
                } else if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getPayType()) && mMyBalanceModel.getPayType().equals("fee")) {
                    holder.binding.tvType.setText("Comisión Camaround");
                    if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getLastName())) {
                        holder.binding.tvName.setText(Utility.capitalize(mMyBalanceModel.getFirstName() + " " + Utility.printInitials(mMyBalanceModel.getLastName()) + " - " + mMyBalanceModel.getServiceName()));
                    } else {
                        holder.binding.tvName.setText(Utility.capitalize(mMyBalanceModel.getFirstName() + " - " + mMyBalanceModel.getServiceName()));
                    }
                    holder.binding.tvBal.setTextColor(mContext.getResources().getColor(R.color.red));
                } else if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getPayType()) && mMyBalanceModel.getPayType().equals("wallet_pay")) {
                    holder.binding.tvType.setText("Pago a Camarounds");
                    holder.binding.tvName.setText("Tarjeta de crédito");
                    holder.binding.tvBal.setTextColor(mContext.getResources().getColor(R.color.red));
                } else if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getPayType()) && mMyBalanceModel.getPayType().equals("bank_pay")) {
                    holder.binding.tvType.setText("Pago a Camarounds");
                    holder.binding.tvName.setText("Transferencia bancaria");
                    holder.binding.tvBal.setTextColor(mContext.getResources().getColor(R.color.red));
                } else {
                    holder.binding.tvType.setText("Pago Realizado");
                    if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getLastName())) {
                        holder.binding.tvName.setText(Utility.capitalize(mMyBalanceModel.getFirstName() + " " + Utility.printInitials(mMyBalanceModel.getLastName()) + " - " + mMyBalanceModel.getServiceName()));
                    } else {
                        holder.binding.tvName.setText(Utility.capitalize(mMyBalanceModel.getFirstName() + " - " + mMyBalanceModel.getServiceName()));
                    }
                    holder.binding.tvBal.setTextColor(mContext.getResources().getColor(R.color.red));
                }
            }
        } else {
            if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getTransAmount())) {
                double amount = Double.parseDouble(mMyBalanceModel.getTransAmount());
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                DecimalFormat formatter = (DecimalFormat) nf;
                formatter.applyPattern("#,###0.00");
                holder.binding.tvBal.setText("$" + formatter.format(amount));
                if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getPayType()) && mMyBalanceModel.getPayType().equals("weekly")) {
                    holder.binding.tvType.setText("Depósito Realizado");
                    holder.binding.tvName.setText("Pago de Camarounds");
                    holder.binding.tvBal.setTextColor(mContext.getResources().getColor(R.color.colorDarkBlack));
                } else if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getPayType()) && mMyBalanceModel.getPayType().equals("adjustment")) {
                    holder.binding.tvType.setText("Cargo de Camarounds");
                    holder.binding.tvName.setText(Utility.capitalize(mMyBalanceModel.getDescription()));
                    holder.binding.tvBal.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                } else if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getPayType()) && mMyBalanceModel.getPayType().equals("fee")) {
                    holder.binding.tvType.setText("Comisión Camaround");
                    if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getLastName())) {
                        holder.binding.tvName.setText(Utility.capitalize(mMyBalanceModel.getFirstName() + " " + Utility.printInitials(mMyBalanceModel.getLastName()) + " - " + mMyBalanceModel.getServiceName()));
                    } else {
                        holder.binding.tvName.setText(Utility.capitalize(mMyBalanceModel.getFirstName() + " - " + mMyBalanceModel.getServiceName()));
                    }
                    holder.binding.tvBal.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                } else if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getPayType()) && mMyBalanceModel.getPayType().equals("wallet_pay")) {
                    holder.binding.tvType.setText("Pago a Camarounds");
                    holder.binding.tvName.setText("Tarjeta de crédito");
                    holder.binding.tvBal.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                } else if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getPayType()) && mMyBalanceModel.getPayType().equals("bank_pay")) {
                    holder.binding.tvType.setText("Pago a Camarounds");
                    holder.binding.tvName.setText("Transferencia bancaria");
                    holder.binding.tvBal.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                } else {
                    holder.binding.tvType.setText("Pago Realizado");
                    if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getLastName())) {
                        holder.binding.tvName.setText(Utility.capitalize(mMyBalanceModel.getFirstName() + " " + Utility.printInitials(mMyBalanceModel.getLastName()) + " - " + mMyBalanceModel.getServiceName()));
                    } else {
                        holder.binding.tvName.setText(Utility.capitalize(mMyBalanceModel.getFirstName() + " - " + mMyBalanceModel.getServiceName()));
                    }
                    holder.binding.tvBal.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                }
            }

        }
        if (Utility.isCheckEmptyOrNull(mMyBalanceModel.getTransDateTime())) {
            holder.binding.tvDate.setVisibility(View.VISIBLE);
            holder.binding.tvDate.setText(Utility.change_format(mMyBalanceModel.getTransDateTime(), Utility.FORMAT_Z, "dd/MM/yyyy"));

        } else {
            holder.binding.tvDate.setVisibility(View.VISIBLE);
        }

        // holder.binding.tvTotBal.setText("Bal - $"+total);

    }

    @Override
    public int getItemCount() {
        return mMyBalanceModelList.size();

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<MyBalanceModel> getMyBalanceModel() {
        return mMyBalanceModelList;
    }

    public void addChatMassgeModel(MyBalanceModel mMyBalanceModel) {
        try {
            this.mMyBalanceModelList.add(mMyBalanceModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMyBalanceModelList(List<MyBalanceModel> mMyBalanceModelList) {
        this.mMyBalanceModelList = mMyBalanceModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MybalanceCellBinding binding;

        public ViewHolder(final View view, final MybalanceCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mMyBalanceModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final MyBalanceModel mMyBalanceModel) {
            // this.binding.setMyBalance(mMyBalanceModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mMyBalanceModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mMyBalanceModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mMyBalanceModelList.size();
        mMyBalanceModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

}