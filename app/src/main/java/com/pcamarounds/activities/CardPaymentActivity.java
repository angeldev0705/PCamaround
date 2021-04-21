package com.pcamarounds.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.pcamarounds.R;
import com.pcamarounds.adapters.CardListAdapter;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.ActivityCardPaymentBinding;
import com.pcamarounds.models.CardListModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.DecimalDigitsInputFilter;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class CardPaymentActivity extends AppCompatActivity {
    private static final String TAG = "CardPaymentActivity";
    private Context context;
    private ActivityCardPaymentBinding binding;
    private List<CardListModel> cardListModelList = new ArrayList<>();
    private Dialog dialog;
    private String cardNumber = "", cvv = "",address = "", expMonthYear = "",customer_mobile_id = "",customer_booking_id = "";
    private int pos = 0;
    private String Amo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_card_payment);
        context = this;
        Utility.hideKeyboardNew(this);
        initView();
    }

    private void initView() {
        dialog = new Dialog(context);
        Amo = getIntent().getStringExtra(AppConstants.AMOUNT);
        if (Utility.isCheckEmptyOrNull(Amo)) {
            binding.etAmount.setText(Amo);
            double amount = Double.parseDouble(Amo);
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat formatter = (DecimalFormat) nf;
            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            formatter.applyPattern("#,###0.00");
            binding.tvPrice.setText("$" + formatter.format(amount));
        }
        binding.etAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        initializeTextWatcher();
        setUpToolbar();
        binding.rlCardList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card_list_popup();
            }
        });

        binding.etBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rlCardList.performClick();
            }
        });

        binding.btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.etAmount.getText().toString().equals("")) {
                    if (Utility.isCheckEmptyOrNull(customer_mobile_id)) {
                       /* if (Utility.isCheckEmptyOrNull(cvv)) {
                            if (Utility.isCheckEmptyOrNull(expMonthYear)) {
                                add_wallet_amount();
                            } else {
                                Utility.toast(context, "Please select exp. month/year");
                            }
                        } else {
                            Utility.toast(context, "Please enter cvv number");
                        }*/
                        if (!binding.etcvv.getText().toString().equals(""))  {
                            add_wallet_amount();
                        } else {
                            Utility.toast(context, "Ingrese el cvv de su tarjeta");
                        }

                    } else {
                        Utility.toast(context, "Please enter card number");
                    }
                }else {
                    Utility.toast(context, "Please enter amount!");
                }

            }
        });

    }

    private void add_wallet_amount() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId() + "");
        hashMap.put(AppConstants.CCNUM, cardNumber);
        hashMap.put(AppConstants.CVV, binding.etcvv.getText().toString().trim());
        hashMap.put(AppConstants.CCEXP, expMonthYear);
        hashMap.put(AppConstants.CUSTOMER_MOBILE_ID, customer_mobile_id);
        hashMap.put(AppConstants.CUSTOMER_BOOKING_ID, customer_booking_id);
        hashMap.put(AppConstants.ADDRESS, address);
        hashMap.put(AppConstants.wallet_amount, binding.etAmount.getText().toString().trim());


        Log.e(TAG, "add_wallet_amount: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).add_wallet_amount(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {

                        Intent intent = new Intent(context, MyWalletActivity.class);
                        startActivity(intent);
                        finishAffinity();

                    } else if (jsonObject.getInt("status") == 0) {

                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(CardPaymentActivity.this);
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
                        cardListModelList.clear();
                        CardListModel chooseStyleModel = new CardListModel();
                        chooseStyleModel.setCcnumber("Agregar tarjeta");
                        chooseStyleModel.setId("0");
                        cardListModelList.add(chooseStyleModel);
                        List<CardListModel> chooseStyleModelList = new ArrayList<>();
                        chooseStyleModelList = Controller.getGson().fromJson(jsonObject.getJSONArray("data").toString(),
                                new TypeToken<ArrayList<CardListModel>>() {
                                }.getType());
                        cardListModelList.addAll(chooseStyleModelList);

                     /*   cardListModelList.clear();
                        cardListModelList = Controller.getGson().fromJson(jsonObject.getJSONArray("data").toString(),
                                new TypeToken<ArrayList<CardListModel>>() {
                                }.getType());
*/
                        if (cardListModelList.size() > 1) {
                            pos = 1;
                            cardNumber = cardListModelList.get(1).getCcnumber();
                            cvv = cardListModelList.get(1).getCvv();
                            expMonthYear = cardListModelList.get(1).getCcexp();
                            customer_mobile_id = cardListModelList.get(1).getCustomerMobileId();
                            customer_booking_id = cardListModelList.get(1).getCustomerBookingId();
                            address = cardListModelList.get(1).getAddress();
                            String number = cardNumber;
                            String mask = number.replaceAll("\\w(?=\\w{4})", "*");
                            if (Utility.isCheckEmptyOrNull(expMonthYear) && expMonthYear.length()>3)
                            {
                                String mm = expMonthYear.charAt(0)+""+expMonthYear.charAt(1);
                                String yy = expMonthYear.charAt(2)+""+expMonthYear.charAt(3);
                                binding.tvMonthYear.setText(mm+"/"+yy);
                            }else {
                                binding.tvMonthYear.setText(expMonthYear);
                            }
                            binding.etBank.setText(mask);
                            changeIcon(binding.etBank,cardListModelList.get(1).getCardType());

                        }
                    } else if (jsonObject.getInt("status") == 0) {

                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(CardPaymentActivity.this);
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

    private void card_list_popup() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.card_list_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewLast);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager1);
        CardListAdapter cardListAdapter = new CardListAdapter(context, cardListModelList);
        recyclerView.setAdapter(cardListAdapter);
        cardListAdapter.setCardListModelList(cardListModelList);
        cardListAdapter.notifyDataSetChanged();
        cardListAdapter.setCurrentPos(pos);
        cardListAdapter.setOnItemClickListener(new CardListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (view.getId() == R.id.rb) {
                    if (position == 0) {
                        dialog.dismiss();
                        Intent intent = new Intent(context, PaymentMethodActivity.class);
                        intent.putExtra(AppConstants.FLAG, "add");
                        startActivity(intent);
                    } else {
                        pos = position;
                        dialog.dismiss();
                        cardNumber = cardListModelList.get(position).getCcnumber();
                        cvv = cardListModelList.get(position).getCvv();
                        expMonthYear = cardListModelList.get(position).getCcexp();
                        customer_mobile_id = cardListModelList.get(position).getCustomerMobileId();
                        customer_booking_id = cardListModelList.get(position).getCustomerBookingId();
                        address = cardListModelList.get(position).getAddress();
                        String number = cardNumber;
                        String mask = number.replaceAll("\\w(?=\\w{4})", "*");
                        if (Utility.isCheckEmptyOrNull(expMonthYear) && expMonthYear.length()>3)
                        {
                            String mm = expMonthYear.charAt(0)+""+expMonthYear.charAt(1);
                            String yy = expMonthYear.charAt(2)+""+expMonthYear.charAt(3);
                            binding.tvMonthYear.setText(mm+"/"+yy);
                        }else {
                            binding.tvMonthYear.setText(expMonthYear);
                        }
                        binding.etBank.setText(mask);
                        changeIcon(binding.etBank,cardListModelList.get(position).getCardType());
                    }
                } else {
                    if (position == 0) {
                        dialog.dismiss();
                        Intent intent = new Intent(context, PaymentMethodActivity.class);
                        intent.putExtra(AppConstants.FLAG, "add");
                        startActivity(intent);
                    } else {
                        pos = position;
                        dialog.dismiss();
                        cardNumber = cardListModelList.get(position).getCcnumber();
                        cvv = cardListModelList.get(position).getCvv();
                        expMonthYear = cardListModelList.get(position).getCcexp();
                        customer_mobile_id = cardListModelList.get(position).getCustomerMobileId();
                        customer_booking_id = cardListModelList.get(position).getCustomerBookingId();
                        address = cardListModelList.get(position).getAddress();
                        String number = cardNumber;
                        String mask = number.replaceAll("\\w(?=\\w{4})", "*");
                        if (Utility.isCheckEmptyOrNull(expMonthYear) && expMonthYear.length()>3)
                        {
                            String mm = expMonthYear.charAt(0)+""+expMonthYear.charAt(1);
                            String yy = expMonthYear.charAt(2)+""+expMonthYear.charAt(3);
                            binding.tvMonthYear.setText(mm+"/"+yy);
                        }else {
                            binding.tvMonthYear.setText(expMonthYear);
                        }
                        binding.etBank.setText(mask);
                        changeIcon(binding.etBank,cardListModelList.get(position).getCardType());
                    }
                }
            }
        });


        dialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

/*
        dialog.findViewById(R.id.btnSaveLast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardListAdapter.getCurrentPos() >= 0) {
                    if (cardListAdapter.getCurrentPos() == 0) {
                        dialog.dismiss();
                        Intent intent = new Intent(context, PaymentMethodActivity.class);
                        intent.putExtra(AppConstants.FLAG, "add");
                        startActivity(intent);
                    } else {
                        dialog.dismiss();
                        cardNumber = cardListModelList.get(cardListAdapter.getCurrentPos()).getCcnumber();
                        cvv = cardListModelList.get(cardListAdapter.getCurrentPos()).getCvv();
                        expMonthYear = cardListModelList.get(cardListAdapter.getCurrentPos()).getCcexp();
                        String number = cardNumber;
                        String mask = number.replaceAll("\\w(?=\\w{4})", "*");
                        binding.etBank.setText(mask);
                    }
                } else {
                    Utility.toast(context, "Por favor seleccione tarjeta");
                }
            }
        });
*/

        dialog.show();
    }
    private void changeIcon(TextView textView, String s) {
        if (s.equals("Visa")) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vi, 0, 0, 0);
            //type = "Visa";
        } else if (s.equals("MasterCard")) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mc, 0, 0, 0);
            //type = "MasterCard";
        } else if (s.equals("American_Express")) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.am, 0, 0, 0);
            // type = "American_Express";
        } else if (s.equals("Discover")) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ds, 0, 0, 0);
            // type = "Discover";
        } else if (s.equals("JCB")) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.jcb, 0, 0, 0);
            // type = "JCB";
        } else if (s.equals("Diners_Club")) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dc, 0, 0, 0);
            // type = "Diners_Club";
        } else if (s.equals("Maestro_Card")) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.maestro, 0, 0, 0);
            // type = "Maestro_Card";
        } else if (s.equals("unknown")) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.card, 0, 0, 0);
            // type = "Maestro_Card";
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.card, 0, 0, 0);
            // type = "UNKNOWN";
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        card_list();
    }
    private void initializeTextWatcher() {
        // mobile text watcher
        binding.etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString();

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    binding.etAmount.setError(null);
                    if (!s.toString().equals("-")) {
                        double amount = Double.parseDouble(s.toString());
                        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                        DecimalFormat formatter = (DecimalFormat) nf;
                        formatter.applyPattern("#,###0.00");
                        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
                        binding.tvPrice.setText("$" + formatter.format(amount));
                    }
                } else {
                    binding.tvPrice.setText("$0.00");
                }
            }
        });

/*
        binding.etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    binding.etAmount.setError(null);
                } else {
                    //binding.etPrice.setError(getResources().getString(R.string.please_enter_dish_price));
                    //categorySheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                // number this the string which not contain prefix and ,
                String number = str.replaceAll("[,]", "");
                // for prevent afterTextChanged recursive call
                if (number.equals(previousNumber) || number.isEmpty()) {
                    return;
                }
                previousNumber = number;

                long pp = Long.parseLong(number);
                String strprice = NumberFormat.getNumberInstance(Locale.ENGLISH).format(pp);

                // String formattedString = formatNumber(number);
                binding.etAmount.removeTextChangedListener(this); // Remove listener
                binding.etAmount.setText(strprice);
                handleSelection();
                binding.etAmount.addTextChangedListener(this); // Add back the listener
            }
        });
*/

    }

    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(getResources().getString(R.string.my_balance));
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
