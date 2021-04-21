package com.pcamarounds.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.pcamarounds.R;
import com.pcamarounds.adapters.RatingReviewAdapter;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.ActivityUserProfileBinding;
import com.pcamarounds.models.RatingReviewModel;
import com.pcamarounds.models.UserInfo;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.EqualSpacing;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG = "UserProfileActivity";
    private Context context;
    private ActivityUserProfileBinding binding;
    private RatingReviewAdapter ratingReviewAdapter;
    private List<RatingReviewModel> ratingReviewModelList = new ArrayList<>();
    private String userid = "", url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile);
        context = this;
        initView();

    }

    private void initView() {
        userid = getIntent().getStringExtra(AppConstants.USER_ID);
        setupRvRating();
        user_reviews();
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isCheckEmptyOrNull(url)) {
                    new PhotoFullPopupWindow(context, R.layout.popup_photo_full_two, v, url, null);
                }

            }
        });
    }

    private void setupRvRating() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerViewRating.setLayoutManager(linearLayoutManager1);
        binding.recyclerViewRating.addItemDecoration(new EqualSpacing(8, context));
        ratingReviewAdapter = new RatingReviewAdapter(context, ratingReviewModelList);
        binding.recyclerViewRating.setAdapter(ratingReviewAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void user_reviews() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, "user");
        hashMap.put(AppConstants.UID, userid);


        Log.e(TAG, "tech_reviews_list: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).user_reviews(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        UserInfo loginModel = Controller.getGson().fromJson(jsonObject.getJSONObject("user_info").toString(), UserInfo.class);
                        binding.rating.setRating(loginModel.getRating());
                        //binding.tvRating.setText("" + loginModel.getRating());

                        if (loginModel.getTotalReviews() != 0) {
                            if (loginModel.getTotalReviews() > 1) {
                                binding.tvTotRating.setText(loginModel.getTotalReviews() + " Calificaciones");
                            } else {
                                binding.tvTotRating.setText(loginModel.getTotalReviews() + " Calificación");
                            }
                        }else {
                            binding.tvTotRating.setText("Sin Calificaciones");
                        }
                        binding.tvRating.setText(Utility.roundOffFloatWithDecimal(RealmController.getUser().getRating(), 1) + "");
                        if (Utility.isCheckEmptyOrNull(RealmController.getUser().getPerformance())) {
                            binding.tvStatus.setText(Utility.roundOffFloatWithDecimal(Float.parseFloat(RealmController.getUser().getPerformance()), 2) + "% Completados");
                        }
                        //binding.tvStatus.setText(loginModel.getPerformance() + "% Completados");
                        binding.tvName.setText(Utility.capitalize(loginModel.getFirstName() + " " + Utility.printInitials(loginModel.getLastName())));

                        Picasso.get().load(AppConstants.PROFILE_IMG_CLIENT + userid + "/" + loginModel.getProfileImage()).placeholder(R.drawable.profile_default).into(binding.ivProfile);
                        if (Utility.isCheckEmptyOrNull(loginModel.getProfileImage())) {
                            url = AppConstants.PROFILE_IMG_CLIENT + userid + "/" + loginModel.getProfileImage();
                        }
                        ratingReviewModelList.clear();
                        ratingReviewModelList = Controller.getGson().fromJson(jsonObject.getJSONArray("job_reviews").toString(),
                                new TypeToken<ArrayList<RatingReviewModel>>() {
                                }.getType());

                        ratingReviewAdapter.setRatingReviewModelList(ratingReviewModelList);
                        ratingReviewAdapter.notifyDataSetChanged();

                    } else if (jsonObject.getInt("status") == 0) {
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(UserProfileActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (ratingReviewModelList.size() > 0) {
                    binding.llReview.setVisibility(View.VISIBLE);
                    binding.llNoLising.setVisibility(View.GONE);
                } else {
                    binding.llReview.setVisibility(View.GONE);
                    binding.llNoLising.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onResponseFailed(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}