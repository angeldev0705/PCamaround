package com.pcamarounds.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

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
import com.pcamarounds.databinding.ActivityResetPasswordBinding;
import com.pcamarounds.models.LoginModel;
import com.pcamarounds.retrofit.Environment;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.AppProgressDialog;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ResetPasswordActivity";
    private ActivityResetPasswordBinding binding;
    private Context context;
    private String flaglogin = "", email = "";
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password);
        context = this;
        Utility.hideKeyboardNew(this);
        initView();
    }

    private void initView() {
        getIntentData();
        setUpToolbar();
        initializeTextWatcher();
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
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
            email = getIntent().getStringExtra(AppConstants.EMAIL);
            flaglogin = getIntent().getStringExtra(AppConstants.FLAGLOGIN);

        }
    }

    private void validation() {
        String code = binding.etCode.getText().toString().trim();
        String newPass = binding.etNewPassword.getText().toString().trim();
        String confirmPass = binding.etConfirmPassword.getText().toString().trim();
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
        if (TextUtils.isEmpty(newPass)) {
            correct = true;
            binding.etNewPassword.setError("Escriba su contraseña nuevamente");
            focusview = binding.etNewPassword;
        } else if (newPass.length() <= 5) {
            correct = true;
            binding.etNewPassword.setError("Su nueva contraseña debe tener 6 caracteres");
            focusview = binding.etNewPassword;
        }
        if (TextUtils.isEmpty(confirmPass)) {
            correct = true;
            binding.etConfirmPassword.setError("Escriba su contraseña nuevamente");
            focusview = binding.etConfirmPassword;
        } else if (!newPass.equals(confirmPass)) {
            correct = true;
            binding.etConfirmPassword.setError("Las contraseñas no coinciden");
            focusview = binding.etConfirmPassword;
        }


        if (correct) {
            focusview.requestFocus();
        } else {
            reset_password(code, newPass, confirmPass);
        }
    }

    private void reset_password(String code, String newPass, String confirmPass) {
        dialog = new Dialog(context);
        AppProgressDialog.show(dialog);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS_TOKEN, SessionManager.getAccessToken(context));
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.EMAIL, email);
        hashMap.put(AppConstants.CODE, code);
        hashMap.put(AppConstants.PASSWORD, newPass);
        hashMap.put(AppConstants.CONFIRM_PASSWORD, confirmPass);
        hashMap.put(AppConstants.IDENTITY_TYPE, AppConstants.EMAIL);
        Log.e(TAG, "reset_password: " + hashMap);

        RetrofitClient.getContentData(null, RetrofitClient.service(context).reset_password(hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                       // Utility.toast(context, jsonObject.getString("message"));
                       /* if (flaglogin != null && flaglogin.equals(AppConstants.GUEST)) {
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.putExtra(AppConstants.FLAGLOGIN, flaglogin);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(context, LoginActivity.class);
                            startActivity(intent);
                        }*/
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
                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(ResetPasswordActivity.this);

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


    public void ShowNewHidePass(View view) {
        if (view.getId() == R.id.show_newpass_btn) {
            if (binding.etNewPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                binding.showNewpassBtn.setImageResource(R.drawable.ic_eye_password_show);
                binding.etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                binding.showNewpassBtn.setImageResource(R.drawable.ic_eye_password_hide);
                binding.etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    public void ShowConfirmHidePass(View view) {
        if (view.getId() == R.id.show_confirmpass_btn) {
            if (binding.etConfirmPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                binding.showConfirmpassBtn.setImageResource(R.drawable.ic_eye_password_show);
                binding.etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                binding.showConfirmpassBtn.setImageResource(R.drawable.ic_eye_password_hide);
                binding.etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
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
                        Utility.logout(ResetPasswordActivity.this);

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
        binding.etNewPassword.addTextChangedListener(new TextWatcher() {
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
                        binding.ivNewPassword.setColorFilter(getResources().getColor(R.color.colorDarkBlack));
                    } else {
                        binding.ivNewPassword.clearColorFilter();
                    }
                } else {
                    binding.ivNewPassword.clearColorFilter();
                }
            }
        });
        binding.etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String pass = String.valueOf(s);
                if (binding.etNewPassword.getText().toString().trim().equals(pass)) {
                    binding.ivConfirmPassword.setColorFilter(getResources().getColor(R.color.colorDarkBlack));
                } else {
                    binding.ivConfirmPassword.clearColorFilter();
                }
            }
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(getResources().getString(R.string.reset_password));
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
