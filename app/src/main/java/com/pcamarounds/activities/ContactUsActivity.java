package com.pcamarounds.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.reflect.TypeToken;
import com.pcamarounds.R;
import com.pcamarounds.adapters.HelpTopicAdapter;
import com.pcamarounds.adapters.ItemImagesAdapter;
import com.pcamarounds.adapters.LastOrderAdapter;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.controller.SessionManager;
import com.pcamarounds.databinding.ActivityContactUsBinding;
import com.pcamarounds.databinding.BsChooseImageBinding;
import com.pcamarounds.models.ItemImagesModel;
import com.pcamarounds.models.helpsupport.HelpTopic;
import com.pcamarounds.models.helpsupport.LastOrderModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.EqualSpacing;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import constant.StringContract;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import utils.Utils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.facebook.appevents.AppEventsLogger;


public class ContactUsActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private AppEventsLogger logger;

    private static final String TAG = "ContactUsActivity";
    private Context context;
    private ActivityContactUsBinding binding;
    private List<LastOrderModel> lastOrderModels = new ArrayList<>();
    private List<HelpTopic> helpTopics = new ArrayList<>();
    private Dialog dialog;
    private String booking_id = "", enquiry_id = "";
    private ItemImagesAdapter adapter;
    private List<ItemImagesModel> itemImagesModelsList;
    private List<String> selectedImages = new ArrayList<>();
    private BroadcastReceiver broadcastReceiver;
    private File Image_Path;
    private String isClicked = "";
    private static final int GALLERY = 1;
    private static final int CAMERA = 2;
    private String[] CAMERA_PERMISSION = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public String photoFileName = "photo.jpg";
    private File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        logger = AppEventsLogger.newLogger(this);
        
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_us);
        context = this;
        Utility.hideKeyboardNew(this);
        initView();
    }

    private void initView() {
        setUpToolbar();
        setupRvImages();
        help_booking_list();

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isCheckEmptyOrNull(enquiry_id)) {
                    help_support_input();
                 /*   if (binding.etConsultation.getText().toString().equals("Otro")) {
                       *//* if (itemImagesModelsList.size() > 0) {
                            help_support_input();
                        } else {
                            Utility.toast(context, "Please select images");
                        }*//*
                        help_support_input();
                    } else {
                        if (Utility.isCheckEmptyOrNull(booking_id)) {
                            help_support_input();
                           *//* if (itemImagesModelsList.size() > 0) {
                                help_support_input();
                            } else {
                                Utility.toast(context, "Please select images");
                            }*//*
                        } else {
                            Utility.toast(context, "Seleccione un Camarounda");
                        }
                    }*/
                } else {
                    Utility.toast(context, "Please select Tipo de Consulta");
                }

            }
        });

        binding.rlEnqury.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enquiry_popup();
            }
        });
        binding.rlLastOrdr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastorder_popup();
            }
        });
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(AppConstants.KEY);
                if (message.equals("size")) {
                    binding.llrv.setVisibility(View.GONE);
                    binding.llPhoto.setVisibility(View.VISIBLE);
                }
            }
        };

        binding.llPhoto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                bottomSheetDialog();


            }
        });
        binding.llAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                bottomSheetDialog();

            }
        });

    }

    private void setupRvImages() {
        itemImagesModelsList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvImage.setLayoutManager(linearLayoutManager1);
        binding.rvImage.addItemDecoration(new EqualSpacing(16, context));
        adapter = new ItemImagesAdapter(context, itemImagesModelsList);
        binding.rvImage.setAdapter(adapter);

    }

    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(context.getResources().getString(R.string.help_center));
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
        super.onBackPressed();
        finish();
    }


    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }

    private void help_booking_list() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        Log.e(TAG, "help_booking_list: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).help_booking_list(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {

                        lastOrderModels.clear();
                        lastOrderModels = Controller.getGson().fromJson(jsonObject.getJSONArray("last_order").toString(),
                                new TypeToken<ArrayList<LastOrderModel>>() {
                                }.getType());

                        helpTopics.clear();
                        helpTopics = Controller.getGson().fromJson(jsonObject.getJSONArray("helptopics").toString(),
                                new TypeToken<ArrayList<HelpTopic>>() {
                                }.getType());

                    } else if (jsonObject.getInt("status") == 0) {
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(ContactUsActivity.this);
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

    private void lastorder_popup() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.lastorder_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewLast);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager1);
        LastOrderAdapter lastOrderAdapter = new LastOrderAdapter(context, lastOrderModels);
        recyclerView.setAdapter(lastOrderAdapter);
        lastOrderAdapter.setLastOrderModelList(lastOrderModels);
        lastOrderAdapter.notifyDataSetChanged();


        dialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btnSaveLast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastOrderAdapter.getCurrentPos() >= 0) {
                    dialog.dismiss();
                    booking_id = lastOrderModels.get(lastOrderAdapter.getCurrentPos()).getId();
                    binding.etReference.setText(Utility.capitalize(lastOrderModels.get(lastOrderAdapter.getCurrentPos()).getPostNumber()));
                } else {
                    Utility.toast(context, "Seleccione un Camaround");
                }
            }
        });


        dialog.show();
    }

    private void enquiry_popup() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.enquiry_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewEnquiry);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager1);
        HelpTopicAdapter helpTopicAdapter = new HelpTopicAdapter(context, helpTopics);
        recyclerView.setAdapter(helpTopicAdapter);
        helpTopicAdapter.setHelpTopicList(helpTopics);
        helpTopicAdapter.notifyDataSetChanged();


        dialog.findViewById(R.id.ivCloseEnquiry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnSaveEnquiry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helpTopicAdapter.getCurrentPos() >= 0) {
                    dialog.dismiss();
                    enquiry_id = helpTopics.get(helpTopicAdapter.getCurrentPos()).getId();
                    binding.etConsultation.setText(Utility.capitalize(helpTopics.get(helpTopicAdapter.getCurrentPos()).getType()));
                } else {
                    Utility.toast(context, "Please select Tipo de Consulta");
                }
            }
        });

        dialog.show();
    }

    private void help_support_input() {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        builder.addFormDataPart(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        builder.addFormDataPart(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        builder.addFormDataPart(AppConstants.ACCESS_TOKEN, SessionManager.getAccessToken(context));
        builder.addFormDataPart(AppConstants.UID, RealmController.getUser().getId());
        builder.addFormDataPart(AppConstants.ACCESS, AppConstants.TECH);
        builder.addFormDataPart(AppConstants.DETAILS, binding.etDescribe.getText().toString().trim());
        builder.addFormDataPart(AppConstants.BOOKING_ID, booking_id);
        builder.addFormDataPart(AppConstants.ENQUIRY_TYPE, enquiry_id);

        if (itemImagesModelsList != null && itemImagesModelsList.size() > 0) {
            Log.e(TAG, "uploadGallery:----------->>>>> size() = " + itemImagesModelsList.size());
            for (int i = 0; i < itemImagesModelsList.size(); i++) {
                File file = new File(itemImagesModelsList.get(i).getImage());
                Log.e(TAG, "uploadGallery:----------->>>>> " + file.getName());
                builder.addFormDataPart(AppConstants.IMAGES, file.getName(), RequestBody.create(MediaType.parse("image*//*"), file));
            }
        } else {
            builder.addFormDataPart(AppConstants.IMAGES, "");
        }
        final MultipartBody requestBody = builder.build();
        Log.e(TAG, "help_support_input: " + requestBody);
        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).help_support_input(Utility.getHeaderAuthentication(context), requestBody), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    recordSupportContactUsage();
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {

                        Intent intent = new Intent(context, HelpCenterSuccessActivity.class);
                        startActivity(intent);
                        finish();

                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(ContactUsActivity.this);

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
    private void recordSupportContactUsage() {
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("support_contact_usage", bundle);
        logger.logEvent("support_contact_usage");

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void bottomSheetDialog() {
        //View dialogView = getLayoutInflater().inflate(R.layout.bts_payment, null);
        final BsChooseImageBinding bsBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.bs_choose_image, (ViewGroup) binding.getRoot(), false);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        bsBinding.llCam.setOnClickListener(v -> {
            isClicked = "true1";
            dialog.dismiss();
            if (Utils.hasPermissions(context, CAMERA_PERMISSION)) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA);
               /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
            isClicked = "true1";
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isClicked != null && isClicked.equals("true1")) {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == GALLERY) {
                    if (data.getData() != null) {
                        // Log.e(TAG, "onActivityResult: iff ");
                        try {
                            Uri uri = data.getData();
                           File url = com.pcamarounds.utils.MediaUtils.getRealPath(context, uri);
                            //File url = Utility.copyFileToDownloads(context, uri);
                            if ((url.length() / 1024) < 1024) {
                                Image_Path = url;
                            } else if ((url.length() / 1024) > 1024 && (url.length() / 1024) < 2048) {
                                Image_Path = new Compressor(context).setQuality(50).compressToFile(url);
                            } else if ((url.length() / 1024) > 3072) {
                                Image_Path = new Compressor(context).setQuality(75).compressToFile(url);
                            }
                            selectedImages.add(Image_Path.getAbsolutePath());
                            //selectedImages.add(url.getAbsolutePath());
                            binding.llrv.setVisibility(View.VISIBLE);
                            binding.llPhoto.setVisibility(View.GONE);
                            ArrayList<ItemImagesModel> imagesModelsList = new ArrayList<>();
                            for (int i = 0; i < selectedImages.size(); i++) {
                                 Log.e(TAG, "onActivityResult: " + selectedImages.get(i));
                                ItemImagesModel itemImagesModel = new ItemImagesModel();
                                itemImagesModel.setImage(selectedImages.get(i));
                                itemImagesModel.setmType("local");
                                imagesModelsList.add(itemImagesModel);
                            }
                            selectedImages.clear();
                            itemImagesModelsList.addAll(imagesModelsList);
                            Collections.reverse(itemImagesModelsList);
                            adapter.setItemImagesModelList(itemImagesModelsList, "");
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                         Log.e(TAG, "onActivityResult: elseee ");
                        //If uploaded with the new Android Photos gallery
                        try {
                            ClipData clipData = data.getClipData();
                            if (clipData != null) {
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    ClipData.Item item = clipData.getItemAt(i);
                                    Uri uri = item.getUri();
                                    File url = com.pcamarounds.utils.MediaUtils.getRealPath(context, uri);
                                   // File url = Utility.copyFileToDownloads(context, uri);
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
                                binding.llrv.setVisibility(View.VISIBLE);
                                binding.llPhoto.setVisibility(View.GONE);
                                ArrayList<ItemImagesModel> imagesModelsList = new ArrayList<>();
                                for (int i = 0; i < selectedImages.size(); i++) {
                                     Log.e(TAG, "onActivityResult: " + selectedImages.get(i));
                                    ItemImagesModel itemImagesModel = new ItemImagesModel();
                                    itemImagesModel.setImage(selectedImages.get(i));
                                    itemImagesModel.setmType("local");
                                    imagesModelsList.add(itemImagesModel);
                                }
                                selectedImages.clear();
                                itemImagesModelsList.addAll(imagesModelsList);
                                Collections.reverse(itemImagesModelsList);
                                adapter.setItemImagesModelList(itemImagesModelsList, "");
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
                        selectedImages.add(Image_Path.getAbsolutePath());
                        Image_Path = null;
                        binding.llrv.setVisibility(View.VISIBLE);
                        binding.llPhoto.setVisibility(View.GONE);
                        ArrayList<ItemImagesModel> imagesModelsList = new ArrayList<>();
                        for (int i = 0; i < selectedImages.size(); i++) {
                            Log.e(TAG, "onActivityResult: " + selectedImages.get(i));
                            ItemImagesModel itemImagesModel = new ItemImagesModel();
                            itemImagesModel.setImage(selectedImages.get(i));
                            itemImagesModel.setmType("local");
                            imagesModelsList.add(itemImagesModel);
                        }
                        selectedImages.clear();
                        itemImagesModelsList.addAll(imagesModelsList);
                        Collections.reverse(itemImagesModelsList);
                        adapter.setItemImagesModelList(itemImagesModelsList, "");
                        adapter.notifyDataSetChanged();
                    } else {
                        // Log.e(TAG, "onActivityResult: " + com.cometchat.pro.uikit.R.string.file_not_exist);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult: ");
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

}
