package com.pcamarounds.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.pcamarounds.R;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.controller.SessionManager;
import com.pcamarounds.databinding.CardlistshowCellBinding;
import com.pcamarounds.models.CardListModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 20,July,2020
 */
public class CardListShowAdapter extends RecyclerView.Adapter<CardListShowAdapter.ViewHolder> {


    private final Context mContext;
    private List<CardListModel> mCardListModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;
    private Dialog dialog;

    public CardListShowAdapter(Context mContext, List<CardListModel> mCardListModelList) {
        this.mCardListModelList = mCardListModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final CardlistshowCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.cardlistshow_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CardListModel mCardListModel = this.mCardListModelList.get(position);
        holder.bind(mCardListModel);
        String number = mCardListModel.getCcnumber();
        String mask = number.replaceAll("\\w(?=\\w{4})", "*");
        holder.binding.tvAddCard.setText(mask);
        holder.binding.ivDelete.setOnClickListener(v -> mark_complete_confirmation(mCardListModel.getId(), position));
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
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.card, 0, 0, 0);
            // type = "UNKNOWN";
        }

    }

    /*
    private void changeIcon(TextView textView,String s) {
        if (s.startsWith("4") || s.matches(CardPattern.VISA)) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vi,0,0, 0);
            //type = "Visa";
        } else if (s.matches(CardPattern.MASTERCARD_SHORTER) || s.matches(CardPattern.MASTERCARD_SHORT) || s.matches(CardPattern.MASTERCARD)) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mc,0,0, 0);
            //type = "MasterCard";
        } else if (s.matches(CardPattern.AMERICAN_EXPRESS)) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.am,0,0, 0);
           // type = "American_Express";
        } else if (s.matches(CardPattern.DISCOVER_SHORT) || s.matches(CardPattern.DISCOVER)) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ds,0,0, 0);
           // type = "Discover";
        } else if (s.matches(CardPattern.JCB_SHORT) || s.matches(CardPattern.JCB)) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.jcb,0,0, 0);
           // type = "JCB";
        } else if (s.matches(CardPattern.DINERS_CLUB_SHORT) || s.matches(CardPattern.DINERS_CLUB)) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dc,0,0, 0);
           // type = "Diners_Club";
        }  else if (s.matches(Utility.Maestro_Card)) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.maestro,0,0, 0);
            // type = "Maestro_Card";
        }
        else {
            textView.setCompoundDrawablesWithIntrinsicBounds(0,0,0, 0);
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.card,0,0, 0);
           // type = "UNKNOWN";
        }

    }
*/
    private void mark_complete_confirmation(String id, int pos) {
        dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.mark_complete_confirmation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView textView = dialog.findViewById(R.id.tvmsggggg);
        ImageView ivwarningimage = dialog.findViewById(R.id.ivwarningimage);
        com.google.android.material.button.MaterialButton btnConfirm = dialog.findViewById(R.id.btnConfirm);
        com.google.android.material.button.MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);
        textView.setText("¿Estás seguro deseas eliminar esta tarjeta?");
        btnConfirm.setText("si");
        btnCancel.setText("no");
        ivwarningimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_cancel_icon));
        dialog.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                delete_card(id,pos);

            }
        });
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void delete_card(String id, int pos) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(mContext));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS_TOKEN, SessionManager.getAccessToken(mContext));
        if (RealmController.getUser() != null) {
            hashMap.put(AppConstants.UID, RealmController.getUser().getId() + "");
        } else {
            hashMap.put(AppConstants.UID, "");
        }
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.CART_ID, id);

        RetrofitClient.getContentData(new Dialog(mContext), RetrofitClient.service(mContext).delete_card(Utility.getHeaderAuthentication(mContext), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        deleteitem(pos);
                    } else if (jsonObject.getInt("status") == 0) {
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
        private final CardlistshowCellBinding binding;

        public ViewHolder(final View view, final CardlistshowCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
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
            // this.binding.setCardListShow(mCardListModel);
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