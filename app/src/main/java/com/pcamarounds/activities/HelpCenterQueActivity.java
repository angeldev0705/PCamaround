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
import com.pcamarounds.adapters.HelpCenterQueAnsAdapter;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.ActivityHelpCenterQueBinding;
import com.pcamarounds.models.HelpCenterQueAnsModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class HelpCenterQueActivity extends AppCompatActivity {
    private static final String TAG = "HelpCenterQueActivity";
    private Context context;
    private ActivityHelpCenterQueBinding binding;
    private HelpCenterQueAnsAdapter helpCenterAdapter;
    private List<HelpCenterQueAnsModel> helpCenterModelList = new ArrayList<>();
    private String category_id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_help_center_que);
        context = this;
        initView();
    }
    private void initView() {
        category_id = getIntent().getStringExtra(AppConstants.CATEGORY_ID);
        setUpToolbar();
        setupRvHelp();
        help_support_question();
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
        helpCenterAdapter = new HelpCenterQueAnsAdapter(context, helpCenterModelList);
        binding.rvHelp.setAdapter(helpCenterAdapter);

        helpCenterAdapter.setOnItemClickListener(new HelpCenterQueAnsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context,AnswerActivity.class);
                intent.putExtra(AppConstants.QUE,helpCenterModelList.get(position).getQuestion());
                intent.putExtra(AppConstants.ANS,helpCenterModelList.get(position).getAnswer());
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
    private void help_support_question() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        hashMap.put(AppConstants.CATEGORY_ID, category_id);
        Log.e(TAG, "help_support_question: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).help_support_question(Utility.getHeaderAuthentication(context),hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {

                        helpCenterModelList.clear();
                        helpCenterModelList = Controller.getGson().fromJson(jsonObject.getJSONArray("data").toString(),
                                new TypeToken<ArrayList<HelpCenterQueAnsModel>>() {
                                }.getType());

                        helpCenterAdapter.setHelpCenterQueAnsModelList(helpCenterModelList);
                        helpCenterAdapter.notifyDataSetChanged();


                    } else if (jsonObject.getInt("status") == 0) {
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(HelpCenterQueActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (helpCenterModelList.size()>0) {
                    binding.rvHelp.setVisibility(View.VISIBLE);
                    binding.llNoLising.setVisibility(View.GONE);
                } else {
                    binding.rvHelp.setVisibility(View.GONE);
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