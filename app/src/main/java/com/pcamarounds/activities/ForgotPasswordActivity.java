package com.pcamarounds.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

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

import com.pcamarounds.R;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.SessionManager;
import com.pcamarounds.databinding.ActivityForgotPasswordBinding;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";
    private Context context;
    private ActivityForgotPasswordBinding binding;
    private String flaglogin = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_forgot_password);
        context = this;
        Utility.hideKeyboardNew(this);
        initView();
    }
    private void initView() {
        getIntentData();
        setUpToolbar();
        initializeTextWatcher();
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
                //finish();
            }
        });

    }
    private void getIntentData() {
        if (getIntent() != null) {
            flaglogin = getIntent().getStringExtra(AppConstants.FLAGLOGIN);
            // Log.e(TAG, "getIntentData: " + flaglogin);
        }
    }

    private void validation() {
        String email = binding.etEmail.getText().toString().trim();
        boolean correct = false;
        View focusview = null;

        if (TextUtils.isEmpty(email)) {
            correct = true;
            binding.etEmail.setError("Ingrese un email");
            focusview = binding.etEmail;
        } else if (!Utility.isValidEmail(email)) {
            correct = true;
            binding.etEmail.setError("Ingrese un email");
            focusview = binding.etEmail;
        }

        if (correct) {
            focusview.requestFocus();
        } else {
            forgot_password(email);
        }
    }

    private void forgot_password(String email) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS_TOKEN, SessionManager.getAccessToken(context));
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.EMAIL, email);
        Log.e(TAG, "forgot_password: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).forgot_password(hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                      //  Utility.toast(context, jsonObject.getString("message"));
                        if (flaglogin != null && flaglogin.equals(AppConstants.GUEST)) {
                            Intent intent = new Intent(context, ResetPasswordActivity.class);
                            intent.putExtra(AppConstants.EMAIL, email);
                            intent.putExtra(AppConstants.FLAGLOGIN, flaglogin);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(context, ResetPasswordActivity.class);
                            intent.putExtra(AppConstants.EMAIL, email);
                            startActivity(intent);
                        }
                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(ForgotPasswordActivity.this);

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
        binding.myToolbar.toolbarTitle.setText(getResources().getString(R.string.did_you_forget_your_password));
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

    private void initializeTextWatcher() {
        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString();
                if (email.length() > 0) {
                    if (!Utility.isValidEmail(email)) {
                        binding.ivEmail.setImageDrawable(getResources().getDrawable(R.drawable.ic_email));
                        binding.ivVerifiedEmail.setImageDrawable(getResources().getDrawable(R.drawable.ic_unverified));
                    } else {
                        binding.ivEmail.setImageDrawable(getResources().getDrawable(R.drawable.ic_email_fill));
                        binding.ivVerifiedEmail.setImageDrawable(getResources().getDrawable(R.drawable.ic_verified));
                    }
                } else {
                    binding.ivEmail.setImageDrawable(getResources().getDrawable(R.drawable.ic_email));
                    binding.ivVerifiedEmail.setImageDrawable(getResources().getDrawable(R.drawable.ic_unverified));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
