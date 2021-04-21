package com.pcamarounds.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.pcamarounds.R;
import com.pcamarounds.adapters.AddonAdapter;
import com.pcamarounds.adapters.ImagesAdapter;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.ActivitySearchDetailBinding;
import com.pcamarounds.models.postdetail.Addon;
import com.pcamarounds.models.postdetail.ImagesModel;
import com.pcamarounds.models.postdetail.PostDetailModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.DecimalDigitsInputFilter;
import com.pcamarounds.utils.EqualSpacing;
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

public class SearchDetailActivity extends AppCompatActivity {
    private static final String TAG = "SearchDetailActivity";
    private Context context;
    private ActivitySearchDetailBinding binding;
    private ImagesAdapter imagesAdapter;
    private List<ImagesModel> imagesModelsList = new ArrayList<>();
    private String job_id = "", actual_amount = "", admin_commision_discount = "", service_name = "", material_include = "no", user_id = "", discount = "";
    private AddonAdapter addonAdapter;
    private List<Addon> addonList = new ArrayList<>();
    private PostDetailModel postDetailModel;
    //private CommisionModel commisionModel;
    private BroadcastReceiver broadcastReceiver;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_detail);
        context = this;
        dialog = new Dialog(context);
        Utility.hideKeyboardNew(this);
        initView();
    }

    private void initView() {
        binding.etUSD.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(AppConstants.KEY);
                if (message != null && message.equals(AppConstants.NOTIFICATION)) {
                    job_id = intent.getStringExtra(AppConstants.JOB_ID);
                    user_id = intent.getStringExtra(AppConstants.USER_ID);
                    service_name = intent.getStringExtra(AppConstants.SERVICE_NAME);
                    post_details();
                }
            }
        };


        job_id = getIntent().getStringExtra(AppConstants.JOB_ID);
        user_id = getIntent().getStringExtra(AppConstants.USER_ID);
        service_name = getIntent().getStringExtra(AppConstants.SERVICE_NAME);
        setUpToolbar();
        setupRvImages();
        setupRvService();
        post_details();

        binding.rlCamaroundDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.dropDownVisibleAnimation(binding.llCamaroundDetails, binding.ivDetailDD);
            }
        });
/*
        binding.rgMaterials.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbYes:
                        material_include = "yes";
                        break;
                    case R.id.rbNo:
                        material_include = "no";
                        break;
                }
            }
        });
*/

        binding.btnSendQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   Intent intent = new Intent(context, PreviewDetailSearchActivity.class);
                //   startActivity(intent);
                validation();
            }
        });

    }

    private void validation() {
        String amount = binding.etUSD.getText().toString().trim();
        // String days = binding.etDays.getText().toString().trim();
        String days = "0";
        String comment = binding.etComment.getText().toString().trim();
        boolean correct = false;
        View focusview = null;
        if (TextUtils.isEmpty(amount)) {
            correct = true;
            binding.etUSD.setError("Amount can not be empty!");
            focusview = binding.etUSD;
        }
        if (amount.equals("0")) {
            correct = true;
            binding.etUSD.setError("Amount can not be zero!");
            focusview = binding.etUSD;
        }

       /* if (TextUtils.isEmpty(days)) {
            correct = true;
            binding.etDays.setError("Days can not be empty!");
            focusview = binding.etDays;
        }*/
        if (TextUtils.isEmpty(comment)) {
            correct = true;
            binding.etComment.setError("Escriba un comentario");
            focusview = binding.etComment;
        }

        if (!Utility.isCheckEmptyOrNull(material_include)) {
            correct = true;
            Utility.toast(context, "Please select material include!");
        }


        if (correct) {
            focusview.requestFocus();
        } else {
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat formatter = (DecimalFormat) nf;
            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            formatter.applyPattern("#,###0.00");
            if (postDetailModel.getPostBidData() != null && Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getId())) {
                //long tot = Long.parseLong(amount) + postDetailModel.getPostBidData().getAdmin_percent();

                double cCom = (Double.parseDouble(amount) * Double.parseDouble(postDetailModel.getPostBidData().getAdmin_percent())) / 100;
                double tot = Double.parseDouble(amount) + cCom;
                double td = Double.parseDouble(formatter.format(tot));
                double cd = Double.parseDouble(formatter.format(cCom));
                post_bid(td, days, comment, "edit", cd);
            } else {

                double cCom = (Double.parseDouble(amount) * Double.parseDouble(postDetailModel.getData().getComission())) / 100;
                double tot = Double.parseDouble(amount) + cCom;

                double td = Double.parseDouble(formatter.format(tot));
                double cd = Double.parseDouble(formatter.format(cCom));
                post_bid(td, days, comment, "add", cd);
            }

        }
    }

    private void setupRvService() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        binding.rvService.setLayoutManager(linearLayoutManager1);
        binding.rvService.addItemDecoration(new EqualSpacing(0, context));
        addonAdapter = new AddonAdapter(context, addonList);
        binding.rvService.setAdapter(addonAdapter);
    }

    private void setupRvImages() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvImages.setLayoutManager(linearLayoutManager1);
        binding.rvImages.addItemDecoration(new EqualSpacing(16, context));
        imagesAdapter = new ImagesAdapter(context, imagesModelsList);
        binding.rvImages.setAdapter(imagesAdapter);

    }

    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(service_name);
        binding.myToolbar.myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_white));
        binding.myToolbar.myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void post_details() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        if (RealmController.getUser() != null) {
            hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        } else {
            hashMap.put(AppConstants.UID, "");
        }
        hashMap.put(AppConstants.JOB_ID, job_id);
        hashMap.put(AppConstants.USER_ID, user_id);
        Log.e(TAG, "post_details : " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).post_details(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    postDetailModel = Controller.getGson().fromJson(JsonUtil.mainjson(response).toString(), PostDetailModel.class);
                    if (postDetailModel.getStatus() == 1) {
                        setDataFom(postDetailModel);
                    } else if (postDetailModel.getStatus() == 0) {

                    } else if (postDetailModel.getStatus() == 4) { //Invalid Access Token
                        Utility.toast(context, postDetailModel.getMessage());
                        Utility.logout(SearchDetailActivity.this);
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

    private void setDataFom(PostDetailModel postDetailModel) {
        user_id = postDetailModel.getData().getUserId();
        discount = postDetailModel.getData().getDiscount();
        if (Utility.isCheckEmptyOrNull(postDetailModel.getData().getAddress())) {
            binding.llAddress.setVisibility(View.VISIBLE);
        } else {
            binding.llAddress.setVisibility(View.GONE);
        }
        binding.tvAddress.setText(postDetailModel.getData().getAddress());
        if (Utility.isCheckEmptyOrNull(postDetailModel.getData().getServiceDetails())) {
            binding.tvServiceDetail.setText(postDetailModel.getData().getServiceDetails());
            binding.tvDetail.setVisibility(View.VISIBLE);
            binding.tvServiceDetail.setVisibility(View.VISIBLE);
        }
        addonAdapter.setAddonList(postDetailModel.getAddons());
        if (postDetailModel.getImages().size() > 0) {
            binding.tvPhoto.setVisibility(View.VISIBLE);
            binding.rvImages.setVisibility(View.VISIBLE);
            imagesAdapter.setImagesModelList(postDetailModel.getImages(), postDetailModel.getData().getUserId());
        }
        if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getJobAmount())) {
            binding.btnSendQuote.setText(context.getResources().getString(R.string.modify_quote));
            binding.etComment.setText(postDetailModel.getPostBidData().getComment());
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat formatter = (DecimalFormat) nf;
            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            formatter.applyPattern("#,###0.00");
            initializeTextWatcher("update");

            if (postDetailModel.getPostBidData() != null) {
                double tot = Double.parseDouble(postDetailModel.getPostBidData().getJobAmount()) - Double.parseDouble(postDetailModel.getPostBidData().getAdmin_commision());
                NumberFormat nf1 = NumberFormat.getNumberInstance(Locale.US);
                DecimalFormat formatter12 = (DecimalFormat) nf1;
                formatter12.setRoundingMode(RoundingMode.HALF_EVEN);
                formatter12.applyPattern("####0.00");
                binding.etUSD.setText("" + formatter12.format(tot));
            } else {
                binding.etUSD.setText("" + postDetailModel.getPostBidData().getJobAmount());
            }

            double persent = Double.parseDouble(postDetailModel.getPostBidData().getAdmin_commision());
            binding.tvCommition.setText("$" + formatter.format(persent));

            double quoteamount = Double.parseDouble(postDetailModel.getPostBidData().getJobAmount());

            binding.tvYourQuote.setText("$" + formatter.format(quoteamount));

            //binding.etDays.setText("" + postDetailModel.getPostBidData().getWorkingDays());
            //  material_include = postDetailModel.getPostBidData().getMaterialInclude();
          /*  if (Utility.isCheckEmptyOrNull(postDetailModel.getPostBidData().getMaterialInclude()) && postDetailModel.getPostBidData().getMaterialInclude().equals("yes")) {
                binding.rbYes.setChecked(true);
            } else {
                binding.rbNo.setChecked(true);
            }*/
        } else {
            // admin_charges();
            binding.tvCommition.setText(postDetailModel.getData().getComission() + "%");
            initializeTextWatcher("new");
            binding.btnSendQuote.setText(context.getResources().getString(R.string.send_quote));
        }

        if (postDetailModel.getUserDetailsModel() != null) {
            if (Utility.isCheckEmptyOrNull(postDetailModel.getUserDetailsModel().getLastName())) {
                binding.tvClientName.setText(Utility.capitalize(postDetailModel.getUserDetailsModel().getFirstName() + " " + Utility.printInitials(postDetailModel.getUserDetailsModel().getLastName())));
            } else {
                binding.tvClientName.setText(Utility.capitalize(postDetailModel.getUserDetailsModel().getFirstName()));
            }
            //binding.tvRating.setText("" + postDetailModel.getUserDetailsModel().getRating());
            binding.tvRating.setText(Utility.roundOffFloatWithDecimal(postDetailModel.getUserDetailsModel().getRating(), 1) + "");
            binding.rating.setRating(postDetailModel.getUserDetailsModel().getRating());
            binding.tvTotRatings.setText("(" + postDetailModel.getUserDetailsModel().getTotalReviews() + ")");
            Picasso.get()
                    .load(AppConstants.PROFILE_IMG_CLIENT + postDetailModel.getUserDetailsModel().getId() + "/" + postDetailModel.getUserDetailsModel().getProfileImage())
                    .placeholder(context.getResources().getDrawable(R.drawable.profile_default))
                    .into(binding.ivClientProfile);


            binding.rlClient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra(AppConstants.USER_ID, postDetailModel.getUserDetailsModel().getId());
                    startActivity(intent);

                }
            });

        }
    }

    private void post_bid(double amount, String days, String comment, String action, double commi) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formatter = (DecimalFormat) nf;
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
        formatter.applyPattern("#,###0.00");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        if (RealmController.getUser() != null) {
            hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        } else {
            hashMap.put(AppConstants.UID, "");
        }
        hashMap.put(AppConstants.JOB_ID, job_id);
        hashMap.put(AppConstants.JOB_AMOUNT, formatter.format(amount) + "");
        hashMap.put(AppConstants.WORKING_DAYS, "0");
        hashMap.put(AppConstants.MATERIAL_INCLUDE, material_include);
        hashMap.put(AppConstants.COMMENT, comment);
        hashMap.put(AppConstants.USER_ID, user_id);
        hashMap.put(AppConstants.ACTION, action);
        hashMap.put(AppConstants.ACTUAL_AMOUNT, binding.etUSD.getText().toString().trim());
        hashMap.put(AppConstants.ADMIN_COMMISION, commi + "");
        hashMap.put(AppConstants.USER_DISCOUNT, discount);
        if (action.equals("edit")) {
            hashMap.put(AppConstants.BID_ID, postDetailModel.getPostBidData().getId());
            hashMap.put(AppConstants.ADMIN_PERCENT, postDetailModel.getPostBidData().getAdmin_percent() + "");
            double dis = Double.parseDouble(postDetailModel.getData().getDiscount());
            if (dis > 0) {

                double cComDicount = ((Double.parseDouble(postDetailModel.getPostBidData().getJobAmount()) * dis) / 100);
                double adminper = 0.0;
                if (postDetailModel.getPostBidData().getAdmin_commision() != null && !postDetailModel.getPostBidData().getAdmin_commision().equals("")) {
                    adminper = Double.parseDouble(postDetailModel.getPostBidData().getAdmin_commision());
                }
                String ss = formatter.format(adminper - cComDicount);
                hashMap.put(AppConstants.ADMIN_COMMISION_DISCOUNT, ss);

            } else {
                hashMap.put(AppConstants.ADMIN_COMMISION_DISCOUNT, "0");

            }
        } else {
            hashMap.put(AppConstants.ADMIN_PERCENT, postDetailModel.getData().getComission() + "");
            double dis = Double.parseDouble(postDetailModel.getData().getDiscount());
            if (dis > 0) {
                if (!binding.tvYourQuote.getText().toString().equals("")) {
                    double cComDicount = ((Double.parseDouble(binding.tvYourQuote.getText().toString().replace("$", "")) * dis) / 100);
                    String ss = formatter.format(commi - cComDicount);
                    hashMap.put(AppConstants.ADMIN_COMMISION_DISCOUNT, ss);
                } else {
                    hashMap.put(AppConstants.ADMIN_COMMISION_DISCOUNT, "0");
                }
            } else {
                hashMap.put(AppConstants.ADMIN_COMMISION_DISCOUNT, "0");

            }
        }

        Log.e(TAG, "post_bid : " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).post_bid(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {

                        String msg = "";
                        if (action.equals("edit")) {
                            msg = "Tu cotización ha sido modificada";
                        } else {
                            msg = "Gracias. Tu cotización ha sido enviada";
                        }

                        deposit_popup(msg);

                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(SearchDetailActivity.this);

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

    private void deposit_popup(String msg) {
        dialog.setContentView(R.layout.deposit_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = dialog.findViewById(R.id.tvmsgggg);
        textView.setText(msg);

        dialog.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
                /*  Intent intent = new Intent(context, MainActivity.class);
                  intent.putExtra(AppConstants.FRAGMENTFROM, "MenuFragment");
                  startActivity(intent);
                  finishAffinity();*/

            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        } else {
            finish();
        }
    }

    private void initializeTextWatcher(String check) {
        binding.etUSD.addTextChangedListener(new TextWatcher() {
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
                    NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                    DecimalFormat formatter = (DecimalFormat) nf;
                    formatter.applyPattern("#,###0.00");
                    formatter.setRoundingMode(RoundingMode.HALF_EVEN);
                    if (Utility.isCheckEmptyOrNull(check) && check.equals("new")) {
                        if (Utility.isCheckEmptyOrNull(postDetailModel.getData().getComission())) {
                            binding.tvCommition.setText(postDetailModel.getData().getComission() + "%");
                            binding.tvYourQuote.setText("$" + formatter.format(getCommition()));
                        } else {
                            binding.tvYourQuote.setText("$" + formatter.format(getCommition()));
                        }
                    } else {
                        double pre = Double.parseDouble(postDetailModel.getPostBidData().getAdmin_percent());
                        binding.tvCommition.setText("$" + formatter.format(pre));
                        binding.tvYourQuote.setText("$" + formatter.format(getCommitionUpdate(Double.parseDouble(postDetailModel.getPostBidData().getAdmin_percent()))));
                    }
                } else {
                    NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                    DecimalFormat formatter = (DecimalFormat) nf;
                    formatter.setRoundingMode(RoundingMode.HALF_EVEN);
                    formatter.applyPattern("#,###0.00");

                    if (Utility.isCheckEmptyOrNull(check) && check.equals("new")) {
                        if (Utility.isCheckEmptyOrNull(postDetailModel.getData().getComission())) {
                            binding.tvCommition.setText(postDetailModel.getData().getComission() + "%");
                            binding.tvYourQuote.setText("$0");
                        }
                    } else {
                        double ss = Double.parseDouble(postDetailModel.getPostBidData().getAdmin_percent());
                        binding.tvCommition.setText("$" + formatter.format(ss));
                        binding.tvYourQuote.setText("$0");
                    }
                }
            }
        });
    }

    private double getCommition() {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formatter = (DecimalFormat) nf;
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
        formatter.applyPattern("#,###0.00");
        if (!binding.etUSD.getText().toString().equals("")) {
            double price = Double.parseDouble(binding.etUSD.getText().toString().trim());
            double cCom = (price * Double.parseDouble(postDetailModel.getData().getComission())) / 100;
            binding.tvCommition.setText("$" + formatter.format(cCom));
            double commisition = price + cCom;
            return commisition;
        } else {
            double price = 0.0;
            double cCom = (price * Double.parseDouble(postDetailModel.getData().getComission())) / 100;
            binding.tvCommition.setText("$" + formatter.format(cCom));
            double commisition = price + cCom;
            return commisition;
        }

    }

    private double getCommitionUpdate(double comii) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formatter = (DecimalFormat) nf;
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
        formatter.applyPattern("#,###0.00");
        if (!binding.etUSD.getText().toString().equals("")) {
            double price = Double.parseDouble(binding.etUSD.getText().toString().trim());
            double cCom = (price * comii) / 100;
            binding.tvCommition.setText("$" + formatter.format(cCom));
            double commisition = price + cCom;
            return commisition;
        } else {
            double price = 0.0;
            double cCom = (price * comii) / 100;
            binding.tvCommition.setText("$" + formatter.format(cCom));
            double commisition = price + cCom;
            return commisition;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter(AppConstants.BROADCAST));
        //  post_details();
    }
}
