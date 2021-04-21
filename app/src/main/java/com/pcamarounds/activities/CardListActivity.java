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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.reflect.TypeToken;
import com.pcamarounds.R;
import com.pcamarounds.adapters.CardListShowAdapter;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.ActivityCardListBinding;
import com.pcamarounds.models.CardListModel;
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

public class CardListActivity extends AppCompatActivity {
    private static final String TAG = "CardListActivity";
    private Context context;
    private ActivityCardListBinding binding;
    private String flag = "";
    private List<CardListModel> cardListModelList = new ArrayList<>();
    private CardListShowAdapter cardListShowAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_card_list);
        context = this;
        initView();
    }
    private void initView() {
        flag = getIntent().getStringExtra(AppConstants.FLAG);
        setUpToolbar();
        setupRvCard();

        binding.btnAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PaymentMethodActivity.class);
                intent.putExtra(AppConstants.FLAG,flag);
                startActivity(intent);
            }
        });


    }

    private void setupRvCard() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rv.setLayoutManager(linearLayoutManager1);
       // binding.rv.addItemDecoration(new EqualSpacing(0,context));
        binding.rv.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        cardListShowAdapter = new CardListShowAdapter(context, cardListModelList);
        binding.rv.setAdapter(cardListShowAdapter);

    }

    private void card_list() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId() + "");

        Log.e(TAG, "card_list: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).card_list(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        if (jsonObject.getJSONArray("data").length()>0) {
                            cardListModelList.clear();
                            cardListModelList = Controller.getGson().fromJson(jsonObject.getJSONArray("data").toString(),
                                    new TypeToken<ArrayList<CardListModel>>() {
                                    }.getType());
                            cardListShowAdapter.setCardListModelList(cardListModelList);
                            cardListShowAdapter.notifyDataSetChanged();
                        }
                    } else if (jsonObject.getInt("status") == 0) {

                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(CardListActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cardListModelList.size() > 0) {
                    binding.rv.setVisibility(View.VISIBLE);
                    binding.llNoLising.setVisibility(View.GONE);
                } else {
                    binding.rv.setVisibility(View.GONE);
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
    protected void onResume() {
        super.onResume();
        card_list();
    }

    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(context.getResources().getString(R.string.payment_method));
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
        finish();
    }
}