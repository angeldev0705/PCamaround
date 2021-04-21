package com.pcamarounds.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.pcamarounds.R;
import com.pcamarounds.adapters.AddonAdapter;
import com.pcamarounds.adapters.ImagesAdapter;
import com.pcamarounds.constants.AppConfig;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.controller.SessionManager;
import com.pcamarounds.databinding.ActivityGiveReviewBinding;
import com.pcamarounds.models.postdetail.Addon;
import com.pcamarounds.models.postdetail.ImagesModel;
import com.pcamarounds.models.postdetail.PostDetailModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.AppProgressDialog;
import com.pcamarounds.utils.EqualSpacing;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import constant.StringContract;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class GiveReviewActivity extends AppCompatActivity {
    private static final String TAG = "GiveReviewActivity";
    private Context context;
    private ActivityGiveReviewBinding binding;
    private ImagesAdapter imagesAdapter;
    private List<ImagesModel> imagesModelsList = new ArrayList<>();
    private String job_id = "", service_name = "", bid_status = "", user_id = "";
    private AddonAdapter addonAdapter;
    private List<Addon> addonList = new ArrayList<>();
    private PostDetailModel postDetailModel;
    private float rateOne = 0, rateTwo = 0, rateThree = 0;
    private Dialog dialog;
    private String GUID = "", groupName = "", groupDescription = "", url = " ", techUrl = " ";
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_give_review);
        context = this;
        dialog = new Dialog(context);
        Utility.hideKeyboardNew(GiveReviewActivity.this);
        initView();
    }

    private void initView() {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(AppConstants.KEY);
                if (message != null && message.equals(AppConstants.NOTIFICATION)) {
                    job_id = intent.getStringExtra(AppConstants.JOB_ID);
                    user_id = intent.getStringExtra(AppConstants.USER_ID);
                    bid_status = intent.getStringExtra(AppConstants.BID_STATUS);
                    service_name = intent.getStringExtra(AppConstants.SERVICE_NAME);
                    post_details();
                }
            }
        };


        job_id = getIntent().getStringExtra(AppConstants.JOB_ID);
        user_id = getIntent().getStringExtra(AppConstants.USER_ID);
        service_name = getIntent().getStringExtra(AppConstants.SERVICE_NAME);
        bid_status = getIntent().getStringExtra(AppConstants.BID_STATUS);
        // groupName = service_name + job_id;
        Log.e(TAG, "initView: " + job_id + "  " + user_id);
        setUpToolbar();
        setupRvImages();
        setupRvService();
        post_details();
        binding.rlCamaroundDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.dropDownVisibleAnimation(binding.llCamaroundDetails, binding.ivDetailDD);
            }
        });
        binding.rlQuotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.dropDownVisibleAnimation(binding.llQuotation, binding.ivQuotationDD);
            }
        });
        binding.rlReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.dropDownVisibleAnimation(binding.llReview, binding.ivReviewDD);
            }
        });

        binding.ratingBarOne.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateOne = rating;
            }
        });
        binding.ratingBarTwo.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateTwo = rating;
            }
        });
        binding.ratingBarTheen.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateThree = rating;
            }
        });
        binding.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postDetailModel != null) {
                    String strUri = "http://maps.google.com/maps?q=loc:" + postDetailModel.getData().getLatitude() + "," + postDetailModel.getData().getLongitude() + " (" + postDetailModel.getData().getAddress() + ")";
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                }
            }
        });
        binding.btnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent intent = new Intent(context, ViewReviewActivity.class);
                intent.putExtra(AppConstants.SERVICE_NAME, service_name);
                intent.putExtra(AppConstants.VIEWREVIEW, "User");
                intent.putExtra(AppConstants.FLAG, postDetailModel.getUserReviewsModel());
                startActivity(intent);*/
                Intent intent = new Intent(context,UserProfileActivity.class);
                intent.putExtra(AppConstants.USER_ID,postDetailModel.getUserDetailsModel().getId());
                startActivity(intent);
            }
        });
        binding.btnVerTech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(context, ViewReviewActivity.class);
                intent.putExtra(AppConstants.SERVICE_NAME, service_name);
                intent.putExtra(AppConstants.VIEWREVIEW, "Tech");
                intent.putExtra(AppConstants.FLAG, postDetailModel.getProJobReviews());
                startActivity(intent);*/
                Intent intent = new Intent(context,ProfileActivity.class);
                startActivity(intent);
            }
        });
        binding.btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rateOne == 0) {
                    Utility.toast(context, "Please rate Puntualidad");
                } else {
                    if (rateTwo == 0) {
                        Utility.toast(context, "Please rate Amabilidad");
                    } else {
                        if (rateThree == 0) {
                            Utility.toast(context, "Please rate Calidad del servicio");
                        } else {
                            if (binding.etComment.getText().toString().isEmpty()) {
                                Utility.toast(context, "Comparta su experiencia");
                            } else {
                                tech_give_review();
                            }

                        }
                    }
                }
            }
        });
        binding.tvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GUID = Utility.createGroupId(job_id, postDetailModel.getPostBidData().getId(), postDetailModel.getUserDetailsModel().getId(), RealmController.getUser().getId() + "");
                groupName = postDetailModel.getUserDetailsModel().getFirstName() + RealmController.getUser().getFirstName() + job_id+"_"+service_name;
                if (Utility.isCheckEmptyOrNull(postDetailModel.getUserDetailsModel().getProfileImage())) {
                    url = AppConstants.PROFILE_IMG_CLIENT + postDetailModel.getUserDetailsModel().getId() + "/" + postDetailModel.getUserDetailsModel().getProfileImage();

                }
                if (Utility.isCheckEmptyOrNull(RealmController.getUser().getProfileImage())) {
                    techUrl = AppConstants.PROFILE_IMG + RealmController.getUser().getProfileImage();
                }
                groupDescription = postDetailModel.getUserDetailsModel().getFirstName()+" "+Utility.printInitials(postDetailModel.getUserDetailsModel().getLastName()) + "*" + RealmController.getUser().getFirstName()+" "+RealmController.getUser().getLastName() + "*" + url + "*" + techUrl;
                add_group_id(GUID, postDetailModel.getPostBidData().getId());
                getGroupInfo(postDetailModel.getUserDetailsModel().getId());
            }
        });

    }

    private void add_group_id(String gid, String bid) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS_TOKEN, SessionManager.getAccessToken(context));
        hashMap.put(AppConstants.UID, RealmController.getUser().getId() + "");
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.GROUP_ID, gid);
        hashMap.put(AppConstants.BID_ID, bid);
        hashMap.put(AppConstants.JOB_ID, job_id);

        Log.e(TAG, "add_group_id: " + hashMap);
        RetrofitClient.getContentData(null, RetrofitClient.service(context).add_group_id(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {

                    } else if (jsonObject.getInt("status") == 0) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getGroupInfo(String userid) {
        CometChat.getGroup(GUID, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                Log.e(TAG, "Group details fetched successfully: " + group.toString());
                FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.AppDetails.APP_ID + "_" + CometChatConstants.RECEIVER_TYPE_GROUP + "_" + GUID);
                Intent intent = new Intent(context, MessagesDetailActivity.class);
                intent.putExtra(StringContract.IntentStrings.NAME, group.getName());
                intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, group.getOwner());
                intent.putExtra(StringContract.IntentStrings.GUID, group.getGuid());
                intent.putExtra(StringContract.IntentStrings.AVATAR, group.getIcon());
                intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_GROUP);
                startActivity(intent);
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "Group details fetching failed with exception: " + e.getMessage());
                dialog = new Dialog(context);
                AppProgressDialog.show(dialog);
                createGroup(userid);
            }
        });
    }

    private void createGroup(String userid) {
        String groupType = CometChatConstants.GROUP_TYPE_PRIVATE;
        Group group = new Group(GUID, groupName, groupType, "", techUrl, groupDescription);
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                Log.e(TAG, "Group created successfully: " + group.toString());
                addGroup(userid, group);

            }

            @Override
            public void onError(CometChatException e) {
                AppProgressDialog.hide(dialog);
                Log.e(TAG, "Group creation failed with exception: " + e.getMessage());
            }
        });
    }

    private void addGroup(String userid, Group group) {
        String useriddd = Utility.getCometId(userid);
        List<GroupMember> members = new ArrayList<>();
        members.add(new GroupMember(useriddd, CometChatConstants.SCOPE_MODERATOR));
        members.add(new GroupMember(SessionManager.getCometId(context), CometChatConstants.SCOPE_ADMIN));
        members.add(new GroupMember("camaround_admin_1", CometChatConstants.SCOPE_ADMIN));
        CometChat.addMembersToGroup(GUID, members, null, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> successMap) {
                AppProgressDialog.hide(dialog);
                Log.e(TAG, "CometChatActivity");
                FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.AppDetails.APP_ID + "_" + CometChatConstants.RECEIVER_TYPE_GROUP + "_" + GUID);
                Intent intent = new Intent(context, MessagesDetailActivity.class);
                intent.putExtra(StringContract.IntentStrings.NAME, group.getName());
                intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, group.getOwner());
                intent.putExtra(StringContract.IntentStrings.GUID, group.getGuid());
                intent.putExtra(StringContract.IntentStrings.AVATAR, group.getIcon());
                intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_GROUP);
                startActivity(intent);
            }

            @Override
            public void onError(CometChatException e) {
                AppProgressDialog.hide(dialog);
                Log.e(TAG, "onError: " + e.getMessage());
            }
        });
    }

    private void setupRvService() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        binding.rvService.setLayoutManager(linearLayoutManager1);
        binding.rvService.addItemDecoration(new EqualSpacing(0, context));
        addonAdapter = new AddonAdapter(context, addonList);
        binding.rvService.setAdapter(addonAdapter);
    }

    private void setupRvImages() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvImages.setLayoutManager(linearLayoutManager1);
        binding.rvImages.addItemDecoration(new EqualSpacing(16, context));
        imagesAdapter = new ImagesAdapter(context, imagesModelsList);
        binding.rvImages.setAdapter(imagesAdapter);

    }


    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(Utility.capitalize(service_name));
        binding.myToolbar.myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_white));
        binding.myToolbar.myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void post_details() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        hashMap.put(AppConstants.JOB_ID, job_id);
        hashMap.put(AppConstants.USER_ID, user_id);


        Log.e(TAG, "post_details : " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).post_details(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    postDetailModel = Controller.getGson().fromJson(JsonUtil.mainjson(response).toString(), PostDetailModel.class);
                    if (postDetailModel.getStatus() == 1) {
                        setDataFom(postDetailModel);
                    } else if (postDetailModel.getStatus() == 0) {
                        Utility.toast(context, postDetailModel.getMessage());
                    } else if (postDetailModel.getStatus() == 4) { //Invalid Access Token
                        Utility.toast(context, postDetailModel.getMessage());
                        Utility.logout(GiveReviewActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataFom(PostDetailModel postDetailModel) {
        binding.tvAddress.setText(Utility.capitalize(postDetailModel.getData().getAddress()));
        if (Utility.isCheckEmptyOrNull(postDetailModel.getData().getServiceDetails())) {
            binding.tvServiceDetail.setText(Utility.capitalize(postDetailModel.getData().getServiceDetails()));
            binding.tvDetall.setVisibility(View.VISIBLE);
            binding.tvServiceDetail.setVisibility(View.VISIBLE);
        }
        addonAdapter.setAddonList(postDetailModel.getAddons());
        if (postDetailModel.getImages().size() > 0) {
            binding.tvPhoto.setVisibility(View.VISIBLE);
            binding.rvImages.setVisibility(View.VISIBLE);
            imagesAdapter.setImagesModelList(postDetailModel.getImages(), postDetailModel.getData().getUserId());
        }
        if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getJobAmount())) {

            double dis = Double.parseDouble(postDetailModel.getData().getDiscount());
            if (dis > 0) {
                binding.tvAdminPercentDicount.setVisibility(View.VISIBLE);
                binding.tvYourQuoteDiscount.setVisibility(View.VISIBLE);
                double jobamount = Double.parseDouble(postDetailModel.getPostBidData().getJobAmount());
                DecimalFormat formatter = new DecimalFormat("#,###.00");

                double cCom = ((Double.parseDouble(postDetailModel.getPostBidData().getAdmin_commision()) * dis) / 100);
                double tot = Double.parseDouble(postDetailModel.getPostBidData().getJobAmount()) - Double.parseDouble(postDetailModel.getPostBidData().getAdmin_commision());
                binding.tvAmount.setText("$" + formatter.format(tot));
                binding.tvAdminPercent.setPaintFlags(binding.tvAdminPercent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                double adminper = Double.parseDouble(postDetailModel.getPostBidData().getAdmin_commision());

                binding.tvAdminPercentDicount.setText("$" + formatter.format(adminper - cCom));
                binding.tvAdminPercent.setText("-$" + formatter.format(adminper));

                binding.tvYourQuote.setPaintFlags(binding.tvYourQuote.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvYourQuote.setText("$" + formatter.format(jobamount));
                binding.tvYourQuoteDiscount.setText("$" + formatter.format(jobamount - cCom));
                if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getMaterialInclude()) && postDetailModel.getPostBidData().getMaterialInclude().equals("yes")) {
                    binding.tvMaterialInclude.setVisibility(View.VISIBLE);
                }
            } else {
                binding.tvAdminPercentDicount.setVisibility(View.GONE);
                binding.tvYourQuoteDiscount.setVisibility(View.GONE);
                double jobamount = Double.parseDouble(postDetailModel.getPostBidData().getJobAmount());
                DecimalFormat formatter = new DecimalFormat("#,###.00");

                double tot = Double.parseDouble(postDetailModel.getPostBidData().getJobAmount()) - Double.parseDouble(postDetailModel.getPostBidData().getAdmin_commision());
                binding.tvAmount.setText("$" + formatter.format(tot));

               /* if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getAdmin_commision())) {
                    double commition = Double.parseDouble(postDetailModel.getPostBidData().getAdmin_commision());
                    binding.tvAmount.setText("$" + formatter.format(commition));
                }*/

                if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getAdmin_commision())) {
                    double adminper = Double.parseDouble(postDetailModel.getPostBidData().getAdmin_commision());
                    binding.tvAdminPercent.setText("$" + formatter.format(adminper));
                }
                binding.tvYourQuote.setText("$" + formatter.format(jobamount));
                // binding.tvDays.setText(postDetailModel.getPostBidData().getWorkingDays() + " Días");
                if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getMaterialInclude()) && postDetailModel.getPostBidData().getMaterialInclude().equals("yes")) {
                    binding.tvMaterialInclude.setVisibility(View.VISIBLE);
                }
            }
        }

        if (postDetailModel.getUserDetailsModel() != null) {
            binding.tvClientName.setText(Utility.capitalize(postDetailModel.getUserDetailsModel().getFirstName() + " " + Utility.printInitials(postDetailModel.getUserDetailsModel().getLastName())));
            binding.tvRating.setText("" + postDetailModel.getUserDetailsModel().getRating());
            binding.rating.setRating(postDetailModel.getUserDetailsModel().getRating());
            binding.tvTotRatings.setText("(" + postDetailModel.getUserDetailsModel().getTotalReviews() + ")");
            Picasso.get()
                    .load(AppConstants.PROFILE_IMG_CLIENT + postDetailModel.getUserDetailsModel().getId() + "/" + postDetailModel.getUserDetailsModel().getProfileImage())
                    .placeholder(context.getResources().getDrawable(R.drawable.profile_default))
                    .into(binding.ivClientProfile);

            binding.rlClient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,UserProfileActivity.class);
                    intent.putExtra(AppConstants.USER_ID,postDetailModel.getUserDetailsModel().getId());
                    startActivity(intent);

                }
            });
        }
        if (postDetailModel.getUserReviewsModel() != null && Utility.isCheckEmptyOrNull(postDetailModel.getUserReviewsModel().getId())) {
            binding.tvFeedback.setText("Calificación de " + Utility.capitalize(postDetailModel.getUserDetailsModel().getFirstName() + " " + Utility.printInitials(postDetailModel.getUserDetailsModel().getLastName())));
            binding.ratingBarUser.setRating(postDetailModel.getUserReviewsModel().getAverage());
            binding.tvRatingUser.setText("" + postDetailModel.getUserReviewsModel().getAverage());
            binding.tvDate.setText(Utility.change_format(postDetailModel.getUserReviewsModel().getCreated(), "yyyy-MM-dd", "dd MMM yyyy"));
        }

        if (postDetailModel.getProJobReviews() != null && Utility.isCheckEmptyOrNull(postDetailModel.getProJobReviews().getId())) {
            //  binding.llCompletedTech.setVisibility(View.VISIBLE);
            binding.cvReview.setVisibility(View.GONE);

            binding.llCamaroundDetails.setVisibility(View.VISIBLE);
            binding.ivDetailDD.animate().setDuration(150).rotationBy(-180);
            binding.llQuotation.setVisibility(View.VISIBLE);
            binding.ivQuotationDD.animate().setDuration(150).rotationBy(-180);
            // binding.llGiveRate.setVisibility(View.GONE);
            binding.ratingBarTech.setRating(postDetailModel.getProJobReviews().getAverage());
            binding.tvRatingTech.setText("" + postDetailModel.getProJobReviews().getAverage());
            binding.tvDateTech.setText(Utility.change_format(postDetailModel.getProJobReviews().getCreated(), "yyyy-MM-dd", "dd MMM yyyy"));
        } else {
            //binding.llCompletedTech.setVisibility(View.GONE);
            //binding.llGiveRate.setVisibility(View.VISIBLE);
            binding.llCamaroundDetails.setVisibility(View.GONE);
            binding.ivDetailDD.animate().setDuration(150).rotationBy(180);
            binding.llQuotation.setVisibility(View.GONE);
            binding.ivQuotationDD.animate().setDuration(150).rotationBy(180);
            binding.cvReview.setVisibility(View.VISIBLE);
        }

        if ((postDetailModel.getUserReviewsModel() != null && Utility.isCheckEmptyOrNull(postDetailModel.getUserReviewsModel().getId())) && (postDetailModel.getProJobReviews() != null && Utility.isCheckEmptyOrNull(postDetailModel.getProJobReviews().getId()))) {
            binding.llCompletedTech.setVisibility(View.VISIBLE);
            binding.llCompleted.setVisibility(View.VISIBLE);
        } else {
            binding.llCompletedTech.setVisibility(View.GONE);
            binding.llCompleted.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        } else {
            finish();
        }
    }

    private void tech_give_review() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS_TOKEN, SessionManager.getAccessToken(context));
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.JOB_ID, job_id);
        hashMap.put(AppConstants.USER_ID, user_id);
        hashMap.put(AppConstants.RATE_ONE, rateOne + "");
        hashMap.put(AppConstants.RATE_TWO, rateTwo + "");
        hashMap.put(AppConstants.RATE_THREE, rateThree + "");
        hashMap.put(AppConstants.COMMENT, binding.etComment.getText().toString().trim());
        Log.e(TAG, "tech_give_review: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).tech_give_review(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {

                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                        finishAffinity();

                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(GiveReviewActivity.this);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter(AppConstants.BROADCAST));
        //  post_details();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

}
