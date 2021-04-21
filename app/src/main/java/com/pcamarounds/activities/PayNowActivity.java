package com.pcamarounds.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pcamarounds.R;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.controller.SessionManager;
import com.pcamarounds.databinding.ActivityPayNowBinding;
import com.pcamarounds.databinding.BsChooseImageBinding;
import com.pcamarounds.models.BankInfoModel;
import com.pcamarounds.models.ItemImagesModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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

public class PayNowActivity extends AppCompatActivity {
    private static final String TAG = "PayNowActivity";
    private Context context;
    private ActivityPayNowBinding binding;
    private String paymentType = "card";
    private String previousNumber;
    private List<ItemImagesModel> itemImagesModelsList = new ArrayList<>();
    private List<String> selectedImages = new ArrayList<>();
    private File Image_Path;
    private String isClicked = "";
    private static final int GALLERY = 1;
    private static final int CAMERA = 2;
    private String[] CAMERA_PERMISSION = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public String photoFileName = "photo.jpg";
    private File file;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay_now);
        context = this;
        dialog = new Dialog(context);
        Utility.hideKeyboardNew(this);
        initView();
    }

    private void initView() {
        setUpToolbar();
        get_admin_bankinfo();
        if (Utility.isCheckEmptyOrNull(RealmController.getUser().getWalletAmount()) && !RealmController.getUser().getWalletAmount().equals("0")) {
            double amount = Double.parseDouble(RealmController.getUser().getWalletAmount());
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat formatter = (DecimalFormat) nf;
            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            formatter.applyPattern("#,###0.00");
            binding.etAmmount.setText(formatter.format(amount).replace("-", ""));
            binding.tvPrice.setText("$" + formatter.format(amount).replace("-", ""));
        }
        initializeTextWatcher();
        binding.rgPayMethod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbCardPay:
                        paymentType = "card";
                        Log.e(TAG, "onCheckedChanged: " + paymentType);
                        binding.rlBankTransfer.setVisibility(View.GONE);
                        binding.tvmsg.setVisibility(View.GONE);
                        binding.tv1.setVisibility(View.GONE);
                        binding.tvAccountInfo.setVisibility(View.GONE);
                        binding.rlCardPayment.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rbBankTranfer:
                        paymentType = "bank";
                        Log.e(TAG, "onCheckedChanged: " + paymentType);
                        binding.rlCardPayment.setVisibility(View.GONE);
                        binding.rlBankTransfer.setVisibility(View.VISIBLE);
                        binding.tvmsg.setVisibility(View.VISIBLE);
                        binding.tv1.setVisibility(View.GONE);
                        binding.tvAccountInfo.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        binding.btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: " + paymentType);
                if (paymentType != null && !paymentType.equals("")) {
                    if (paymentType.equals("card")) {
                        if (!binding.etAmmount.getText().toString().equals("")) {
                            Intent intent = new Intent(context, CardPaymentActivity.class);
                            intent.putExtra(AppConstants.AMOUNT, binding.etAmmount.getText().toString());
                            startActivity(intent);
                        } else {
                            Utility.toast(context, "Please enter amount!");
                        }
                    } else {
                        if (!binding.etAmmount.getText().toString().equals("")) {
                            if (itemImagesModelsList.size() > 0) {
                                add_bank_transfer();
                            } else {
                                Utility.toast(context, "Please select image!");
                            }
                        } else {
                            Utility.toast(context, "Please enter amount!");
                        }
                    }
                } else {
                    Utility.toast(context, "Please select payment method!");
                }
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: " + paymentType);
                if (paymentType != null && !paymentType.equals("")) {
                    if (paymentType.equals("card")) {
                        Intent intent = new Intent(context, CardPaymentActivity.class);
                        intent.putExtra(AppConstants.AMOUNT, binding.etAmmount.getText().toString());
                        startActivity(intent);
                    } else {
                        if (!binding.etAmmount.getText().toString().equals("")) {
                            if (itemImagesModelsList.size() > 0) {
                                add_bank_transfer();
                            } else {
                                Utility.toast(context, "Please select image!");
                            }
                        } else {
                            Utility.toast(context, "Please enter amount!");
                        }
                    }
                } else {
                    Utility.toast(context, "Please select payment method!");
                }
            }
        });
        binding.tvSelectImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                bottomSheetDialog();
            }
        });
    }

    private void add_bank_transfer() {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        builder.addFormDataPart(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        builder.addFormDataPart(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        builder.addFormDataPart(AppConstants.ACCESS_TOKEN, SessionManager.getAccessToken(context));
        builder.addFormDataPart(AppConstants.UID, RealmController.getUser().getId());
        builder.addFormDataPart(AppConstants.ACCESS, AppConstants.TECH);
        builder.addFormDataPart(AppConstants.AMOUNT, binding.etAmmount.getText().toString().trim());


        if (itemImagesModelsList != null && itemImagesModelsList.size() > 0) {
            for (int i = 0; i < itemImagesModelsList.size(); i++) {
                File file = new File(itemImagesModelsList.get(i).getImage());
                Log.e(TAG, "uploadGallery image name :----------->>>>> " + file.getName());
                Log.e(TAG, "uploadGallery:----------->>>>> " + file);
                builder.addFormDataPart("image[]", file.getName(), RequestBody.create(MediaType.parse("image*//*"), file));
            }
        } else {
            builder.addFormDataPart("image[]", "");
        }

        final MultipartBody requestBody = builder.build();
        Log.e(TAG, "add_bank_transfer: " + requestBody);
        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).add_bank_transfer(Utility.getHeaderAuthentication(context), requestBody), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        deposit_popup();
                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(PayNowActivity.this);

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

    private void get_admin_bankinfo() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId() + "");
        Log.e(TAG, "get_admin_bankinfo: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).get_admin_bankinfo(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {

                        BankInfoModel bankInfoModel = Controller.getGson().fromJson(jsonObject.getJSONObject("data").toString(), BankInfoModel.class);
                        String accountdetail = bankInfoModel.getAccount();
                        binding.tvAccountInfo.setText(accountdetail);


                    } else if (jsonObject.getInt("status") == 0) {

                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(PayNowActivity.this);
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

    private void deposit_popup() {
        dialog.setContentView(R.layout.deposit_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = dialog.findViewById(R.id.tvmsgggg);
        textView.setText("Gracias por su depósito. Verificaremos su solicitud en las próximas 24-48 horas hábiles.");

        dialog.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onBackPressed();

            }
        });

        dialog.show();
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

    private void initializeTextWatcher() {
        binding.etAmmount.addTextChangedListener(new TextWatcher() {
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
                    binding.etAmmount.setError(null);
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


        // mobile text watcher

/*
        binding.etAmmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    binding.etAmmount.setError(null);
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
                binding.etAmmount.removeTextChangedListener(this); // Remove listener
                binding.etAmmount.setText(strprice);
                handleSelection();
                binding.etAmmount.addTextChangedListener(this); // Add back the listener
            }
        });
*/


    }

    /*
        private void handleSelection() {
            binding.etAmmount.setSelection(binding.etAmmount.getText().length());
        }
    */
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
                file = getPhotoFileUri(photoFileName);
                Uri fileProvider = FileProvider.getUriForFile(context, context.getResources().getString(R.string.file_provider_authorities), file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, StringContract.RequestCode.CAMERA);
                }
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
                            if ((url.length() / 1024) < 1024) {
                                Image_Path = url;
                            } else if ((url.length() / 1024) > 1024 && (url.length() / 1024) < 2048) {
                                Image_Path = new Compressor(context).setQuality(50).compressToFile(url);
                            } else if ((url.length() / 1024) > 3072) {
                                Image_Path = new Compressor(context).setQuality(75).compressToFile(url);
                            }
                            selectedImages.add(Image_Path.getAbsolutePath());
                            ArrayList<ItemImagesModel> imagesModelsList = new ArrayList<>();
                            for (int i = 0; i < selectedImages.size(); i++) {
                                Log.e(TAG, "onActivityResult: " + selectedImages.get(i));
                                ItemImagesModel itemImagesModel = new ItemImagesModel();
                                itemImagesModel.setImage(selectedImages.get(i));
                                imagesModelsList.add(itemImagesModel);
                            }
                            selectedImages.clear();
                            itemImagesModelsList.addAll(imagesModelsList);
                            if (itemImagesModelsList.size() > 1) {
                                binding.tvSelectImage.setText(itemImagesModelsList.size() + " imágenes");
                            } else {
                                binding.tvSelectImage.setText(itemImagesModelsList.size() + " imagen");
                            }
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
                                ArrayList<ItemImagesModel> imagesModelsList = new ArrayList<>();
                                for (int i = 0; i < selectedImages.size(); i++) {
                                    Log.e(TAG, "onActivityResult: " + selectedImages.get(i));
                                    ItemImagesModel itemImagesModel = new ItemImagesModel();
                                    itemImagesModel.setImage(selectedImages.get(i));
                                    imagesModelsList.add(itemImagesModel);
                                }
                                selectedImages.clear();
                                itemImagesModelsList.addAll(imagesModelsList);
                                if (itemImagesModelsList.size() > 1) {
                                    binding.tvSelectImage.setText(itemImagesModelsList.size() + " imágenes");
                                } else {
                                    binding.tvSelectImage.setText(itemImagesModelsList.size() + " imagen");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (requestCode == CAMERA) {
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
                        ArrayList<ItemImagesModel> imagesModelsList = new ArrayList<>();
                        for (int i = 0; i < selectedImages.size(); i++) {
                            Log.e(TAG, "onActivityResult: " + selectedImages.get(i));
                            ItemImagesModel itemImagesModel = new ItemImagesModel();
                            itemImagesModel.setImage(selectedImages.get(i));
                            imagesModelsList.add(itemImagesModel);
                        }
                        selectedImages.clear();
                        itemImagesModelsList.addAll(imagesModelsList);
                        if (itemImagesModelsList.size() > 1) {
                            binding.tvSelectImage.setText(itemImagesModelsList.size() + " imágenes");
                        } else {
                            binding.tvSelectImage.setText(itemImagesModelsList.size() + " imagen");
                        }
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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    file = getPhotoFileUri(photoFileName);
                    Uri fileProvider = FileProvider.getUriForFile(context, context.getResources().getString(R.string.file_provider_authorities), file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, StringContract.RequestCode.CAMERA);
                    }
                } else
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
