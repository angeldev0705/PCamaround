package com.pcamarounds.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.pcamarounds.R;
import com.pcamarounds.adapters.BankListAdapter;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.ActivityBankAccountBinding;
import com.pcamarounds.models.BankListModel;
import com.pcamarounds.models.BankModel;
import com.pcamarounds.models.IFSCModel;
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

public class BankAccountActivity extends AppCompatActivity {
    private static final String TAG = "BankAccountActivity";
    private Context context;
    private ActivityBankAccountBinding binding;
    private String accountType = "";
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
    private List<BankListModel> bankListModels = new ArrayList<>();
    private List<IFSCModel> ifscModels = new ArrayList<>();

    private Dialog dialog;
    private String bank_id = "", route_no = "";
    private int pos = -1;
    private int posroute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bank_account);
        context = this;
        Utility.hideKeyboardNew(this);
        initView();
    }

    private void initView() {
        setUpToolbar();
        initializeTextWatcher();
        tech_bank_list();
        binding.tvBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bank_list_popup();
            }
        });
/*
        binding.tvIFSC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ifsc_list_popup();
            }
        });
*/

        binding.rgAcType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbSaving:
                        accountType = "Saving";
                        break;
                    case R.id.rbStream:
                        accountType = "Corriente";
                        break;
                }
            }
        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.etAcHolderName.getText().toString().equals("")) {
                    if (Utility.isCheckEmptyOrNull(binding.etAcNo.getText().toString())) {
                        if (Utility.isCheckEmptyOrNull(bank_id)) {
                            if (Utility.isCheckEmptyOrNull(accountType)) {
                                add_bank_infotech();
                            } else {
                                Utility.toast(context, "Seleccione un tipo de cuenta");
                            }
                         /*   if (Utility.isCheckEmptyOrNull(route_no)) {

                            } else {
                                Utility.toast(context, "Please select route number");
                            }*/
                        } else {
                            Utility.toast(context, "Seleccione un banco");
                        }
                    } else {
                        Utility.toast(context, "Ingrese el número de cuenta");
                    }
                } else {
                    Utility.toast(context, "Ingrese el titular de la cuenta");
                }

            }
        });
        binding.btnSaveUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.etAcHolderName.getText().toString().equals("")) {
                    if (Utility.isCheckEmptyOrNull(binding.etAcNo.getText().toString())) {
                        if (Utility.isCheckEmptyOrNull(bank_id)) {
                            if (Utility.isCheckEmptyOrNull(accountType)) {
                                view_edit_infotech("edit");
                            } else {
                                Utility.toast(context, "Seleccione un tipo de cuenta");
                            }
                          /*  if (Utility.isCheckEmptyOrNull(route_no)) {

                            } else {
                                Utility.toast(context, "Please select route number");
                            }*/
                        } else {
                            Utility.toast(context, "Seleccione un banco");
                        }
                    } else {
                        Utility.toast(context, "Ingrese el número de cuenta");
                    }
                } else {
                    Utility.toast(context, "Please enter amount holder name!");
                }

            }
        });

    }

    private void add_bank_infotech() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId() + "");
        hashMap.put(AppConstants.ACC_NUMBER, binding.etAcNo.getText().toString());
        hashMap.put(AppConstants.ACC_HOLDER, binding.etAcHolderName.getText().toString());
        hashMap.put(AppConstants.BANKS_ID, bank_id);
        hashMap.put(AppConstants.ACC_TYPE, accountType);
        hashMap.put(AppConstants.ACC_BANK, binding.tvBank.getText().toString());
        hashMap.put(AppConstants.ROUTE_NO, route_no);

        Log.e(TAG, "add_bank_infotech: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).add_bank_infotech(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        onBackPressed();
                    } else if (jsonObject.getInt("status") == 0) {

                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(BankAccountActivity.this);
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

    private void view_edit_infotech(String action) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId() + "");
        hashMap.put(AppConstants.ACC_NUMBER, binding.etAcNo.getText().toString());
        hashMap.put(AppConstants.ACC_HOLDER, binding.etAcHolderName.getText().toString());
        hashMap.put(AppConstants.BANKS_ID, bank_id);
        hashMap.put(AppConstants.ACC_TYPE, accountType);
        hashMap.put(AppConstants.ROUTE_NO, route_no);
        hashMap.put(AppConstants.ACC_BANK, binding.tvBank.getText().toString());
        hashMap.put(AppConstants.ACTION, action);

        Log.e(TAG, "view_edit_infotech: " + hashMap);
        Dialog dialog;
        if (action.equals("view")) {
            dialog = null;
        } else {
            dialog = new Dialog(context);
        }
        RetrofitClient.getContentData(dialog, RetrofitClient.service(context).view_edit_infotech(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        if (action.equals("view")) {
                            if (jsonObject.getJSONObject("data").length() > 0) {
                                BankModel bankModel = Controller.getGson().fromJson(jsonObject.getJSONObject("data").toString(), BankModel.class);
                                binding.etAcHolderName.setText(bankModel.getAccHolder());
                                binding.etAcNo.setText(bankModel.getAccNumber());
                                if (bankModel.getAccType().equals("Saving")) {
                                    accountType = "Saving";
                                    binding.rbSaving.setChecked(true);
                                } else {
                                    accountType = "Corriente";
                                    binding.rbStream.setChecked(true);
                                }
                                binding.btnSave.setVisibility(View.GONE);
                                binding.btnSaveUpdate.setVisibility(View.VISIBLE);
                                for (int i = 0; i < bankListModels.size(); i++) {
                                    if (bankModel.getBanksId().equals(bankListModels.get(i).getId())) {
                                        pos = i;
                                        bank_id = bankListModels.get(i).getId();
                                        binding.tvBank.setText(Utility.capitalize(bankListModels.get(i).getBankName()));
                                       // route_no = bankModel.getRouteNo();
                                       // binding.tvIFSC.setText(Utility.capitalize(bankModel.getRouteNo()));
                                       // tech_bank_route();
                                    }
                                }


                            }
                        } else {
                            onBackPressed();
                        }
                    } else if (jsonObject.getInt("status") == 0) {
                        binding.btnSave.setVisibility(View.VISIBLE);
                        binding.btnSaveUpdate.setVisibility(View.GONE);

                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(BankAccountActivity.this);
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

    private void tech_bank_list() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId() + "");

        Log.e(TAG, "tech_bank_list: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).tech_bank_list(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        bankListModels.clear();
                        bankListModels = Controller.getGson().fromJson(jsonObject.getJSONArray("data").toString(),
                                new TypeToken<ArrayList<BankListModel>>() {
                                }.getType());
                    } else if (jsonObject.getInt("status") == 0) {

                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(BankAccountActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                view_edit_infotech("view");
            }

            @Override
            public void onResponseFailed(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

/*
    private void tech_bank_route() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId() + "");
        hashMap.put(AppConstants.BANK_ID, bank_id);

        Log.e(TAG, "tech_bank_route: " + hashMap);

        RetrofitClient.getContentData(null, RetrofitClient.service(context).tech_bank_route(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        ifscModels.clear();
                        ifscModels = Controller.getGson().fromJson(jsonObject.getJSONArray("data").toString(),
                                new TypeToken<ArrayList<IFSCModel>>() {
                                }.getType());

                            posroute = 0;
                            route_no = ifscModels.get(0).getRoutes();
                            binding.tvIFSC.setText(Utility.capitalize(ifscModels.get(0).getRoutes()));

*/
/*
                        for (int i = 0; i < ifscModels.size(); i++) {
                            if (route_no.equals(ifscModels.get(i).getRoutes())) {
                                posroute = i;
                                route_no = ifscModels.get(i).getRoutes();
                                binding.tvIFSC.setText(Utility.capitalize(ifscModels.get(i).getRoutes()));
                            }
                        }
*//*

                    } else if (jsonObject.getInt("status") == 0) {

                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(BankAccountActivity.this);
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
*/

    private void bank_list_popup() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.bank_list_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = dialog.findViewById(R.id.tvTitle);
        textView.setText("Seleccionar Bank");
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewLast);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager1);
        BankListAdapter cardListAdapter = new BankListAdapter(context, bankListModels);
        recyclerView.setAdapter(cardListAdapter);
        cardListAdapter.setBankListModelList(bankListModels);
        cardListAdapter.notifyDataSetChanged();
        cardListAdapter.setCurrentPos(pos);
        cardListAdapter.setOnItemClickListener(new BankListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (view.getId() == R.id.rb) {
                    binding.tvBank.setText(Utility.capitalize(bankListModels.get(position).getBankName()));
                    bank_id = bankListModels.get(position).getId();
                    //tech_bank_route();
                    dialog.dismiss();
                } else {
                    binding.tvBank.setText(Utility.capitalize(bankListModels.get(position).getBankName()));
                    bank_id = bankListModels.get(position).getId();
                   // tech_bank_route();
                    dialog.dismiss();
                }
            }
        });


        dialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

/*
    private void ifsc_list_popup() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.bank_list_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = dialog.findViewById(R.id.tvTitle);
        textView.setText("Seleccione banco ruta");
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewLast);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager1);
        IFSCAdapter cardListAdapter = new IFSCAdapter(context, ifscModels);
        recyclerView.setAdapter(cardListAdapter);
        cardListAdapter.setIFSCModelList(ifscModels);
        cardListAdapter.notifyDataSetChanged();
        cardListAdapter.setCurrentPos(posroute);
        cardListAdapter.setOnItemClickListener(new IFSCAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (view.getId() == R.id.rb) {
                    posroute = position;
                    binding.tvIFSC.setText(Utility.capitalize(ifscModels.get(position).getRoutes()));
                    route_no = ifscModels.get(position).getRoutes();
                    dialog.dismiss();
                } else {
                    posroute = position;
                    binding.tvIFSC.setText(Utility.capitalize(ifscModels.get(position).getRoutes()));
                    route_no = ifscModels.get(position).getRoutes();
                    dialog.dismiss();
                }
            }
        });


        dialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
*/


    private void initializeTextWatcher() {
        // mobile text watcher
        binding.etAcHolderName.addTextChangedListener(new TextWatcher() {
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
                    binding.etAcHolderName.setError(null);
                }
            }
        });

        binding.etAcNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    binding.etAcNo.setError(null);
                }
            }
        });

        binding.tvBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    binding.tvBank.setError(null);
                }

            }
        });

    }


    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(context.getResources().getString(R.string.bank_account));
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
