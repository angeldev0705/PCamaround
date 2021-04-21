package com.pcamarounds.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;


import com.google.firebase.analytics.FirebaseAnalytics;
import com.pcamarounds.R;
import com.pcamarounds.activities.address.LocationMapActivity;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.ActivityPaymentMethodBinding;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.GPSTracker;
import com.pcamarounds.utils.PersmissionUtils;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import morxander.editcard.CardPattern;
import okhttp3.ResponseBody;
import retrofit2.Response;
import com.facebook.appevents.AppEventsLogger;

public class PaymentMethodActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private AppEventsLogger logger;

    private static final String TAG = "PaymentMethodActivity";
    private Context context;
    private ActivityPaymentMethodBinding binding;
    private static final int CARD_NUMBER_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    private static final int CARD_NUMBER_TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    private static final int CARD_NUMBER_DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    private static final int CARD_NUMBER_DIVIDER_POSITION = CARD_NUMBER_DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    private static final char CARD_NUMBER_DIVIDER = '-';
    private static final int CARD_DATE_TOTAL_SYMBOLS = 5; // size of pattern MM/YY
    private static final int CARD_DATE_TOTAL_DIGITS = 4; // max numbers of digits in pattern: MM + YY
    private static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
    private static final int CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
    private static final char CARD_DATE_DIVIDER = '/';
    private static final int CARD_CVC_TOTAL_SYMBOLS = 3;

    private String cardMonth = "", cardYear = "", cardno = "", cvv = "", cardname = "",address = "";
    private String[] stringsmonth;
    private String[] stringsyear;
    private int checkedItem = 0, checkedmonth = -1, checkedyear = -1;
    ArrayList<String> monthListSpinner = new ArrayList<>();
    ArrayList<String> yearListSpinner = new ArrayList<>();
    private String previousNumber;
    private String flag = "",card_type = "";
    private Dialog dialog;
    private PersmissionUtils persmissionUtils;
    private GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        logger = AppEventsLogger.newLogger(this);

        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_method);
        context = this;
        persmissionUtils = new PersmissionUtils(context, PaymentMethodActivity.this);
        persmissionUtils.checkLocationPermissions();
        dialog = new Dialog(context);
        Utility.hideKeyboardNew(this);
        initView();
    }

    private void initView() {
        flag = getIntent().getStringExtra(AppConstants.FLAG);
        if (Utility.isCheckEmptyOrNull(flag) && flag.equals("add")) {
            binding.rlPrice.setVisibility(View.GONE);
        } else {
            binding.rlPrice.setVisibility(View.VISIBLE);
        }

        if (persmissionUtils.checkLocationPermissions()) {
            gps = new GPSTracker(context);
            address = Utility.getAddressFromLatLong(gps.getLatitude(), gps.getLongitude(),context);
        } else {
            Log.e(TAG, "onClick: elseee ");
        }


        initializeTextWatcher();
        setMonthList();
        setYearList();
        setUpToolbar();
        binding.tvMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedItem = 0;
                popupSingle(stringsmonth, "mes", checkedmonth);

            }
        });
        binding.tvYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedItem = 0;
                popupSingle(stringsyear, "año", checkedyear);
            }
        });

        binding.btnAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isCheckEmptyOrNull(binding.etCardNo.getText().toString())) {
                    String cardnum = binding.etCardNo.getText().toString().replace("-", "");
                    changeIcon(cardnum);
                    if (Utility.isCheckEmptyOrNull(binding.etcvv.getText().toString())) {
                        if (Utility.isCheckEmptyOrNull(cardMonth)) {
                            if (Utility.isCheckEmptyOrNull(cardYear)) {
                                if (Utility.isCheckEmptyOrNull(flag) && flag.equals("add")) {
                                    job_card_add("add");
                                } else {
                                    job_card_add("payment");
                                }
                            } else {
                                Utility.toast(context, "Please select exp year");
                            }
                        } else {
                            Utility.toast(context, "Please select exp. month");
                        }
                    } else {
                        Utility.toast(context, "Please enter cvv number");
                    }
                 /*  if (cardnum.length() == 16) {

                   }else {
                       Utility.toast(context, "Please enter 16 digit card number");
                   }*/
                } else {
                    Utility.toast(context, "Please enter card number");
                }
            }
        });
    }
    private void changeIcon(String s) {
        if (s.startsWith("4") || s.matches(CardPattern.VISA)) {
            card_type = "Visa";
        } else if (s.matches(CardPattern.MASTERCARD_SHORTER) || s.matches(CardPattern.MASTERCARD_SHORT) || s.matches(CardPattern.MASTERCARD)) {
            card_type = "MasterCard";
        } else if (s.matches(CardPattern.AMERICAN_EXPRESS)) {
            card_type = "American_Express";
        } else if (s.matches(CardPattern.DISCOVER_SHORT) || s.matches(CardPattern.DISCOVER)) {
            card_type = "Discover";
        } else if (s.matches(CardPattern.JCB_SHORT) || s.matches(CardPattern.JCB)) {
            card_type = "JCB";
        } else if (s.matches(CardPattern.DINERS_CLUB_SHORT) || s.matches(CardPattern.DINERS_CLUB)) {
            card_type = "Diners_Club";
        }  else if (s.matches(Utility.Maestro_Card)) {
            card_type = "Maestro_Card";
        }
        else {
            card_type = "unknown";
        }

    }

    private void job_card_add(String action) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId() + "");
        hashMap.put(AppConstants.CCNUM, binding.etCardNo.getText().toString().replace("-", ""));
        hashMap.put(AppConstants.CVV, binding.etcvv.getText().toString());
        String yearLastTwo = cardYear.substring(2);
        hashMap.put(AppConstants.CCEXP, cardMonth + yearLastTwo);
        hashMap.put(AppConstants.ACTION, action);
        hashMap.put(AppConstants.CARD_TYPE, card_type);
        hashMap.put(AppConstants.ADDRESS, address);
        Log.e(TAG, "job_card_add: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).job_card_add(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        Bundle bundle = new Bundle();
                        mFirebaseAnalytics.logEvent("add_payment_method", bundle);
                        logger.logEvent("add_payment_method");

                        if (action.equals("add")) {
                            onBackPressed();
                        } else {
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    } else if (jsonObject.getInt("status") == 0) {
                        if (jsonObject.getString("message").equals("Customer Added Please try again")) {
                            request_Confirmed_pop("Esta tarjeta ya está agregada a su cuenta");
                        } else {
                            Utility.toast(context, jsonObject.getString("message"));
                        }
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(PaymentMethodActivity.this);
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

    private void request_Confirmed_pop(String name) {
        dialog.setContentView(R.layout.request_completed_pop);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = dialog.findViewById(R.id.tvmsgggg);
        textView.setText(name);
        com.google.android.material.button.MaterialButton canel = dialog.findViewById(R.id.btnCancel);
        com.google.android.material.button.MaterialButton ok = dialog.findViewById(R.id.btnConfirm);
        canel.setVisibility(View.GONE);
        ok.setText("Aceptar");
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.show();
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


    private void popupSingle(final String[] sss, final String flag, int checklist) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Por favor seleccione " + flag);
        checkedItem = checklist;
        dialogBuilder.setSingleChoiceItems(sss, checkedItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItem = which;
                        if (flag.equalsIgnoreCase("mes")) {
                            binding.tvMonth.setText(sss[checkedItem]);
                            checkedmonth = checkedItem;
                            cardMonth = sss[checkedItem];
                        } else if (flag.equalsIgnoreCase("año")) {
                            binding.tvYear.setText(sss[checkedItem]);
                            checkedyear = checkedItem;
                            cardYear = sss[checkedItem];
                        }
                        dialog.dismiss();
                    }
                });

        dialogBuilder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        dialogBuilder.create().show();
    }


    private void setMonthList() {
        for (int i = 1; i <= 12; i++) {
            if (i <= 9) {
                monthListSpinner.add("0" + i);
            } else {
                monthListSpinner.add("" + i);
            }
        }
        stringsmonth = monthListSpinner.toArray(new String[monthListSpinner.size()]);

    }

    private void setYearList() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        // Log.e(TAG, "alert() called current year " + year);
        for (int i = 0; i <= 50; i++) {
            int y = year + i;
            yearListSpinner.add("" + y);
        }
        stringsyear = yearListSpinner.toArray(new String[yearListSpinner.size()]);

    }

    private void initializeTextWatcher() {
        // mobile text watcher
        binding.etCardNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, CARD_NUMBER_TOTAL_SYMBOLS, CARD_NUMBER_DIVIDER_MODULO, CARD_NUMBER_DIVIDER)) {
                    s.replace(0, s.length(), concatString(getDigitArray(s, CARD_NUMBER_TOTAL_DIGITS), CARD_NUMBER_DIVIDER_POSITION, CARD_NUMBER_DIVIDER));
                }
            }
        });

        binding.etcvv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > CARD_CVC_TOTAL_SYMBOLS) {
                    s.delete(CARD_CVC_TOTAL_SYMBOLS, s.length());
                }
            }
        });
        binding.etNameOnCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = String.valueOf(s);

            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        onRequestPermissionsRes(requestCode, permissions, grantResults);
    }

    public void onRequestPermissionsRes(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (permissions.length == 0) {
            return;
        }
        boolean allPermissionsGranted = true;
        if (grantResults.length > 0) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
        }
        if (!allPermissionsGranted) {
            boolean somePermissionsForeverDenied = false;
            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    //denied
                    Log.e("denied", permission);
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
                } else {
                    if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                        //allowed
                        Log.e("allowed", permission);
                    } else {
                        //set to never ask again
                        Log.e("set to never ask again", permission);
                        somePermissionsForeverDenied = true;
                    }
                }
            }
            if (somePermissionsForeverDenied) {
                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Permissions Required")
                        .setMessage(context.getString(R.string.permission_message))

                        .setPositiveButton(context.getString(R.string.settings), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        } else {
            switch (requestCode) {
                case PersmissionUtils.REQUEST_LOCATION_PERMISSION:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }

    }

    private boolean isInputCorrect(Editable s, int size, int dividerPosition, char divider) {
        boolean isCorrect = s.length() <= size;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    private String concatString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }
}
