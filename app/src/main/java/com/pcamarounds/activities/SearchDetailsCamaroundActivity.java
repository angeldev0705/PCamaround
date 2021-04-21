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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
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
import com.pcamarounds.adapters.PostBidLogsAdapter;
import com.pcamarounds.constants.AppConfig;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.controller.SessionManager;
import com.pcamarounds.databinding.ActivitySearchDetailsCamaroundBinding;
import com.pcamarounds.models.postdetail.Addon;
import com.pcamarounds.models.postdetail.ImagesModel;
import com.pcamarounds.models.postdetail.PostBidLogs;
import com.pcamarounds.models.postdetail.PostDetailModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.AppProgressDialog;
import com.pcamarounds.utils.EqualSpacing;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import constant.StringContract;
import okhttp3.ResponseBody;
import retrofit2.Response;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.facebook.appevents.AppEventsLogger;

public class SearchDetailsCamaroundActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private AppEventsLogger logger;

    private static final String TAG = "SearchDetailsCamaroundA";
    private Context context;
    private ActivitySearchDetailsCamaroundBinding binding;
    private ImagesAdapter imagesAdapter;
    private List<ImagesModel> imagesModelsList = new ArrayList<>();
    private String job_id = "", service_name = "", bid_status = "", user_id = "";
    private AddonAdapter addonAdapter;
    private List<Addon> addonList = new ArrayList<>();
    private PostDetailModel postDetailModel;
    private Dialog dialog;
    private String GUID = "", groupName = "", groupDescription = "", url = "http://34.223.228.22/52a363e725235501a0fc865f1f628006.jpg", techUrl = "http://34.223.228.22/52a363e725235501a0fc865f1f628006.jpg";
    private BroadcastReceiver broadcastReceiver;
    private float rateOne = 0, rateTwo = 0, rateThree = 0;
    private List<PostBidLogs> postBidLogs = new ArrayList<>();
    private PostBidLogsAdapter postBidLogsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        logger = AppEventsLogger.newLogger(this);

        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_details_camaround);
        context = this;
        Utility.hideKeyboardNew(SearchDetailsCamaroundActivity.this);
        dialog = new Dialog(context);
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
        //groupName = service_name + job_id;
        Log.e(TAG, "initView: " + job_id + "  " + user_id);

        setUpToolbar();
        setupRvImages();
        setupRvService();
        setupRvPostBidLogs();
        //post_details();
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
        binding.rlQuotationBelowpopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.dropDownVisibleAnimation(binding.llQuotationBelowpopup, binding.ivQuotationDDBelowpopup);
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
        binding.btnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent intent = new Intent(context, ViewReviewActivity.class);
                intent.putExtra(AppConstants.SERVICE_NAME, service_name);
                intent.putExtra(AppConstants.VIEWREVIEW, "User");
                intent.putExtra(AppConstants.FLAG, postDetailModel.getUserReviewsModel());
                startActivity(intent);*/
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra(AppConstants.USER_ID, postDetailModel.getUserDetailsModel().getId());
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
                Intent intent = new Intent(context, ProfileActivity.class);
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
        binding.btnToAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept_decliend_request("approve");
            }
        });
        binding.btnToExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPopup();
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

    }

    private void setupRvService() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        binding.rvService.setLayoutManager(linearLayoutManager1);
        binding.rvService.addItemDecoration(new EqualSpacing(0, context));
        addonAdapter = new AddonAdapter(context, addonList);
        binding.rvService.setAdapter(addonAdapter);
    }

    private void setupRvPostBidLogs() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        binding.rvpaymentlogs.setLayoutManager(linearLayoutManager1);
        binding.rvpaymentlogs.addItemDecoration(new EqualSpacing(0, context));
        postBidLogsAdapter = new PostBidLogsAdapter(context, postBidLogs);
        binding.rvpaymentlogs.setAdapter(postBidLogsAdapter);
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
        binding.myToolbar.toolbarTitle.setText(service_name);
        binding.myToolbar.myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_white));
        binding.myToolbar.myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
                        Utility.toast(context, "Su calificación ha sido enviada");
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                        finishAffinity();

                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(SearchDetailsCamaroundActivity.this);

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

    private void post_details() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        if (RealmController.getUser() != null) {
            hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        } else {
            hashMap.put(AppConstants.UID, "");
        }
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
                        Utility.logout(SearchDetailsCamaroundActivity.this);
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

        if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getBidStatus()) && postDetailModel.getPostBidData().getBidStatus().equals("awarded")) {
            binding.cvTuCamaround.setVisibility(View.VISIBLE);
            binding.cvQuotationBelowpopup.setVisibility(View.VISIBLE);
            binding.cvQuotation.setVisibility(View.GONE);
            binding.cvCancelled.setVisibility(View.GONE);
            binding.btnMap.setVisibility(View.GONE);
            binding.cvDeclined.setVisibility(View.GONE);
            binding.cvDispute.setVisibility(View.GONE);
        } else if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getBidStatus()) && postDetailModel.getPostBidData().getBidStatus().equals("cancelled")) {
            binding.cvQuotationBelowpopup.setVisibility(View.GONE);
            binding.cvQuotation.setVisibility(View.VISIBLE);
            binding.cvTuCamaround.setVisibility(View.GONE);
            binding.btnMap.setVisibility(View.GONE);
            binding.cvDeclined.setVisibility(View.GONE);
            binding.cvDispute.setVisibility(View.GONE);
            binding.cvCancelled.setVisibility(View.VISIBLE);
        } else if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getBidStatus()) && postDetailModel.getPostBidData().getBidStatus().equals("declined")) {
            binding.cvQuotationBelowpopup.setVisibility(View.GONE);
            binding.cvQuotation.setVisibility(View.VISIBLE);
            binding.cvTuCamaround.setVisibility(View.GONE);
            binding.btnMap.setVisibility(View.GONE);
            binding.cvCancelled.setVisibility(View.GONE);
            binding.cvDispute.setVisibility(View.GONE);
            binding.cvDeclined.setVisibility(View.VISIBLE);
        } else if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getBidStatus()) && postDetailModel.getPostBidData().getBidStatus().equals("dispute") || postDetailModel.getPostBidData().getBidStatus().equals("incomplete") || postDetailModel.getPostBidData().getBidStatus().equals("closed")) {
            binding.cvQuotationBelowpopup.setVisibility(View.GONE);
            binding.cvQuotation.setVisibility(View.VISIBLE);
            binding.cvTuCamaround.setVisibility(View.GONE);
            binding.btnMap.setVisibility(View.GONE);
            binding.cvCancelled.setVisibility(View.GONE);
            binding.cvDeclined.setVisibility(View.GONE);
            binding.cvDispute.setVisibility(View.VISIBLE);
            for (int i = 0; i < AppConstants.bookingStatus.length; i++) {
                if (postDetailModel.getPostBidData().getBidStatus().equals(AppConstants.bookingStatus[i])) {
                    if (postDetailModel.getPostBidData().getBidStatus().equals("dispute")) {
                        binding.tvvvv.setText("Proyecto en revisión");
                        binding.tvDisputeMsg.setText("Nuestro equipo está revisando el reclamo sobre este camaround.");
                    }else {
                        binding.tvvvv.setText(AppConstants.bookingStatusTitle[i]);
                        binding.tvDisputeMsg.setText("Este proyecto ha sido calificado como " + AppConstants.bookingStatusTitle[i] + " por Camarounds");

                    }
                }
            }
        } else {
            binding.cvQuotationBelowpopup.setVisibility(View.GONE);
            binding.cvQuotation.setVisibility(View.VISIBLE);
            binding.cvTuCamaround.setVisibility(View.GONE);
            binding.btnMap.setVisibility(View.VISIBLE);
            binding.cvCancelled.setVisibility(View.GONE);
            binding.cvDeclined.setVisibility(View.GONE);
            binding.cvDispute.setVisibility(View.GONE);
        }
        if (Utility.isCheckEmptyOrNull(postDetailModel.getData().getAddress())) {
            binding.llAddress.setVisibility(View.VISIBLE);
        } else {
            binding.llAddress.setVisibility(View.GONE);
        }
        binding.tvAddress.setText(postDetailModel.getData().getAddress());
        if (Utility.isCheckEmptyOrNull(postDetailModel.getData().getServiceDetails())) {
            binding.tvServiceDetail.setText(postDetailModel.getData().getServiceDetails());
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
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat formatter = (DecimalFormat) nf;
            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            formatter.applyPattern("#,###0.00");
            double dis = Double.parseDouble(postDetailModel.getData().getDiscount());

            if (dis > 0) {
                binding.tvYourQuoteDiscount.setVisibility(View.VISIBLE);
                binding.tvAdminPercentDicount.setVisibility(View.VISIBLE);

                binding.tvYourQuoteDiscountBelowpopup.setVisibility(View.VISIBLE);
                binding.tvAdminPercentDicountBelowpopup.setVisibility(View.VISIBLE);

               // double cCom = ((Double.parseDouble(postDetailModel.getPostBidData().getJobAmount()) * dis) / 100);
                double cComDicount = ((Double.parseDouble(postDetailModel.getPostBidData().getJobAmount()) * dis) / 100);
                double adminper = Double.parseDouble(postDetailModel.getPostBidData().getAdmin_commision());

                binding.tvDDD.setText("Camaround con descuento");

              //  binding.tvAlert.setText("Este camaround tiene un descuento de $" + formatter.format(adminper - cComDicount) + " que son restados de la comisión y no afecta tu monto a recibir.");
               if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getUserDiscount())) {
                   binding.tvAlert.setText("Este camaround tiene un descuento de " + postDetailModel.getPostBidData().getUserDiscount() + "% que son restados de la comisión y no afecta tu monto a recibir.");
                   binding.cvAlert.setVisibility(View.VISIBLE);
               }else {
                   binding.cvAlert.setVisibility(View.GONE);
               }
               double tt = Double.parseDouble(postDetailModel.getPostBidData().getJobAmount());
               // double totot = (Double.parseDouble(formatter.format(tt)) - Double.parseDouble(formatter.format(cComDicount)));


                Log.e(TAG, "setDataFom: " + cComDicount+"  "+ tt+"  "+ (tt - cComDicount));

                if (Utility.isCheckEmptyOrNull(postDetailModel.getUserDetailsModel().getLastName())) {
                    binding.tvTotal.setText(Utility.capitalize(postDetailModel.getUserDetailsModel().getFirstName() + " " + Utility.printInitials(postDetailModel.getUserDetailsModel().getLastName())) + " te ha adjudicado este Camaround por $" + formatter.format(tt - cComDicount));
                } else {
                    binding.tvTotal.setText(Utility.capitalize(postDetailModel.getUserDetailsModel().getFirstName()) + " te ha adjudicado este Camaround por $" + formatter.format(tt - cComDicount));
                }

                double tot = Double.parseDouble(postDetailModel.getPostBidData().getJobAmount()) - Double.parseDouble(postDetailModel.getPostBidData().getAdmin_commision());
                binding.tvAmount.setText("$" + formatter.format(tot));
                binding.tvAmountBelowpopup.setText("$" + formatter.format(tot));


               // double cComDicount = ((Double.parseDouble(postDetailModel.getPostBidData().getJobAmount()) * dis) / 100);


                binding.tvAdminPercent.setPaintFlags(binding.tvAdminPercent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvAdminPercent.setText("$" + formatter.format(adminper));
                binding.tvAdminPercentDicount.setText("$" + formatter.format(adminper - cComDicount));

                binding.tvAdminPercentBelowpopup.setPaintFlags(binding.tvAdminPercentBelowpopup.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvAdminPercentBelowpopup.setText("$" + formatter.format(adminper));
                binding.tvAdminPercentDicountBelowpopup.setText("$" + formatter.format(adminper - cComDicount));


                double jobamount = Double.parseDouble(postDetailModel.getPostBidData().getJobAmount());
                binding.tvYourQuote.setPaintFlags(binding.tvYourQuote.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvYourQuote.setText("$" + formatter.format(jobamount));
                binding.tvYourQuoteDiscount.setText("$" + formatter.format(jobamount - cComDicount));

                binding.tvYourQuoteBelowpopup.setPaintFlags(binding.tvYourQuoteBelowpopup.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvYourQuoteBelowpopup.setText("$" + formatter.format(jobamount));
                binding.tvYourQuoteDiscountBelowpopup.setText("$" + formatter.format(jobamount - cComDicount));

            } else {
                binding.tvAdminPercentDicount.setVisibility(View.GONE);
                binding.tvYourQuoteDiscount.setVisibility(View.GONE);

                binding.tvAdminPercentDicountBelowpopup.setVisibility(View.GONE);
                binding.tvYourQuoteDiscountBelowpopup.setVisibility(View.GONE);

                binding.cvAlert.setVisibility(View.GONE);
                binding.tvDDD.setText("Camaround");
                double jobamount = Double.parseDouble(postDetailModel.getPostBidData().getJobAmount());

                if (Utility.isCheckEmptyOrNull(postDetailModel.getUserDetailsModel().getLastName())) {
                    binding.tvTotal.setText(Utility.capitalize(postDetailModel.getUserDetailsModel().getFirstName() + " " + Utility.printInitials(postDetailModel.getUserDetailsModel().getLastName())) + " te ha adjudicado este Camaround por $" + formatter.format(jobamount));
                } else {
                    binding.tvTotal.setText(Utility.capitalize(postDetailModel.getUserDetailsModel().getFirstName()) + " te ha adjudicado este Camaround por $" + formatter.format(jobamount));
                }
                double tot = Double.parseDouble(postDetailModel.getPostBidData().getJobAmount()) - Double.parseDouble(postDetailModel.getPostBidData().getAdmin_commision());
                binding.tvAmount.setText("$" + formatter.format(tot));
                binding.tvAmountBelowpopup.setText("$" + formatter.format(tot));
                double adminper = Double.parseDouble(postDetailModel.getPostBidData().getAdmin_commision());
                binding.tvAdminPercent.setText("$" + formatter.format(adminper));
                binding.tvYourQuote.setText("$" + formatter.format(jobamount));

                binding.tvAdminPercentBelowpopup.setText("$" + formatter.format(adminper));
                binding.tvYourQuoteBelowpopup.setText("$" + formatter.format(jobamount));


            }
            binding.tvComment.setText(postDetailModel.getPostBidData().getComment());
            binding.tvCommentBelowpopup.setText(postDetailModel.getPostBidData().getComment());
            if (postDetailModel.getPostBidData().getPaymentType().equals("cash")) {
                binding.tvPaymentType.setText("Efectivo");
                binding.rlCard.setVisibility(View.GONE);
            } else {
                binding.rlCard.setVisibility(View.VISIBLE);
                binding.tvPaymentType.setText("Pago depositado en camarounds");
            }

            if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getCalculateAmount())) {
                double amount = Double.parseDouble(postDetailModel.getPostBidData().getCalculateAmount());
                if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getPaidout())) {
                    double amountPaidOut = Double.parseDouble(postDetailModel.getPostBidData().getPaidout());
                    double ddd = Utility.roundOffTwoDecimal(amountPaidOut, 2);
                    if (formatter.format(amount).equals(formatter.format(amountPaidOut))) {
                        binding.tvPaymentStatusCard.setText("Pagado");
                        binding.rlCard.setVisibility(View.GONE);
                        binding.tvTotalCard.setText("$" + formatter.format(amount));
                    } else {
                        binding.tvTotalCard.setText("$" + formatter.format(amount - amountPaidOut));
                    }
                } else {
                    binding.tvTotalCard.setText("$" + formatter.format(amount));
                }
            }

            if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getBidStatus()) && postDetailModel.getPostBidData().getBidStatus().equals("inprogress") || postDetailModel.getPostBidData().getBidStatus().equals("incomplete")) {
                if (postDetailModel.getData().getPaymentType().equals("cash") || postDetailModel.getData().getPaymentType().equals("card")) {
                    if (!postDetailModel.getData().getRequestDate().equals("0000-00-00 00:00:00")) {
                        String requestdate = postDetailModel.getData().getRequestDate();
                        String dateResult = Utility.compareDates(requestdate, Utility.getCurrentDateFORMAT_YYYYMMddHHmmss());
                        if (dateResult.equals("future")) {
                            String strOrderBookingDate = Utility.utcDateTimeZone("yyyy-MM-dd'T'HH:mm", requestdate,
                                    context, "yyyy-MM-dd'T'HH:mm");
                            //String dateeee = strOrderBookingDate.split("T")[0];
                            String timeeeee = strOrderBookingDate.split("T")[1];
                            String timeResult = Utility.compateTime(timeeeee, Utility.getCurrentTime());
                            if (timeResult.equals("before") || timeResult.equals("equals")) {
                                binding.btnRequestCompleted.setText("Solicitar Completar");
                            } else {
                                binding.btnRequestCompleted.setText("Solicitar Completar");
                                // binding.btnRequestCompleted.setText("Solicitud enviada");
                            }
                        } else {
                            binding.btnRequestCompleted.setText("Solicitar Completar");
                            // binding.btnRequestCompleted.setText("Solicitud enviada");
                        }
                        binding.btnRequestCompleted.setVisibility(View.VISIBLE);
                    } else {
                        binding.btnRequestCompleted.setVisibility(View.VISIBLE);
                    }
                }
/*
                else {
                    if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getCalculateAmount()) && postDetailModel.getPostBidData().getCalculateAmount().equals(postDetailModel.getPostBidData().getPaidout())) {
                        if (!postDetailModel.getData().getRequestDate().equals("0000-00-00 00:00:00")) {
                            binding.btnRequestCompleted.setVisibility(View.VISIBLE);
                            String requestdate = postDetailModel.getData().getRequestDate();
                            String dateResult = Utility.compareDates(requestdate, Utility.getCurrentDateFORMAT_YYYYMMddHHmmss());
                            if (dateResult.equals("future")) {
                                String strOrderBookingDate = Utility.utcDateTimeZone("yyyy-MM-dd'T'HH:mm", requestdate,
                                        context, "yyyy-MM-dd'T'HH:mm");
                                //String dateeee = strOrderBookingDate.split("T")[0];
                                String timeeeee = strOrderBookingDate.split("T")[1];
                                String timeResult = Utility.compateTime(timeeeee, Utility.getCurrentTime());
                                if (timeResult.equals("before") || timeResult.equals("equals")) {
                                    binding.btnRequestCompleted.setText("Solicitud completa");
                                } else {
                                    binding.btnRequestCompleted.setText("Solicitud enviada");
                                }
                            } else {
                                binding.btnRequestCompleted.setText("Solicitud enviada");
                            }
                        } else {
                            binding.btnRequestCompleted.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.btnRequestCompleted.setVisibility(View.GONE);
                    }
                }
*/
            } else {
                binding.btnRequestCompleted.setVisibility(View.GONE);
            }
            // binding.tvTotal.setText(Utility.capitalize(postDetailModel.getUserDetailsModel().getFirstName()) + " te ha adjudicado este Camaround por $" + formatter.format(jobamount));
            binding.tvCancelledMsg.setText("Su solicitud ha sido cancelada por el usuario");
            binding.tvDeclineddMsg.setText("Has rechazado esta solicitud.");
            if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getMaterialInclude()) && postDetailModel.getPostBidData().getMaterialInclude().equals("yes")) {
                binding.tvMaterialInclude.setVisibility(View.VISIBLE);
            }
        }

        if (postDetailModel.getUserDetailsModel() != null) {
            if (Utility.isCheckEmptyOrNull(postDetailModel.getUserDetailsModel().getLastName())) {
                binding.tvClientName.setText(Utility.capitalize(postDetailModel.getUserDetailsModel().getFirstName() + " " + Utility.printInitials(postDetailModel.getUserDetailsModel().getLastName())));
            } else {
                binding.tvClientName.setText(Utility.capitalize(postDetailModel.getUserDetailsModel().getFirstName()));
            }
            //binding.tvRating.setText("" + postDetailModel.getUserDetailsModel().getRating());
            binding.tvRating.setText(Utility.roundOffFloatWithDecimal(postDetailModel.getUserDetailsModel().getRating(), 1) + "");
            binding.rating.setRating(postDetailModel.getUserDetailsModel().getRating());
            binding.tvTotRatings.setText("(" + postDetailModel.getUserDetailsModel().getTotalReviews() + ")");
            Picasso.get()
                    .load(AppConstants.PROFILE_IMG_CLIENT + postDetailModel.getUserDetailsModel().getId() + "/" + postDetailModel.getUserDetailsModel().getProfileImage())
                    .placeholder(context.getResources().getDrawable(R.drawable.profile_default))
                    .into(binding.ivClientProfile);

            binding.rlClient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra(AppConstants.USER_ID, postDetailModel.getUserDetailsModel().getId());
                    startActivity(intent);

                }
            });
        }

        if (postDetailModel.getUserReviewsModel() != null && Utility.isCheckEmptyOrNull(postDetailModel.getUserReviewsModel().getId())) {
            if (Utility.isCheckEmptyOrNull(postDetailModel.getUserDetailsModel().getLastName())) {
                binding.tvFeedback.setText("Calificación de " + Utility.capitalize(postDetailModel.getUserDetailsModel().getFirstName() + " " + Utility.printInitials(postDetailModel.getUserDetailsModel().getLastName())));
            } else {
                binding.tvFeedback.setText("Calificación de " + Utility.capitalize(postDetailModel.getUserDetailsModel().getFirstName()));
            }
            binding.ratingBarUser.setRating(postDetailModel.getUserReviewsModel().getAverage());
            binding.tvRatingUser.setText(Utility.roundOffFloatWithDecimal(postDetailModel.getUserReviewsModel().getAverage(), 1) + "");
            binding.tvCommentUser.setText(postDetailModel.getUserReviewsModel().getComment());
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
            binding.tvRatingTech.setText(Utility.roundOffFloatWithDecimal(postDetailModel.getProJobReviews().getAverage(), 1) + "");
            binding.tvCommentTech.setText(postDetailModel.getProJobReviews().getComment());
            binding.tvDateTech.setText(Utility.change_format(postDetailModel.getProJobReviews().getCreated(), "yyyy-MM-dd", "dd MMM yyyy"));
        } else {
            if (postDetailModel.getPostBidData().getBidStatus().equals("completed")) {
                binding.btnRequestCompleted.setVisibility(View.GONE);
                binding.cvReview.setVisibility(View.VISIBLE);
                binding.llCamaroundDetails.setVisibility(View.GONE);
                binding.ivDetailDD.animate().setDuration(150).rotationBy(180);
                binding.llQuotation.setVisibility(View.GONE);
                binding.ivQuotationDD.animate().setDuration(150).rotationBy(180);
                binding.rvpaymentlogs.setVisibility(View.GONE);
            } else {
                binding.cvReview.setVisibility(View.GONE);
                binding.rvpaymentlogs.setVisibility(View.VISIBLE);
            }
        }


        if ((postDetailModel.getUserReviewsModel() != null && Utility.isCheckEmptyOrNull(postDetailModel.getUserReviewsModel().getId())) && (postDetailModel.getProJobReviews() != null && Utility.isCheckEmptyOrNull(postDetailModel.getProJobReviews().getId()))) {
            binding.llCompletedTech.setVisibility(View.VISIBLE);
            binding.llCompleted.setVisibility(View.VISIBLE);
        } else {
            binding.llCompletedTech.setVisibility(View.GONE);
            binding.llCompleted.setVisibility(View.GONE);
        }
        postBidLogsAdapter.setPostBidLogsList(postDetailModel.getPostBidLogs());
        addonAdapter.setAddonList(postDetailModel.getAddons());
        if (postDetailModel.getPostBidLogs().size() > 0) {
            binding.rvpaymentlogs.setVisibility(View.VISIBLE);
        } else {
            binding.rvpaymentlogs.setVisibility(View.GONE);
        }

        binding.tvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GUID = Utility.createGroupId(job_id, postDetailModel.getPostBidData().getId(), postDetailModel.getUserDetailsModel().getId(), RealmController.getUser().getId() + "");
               // groupName = postDetailModel.getUserDetailsModel().getFirstName() + RealmController.getUser().getFirstName() + job_id + "_" + service_name;
                groupName = service_name;
                if (Utility.isCheckEmptyOrNull(postDetailModel.getUserDetailsModel().getProfileImage())) {
                    url = AppConstants.PROFILE_IMG_CLIENT + postDetailModel.getUserDetailsModel().getId() + "/" + postDetailModel.getUserDetailsModel().getProfileImage();
                }
                if (Utility.isCheckEmptyOrNull(RealmController.getUser().getProfileImage())) {
                    techUrl = AppConstants.PROFILE_IMG + RealmController.getUser().getProfileImage();
                }
                groupDescription = postDetailModel.getUserDetailsModel().getFirstName() + " " + Utility.printInitials(postDetailModel.getUserDetailsModel().getLastName()) + "*" + RealmController.getUser().getFirstName() + " " + RealmController.getUser().getLastName() + "*" + url + "*" + techUrl;
                add_group_id(GUID, postDetailModel.getPostBidData().getId());
                getGroupInfo(postDetailModel.getUserDetailsModel().getId());
            }
        });
        binding.btnRequestCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.btnRequestCompleted.getText().toString().equals("Solicitar Completar")) {
                    request_completed_pop(postDetailModel.getUserDetailsModel().getFirstName() + " " + Utility.printInitials(postDetailModel.getUserDetailsModel().getLastName()));
                }
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
                intent.putExtra(StringContract.IntentStrings.DESCRIPTION, group.getDescription());
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
        members.add(new GroupMember("camaround_admin_1", CometChatConstants.SCOPE_ADMIN));
        members.add(new GroupMember(SessionManager.getCometId(context), CometChatConstants.SCOPE_MODERATOR));
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
                intent.putExtra(StringContract.IntentStrings.DESCRIPTION, group.getDescription());
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

    private void cancelPopup() {
        dialog.setContentView(R.layout.cancel_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                accept_decliend_request("declined");

            }
        });

        dialog.show();
    }

    private void recordAcceptJob() {

        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("accept_job", bundle);
        logger.logEvent("accept_job");

    }

    private void accept_decliend_request(String action) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        hashMap.put(AppConstants.BID_ID, postDetailModel.getPostBidData().getId());
        hashMap.put(AppConstants.ACTION, action);
        hashMap.put(AppConstants.JOB_ID, job_id);
        hashMap.put(AppConstants.USER_ID, postDetailModel.getData().getUserId());

        Log.e(TAG, "accept_decliend_request : " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).accept_declined_request(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        binding.cvTuCamaround.setVisibility(View.GONE);

                        binding.cvQuotationBelowpopup.setVisibility(View.GONE);
                        binding.cvQuotation.setVisibility(View.VISIBLE);

                        if (action.equals("declined")) {
                            postDetailModel.getPostBidData().setBidStatus(action);
                            thankyouPopup();
                        } else {
                            //here
                            recordAcceptJob();
                            postDetailModel.getPostBidData().setBidStatus("inprogress");
                            binding.btnMap.setVisibility(View.VISIBLE);
                            binding.btnRequestCompleted.setVisibility(View.VISIBLE);
                            binding.btnRequestCompleted.setText("Solicitar Completar");
/*
                            if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getBidStatus()) && postDetailModel.getPostBidData().getBidStatus().equals("inprogress")) {
                                if (postDetailModel.getData().getPaymentType().equals("cash") || postDetailModel.getData().getPaymentType().equals("card")) {

                                    if (!postDetailModel.getData().getRequestDate().equals("0000-00-00 00:00:00")) {
                                        String requestdate = postDetailModel.getData().getRequestDate();
                                        String dateResult = Utility.compareDates(requestdate, Utility.getCurrentDateFORMAT_YYYYMMddHHmmss());
                                        if (dateResult.equals("future")) {
                                            String strOrderBookingDate = Utility.utcDateTimeZone("yyyy-MM-dd'T'HH:mm", requestdate,
                                                    context, "yyyy-MM-dd'T'HH:mm");
                                            //String dateeee = strOrderBookingDate.split("T")[0];
                                            String timeeeee = strOrderBookingDate.split("T")[1];
                                            String timeResult = Utility.compateTime(timeeeee, Utility.getCurrentTime());
                                            if (timeResult.equals("before") || timeResult.equals("equals")) {
                                                binding.btnRequestCompleted.setText("Solicitar Completar");
                                            } else {
                                                binding.btnRequestCompleted.setText("Solicitud enviada");
                                            }
                                        } else {
                                            binding.btnRequestCompleted.setText("Solicitud enviada");
                                        }
                                        binding.btnRequestCompleted.setVisibility(View.VISIBLE);
                                    } else {
                                        binding.btnRequestCompleted.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
*/

                        }

                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(SearchDetailsCamaroundActivity.this);

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
    public void onBackPressed() {
        if (isTaskRoot()) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        } else {
            finish();
        }
    }

    private void request_completed_pop(String name) {
        dialog.setContentView(R.layout.request_completed_pop);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = dialog.findViewById(R.id.tvmsgggg);
        //textView.setText("¿Deseas enviar una solicitud para completar a " + name + "?");
        textView.setText("¿Deseas enviar una solicitud para completar este camaround?");
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                request_complete(name);

            }
        });

        dialog.show();
    }
    private void request_Confirmed_pop(String name) {
        dialog.setContentView(R.layout.request_completed_pop);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = dialog.findViewById(R.id.tvmsgggg);
        //textView.setText("¿Deseas enviar una solicitud para completar a " + name + "?");
        textView.setText("Hemos enviado una solicitud a " + name + " para completar el camaround.");
        com.google.android.material.button.MaterialButton canel = dialog.findViewById(R.id.btnCancel);
        com.google.android.material.button.MaterialButton ok = dialog.findViewById(R.id.btnConfirm);
        canel.setVisibility(View.GONE);
        ok.setText("Aceptar");
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    private void request_complete(String name) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        hashMap.put(AppConstants.JOB_ID, job_id);
        hashMap.put(AppConstants.REQUEST_DATE, Utility.getCurrentDateFORMAT_YYYYMMddHHmmss());
        Log.e(TAG, "request_complete : " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).request_complete(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    Bundle bundle = new Bundle();
                    mFirebaseAnalytics.logEvent("request_job_completed", bundle);
                    logger.logEvent("request_job_completed");

                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        request_Confirmed_pop(name);
                        binding.btnRequestCompleted.setText("Solicitar Completar");
                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(SearchDetailsCamaroundActivity.this);

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

    private void thankyouPopup() {
        dialog.setContentView(R.layout.thankyou_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView textView = dialog.findViewById(R.id.tvmsgggg);
        textView.setText("El Camaround ha sido rechazado correctamente");
        com.google.android.material.button.MaterialButton button = dialog.findViewById(R.id.btnGotoRequest);
        button.setText("Aceptar");
        dialog.findViewById(R.id.btnGotoRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finishAffinity();

            }
        });

        dialog.show();
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
        post_details();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
