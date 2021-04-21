package com.pcamarounds.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.pcamarounds.BuildConfig;
import com.pcamarounds.R;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.controller.SessionManager;
import com.pcamarounds.databinding.ActivitySplashBinding;
import com.pcamarounds.models.AppVersionModel;
import com.pcamarounds.models.PendingReviewModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class SplashActivity extends AppCompatActivity {
    private AppEventsLogger logger;
    private FirebaseAnalytics mFirebaseAnalytics;

    private static final String TAG = "SplashActivity";
    private Context context;
    private ActivitySplashBinding binding;
    SharedPreferences settings;
    boolean firstRun;
    private Double currentVersion;
    private List<PendingReviewModel> pendingReviewModels = new ArrayList<>();
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        logger = AppEventsLogger.newLogger(this);
        Bundle params = new Bundle();
        recordAppLaunch();

        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        context = this;
        dialog = new Dialog(context);
        settings = getSharedPreferences("prefs", 0);
        firstRun = settings.getBoolean("firstRun", false);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.color_primary));
        }
        initView();
    }

    private void recordAppLaunch() {

        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("app_launch", bundle);
        logger.logEvent("app_launch");
    }
    private void initView() {

        Locale locale = new Locale("es");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        currentVersion = Double.parseDouble(BuildConfig.VERSION_NAME);
       get_version();

    }
    private void getAccessToken() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        Log.e(TAG, "getAccessToken: " + hashMap);
        RetrofitClient.service(context).generateAccessToken(hashMap).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt(JsonUtil.STATUS) == 1) {
                        SessionManager.setAccessToken(context, jsonObject.getString("token"));
                        Log.e(TAG, "onResponse: " + jsonObject.getString("token"));
                        if (firstRun == false) {
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("firstRun", true);
                            editor.commit();
                            Intent in = new Intent(context, WalkthroughActivity.class);
                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(in);
                            finish();
                        } else {
                            Intent in = new Intent(context, LoginActivity.class);
                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(in);
                            finish();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    private void tech_reviews_pending() {
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

        Log.e(TAG, "tech_reviews_pending: " + hashMap);

        RetrofitClient.getContentData(null, RetrofitClient.service(context).tech_reviews_pending(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {

                        pendingReviewModels.clear();
                        pendingReviewModels = Controller.getGson().fromJson(jsonObject.getJSONArray("data").toString(),
                                new TypeToken<ArrayList<PendingReviewModel>>() {
                                }.getType());

                        if (pendingReviewModels.size() > 0) {
                            Intent intent = new Intent(context, SearchDetailsCamaroundActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra(AppConstants.JOB_ID, pendingReviewModels.get(0).getId());
                            intent.putExtra(AppConstants.USER_ID, pendingReviewModels.get(0).getUserId());
                            intent.putExtra(AppConstants.SERVICE_NAME, pendingReviewModels.get(0).getServicename());
                            intent.putExtra(AppConstants.BID_STATUS, pendingReviewModels.get(0).getBookingStatus());
                            startActivity(intent);
                            finish();
                        }else {
                            Intent in = new Intent(context, MainActivity.class);
                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(in);
                            finish();
                        }

                    } else if (jsonObject.getInt("status") == 0) {
                        Intent in = new Intent(context, MainActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(in);
                        finish();
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(SplashActivity.this);
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
    private void get_version() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        Log.e(TAG, "get_version: " + hashMap);
        RetrofitClient.service(context).get_version(hashMap).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt(JsonUtil.STATUS) == 1) {

                       AppVersionModel appVersionModel = Controller.getGson().fromJson(jsonObject.getJSONObject("data").toString(), AppVersionModel.class);

                        if (appVersionModel != null) {

                            if (currentVersion < appVersionModel.getPcamaroundsandroid()) {
                                showUpdateDialog();
                                // handler();

                            } else {
                                handler();

                            }
                        } else {
                            handler();
                        }
                    } else if (jsonObject.getInt(JsonUtil.STATUS) == 0) {
                        handler();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                handler();
            }
        });
    }

    private void showUpdateDialog() {

        dialog.setContentView(R.layout.app_version_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("market://details?id=com.pcamarounds")));
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    private void handler() {
        new Handler().postDelayed(() -> {
            if (RealmController.getUser() == null) {
                getAccessToken();
            } else {
                if (Utility.isCheckEmptyOrNull(RealmController.getUser().getId())) {
                    tech_reviews_pending();
                  /*  Intent in = new Intent(context, MainActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);
                    finish();*/
                } else {
                    Intent in = new Intent(context, LoginActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);
                    finish();
                }
            }
        }, Utility.SPLASH_TTIMEOUT);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

}
