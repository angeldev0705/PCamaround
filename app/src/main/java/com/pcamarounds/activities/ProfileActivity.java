package com.pcamarounds.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.pcamarounds.R;
import com.pcamarounds.adapters.ItemImagesAdapter;
import com.pcamarounds.adapters.RatingReviewAdapter;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.ActivityProfileBinding;
import com.pcamarounds.models.ItemImagesModel;
import com.pcamarounds.models.LoginModel;
import com.pcamarounds.models.RatingReviewModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.EqualSpacing;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private Context context;
    private ActivityProfileBinding binding;
    private ItemImagesAdapter itemImagesAdapter;
    private List<ItemImagesModel> imagesModelsList = new ArrayList<>();
    private RatingReviewAdapter ratingReviewAdapter;
    private List<RatingReviewModel> ratingReviewModelList = new ArrayList<>();
    private RealmResults<LoginModel> loginModelRealmResults;
    private RealmChangeListener<RealmResults<LoginModel>> realmChangeListener = (LoginModel) -> {
        setDataProfile();
    };

    private void setDataProfile() {
        if (RealmController.getUser() != null) {
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getLastName())) {
                binding.tvName.setText(Utility.capitalize(RealmController.getUser().getFirstName() + " " + Utility.printInitials(RealmController.getUser().getLastName())));
            } else {
                binding.tvName.setText(Utility.capitalize(RealmController.getUser().getFirstName()));
            }
            binding.tvAboutMe.setText(RealmController.getUser().getAboutus());
            if (RealmController.getUser().getAboutus().length() > 170) {
                //  Log.e(TAG, "setProfileData: length " + detailmodel.getAboutYouText().length() );
                binding.tvreadmore.setVisibility(View.VISIBLE);
            }
            binding.tvreadmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.tvreadmore.setVisibility(View.GONE);
                    binding.tvreadless.setVisibility(View.VISIBLE);
                    binding.tvAboutMe.setMaxLines(Integer.MAX_VALUE);
                }
            });
            binding.tvreadless.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.tvreadless.setVisibility(View.GONE);
                    binding.tvreadmore.setVisibility(View.VISIBLE);
                    binding.tvAboutMe.setMaxLines(3);
                }
            });


            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getProfileImage())) {
                Picasso.get().load(AppConstants.PROFILE_IMG + RealmController.getUser().getProfileImage()).placeholder(R.drawable.profile_default).into(binding.ivProfile);
              /*  binding.loading.setVisibility(View.VISIBLE);
                Picasso.get().load(AppConstants.PROFILE_IMG + RealmController.getUser().getProfileImage()).placeholder(R.drawable.profile_default).into(binding.ivProfile, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        binding.loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        binding.loading.setVisibility(View.GONE);
                    }
                });*/
                binding.ivProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = AppConstants.PROFILE_IMG + RealmController.getUser().getProfileImage();
                        new PhotoFullPopupWindow(context, R.layout.popup_photo_full_two, v, url, null);
                    }
                });
            }
            binding.rating.setRating(RealmController.getUser().getRating());
            //binding.tvRating.setText(""+RealmController.getUser().getRating());
            if (!RealmController.getUser().getTotalReviews().equals("0") && !RealmController.getUser().getTotalReviews().equals("0")) {
                if (!RealmController.getUser().getTotalReviews().equals("0") && !RealmController.getUser().getTotalReviews().equals("1")) {
                    binding.tvTotRating.setText(RealmController.getUser().getTotalReviews() + " Calificaciones");
                } else {
                    binding.tvTotRating.setText(RealmController.getUser().getTotalReviews() + " Calificación");
                }
            } else {
                binding.tvTotRating.setText("Sin Calificaciones");
            }
            binding.tvRating.setText(Utility.roundOffFloatWithDecimal(RealmController.getUser().getRating(), 1) + "");
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getPerformance())) {
                String[] per = RealmController.getUser().getPerformance().split("\\.");
                binding.tvStatus.setText(per[0] + "% Completados");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        context = this;
        loginModelRealmResults = RealmController.realmControllerInIt().getRealm().where(LoginModel.class).findAllAsync();
        loginModelRealmResults.addChangeListener(realmChangeListener);
        Utility.hideKeyboardNew(this);
        initView();
    }

    private void initView() {
        setupRvImages();
        setupRvRating();
        get_my_profile();
        my_reviews();
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void setupRvImages() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.recyclerViewImage.setLayoutManager(linearLayoutManager1);
        binding.recyclerViewImage.addItemDecoration(new EqualSpacing(16, context));
        itemImagesAdapter = new ItemImagesAdapter(context, imagesModelsList);
        binding.recyclerViewImage.setAdapter(itemImagesAdapter);
    }

    private void setupRvRating() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerViewRating.setLayoutManager(linearLayoutManager1);
        binding.recyclerViewRating.addItemDecoration(new EqualSpacing(0, context));
        ratingReviewAdapter = new RatingReviewAdapter(context, ratingReviewModelList);
        binding.recyclerViewRating.setAdapter(ratingReviewAdapter);
    }

    private void get_my_profile() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        Log.e(TAG, "get_my_profile: " + hashMap);
        Log.e(TAG, "header : " + Utility.getHeaderAuthentication(context));

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).get_my_profile(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        LoginModel loginModel = Controller.getGson().fromJson(jsonObject.getJSONObject("data").toString(), LoginModel.class);
                        RealmController.copyToRealmOrUpdate(loginModel);
                        imagesModelsList.clear();
                        imagesModelsList = Controller.getGson().fromJson(jsonObject.getJSONArray("images").toString(),
                                new TypeToken<ArrayList<ItemImagesModel>>() {
                                }.getType());
                        itemImagesAdapter.setItemImagesModelList(imagesModelsList, "view");
                        itemImagesAdapter.notifyDataSetChanged();


                    } else if (jsonObject.getInt("status") == 0) {
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(ProfileActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (imagesModelsList.size() > 0) {
                    binding.tvWorkDone.setVisibility(View.VISIBLE);
                    binding.recyclerViewImage.setVisibility(View.VISIBLE);
                } else {
                    binding.tvWorkDone.setVisibility(View.GONE);
                    binding.recyclerViewImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onResponseFailed(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void my_reviews() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        hashMap.put(AppConstants.TECH_ID, RealmController.getUser().getId());


        Log.e(TAG, "tech_reviews_list: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).my_reviews(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {

                        ratingReviewModelList.clear();
                        ratingReviewModelList = Controller.getGson().fromJson(jsonObject.getJSONArray("job_reviews").toString(),
                                new TypeToken<ArrayList<RatingReviewModel>>() {
                                }.getType());

                        ratingReviewAdapter.setRatingReviewModelList(ratingReviewModelList);
                        ratingReviewAdapter.notifyDataSetChanged();

                    } else if (jsonObject.getInt("status") == 0) {
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(ProfileActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (ratingReviewModelList.size() > 0) {
                    binding.recyclerViewRating.setVisibility(View.VISIBLE);
                    binding.tvRate.setVisibility(View.VISIBLE);
                    binding.llNoLising.setVisibility(View.GONE);
                } else {
                    binding.recyclerViewRating.setVisibility(View.GONE);
                    binding.tvRate.setVisibility(View.GONE);
                    binding.llNoLising.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onResponseFailed(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
