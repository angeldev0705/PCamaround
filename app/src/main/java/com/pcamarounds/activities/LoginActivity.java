package com.pcamarounds.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pcamarounds.R;
import com.pcamarounds.constants.AppConfig;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.controller.SessionManager;
import com.pcamarounds.databinding.ActivityLoginBinding;
import com.pcamarounds.models.LoginModel;
import com.pcamarounds.retrofit.Environment;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.AppProgressDialog;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private Context context;
    private ActivityLoginBinding binding;
    private String flaglogin = "";
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        context = this;

        Utility.hideKeyboardNew(this);
        initView();
    }

    private void initView() {
        Utility.underline(binding.tvregister);
        Locale locale = new Locale("es");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        getIntentData();
        setUpToolbar();
        initializeTextWatcher();
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(context, MainActivity.class);
                //startActivity(intent);
                //finish();
                validation();
            }
        });
        binding.tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flaglogin != null && flaglogin.equals(AppConstants.GUEST)) {
                    Intent intentsignup = new Intent(context, ForgotPasswordActivity.class);
                    intentsignup.putExtra(AppConstants.FLAGLOGIN, flaglogin);
                    startActivity(intentsignup);
                    finish();
                } else {
                    Intent intent = new Intent(context, ForgotPasswordActivity.class);
                    startActivity(intent);
                }

            }
        });

        binding.tvregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Environment.getBaseUrl() + "auth/recruitment";
                if (!url.startsWith("https://") && !url.startsWith("http://")) {
                    url = "http://" + url;
                }
                Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(openUrlIntent);
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
        String password = binding.etPassword.getText().toString().trim();


        boolean correct = false;
        View focusview = null;

        if (TextUtils.isEmpty(email)) {
            correct = true;
            binding.etEmail.setError("Escribe tu email!");
            focusview = binding.etEmail;
        } else if (!Utility.isValidEmail(email)) {
            correct = true;
            binding.etEmail.setError("Invalid email!");
            focusview = binding.etEmail;
        }
        if (TextUtils.isEmpty(password)) {
            correct = true;
            binding.etPassword.setError("Ingrese una contraseña");
            focusview = binding.etPassword;
        }
        if (correct) {
            focusview.requestFocus();
        } else {
            loginUser(email, password);
        }
    }

    private void loginUser(String email, String password) {
        dialog = new Dialog(context);
        AppProgressDialog.show(dialog);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS_TOKEN, SessionManager.getAccessToken(context));
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.EMAIL, email);
        hashMap.put(AppConstants.PASSWORD, password);
        Log.e(TAG, "loginUser: " + hashMap);

        RetrofitClient.getContentData(null, RetrofitClient.service(context).login(hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        // Utility.toast(context, jsonObject.getString("message"));
                        SessionManager.setAuthenticationToken(context, jsonObject.getString("token"));
                        LoginModel loginModel = Controller.getGson().fromJson(jsonObject.getJSONObject("data").toString(), LoginModel.class);
                        FirebaseMessaging.getInstance().subscribeToTopic(Environment.getTopicId() + loginModel.getId() + "");
                        RealmController.copyToRealmOrUpdate(loginModel);

                        // String cometId = AppConstants.CAMAROUND+loginModel.getId();
                        // createCometChat(cometId,loginModel.getFirstName());
                        String uid = loginModel.getId();
                        String name = loginModel.getFirstName().trim();
                        String email = loginModel.getEmail().trim();
                        String cometUid = Utility.getCometId(uid);
                        if (Utility.isCheckEmptyOrNull(loginModel.getLastName())) {
                            name = loginModel.getFirstName().trim() + " " + Utility.printInitials(loginModel.getLastName());
                        }
                        createUser(cometUid, name, email);
                      /*  Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                        finishAffinity();*/
                    } else if (jsonObject.getInt("status") == 0) {
                        AppProgressDialog.hide(dialog);
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        AppProgressDialog.hide(dialog);
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(LoginActivity.this);

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

    private void createUser(String UID, String name, String email) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("uid", UID);
        builder.addFormDataPart("name", name);
        builder.addFormDataPart("email", email);
        builder.addFormDataPart("avatar", "http://34.223.228.22/52a363e725235501a0fc865f1f628006.jpg");
        builder.addFormDataPart("role", "default");

        final MultipartBody requestbody = builder.build();

        Log.e(TAG, "createUser: name " + name + ", Email " + email + ", Uid " + UID);
        RetrofitClient.getContentData(null, RetrofitClient.service(context).createUser(Utility.getHeadersone(), requestbody), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    if (response.code() == 200) {
                        JSONObject jsonObject = JsonUtil.mainjson(response);
                        JSONObject data = jsonObject.getJSONObject("data");
                        SessionManager.setCometId(context, data.getString("uid"));
                        Log.e(TAG, "data.getString : commit2 " + data.getString("uid"));
                        loginCometChat(data.getString("uid"));
                    } else {
                        // AppProgressDialog.hide(dialog);
                        if (response.errorBody() != null) {
                            String respo = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(respo);
                            Log.e(TAG, "onResponse: = commit3 " + jsonObject.getString("message"));
                        } else {
                            Log.e(TAG, "onResponse error : ");
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(String error) {
                //AppProgressDialog.hide(dialog);
                loginCometChat(UID);
                //Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loginCometChat(String uid) {
        CometChat.login(uid, AppConfig.AppDetails.API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                SessionManager.setCometId(context, user.getUid());
                Log.e(TAG, "onSuccess: " + user.getUid());
                Log.e(TAG, "onSuccess session : " + SessionManager.getCometId(context));
                AppProgressDialog.hide(dialog);
                Log.e(TAG, "onSuccess: subscribeToTopic " + AppConfig.AppDetails.APP_ID + "_" + CometChatConstants.RECEIVER_TYPE_USER + "_" + user.getUid());
                FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.AppDetails.APP_ID + "_" + CometChatConstants.RECEIVER_TYPE_USER + "_" + user.getUid());
                if (flaglogin != null && flaglogin.equals(AppConstants.GUEST)) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra(AppConstants.FLAGLOGIN, flaglogin);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            }

            @Override
            public void onError(CometChatException e) {
                AppProgressDialog.hide(dialog);
                Log.e(TAG, "onError: " + e.getMessage());
            }
        });
    }

    public void ShowHidePass(View view) {
        if (view.getId() == R.id.show_pass_btn) {
            if (binding.etPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                binding.showPassBtn.setImageResource(R.drawable.ic_eye_password_show);
                binding.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                binding.showPassBtn.setImageResource(R.drawable.ic_eye_password_hide);
                binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(getResources().getString(R.string.log_in));
       /* binding.myToolbar.myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_white));
        binding.myToolbar.myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/
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
                        binding.ivVerified.setImageDrawable(getResources().getDrawable(R.drawable.ic_unverified));
                    } else {
                        binding.ivEmail.setImageDrawable(getResources().getDrawable(R.drawable.ic_email_fill));
                        binding.ivVerified.setImageDrawable(getResources().getDrawable(R.drawable.ic_verified));
                    }
                } else {
                    binding.ivEmail.setImageDrawable(getResources().getDrawable(R.drawable.ic_email));
                    binding.ivVerified.setImageDrawable(getResources().getDrawable(R.drawable.ic_unverified));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String pass = String.valueOf(s);
                if (pass.length() > 0) {
                    if (pass.length() > 5) {
                        binding.ivPassword.setColorFilter(getResources().getColor(R.color.colorDarkBlack));
                    } else {
                        binding.ivPassword.clearColorFilter();
                    }
                } else {
                    binding.ivPassword.clearColorFilter();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
