package com.pcamarounds.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.pcamarounds.R;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.databinding.AddonCellBinding;
import com.pcamarounds.models.CheckBoxModel;
import com.pcamarounds.models.RadioBoxModel;
import com.pcamarounds.models.postdetail.Addon;
import com.pcamarounds.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 18,April,2020
 */
public class AddonAdapter extends RecyclerView.Adapter<AddonAdapter.ViewHolder> {


    private final Context mContext;
    private List<Addon> mAddonList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public AddonAdapter(Context mContext, List<Addon> mAddonList) {
        this.mAddonList = mAddonList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final AddonCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.addon_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Addon mAddon = this.mAddonList.get(position);
        holder.bind(mAddon);

        switch (mAddon.getType()) {
            case "textfield":
                if (Utility.isCheckEmptyOrNull(mAddon.getAnswer())) {
                    holder.binding.tvTitle1.setText(mAddon.getQuestionTitle());
                    holder.binding.etAnswer1.setText(mAddon.getAnswer());
                    holder.binding.llTextField1.setVisibility(View.VISIBLE);
                }else {
                    holder.binding.llTextField1.setVisibility(View.GONE);
                }
                break;
            case "textarea":
                if (Utility.isCheckEmptyOrNull(mAddon.getAnswer())) {
                    holder.binding.tvTitleArea1.setText(mAddon.getQuestionTitle());
                    holder.binding.etAnswerArea1.setText(mAddon.getAnswer());
                    holder.binding.llTextArea1.setVisibility(View.VISIBLE);
                }else {
                    holder.binding.llTextArea1.setVisibility(View.GONE);
                }
                break;
            case "checkbox":
                if (mAddon.getAnswer() != null && !mAddon.getAnswer().equals("")) {
                    List<CheckBoxModel> optionsModels = Controller.getGson().fromJson(mAddon.getAnswer(),
                            new TypeToken<ArrayList<CheckBoxModel>>() {
                            }.getType());

                    List<CheckBoxModel> selectedBoxAnsModels = new ArrayList<>();
                    if (optionsModels.size() > 0) {
                        for (int i = 0; i < optionsModels.size(); i++) {
                            if (optionsModels.get(i).getIsselected().equals("true")) {
                                CheckBoxModel selectedBoxAnsModel = new CheckBoxModel();
                                selectedBoxAnsModel.setIsselected(optionsModels.get(i).getIsselected());
                                selectedBoxAnsModel.setOption(optionsModels.get(i).getOption());
                                selectedBoxAnsModels.add(selectedBoxAnsModel);
                                holder.binding.llCheckBox.setVisibility(View.VISIBLE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                                holder.binding.tvTitleCheckBox.setText(mAddon.getQuestionTitle());
                                holder.binding.recycheck.setLayoutManager(linearLayoutManager);
                                holder.binding.recycheck.setHasFixedSize(true);
                                CheckBoxSelectedAdapter serviceDetailDatumAdapter = new CheckBoxSelectedAdapter(mContext, selectedBoxAnsModels);
                                holder.binding.recycheck.setAdapter(serviceDetailDatumAdapter);
                                serviceDetailDatumAdapter.notifyDataSetChanged();
                            } else {
                                // holder.binding.rlCheckBox.setVisibility(View.GONE);
                            }
                        }
                    }
                } else {
                    holder.binding.llCheckBox.setVisibility(View.GONE);
                }
                break;
            case "dropdown":
                if (mAddon.getAnswer() != null && !mAddon.getAnswer().equals("")) {
                    List<RadioBoxModel> optionsModels = Controller.getGson().fromJson(mAddon.getAnswer(),
                            new TypeToken<ArrayList<RadioBoxModel>>() {
                            }.getType());
                    if (optionsModels.size() > 0) {
                        for (int i = 0; i < optionsModels.size(); i++) {
                            if (optionsModels.get(i).getIsselected().equals("true")) {
                                holder.binding.tvTitleRadio.setText(mAddon.getQuestionTitle());
                                holder.binding.llRadio.setVisibility(View.VISIBLE);
                                holder.binding.rb.setText(optionsModels.get(i).getOption());
                            } else {
                                // holder.binding.rldropdown.setVisibility(View.GONE);
                            }
                        }
                    }
                } else {
                    holder.binding.llRadio.setVisibility(View.GONE);
                }

                break;
        }

    }

    @Override
    public int getItemCount() {
        return mAddonList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<Addon> getAddon() {
        return mAddonList;
    }

    public void addChatMassgeModel(Addon mAddon) {
        try {
            this.mAddonList.add(mAddon);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAddonList(List<Addon> mAddonList) {
        this.mAddonList = mAddonList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AddonCellBinding binding;

        public ViewHolder(final View view, final AddonCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mAddonList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final Addon mAddon) {
            // this.binding.setAddon(mAddon);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mAddonList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mAddonList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mAddonList.size();
        mAddonList.clear();
        notifyItemRangeRemoved(0, size);
    }

}