package com.pcamarounds.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.pcamarounds.R;
import com.pcamarounds.activities.PhotoFullPopupWindow;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.databinding.RatingreviewCellBinding;
import com.pcamarounds.models.RatingReviewModel;
import com.pcamarounds.utils.TimeAgo;
import com.pcamarounds.utils.Utility;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 28,March,2020
 */
public class RatingReviewAdapter extends RecyclerView.Adapter<RatingReviewAdapter.ViewHolder> {


    private final Context mContext;
    private List<RatingReviewModel> mRatingReviewModelList;
    private OnItemClickListener onItemClickListener;
    private int currentPos = -1;

    public RatingReviewAdapter(Context mContext, List<RatingReviewModel> mRatingReviewModelList) {
        this.mRatingReviewModelList = mRatingReviewModelList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final RatingreviewCellBinding binding = DataBindingUtil.inflate(inflater, R.layout.ratingreview_cell, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final RatingReviewModel mRatingReviewModel = this.mRatingReviewModelList.get(position);
        holder.bind(mRatingReviewModel);
        if (Utility.isCheckEmptyOrNull(mRatingReviewModel.getLastName())) {
            holder.binding.tvName.setText(Utility.capitalize(mRatingReviewModel.getFirstName()+" "+Utility.printInitials(mRatingReviewModel.getLastName())));
        } else {
            holder.binding.tvName.setText(Utility.capitalize(mRatingReviewModel.getFirstName()));
        }
        holder.binding.tvComment.setText(mRatingReviewModel.getComment());
        holder.binding.rating.setRating(mRatingReviewModel.getAverage());
        holder.binding.tvRating.setText(""+Utility.roundOffFloatWithDecimal(mRatingReviewModel.getAverage(),1));

        if (Utility.isCheckEmptyOrNull(mRatingReviewModel.getProfileImage())) {
            Picasso.get().load(AppConstants.PROFILE_IMG_CLIENT+mRatingReviewModel.getUserId()+"/"+mRatingReviewModel.getProfileImage()).placeholder(R.drawable.profile_default).into(holder.binding.ivImage);
            holder.binding.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = AppConstants.PROFILE_IMG_CLIENT+mRatingReviewModel.getUserId()+"/"+mRatingReviewModel.getProfileImage();
                    new PhotoFullPopupWindow(mContext, R.layout.popup_photo_full_two, v, url, null);
                }
            });
        }


      /*  String datetime = mRatingReviewModel.getCreatedOn();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            long time = sdf.parse(datetime).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            holder.binding.tvAmount.setText("$" + jobprice + " USD en - " + ago);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

      /*  SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String dateStr = "2016-01-24T16:00:00.000Z";
        Date date = inputFormat.parse(dateStr);
        String niceDateStr = DateUtils.getRelativeTimeSpanString(date.getTime() , Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);*/


        String time = mRatingReviewModel.getCreatedOn();
        TimeAgo timeAgo2 = new TimeAgo();
        String MyFinalValue = timeAgo2.covertTimeToText(time);

        if (Utility.isCheckEmptyOrNull(mRatingReviewModel.getBookingAmount())) {
            double amount = Double.parseDouble(mRatingReviewModel.getBookingAmount());
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            holder.binding.tvAmount.setText("$" + formatter.format(amount) + " - " + MyFinalValue);
        }

       // holder.binding.tvAmount.setText("$" + jobprice + " USD - " + MyFinalValue);
    }

    @Override
    public int getItemCount() {
        return mRatingReviewModelList.size();

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public List<RatingReviewModel> getRatingReviewModel() {
        return mRatingReviewModelList;
    }

    public void addChatMassgeModel(RatingReviewModel mRatingReviewModel) {
        try {
            this.mRatingReviewModelList.add(mRatingReviewModel);
            notifyItemInserted(getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRatingReviewModelList(List<RatingReviewModel> mRatingReviewModelList) {
        this.mRatingReviewModelList = mRatingReviewModelList;
        notifyDataSetChanged();
    }

    private void dataSetChanged() {
        this.notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final RatingreviewCellBinding binding;

        public ViewHolder(final View view, final RatingreviewCellBinding binding) {
            super(view);
            this.binding = binding;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (onItemClickListener != null) {
                    setCurrentPos(getAdapterPosition());
                    notifyItemRangeChanged(0, mRatingReviewModelList.size());
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @UiThread
        public void bind(final RatingReviewModel mRatingReviewModel) {
            // this.binding.setRatingReview(mRatingReviewModel);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void deleteitem(int position) {
        mRatingReviewModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mRatingReviewModelList.size());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void clearAdapter() {
        int size = mRatingReviewModelList.size();
        mRatingReviewModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

}