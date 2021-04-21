package com.pcamarounds.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;
import com.pcamarounds.R;
import com.pcamarounds.adapters.ItemImagesAdapter;
import com.pcamarounds.constants.AppConfig;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.controller.SessionManager;
import com.pcamarounds.databinding.ActivityPersonalInformationBinding;
import com.pcamarounds.databinding.BsChooseImageBinding;
import com.pcamarounds.models.ItemImagesModel;
import com.pcamarounds.models.LoginModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.CameraGalleryComman;
import com.pcamarounds.utils.EqualSpacing;
import com.pcamarounds.utils.PersmissionUtils;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import constant.StringContract;
import id.zelory.compressor.Compressor;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import utils.Utils;

public class PersonalInformationActivity extends AppCompatActivity {
    private static final String TAG = "PersonalInformationActi";
    private Context context;
    private BroadcastReceiver broadcastReceiver;
    private ActivityPersonalInformationBinding binding;
    private RealmResults<LoginModel> loginModelRealmResults;
    private RealmChangeListener<RealmResults<LoginModel>> realmChangeListener = (LoginModel) -> {
      //  setDataProfile();
    };
    private PersmissionUtils persmissionUtils;
    private static final int GALLERY = 1;
    private static final int CAMERA = 2;
    private String[] CAMERA_PERMISSION = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public String photoFileName = "photo.jpg";
    private File file;
    private void setDataProfile() {
        if (RealmController.getUser() != null) {
            binding.etName.setText(Utility.capitalize(RealmController.getUser().getFirstName()));
            binding.etLastName.setText(Utility.capitalize(RealmController.getUser().getLastName()));
            binding.etEmail.setText(RealmController.getUser().getEmail());
            binding.etMobileNo.setText(RealmController.getUser().getMobileNo());
            binding.etAboutus.setText(RealmController.getUser().getAboutus());
            binding.etCode.setText("+" + RealmController.getUser().getCountryCode());

            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getCountryCode())) {
                binding.etCode.setText("+" + RealmController.getUser().getCountryCode());
                binding.ccp.setCountryForPhoneCode(Integer.parseInt(RealmController.getUser().getCountryCode()));
            } else {
                binding.ccp.setCountryForPhoneCode(507);
            }

            // binding.ccp.setCountryForPhoneCode(Integer.parseInt(RealmController.getUser().getCountryCode()));
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getProfileImage())) {
                Picasso.get().load(AppConstants.PROFILE_IMG + RealmController.getUser().getProfileImage()).placeholder(R.drawable.profile_default).into(binding.ivProfile);
                binding.ivProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = AppConstants.PROFILE_IMG + RealmController.getUser().getProfileImage();
                        new PhotoFullPopupWindow(context, R.layout.popup_photo_full_two, v, url, null);
                    }
                });
            }

            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getNonverifyMobile())) {
                binding.ivVerifiedMobile.setImageDrawable(getResources().getDrawable(R.drawable.ic_unverified));

            } else {
                binding.ivVerifiedMobile.setImageDrawable(getResources().getDrawable(R.drawable.ic_verified));

            }

        }
    }

    private CameraGalleryComman cameraGalleryComman;
    private File Image_Path;
    private String isClicked = "";
    private String isJobImages = "";

    private ItemImagesAdapter adapter;
    private List<ItemImagesModel> itemImagesModelsList;
    private List<String> selectedImages = new ArrayList<>();
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_personal_information);
        context = this;
        cameraGalleryComman = new CameraGalleryComman(context, this);

        persmissionUtils = new PersmissionUtils(context, PersonalInformationActivity.this);
        persmissionUtils.askPermissionAtStart();
        loginModelRealmResults = RealmController.realmControllerInIt().getRealm().where(LoginModel.class).findAllAsync();
        loginModelRealmResults.addChangeListener(realmChangeListener);
        Utility.hideKeyboardNew(this);
        initView();
    }

    private void initView() {
        get_my_profile();
        Utility.underline(binding.tvChangePassword);
        setupRvImages();
        setUpToolbar();
        initializeTextWatcher();
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
               validationTWO();
            }
        });
        binding.ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isJobImages = "false";
                cameraGalleryComman.setEnableCircleCrop(true);
                bottomSheetDialog();
            }
        });
        binding.ivVerifiedMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isCheckEmptyOrNull(RealmController.getUser().getNonverifyMobile())) {
                    resend_mobile_otp();
                } else {
                    if (!RealmController.getUser().getNonverifyMobile().equals(binding.etMobileNo.getText().toString().trim())) {
                        resend_mobile_otp();
                    }
                }
            }
        });

        binding.tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        binding.llImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                bottomSheetDialogWorkImage();
            }
        });
        binding.llAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                bottomSheetDialogWorkImage();

            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "onReceive: " + intent.getStringExtra(AppConstants.KEY));
                String message = intent.getStringExtra(AppConstants.KEY);
                if (message.equals("size")) {
                    binding.llrv.setVisibility(View.GONE);
                    binding.llImage.setVisibility(View.VISIBLE);
                }else if (message.equals("Update")) {
                  validation();
                }
            }
        };
        binding.etCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.ccp.launchCountrySelectionDialog();
            }
        });


        String isocode = binding.ccp.getDefaultCountryCodeWithPlus();
        if (isocode != null && !isocode.isEmpty()) {
            binding.etCode.setText(isocode);
        }

        binding.ccp.registerCarrierNumberEditText(binding.etMobileNo);
        binding.ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {

                if (isFirst) {
                    isFirst = false;
                    Log.e(TAG, "onValidityChanged: " + isValidNumber);
                    return;
                }
                String moible = binding.etCode.getText().toString().trim();
                if (Utility.isCheckEmptyOrNull(moible)) {
                    if (isValidNumber) {
                        binding.ivVerifiedMobile.setImageDrawable(getResources().getDrawable(R.drawable.ic_unverified));
                    } else {
                        binding.ivVerifiedMobile.setImageDrawable(getResources().getDrawable(R.drawable.ic_unverified));

                    }
                }


            }
        });

        /*Counttry dialog listnere*/
        binding.ccp.setDialogEventsListener(new CountryCodePicker.DialogEventsListener() {
            @Override
            public void onCcpDialogOpen(Dialog dialog) {

            }

            @Override
            public void onCcpDialogDismiss(DialogInterface dialogInterface) {
                Log.e(TAG, "onCcpDialogDismiss: " + binding.ccp.getSelectedCountryCodeWithPlus());
                String prefix = binding.ccp.getSelectedCountryCodeWithPlus();
                binding.etCode.setText(prefix);
            }

            @Override
            public void onCcpDialogCancel(DialogInterface dialogInterface) {

            }
        });

    }

    private void setupRvImages() {
        itemImagesModelsList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvImages.setLayoutManager(linearLayoutManager1);
        binding.rvImages.addItemDecoration(new EqualSpacing(16, context));
        adapter = new ItemImagesAdapter(context, itemImagesModelsList);
        binding.rvImages.setAdapter(adapter);

    }

    private void validation() {
        String name = binding.etName.getText().toString().trim();
        String lastname = binding.etLastName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String mobile = binding.etMobileNo.getText().toString().trim();
        String code = binding.etCode.getText().toString().trim();
        String about = binding.etAboutus.getText().toString().trim();

        boolean correct = false;
        View focusview = null;
        if (TextUtils.isEmpty(name)) {
            correct = true;
            binding.etName.setError("Name can not be empty!");
            focusview = binding.etName;
        }
        if (TextUtils.isEmpty(lastname)) {
            correct = true;
            binding.etLastName.setError("Last Name can not be empty!");
            focusview = binding.etLastName;
        }

        if (TextUtils.isEmpty(email)) {
            correct = true;
            binding.etEmail.setError("Email id can not be empty!");
            focusview = binding.etEmail;
        } else if (!Utility.isValidEmail(email)) {
            correct = true;
            binding.etEmail.setError("Invalid email!");
            focusview = binding.etEmail;
        }
        if (TextUtils.isEmpty(mobile)) {
            correct = true;
            binding.etMobileNo.setError("Introduzca un teléfono móvil");
            focusview = binding.etMobileNo;
        } else if (mobile.length() <= 7) {
            correct = true;
            binding.etMobileNo.setError("Mobile Number must be 8 characters!");
            focusview = binding.etMobileNo;
        }
        if (TextUtils.isEmpty(about)) {
            correct = true;
            binding.etAboutus.setError("Este campo es requerido!");
            focusview = binding.etAboutus;
        }
        if (correct) {
            focusview.requestFocus();
        } else {
            user_update_profile(name, lastname, email, mobile, code, about);
        }
    }
    private void validationTWO() {
        String name = binding.etName.getText().toString().trim();
        String lastname = binding.etLastName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String mobile = binding.etMobileNo.getText().toString().trim();
        String code = binding.etCode.getText().toString().trim();
        String about = binding.etAboutus.getText().toString().trim();

        boolean correct = false;
        View focusview = null;
        if (TextUtils.isEmpty(name)) {
            correct = true;
            binding.etName.setError("Name can not be empty!");
            focusview = binding.etName;
        }
        if (TextUtils.isEmpty(lastname)) {
            correct = true;
            binding.etLastName.setError("Last Name can not be empty!");
            focusview = binding.etLastName;
        }

        if (TextUtils.isEmpty(email)) {
            correct = true;
            binding.etEmail.setError("Email id can not be empty!");
            focusview = binding.etEmail;
        } else if (!Utility.isValidEmail(email)) {
            correct = true;
            binding.etEmail.setError("Invalid email!");
            focusview = binding.etEmail;
        }
        if (TextUtils.isEmpty(mobile)) {
            correct = true;
            binding.etMobileNo.setError("Introduzca un teléfono móvil");
            focusview = binding.etMobileNo;
        } else if (mobile.length() <= 7) {
            correct = true;
            binding.etMobileNo.setError("Mobile Number must be 8 characters!");
            focusview = binding.etMobileNo;
        }
        if (TextUtils.isEmpty(about)) {
            correct = true;
            binding.etAboutus.setError("Este campo es requerido!");
            focusview = binding.etAboutus;
        }
        if (correct) {
            focusview.requestFocus();
        } else {
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getNonverifyMobile())) {
                resend_mobile_otp();
            } else {
                if (Utility.isCheckEmptyOrNull(RealmController.getUser().getMobileNo()) && !RealmController.getUser().getMobileNo().equals(binding.etMobileNo.getText().toString().trim())) {
                    resend_mobile_otp();
                }else {
                    validation();
                }

            }
        }
    }

    private void user_update_profile(String name, String lastname, String email, String mobile, String code, String about) {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        builder.addFormDataPart(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        builder.addFormDataPart(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        builder.addFormDataPart(AppConstants.ACCESS_TOKEN, SessionManager.getAccessToken(context));
        builder.addFormDataPart(AppConstants.UID, RealmController.getUser().getId());
        builder.addFormDataPart(AppConstants.ACCESS, AppConstants.TECH);
        builder.addFormDataPart(AppConstants.NAME, name);
        builder.addFormDataPart(AppConstants.LAST_NAME, lastname);
        builder.addFormDataPart(AppConstants.EMAIL, email);
        builder.addFormDataPart(AppConstants.MOBILE, mobile);
        builder.addFormDataPart(AppConstants.COUNTRY_CODE, binding.ccp.getSelectedCountryCode());
        builder.addFormDataPart(AppConstants.ABOUTUS, about);
        Log.e(TAG, "user_update_profile:::::::::::::::::::::::::: " + binding.ccp.getSelectedCountryCode() );

        //  if (selectedImages != null) {
        //   Log.e(TAG, "uploadGallery:----------->>>>> size() = " + selectedImages.size());
        //   for (int i = 0; i < selectedImages.size(); i++) {
        //      File file = new File(selectedImages.get(i));
        //     Log.e(TAG, "uploadGallery:----------->>>>> " + file.getName());
        //  builder.addFormDataPart(AppConstants.IMAGES, file.getName(), RequestBody.create(MediaType.parse("image*//*"), file));
        //  }
        //  } else {
        //   builder.addFormDataPart(AppConstants.IMAGES, "");
        // }
        if (itemImagesModelsList != null && itemImagesModelsList.size() > 0) {
            File file = null;
            //String name122 = "";
            for (int i = 0; i < itemImagesModelsList.size(); i++) {
                if (Utility.isCheckEmptyOrNull(itemImagesModelsList.get(i).getmType())) {
                    Log.e(TAG, "uploadGallery:----------->>>>> size() = " + itemImagesModelsList.size());
                    file = new File(itemImagesModelsList.get(i).getImage());
                   // name122 = itemImagesModelsList.get(i).getmFileName();

                    Log.e(TAG, "uploadGallery image name :----------->>>>> " + file.getName());
                    Log.e(TAG, "uploadGallery:----------->>>>> " + file);
                    builder.addFormDataPart(AppConstants.IMAGES, file.getName(), RequestBody.create(MediaType.parse("image*//*"), file));
                }
            }
        } else {
            builder.addFormDataPart(AppConstants.IMAGES, "");
        }

        final MultipartBody requestBody = builder.build();
        Log.e(TAG, "user_update_profile: " + requestBody);
        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).user_update_profile(Utility.getHeaderAuthentication(context), requestBody), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        Utility.toast(context, "Perfil Actualizado");
                        LoginModel loginModel = Controller.getGson().fromJson(jsonObject.getJSONObject("data").toString(), LoginModel.class);
                        // RealmController.setUser(loginModel);
                      /*  RealmController.realmControllerInIt().getRealm().beginTransaction();
                        RealmController.realmControllerInIt().getRealm().copyToRealmOrUpdate(loginModel);
                        RealmController.realmControllerInIt().getRealm().commitTransaction();*/

                        RealmController.copyToRealmOrUpdate(loginModel);

                        if (Utility.isCheckEmptyOrNull(loginModel.getProfileImage())) {
                            String url = AppConstants.PROFILE_IMG + loginModel.getProfileImage();
                            updateCometChat(loginModel.getFirstName() + " " + Utility.printInitials(loginModel.getLastName()), url);
                        } else {
                            updateCometChat(loginModel.getFirstName() + " " + Utility.printInitials(loginModel.getLastName()), "http://34.223.228.22/52a363e725235501a0fc865f1f628006.jpg");

                        }
                        itemImagesModelsList = Controller.getGson().fromJson(jsonObject.getJSONArray("images").toString(),
                                new TypeToken<ArrayList<ItemImagesModel>>() {
                                }.getType());
                        adapter.setItemImagesModelList(itemImagesModelsList, "update");
                        adapter.notifyDataSetChanged();
                        selectedImages.clear();
                        if (itemImagesModelsList.size() > 0) {
                            binding.llImage.setVisibility(View.GONE);
                            binding.llrv.setVisibility(View.VISIBLE);
                        } else {
                            binding.llrv.setVisibility(View.GONE);
                            binding.llImage.setVisibility(View.VISIBLE);
                        }
                        if (Utility.isCheckEmptyOrNull(RealmController.getUser().getNonverifyMobile())) {
                            Log.e(TAG, "onResponseSuccess: ifff"+ RealmController.getUser().getNonverifyMobile() );
                            binding.ivVerifiedMobile.setImageDrawable(getResources().getDrawable(R.drawable.ic_unverified));
                        } else {
                            Log.e(TAG, "onResponseSuccess: elsee"+ RealmController.getUser().getNonverifyMobile() );
                            binding.ivVerifiedMobile.setImageDrawable(getResources().getDrawable(R.drawable.ic_verified));

                        }
                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(PersonalInformationActivity.this);

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

    private void updateCometChat(String name, String url) {
        User user = new User();
        user.setUid(SessionManager.getCometId(context));
        user.setName(name);
        user.setAvatar(url);
        CometChat.updateUser(user, AppConfig.AppDetails.API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.e(TAG, "onSuccess" + user.toString());
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError" + e.getMessage());
            }
        });
    }

    private void get_my_profile() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        Log.e(TAG, "get_my_profile: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).get_my_profile(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {


                        setDataProfile();
                        itemImagesModelsList = Controller.getGson().fromJson(jsonObject.getJSONArray("images").toString(),
                                new TypeToken<ArrayList<ItemImagesModel>>() {
                                }.getType());
                        adapter.setItemImagesModelList(itemImagesModelsList, "update");
                        adapter.notifyDataSetChanged();

                        if (itemImagesModelsList.size() > 0) {
                            binding.llImage.setVisibility(View.GONE);
                            binding.llrv.setVisibility(View.VISIBLE);
                        } else {
                            binding.llrv.setVisibility(View.GONE);
                            binding.llImage.setVisibility(View.VISIBLE);
                        }

                    } else if (jsonObject.getInt("status") == 0) {
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(PersonalInformationActivity.this);
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


    /******************************************************Update Profile***********************************************************/


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void bottomSheetDialogWorkImage() {
        //View dialogView = getLayoutInflater().inflate(R.layout.bts_payment, null);
        final BsChooseImageBinding bsBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.bs_choose_image, (ViewGroup) binding.getRoot(), false);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        bsBinding.llCam.setOnClickListener(v -> {
            isJobImages = "true1";
            dialog.dismiss();
            if (Utils.hasPermissions(context, CAMERA_PERMISSION)) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA);
             /*   Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = getPhotoFileUri(photoFileName);
                Uri fileProvider = FileProvider.getUriForFile(context, context.getResources().getString(R.string.file_provider_authorities), file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, StringContract.RequestCode.CAMERA);
                }*/
            } else {
                requestPermissions(CAMERA_PERMISSION, StringContract.RequestCode.CAMERA);
            }

        });
        bsBinding.llGallery.setOnClickListener(v -> {
            isJobImages = "true1";
            dialog.dismiss();
            if (Utils.hasPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, StringContract.RequestCode.GALLERY);
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(bsBinding.getRoot());
        dialog.show();
    }


    private void bottomSheetDialog() {
        //View dialogView = getLayoutInflater().inflate(R.layout.bts_payment, null);
        final BsChooseImageBinding bsBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.bs_choose_image, (ViewGroup) binding.getRoot(), false);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        bsBinding.llCam.setOnClickListener(v -> {
            isClicked = "true";
            cameraGalleryComman.onLaunchCamera();
            dialog.dismiss();
        });
        bsBinding.llGallery.setOnClickListener(v -> {
            isClicked = "true";
            cameraGalleryComman.pickFromGallery();
            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(bsBinding.getRoot());
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cameraGalleryComman.onActivityResult(requestCode, resultCode, data);
        if (isJobImages != null && isJobImages.equals("true1")) {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == GALLERY) {
                    if (data.getData() != null) {
                        // Log.e(TAG, "onActivityResult: iff ");
                        try {
                            Uri uri = data.getData();
                            File url = com.pcamarounds.utils.MediaUtils.getRealPath(context, uri);
                            if ((url.length() / 1024) < 1024) {
                                Image_Path = url;
                            } else if ((url.length() / 1024) > 1024 && (url.length() / 1024) < 2048) {
                                Image_Path = new Compressor(context).setQuality(50).compressToFile(url);
                            } else if ((url.length() / 1024) > 3072) {
                                Image_Path = new Compressor(context).setQuality(75).compressToFile(url);
                            }
                            selectedImages.add(Image_Path.getAbsolutePath());
                           // selectedImages.add(url.getAbsolutePath());
                            if (itemImagesModelsList.size() > 0) {
                                binding.llImage.setVisibility(View.GONE);
                                binding.llrv.setVisibility(View.VISIBLE);
                            } else {
                                binding.llImage.setVisibility(View.GONE);
                                binding.llrv.setVisibility(View.VISIBLE);
                            }

                            ArrayList<ItemImagesModel> imagesModelsList = new ArrayList<>();
                            for (int i = 0; i < selectedImages.size(); i++) {
                                // Log.e(TAG, "onActivityResult: " + i);
                                ItemImagesModel itemImagesModel = new ItemImagesModel();
                                itemImagesModel.setImage(selectedImages.get(i));
                                itemImagesModel.setmType("local");
                                imagesModelsList.add(itemImagesModel);
                            }
                            selectedImages.clear();
                            itemImagesModelsList.addAll(imagesModelsList);
                            Collections.reverse(itemImagesModelsList);
                            adapter.setItemImagesModelList(itemImagesModelsList, "update");
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Log.e(TAG, "onActivityResult: elseee ");
                        //If uploaded with the new Android Photos gallery
                        try {
                            ClipData clipData = data.getClipData();
                            if (clipData != null) {
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    ClipData.Item item = clipData.getItemAt(i);
                                    Uri uri = item.getUri();
                                    File url = com.pcamarounds.utils.MediaUtils.getRealPath(context, uri);
                                    if ((url.length() / 1024) < 1024) {
                                        Image_Path = url;
                                    } else if ((url.length() / 1024) > 1024 && (url.length() / 1024) < 2048) {
                                        Image_Path = new Compressor(context).setQuality(50).compressToFile(url);
                                    } else if ((url.length() / 1024) > 3072) {
                                        Image_Path = new Compressor(context).setQuality(75).compressToFile(url);
                                    }
                                    selectedImages.add(Image_Path.getAbsolutePath());
                                   // selectedImages.add(url.getAbsolutePath());

                                }
                                if (itemImagesModelsList.size() > 0) {
                                    binding.llImage.setVisibility(View.GONE);
                                    binding.llrv.setVisibility(View.VISIBLE);
                                } else {
                                    binding.llImage.setVisibility(View.GONE);
                                    binding.llrv.setVisibility(View.VISIBLE);
                                }
                                ArrayList<ItemImagesModel> imagesModelsList = new ArrayList<>();
                                for (int i = 0; i < selectedImages.size(); i++) {
                                    // Log.e(TAG, "onActivityResult: " + i);
                                    ItemImagesModel itemImagesModel = new ItemImagesModel();
                                    itemImagesModel.setImage(selectedImages.get(i));
                                    itemImagesModel.setmType("local");
                                    imagesModelsList.add(itemImagesModel);
                                }
                                selectedImages.clear();
                                itemImagesModelsList.addAll(imagesModelsList);
                                Collections.reverse(itemImagesModelsList);
                                adapter.setItemImagesModelList(itemImagesModelsList, "update");
                                adapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (requestCode == CAMERA) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    file = Utility.bitmapToFile(context,bitmap,photoFileName);
                    if (file.exists()) {
                        try {
                            Image_Path = null;
                            if ((file.length() / 1024) < 1024) {
                                Image_Path = file;
                            } else if ((file.length() / 1024) > 1024 && (file.length() / 1024) < 2048) {
                                Image_Path = new Compressor(context).setQuality(50).compressToFile(file);
                            } else if ((file.length() / 1024) > 3072) {
                                Image_Path = new Compressor(context).setQuality(75).compressToFile(file);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (Image_Path != null) {
                            if (itemImagesModelsList.size() > 0) {
                                binding.llImage.setVisibility(View.GONE);
                                binding.llrv.setVisibility(View.VISIBLE);
                            } else {
                                binding.llImage.setVisibility(View.GONE);
                                binding.llrv.setVisibility(View.VISIBLE);
                            }
                        }
                        selectedImages.add(Image_Path.getAbsolutePath());
                        Image_Path = null;
                        ArrayList<ItemImagesModel> imagesModelsList = new ArrayList<>();
                        for (int i = 0; i < selectedImages.size(); i++) {
                            // Log.e(TAG, "onActivityResult: " + i);
                            ItemImagesModel itemImagesModel = new ItemImagesModel();
                            itemImagesModel.setImage(selectedImages.get(i));
                            itemImagesModel.setmType("local");
                            imagesModelsList.add(itemImagesModel);
                        }
                        selectedImages.clear();
                        itemImagesModelsList.addAll(imagesModelsList);
                        Collections.reverse(itemImagesModelsList);
                        adapter.setItemImagesModelList(itemImagesModelsList, "update");
                        adapter.notifyDataSetChanged();
                    } else {
                        // Log.e(TAG, "onActivityResult: " + com.cometchat.pro.uikit.R.string.file_not_exist);
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter(AppConstants.BROADCAST));
        if (isClicked != null && !isClicked.equals("")) {
            if (cameraGalleryComman.outputImageFile() != null) {
                Log.e(TAG, "onResume: " + cameraGalleryComman.outputImageFile());
                try {
                    Image_Path = null;
                    if ((cameraGalleryComman.outputImageFile().length() / 1024) < 1024) {
                        Image_Path = cameraGalleryComman.outputImageFile();
                    } else if ((cameraGalleryComman.outputImageFile().length() / 1024) > 1024 && (cameraGalleryComman.outputImageFile().length() / 1024) < 2048) {
                        Image_Path = new Compressor(context).setQuality(50).compressToFile(cameraGalleryComman.outputImageFile());
                    } else if ((cameraGalleryComman.outputImageFile().length() / 1024) > 3072) {
                        Image_Path = new Compressor(context).setQuality(75).compressToFile(cameraGalleryComman.outputImageFile());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Image_Path != null) {
                    cameraGalleryComman.removeImage();
                    Picasso.get().load(Image_Path).into(binding.ivProfile);
                    user_profilepic();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult: ");
        cameraGalleryComman.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    file = getPhotoFileUri(photoFileName);
                    Uri fileProvider = FileProvider.getUriForFile(context, context.getResources().getString(R.string.file_provider_authorities), file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, StringContract.RequestCode.CAMERA);
                    }
                }
                else
                    // showSnackBar(view.findViewById(com.cometchat.pro.uikit.R.id.message_box), getResources().getString(com.cometchat.pro.uikit.R.string.grant_camera_permission));
                    break;
            case GALLERY:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
                } else
                    // showSnackBar(view.findViewById(com.cometchat.pro.uikit.R.id.message_box), getResources().getString(com.cometchat.pro.uikit.R.string.grant_storage_permission));
                    break;
        }
    }
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.e(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }

    private void user_profilepic() {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        builder.addFormDataPart(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        builder.addFormDataPart(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        builder.addFormDataPart(AppConstants.ACCESS_TOKEN, SessionManager.getAccessToken(context));
        builder.addFormDataPart(AppConstants.ACCESS, AppConstants.TECH);
        builder.addFormDataPart(AppConstants.UID, RealmController.getUser().getId());

        if (Image_Path != null) {
            builder.addFormDataPart(AppConstants.IMAGE, Image_Path.getName(), RequestBody.create(MediaType.parse("image*//*"), Image_Path));
        }
        final MultipartBody requestBody = builder.build();

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).user_profilepic(Utility.getHeaderAuthentication(context), requestBody), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        //  Utility.toast(context, jsonObject.getString("message"));
                        LoginModel loginModel = Controller.getGson().fromJson(jsonObject.getJSONObject("data").toString(), LoginModel.class);
                     /*   RealmController.realmControllerInIt().getRealm().beginTransaction();
                        RealmController.realmControllerInIt().getRealm().copyToRealmOrUpdate(loginModel);
                        RealmController.realmControllerInIt().getRealm().commitTransaction();*/

                        RealmController.copyToRealmOrUpdate(loginModel);
                        if (Utility.isCheckEmptyOrNull(loginModel.getProfileImage())) {
                            String url = AppConstants.PROFILE_IMG + loginModel.getProfileImage();
                            updateCometChat(loginModel.getFirstName() + " " + Utility.printInitials(loginModel.getLastName()), url);
                        }

                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(PersonalInformationActivity.this);

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
        binding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString();
                if (email.length() > 0) {
                    binding.ivName.setColorFilter(context.getResources().getColor(R.color.colorDarkBlack));
                    binding.ivVerifiedName.setImageDrawable(getResources().getDrawable(R.drawable.ic_verified));
                } else {
                    binding.ivName.clearColorFilter();
                    binding.ivVerifiedName.setImageDrawable(getResources().getDrawable(R.drawable.ic_unverified));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString();
                if (email.length() > 0) {
                    binding.ivLastName.setColorFilter(context.getResources().getColor(R.color.colorDarkBlack));
                    binding.ivVerifiedLastName.setImageDrawable(getResources().getDrawable(R.drawable.ic_verified));
                } else {
                    binding.ivLastName.clearColorFilter();
                    binding.ivVerifiedLastName.setImageDrawable(getResources().getDrawable(R.drawable.ic_unverified));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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


/*
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
                    if (pass.length() > 7) {
                        binding.ivPassword.setColorFilter(getResources().getColor(R.color.colorDarkBlack));
                    } else {
                        binding.ivPassword.clearColorFilter();
                    }
                } else {
                    binding.ivPassword.clearColorFilter();
                }
            }
        });
*/
        binding.etMobileNo.addTextChangedListener(new TextWatcher() {
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
                    if (pass.length() > 7) {
                        binding.ivMobileNo.setColorFilter(getResources().getColor(R.color.colorDarkBlack));
                        binding.ivVerifiedMobile.setImageDrawable(getResources().getDrawable(R.drawable.ic_unverified));
                    } else {
                        binding.ivMobileNo.clearColorFilter();
                        binding.ivVerifiedMobile.setImageDrawable(getResources().getDrawable(R.drawable.ic_unverified));
                    }
                } else {
                    binding.ivMobileNo.clearColorFilter();
                    binding.ivVerifiedMobile.setImageDrawable(getResources().getDrawable(R.drawable.ic_unverified));
                }
            }
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(context.getResources().getString(R.string.personal_information));
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

    private void resend_mobile_otp() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS_TOKEN, SessionManager.getAccessToken(context));
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.MOBILE, binding.etMobileNo.getText().toString().trim().replace(" ", ""));
        hashMap.put(AppConstants.COUNTRY_CODE, binding.ccp.getSelectedCountryCode());
        Log.e(TAG, "resend_mobile_otp: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).resend_mobile_otp(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {

                        Intent intent = new Intent(context, VerifyMobileActivity.class);
                        intent.putExtra(AppConstants.CODE, binding.ccp.getSelectedCountryCode());
                        intent.putExtra(AppConstants.MOBILE, binding.etMobileNo.getText().toString().trim().replace(" ", ""));
                        intent.putExtra(AppConstants.EMAIL, RealmController.getUser().getEmail());
                        startActivity(intent);

                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(PersonalInformationActivity.this);

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

}
