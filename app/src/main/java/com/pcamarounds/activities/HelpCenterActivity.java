package com.pcamarounds.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pcamarounds.R;
import com.pcamarounds.adapters.HelpCenterAdapter;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.ActivityHelpCenterBinding;
import com.pcamarounds.models.HelpCategoryModel;
import com.pcamarounds.models.helpsupport.HelpSupportModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.facebook.appevents.AppEventsLogger;


public class HelpCenterActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private AppEventsLogger logger;

    private static final String TAG = "HelpCenterActivity";
    private Context context;
    private ActivityHelpCenterBinding binding;
    private HelpCenterAdapter helpCenterAdapter;
    private List<HelpCategoryModel> helpCenterModelList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        logger = AppEventsLogger.newLogger(this);

        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("faq_usage", bundle);
        logger.logEvent("faq_usage");

        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_help_center);
        context = this;
        Utility.hideKeyboardNew(this);
        initView();
    }
    private void initView() {
        setUpToolbar();
        setupRvHelp();
        help_support();
        binding.btnContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ContactUsActivity.class);
                startActivity(intent);
            }
        });

    }
    private void setupRvHelp() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        binding.rvHelp.setLayoutManager(linearLayoutManager1);
        //binding.rvHelp.addItemDecoration(new EqualSpacing(16, context));
        helpCenterAdapter = new HelpCenterAdapter(context, helpCenterModelList);
        binding.rvHelp.setAdapter(helpCenterAdapter);

        helpCenterAdapter.setOnItemClickListener(new HelpCenterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context,HelpCenterQueActivity.class);
                intent.putExtra(AppConstants.CATEGORY_ID,helpCenterModelList.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(context.getResources().getString(R.string.help_center));
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
        super.onBackPressed();
        finish();
    }
    private void help_support() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        Log.e(TAG, "help_support: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).help_support_category(Utility.getHeaderAuthentication(context),hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    HelpSupportModel helpSupportModel = Controller.getGson().fromJson(JsonUtil.mainjson(response).toString(),HelpSupportModel.class);
                    if (helpSupportModel.getStatus() == 1) {

                        helpCenterModelList.clear();
                        helpCenterModelList = helpSupportModel.getHelpTopics();
                        helpCenterAdapter.setHelpCenterModelList(helpCenterModelList);
                        helpCenterAdapter.notifyDataSetChanged();

                    } else if (helpSupportModel.getStatus() == 0) {
                        Utility.toast(context, helpSupportModel.getMessage());
                    } else if (helpSupportModel.getStatus() == 4) { //Invalid Access Token
                        Utility.toast(context, helpSupportModel.getMessage());
                        Utility.logout(HelpCenterActivity.this);
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

}
