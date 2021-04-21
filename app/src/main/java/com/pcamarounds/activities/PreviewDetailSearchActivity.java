package com.pcamarounds.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.pcamarounds.R;
import com.pcamarounds.adapters.AddonAdapter;
import com.pcamarounds.adapters.ImagesAdapter;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.ActivityEditDetailsCamaroundBinding;
import com.pcamarounds.models.postdetail.Addon;
import com.pcamarounds.models.postdetail.ImagesModel;
import com.pcamarounds.models.postdetail.PostDetailModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.EqualSpacing;
import com.pcamarounds.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class PreviewDetailSearchActivity extends AppCompatActivity {
    private static final String TAG = "EditDetailsCamaroundAct";
    private Context context;
    private ActivityEditDetailsCamaroundBinding binding;
    private ImagesAdapter imagesAdapter;
    private List<ImagesModel> imagesModelsList = new ArrayList<>();
    private String job_id = "", service_name = "";
    private AddonAdapter addonAdapter;
    private List<Addon> addonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_details_camaround);
        context = this;
        Utility.hideKeyboardNew(this);
        initView();
    }

    private void initView() {
        job_id = getIntent().getStringExtra(AppConstants.JOB_ID);
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
        binding.btnModifyQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

    private void post_details() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        hashMap.put(AppConstants.JOB_ID, job_id);


        Log.e(TAG, "post_details : " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).post_details(Utility.getHeaderAuthentication(context),hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    PostDetailModel postDetailModel = Controller.getGson().fromJson(JsonUtil.mainjson(response).toString(), PostDetailModel.class);
                    if (postDetailModel.getStatus() == 1) {
                        setDataFom(postDetailModel);
                    } else if (postDetailModel.getStatus() == 0) {

                    } else if (postDetailModel.getStatus() == 4) { //Invalid Access Token
                        Utility.toast(context, postDetailModel.getMessage());
                        Utility.logout(PreviewDetailSearchActivity.this);
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
        binding.tvAddress.setText(Utility.capitalize(postDetailModel.getData().getAddress()));
        if (Utility.isCheckEmptyOrNull(postDetailModel.getData().getServiceDetails())) {
            binding.tvServiceDetail.setText(Utility.capitalize(postDetailModel.getData().getServiceDetails()));
            binding.tvDetall.setVisibility(View.VISIBLE);
            binding.tvServiceDetail.setVisibility(View.VISIBLE);
        }
        addonAdapter.setAddonList(postDetailModel.getAddons());
        if (postDetailModel.getImages().size() > 0) {
            binding.tvPhoto.setVisibility(View.VISIBLE);
            binding.rvImages.setVisibility(View.VISIBLE);
            imagesAdapter.setImagesModelList(postDetailModel.getImages(),postDetailModel.getData().getUserId());
        }
      /*  if (postDetailModel.getPostBidData().size() > 0) {
            binding.llNoLising.setVisibility(View.GONE);
            binding.tvDiscountLbl.setVisibility(View.VISIBLE);
            binding.rvQuotes.setVisibility(View.VISIBLE);
        } else {
            binding.tvDiscountLbl.setVisibility(View.GONE);
            binding.rvQuotes.setVisibility(View.GONE);
            binding.llNoLising.setVisibility(View.VISIBLE);
        }*/

    }

    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(Utility.capitalize(service_name));
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
