package com.pcamarounds.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.pcamarounds.R;
import com.pcamarounds.activities.address.LocationMapActivity;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.ActivityNotificationBinding;
import com.pcamarounds.models.LoginModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.util.HashMap;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    private static final String TAG = "NotificationActivity";
    private Context context;
    private ActivityNotificationBinding binding;
    private RealmResults<LoginModel> loginModelRealmResults;
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    private double lat, longi;
    private String notification_on = "", email_on = "";
    private Dialog dialog;
    private RealmChangeListener<RealmResults<LoginModel>> realmChangeListener = (LoginModel) -> {
        setDataProfile();
    };

    private void setDataProfile() {
        if (RealmController.getUser() != null) {
            binding.etAddress.setText(RealmController.getUser().getAddress());
            binding.etRadius.setText("" + RealmController.getUser().getRadious());
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getLatitude())) {
                lat = Double.parseDouble(RealmController.getUser().getLatitude());
            }
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getLongitude())) {
                longi = Double.parseDouble(RealmController.getUser().getLongitude());
            }
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getEmailOn())) {
                if (RealmController.getUser().getEmailOn().equals("yes")) {
                    binding.switchEmail.setChecked(true);
                    email_on = "yes";
                } else {
                    binding.switchEmail.setChecked(false);
                    email_on = "no";
                }
            } else {
                binding.switchEmail.setChecked(false);
                email_on = "no";
            }
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getNotificationOn())) {
                if (RealmController.getUser().getNotificationOn().equals("yes")) {
                    binding.switchNotification.setChecked(true);
                    notification_on = "yes";
                } else {
                    binding.switchNotification.setChecked(false);
                    notification_on = "no";
                }
            } else {
                binding.switchNotification.setChecked(false);
                notification_on = "no";
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        context = this;
        dialog = new Dialog(context);
        loginModelRealmResults = RealmController.realmControllerInIt().getRealm().where(LoginModel.class).findAllAsync();
        loginModelRealmResults.addChangeListener(realmChangeListener);
        Utility.hideKeyboardNew(this);
        initView();
    }

    private void initView() {
        setUpToolbar();
        binding.switchEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        email_on = "yes";
                        email_notification();
                    } else {
                        email_on = "no";
                        email_notification();
                    }
                }

            }
        });
        binding.switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        notification_on = "yes";
                        email_notification();
                    } else {
                        notification_on = "no";
                        email_notification();
                    }
                }
            }
        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.etAddress.getText().toString().trim().equals("")) {
                    if (!binding.etRadius.getText().toString().trim().equals("")) {
                        address_pop();
                    } else {
                        Utility.toast(context, "Please enter radius!");
                    }
                } else {
                    Utility.toast(context, "Seleccione una dirección");
                }

            }
        });

        binding.rlAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LocationMapActivity.class);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
        binding.ivInfoRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.infoPopup(context, "Recibirás notificaciones de trabajos cerca de ti dentro de este radio de alcance.");
            }
        });

    }

    private void email_notification() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId() + "");
        hashMap.put(AppConstants.EMAIL_ON, email_on);
        hashMap.put(AppConstants.NOTIFICATION_ON, notification_on);

        Log.e(TAG, "email_notification: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).email_notification(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {

                        LoginModel loginModel = Controller.getGson().fromJson(jsonObject.getJSONObject("data").toString(), LoginModel.class);
                        RealmController.copyToRealmOrUpdate(loginModel);

                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(NotificationActivity.this);
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

    private void address_pop() {
        dialog.setContentView(R.layout.address_pop);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = dialog.findViewById(R.id.tvmsgggg);
        com.google.android.material.button.MaterialButton btnConfirm = dialog.findViewById(R.id.btnConfirm);
        btnConfirm.setText("Aceptar");
        textView.setText("Cambios guardados");
        dialog.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                tech_address();

            }
        });

        dialog.show();
    }

    private void tech_address() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        hashMap.put(AppConstants.LATITUDE, lat + "");
        hashMap.put(AppConstants.LONGITUDE, longi + "");
        hashMap.put(AppConstants.LOCATION, binding.etAddress.getText().toString().trim());
        hashMap.put(AppConstants.RADIOUS, binding.etRadius.getText().toString().trim());
        Log.e(TAG, "tech_address: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).tech_address(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        finish();
                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(NotificationActivity.this);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (data != null) {
                String addd = data.getStringExtra("address");
                LatLng latLng = Utility.getLatLongFromAddress(context, addd);
                if (latLng != null) {
                    lat = latLng.latitude;
                    longi = latLng.longitude;

                }
                binding.etAddress.setText(Utility.capitalize(addd));

            }
        }

    }

    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(context.getResources().getString(R.string.notification));
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
