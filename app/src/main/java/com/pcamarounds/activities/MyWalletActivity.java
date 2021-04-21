package com.pcamarounds.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.pcamarounds.R;
import com.pcamarounds.adapters.MyBalanceAdapter;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.ActivityMyWalletBinding;
import com.pcamarounds.models.LoginModel;
import com.pcamarounds.models.MyBalanceModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Response;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.facebook.appevents.AppEventsLogger;

public class MyWalletActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private AppEventsLogger logger;

    private static final String TAG = "MyBalanceActivity";
    private Context context;
    private ActivityMyWalletBinding binding;
    private MyBalanceAdapter balanceAdapter;
    private List<MyBalanceModel> myBalanceModelList = new ArrayList<>();
    private RealmResults<LoginModel> loginModelRealmResults;
    private RealmChangeListener<RealmResults<LoginModel>> realmChangeListener = (LoginModel) -> {
        setDataProfile();
    };

    private void setDataProfile() {
        if (RealmController.getUser() != null) {
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getWalletAmount()) && !RealmController.getUser().getWalletAmount().equals("0")) {
                double amount = Double.parseDouble(RealmController.getUser().getWalletAmount());
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                DecimalFormat formatter = (DecimalFormat) nf;
                formatter.setRoundingMode(RoundingMode.HALF_EVEN);
                formatter.applyPattern("#,###0.00");
                if (amount >= 0) {
                    binding.tvAmount.setText("$" + formatter.format(amount));
                    binding.tvPaynow.setVisibility(View.GONE);
                    binding.cvNegetive.setVisibility(View.GONE);
                } else {
                    recordNegativeBalance();

                    double neg = Double.parseDouble(formatter.format(amount).replace("-", ""));
                    binding.tvAmount.setText("-$" + formatter.format(amount).replace("-",""));
                    binding.tvPaynow.setVisibility(View.VISIBLE);
                    if (neg > 500) {
                        binding.cvNegetive.setVisibility(View.VISIBLE);
                    } else {
                        binding.cvNegetive.setVisibility(View.GONE);
                    }
                }
            }else {
                binding.tvAmount.setText("$0.00");
            }
        }
    }
    private void recordNegativeBalance() {

        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("negative_balance", bundle);
        logger.logEvent("negative_balance");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        logger = AppEventsLogger.newLogger(this);

        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_wallet);
        context = this;
        loginModelRealmResults = RealmController.realmControllerInIt().getRealm().where(LoginModel.class).findAllAsync();
        loginModelRealmResults.addChangeListener(realmChangeListener);
        initView();
    }

    private void initView() {
        setUpToolbar();
        setupRvBalance();

        binding.btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PayNowActivity.class);
                startActivity(intent);
            }
        });
        binding.tvPaynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PayNowActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setupRvBalance() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        binding.recyclerView.setLayoutManager(linearLayoutManager1);
        balanceAdapter = new MyBalanceAdapter(context, myBalanceModelList);
        binding.recyclerView.setAdapter(balanceAdapter);

/*
        balanceAdapter.setOnItemClickListener(new MyBalanceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, SearchDetailsCamaroundActivity.class);
                startActivity(intent);
            }
        });
*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        get_my_profile();
        my_transaction();
    }

    private void my_transaction() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        Log.e(TAG, "my_transaction: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).my_transaction(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {

                        myBalanceModelList.clear();
                        myBalanceModelList = Controller.getGson().fromJson(jsonObject.getJSONArray("transaction").toString(),
                                new TypeToken<ArrayList<MyBalanceModel>>() {
                                }.getType());

                        balanceAdapter.setMyBalanceModelList(myBalanceModelList);
                        balanceAdapter.notifyDataSetChanged();


                    } else if (jsonObject.getInt("status") == 0) {
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(MyWalletActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (myBalanceModelList.size() > 0) {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.llNoLising.setVisibility(View.GONE);
                } else {
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.llNoLising.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onResponseFailed(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void get_my_profile() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        Log.e(TAG, "get_my_profile: " + hashMap);

        RetrofitClient.getContentData(null, RetrofitClient.service(context).get_my_profile(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        LoginModel loginModel = Controller.getGson().fromJson(jsonObject.getJSONObject("data").toString(), LoginModel.class);
                        RealmController.copyToRealmOrUpdate(loginModel);
                    } else if (jsonObject.getInt("status") == 0) {
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(MyWalletActivity.this);
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

    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(getResources().getString(R.string.my_balance));
        binding.myToolbar.myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_white));
        binding.myToolbar.myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
}
