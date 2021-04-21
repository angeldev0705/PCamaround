package com.pcamarounds.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.pcamarounds.R;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.controller.SessionManager;
import com.pcamarounds.databinding.ActivityVerifyMobileBinding;
import com.pcamarounds.models.LoginModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class VerifyMobileActivity extends AppCompatActivity {
    private static final String TAG = "VerifyMobileActivity";
    private Context context;
    private ActivityVerifyMobileBinding binding;
    private String countrycode = "", mobile = "",email = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify_mobile);
        context = this;
        Utility.hideKeyboardNew(this);
        initView();

    }
    private void initView() {
        getIntentData();
        setUpToolbar();
        initializeTextWatcher();


        binding.btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });
        binding.tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resend_otp();
            }
        });

    }

    private void getIntentData() {
        if (getIntent() != null) {
            countrycode = getIntent().getStringExtra(AppConstants.CODE);
            mobile = getIntent().getStringExtra(AppConstants.MOBILE);
            email = getIntent().getStringExtra(AppConstants.EMAIL);
            binding.tvMsg.setText("Te hemos enviado un código de acceso vía SMS a tu celular +"+countrycode + " "+ mobile +" para completar la verificación");
        }
    }

    private void validation() {
        String code = binding.etCode.getText().toString().trim();
        boolean correct = false;
        View focusview = null;

        if (TextUtils.isEmpty(code)) {
            correct = true;
            binding.etCode.setError("El código debe ser 6 dígitos");
            focusview = binding.etCode;
        } else if (code.length() <= 5) {
            correct = true;
            binding.etCode.setError("El código debe ser 6 dígitos");
            focusview = binding.etCode;
        }
        if (correct) {
            focusview.requestFocus();
        } else {
            verify_mobile(code);
        }
    }

    private void verify_mobile(String code) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS_TOKEN, SessionManager.getAccessToken(context));
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        hashMap.put(AppConstants.MOBILE, mobile);
        hashMap.put(AppConstants.COUNTRY_CODE, countrycode);
        hashMap.put(AppConstants.CODE, code);
        Log.e(TAG, "verify_mobile: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).verify_mobile(Utility.getHeaderAuthentication(context),hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        Utility.toast(context, jsonObject.getString("message"));
                        LoginModel loginModel = Controller.getGson().fromJson(jsonObject.getJSONObject("data").toString(), LoginModel.class);
                        RealmController.realmControllerInIt().getRealm().beginTransaction();
                        RealmController.realmControllerInIt().getRealm().copyToRealmOrUpdate(loginModel);
                        RealmController.realmControllerInIt().getRealm().commitTransaction();
                        Intent intent = new Intent(AppConstants.BROADCAST);
                        intent.putExtra(AppConstants.KEY, "Update");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        finish();

                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(VerifyMobileActivity.this);

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

    private void initializeTextWatcher() {
        binding.etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString();
                if (email.length() > 0) {
                    if (email.length() > 5) {
                        binding.ivCode.setImageDrawable(getResources().getDrawable(R.drawable.ic_email_fill));
                        binding.ivVerifiedCode.setImageDrawable(getResources().getDrawable(R.drawable.ic_verified));
                    } else {
                        binding.ivCode.setImageDrawable(getResources().getDrawable(R.drawable.ic_email));
                        binding.ivVerifiedCode.setImageDrawable(getResources().getDrawable(R.drawable.ic_unverified));
                    }
                } else {
                    binding.ivCode.setImageDrawable(getResources().getDrawable(R.drawable.ic_email));
                    binding.ivVerifiedCode.setImageDrawable(getResources().getDrawable(R.drawable.ic_unverified));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void resend_otp() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS_TOKEN, SessionManager.getAccessToken(context));
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.EMAIL, email);
        Log.e(TAG, "resend_otp: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).resend_otp(hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(VerifyMobileActivity.this);

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText("Crear Cuenta");
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
