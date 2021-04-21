package com.pcamarounds.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.pcamarounds.R;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.controller.SessionManager;
import com.pcamarounds.databinding.ActivityChangePasswordBinding;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    private static final String TAG = "ChangePasswordActivity";
    private Context context;
    private ActivityChangePasswordBinding binding;
    private boolean isClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        context = this;
        Utility.hideKeyboardNew(this);
        initView();
    }

    private void initView() {
        setUpToolbar();
        initializeTextWatcher();
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                validation();
            }
        });

    }

    private void validation() {
        String oldPass = binding.etOldPassword.getText().toString().trim();
        String newPass = binding.etNewPassword.getText().toString().trim();
        String confirmPass = binding.etConfirmPassword.getText().toString().trim();


        boolean correct = false;
        View focusview = null;

        if (TextUtils.isEmpty(oldPass)) {
            correct = true;
            binding.etOldPassword.setError("Old Password can not be empty!");
            focusview = binding.etOldPassword;
        }/* else if (oldPass.length() <= 7) {
            correct = true;
            binding.etOldPassword.setError("Old Password must be 8 characters!");
            focusview = binding.etOldPassword;
        }*/
        if (TextUtils.isEmpty(newPass)) {
            correct = true;
            binding.etNewPassword.setError("New Password can not be empty!");
            focusview = binding.etNewPassword;
        } else if (newPass.length() <= 5) {
            correct = true;
            binding.etNewPassword.setError("Su nueva contraseña debe tener 6 caracteres");
            focusview = binding.etNewPassword;
        }
        if (TextUtils.isEmpty(confirmPass)) {
            correct = true;
            binding.etConfirmPassword.setError("Escriba su contraseña neuvamente");
            focusview = binding.etConfirmPassword;
        } else if (!newPass.equals(confirmPass)) {
            correct = true;
            binding.etConfirmPassword.setError("Las contraseñas no coinciden");
            focusview = binding.etConfirmPassword;
        }

        if (correct) {
            focusview.requestFocus();
        } else {
            change_password(oldPass, newPass, confirmPass);
        }
    }

    private void change_password(String oldPass, String newPass, String confirmPass) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS_TOKEN, SessionManager.getAccessToken(context));
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.OLD_PASSWORD, oldPass);
        hashMap.put(AppConstants.NEW_PASSWORD, newPass);
        hashMap.put(AppConstants.CONFIRM_PASSWORD, confirmPass);
        Log.e(TAG, "change_password: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).change_password(Utility.getHeaderAuthentication(context),hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        Utility.toast(context, jsonObject.getString("message"));
                        finish();
                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(ChangePasswordActivity.this);

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
        binding.etOldPassword.addTextChangedListener(new TextWatcher() {
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
                    binding.ivOldPassword.setColorFilter(getResources().getColor(R.color.colorDarkBlack));
                 /*   if (pass.length() > 7) {
                        binding.ivOldPassword.setColorFilter(getResources().getColor(R.color.colorDarkBlack));
                    } else {
                        binding.ivOldPassword.clearColorFilter();
                    }*/
                } else {
                    binding.ivOldPassword.clearColorFilter();
                }
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
    public void ShowOldHidePass(View view){
        if(view.getId()==R.id.show_oldpass_btn){
            if(binding.etOldPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                binding.showOldpassBtn.setImageResource(R.drawable.ic_eye_password_show);
                binding.etOldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                binding.showOldpassBtn.setImageResource(R.drawable.ic_eye_password_hide);
                binding.etOldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }
    public void ShowNewHidePass(View view){
        if(view.getId()==R.id.show_newpass_btn){
            if(binding.etNewPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                binding.showNewpassBtn.setImageResource(R.drawable.ic_eye_password_show);
                binding.etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                binding.showNewpassBtn.setImageResource(R.drawable.ic_eye_password_hide);
                binding.etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }
    public void ShowConfirmHidePass(View view){
        if(view.getId()==R.id.show_confirmpass_btn){
            if(binding.etConfirmPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                binding.showConfirmpassBtn.setImageResource(R.drawable.ic_eye_password_show);
                binding.etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                binding.showConfirmpassBtn.setImageResource(R.drawable.ic_eye_password_hide);
                binding.etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }
    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(context.getResources().getString(R.string.change_password));
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
