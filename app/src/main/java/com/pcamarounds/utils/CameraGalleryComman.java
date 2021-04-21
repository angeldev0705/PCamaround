package com.pcamarounds.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.pcamarounds.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class CameraGalleryComman {
    /**
     * Id to identify a camera permission request.
     */
    public final static int REQUEST_CAMERA_IMAGE = 1034;
    private static final String TAG = "CameraGalleryComman";
    /**
     * Id to identify a gallery permission request.
     */
    private static final int REQUEST_GALLERY_IMAGE = 1063;
    /**
     * variable to enable only one functionality of cropping in one time
     */
    public boolean enableCircleCrop = false;
    public boolean enableSourceImageAspectRatio = false;
    public boolean enableProfileAspectRatio = false;
    public boolean enableBannerAspectRatio = false;
    public boolean enableFreeStyleCrop = false;
    /**
     * variable to handle the output file
     */
    private File photoFile;
    /**
     * variable to get the result uri
     */
    private Uri resultUri;
    /**
     * variable to check the file is intiated by fragment or activity
     */
    private boolean isFragment;
    /**
     * variable to check the file is intiated by fragment or activity
     */
    private File saveFile;
    /**
     * Include the permission file to request the desired the permissions
     */
    private PersmissionUtils persmissionUtils;

    private Context context;
    private AppCompatActivity activity;
    private Fragment fragment;


    // constructor for activity
    public CameraGalleryComman(Context context, AppCompatActivity activity) {
        this.context = context;
        this.activity = activity;
        this.isFragment = false;
        this.persmissionUtils = new PersmissionUtils(context, activity);
        // this.persmissionUtils.askPermissionAtStart();
    }

    // constructor for fragments
    public CameraGalleryComman(Context context, Fragment fragment) {
        this.isFragment = true;
        this.context = context;
        this.fragment = fragment;
        this.persmissionUtils = new PersmissionUtils(context, fragment);
        //   this.persmissionUtils.askPermissionAtStart();
    }

    /**
     * Called to launch camera which includes request permission if it is denied first time
     * and creation of file using the file provider.
     */
    public void onLaunchCamera() {

        if (isFragment) {
            if (persmissionUtils.requestCameraPermissions() && persmissionUtils.requestWritePermission()) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoFile = getPhotoFileUri();
                Uri fileProvider = FileProvider.getUriForFile(fragment.getActivity(), context.getString(R.string.file_provider_authorities), photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    fragment.startActivityForResult(intent, REQUEST_CAMERA_IMAGE);
                }
            }
        } else {
            if (persmissionUtils.requestCameraPermissions() && persmissionUtils.requestWritePermission()) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoFile = getPhotoFileUri();
                Uri fileProvider = FileProvider.getUriForFile(activity, context.getString(R.string.file_provider_authorities), photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    activity.startActivityForResult(intent, REQUEST_CAMERA_IMAGE);
                }
            }
        }
    }

    /**
     * Called to launch gallery which includes request permission if it is denied first time
     * and intent to the galllery
     */
    public void pickFromGallery() {

        if (isFragment) {
            if (persmissionUtils.requestWritePermission() && persmissionUtils.requestReadPermission()) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                fragment.startActivityForResult(Intent.createChooser(intent, context.getString(R.string.label_select_picture)), REQUEST_GALLERY_IMAGE);
            }
        } else {
            if (persmissionUtils.requestReadPermission()) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                activity.startActivityForResult(Intent.createChooser(intent, context.getString(R.string.label_select_picture)), REQUEST_GALLERY_IMAGE);
            }
        }


    }

    public void removeImage() {
        saveFile = null;
    }

    /**
     * This function creates a temporary file of image from camera with specified naming of the image
     * and save the file in the desired directory.
     */
    private File getPhotoFileUri() {
        File file;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = timeStamp + context.getString(R.string.photo_jpg);
        /*Use this if you did not want to repeat images*/
        String photoFileName = "photo.jpg";
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name));
        // File mediaStorageDir = new File(getCacheDir(),getString(R.string.app_name));
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.e(TAG, "failed to create directory");
        }
        file = new File(mediaStorageDir.getPath() + File.separator + imageFileName);
        return file;
    }

    /**
     * Callback to handle the intent result for images
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle the images from camera
        if (requestCode == REQUEST_CAMERA_IMAGE) {
            if (resultCode == RESULT_OK) {
                saveFile = null;
                if (photoFile != null) {
                    Uri camerafile = Uri.fromFile(photoFile);
                    startCropActivity(camerafile);
                }
            } else {
                Toast.makeText(activity, context.getString(R.string.pic_not_taken_error), Toast.LENGTH_SHORT).show();
            }

            // handle the images from gallery
        } else if (requestCode == REQUEST_GALLERY_IMAGE) {
            if (resultCode == RESULT_OK) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    saveFile = null;
                    startCropActivity(selectedUri);
                } else {
                    Toast.makeText(activity, R.string.toast_cannot_retrieve_selected_image, Toast.LENGTH_SHORT).show();
                }
            }

            // handle the images which are cropped
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == UCrop.RESULT_ERROR) {
                handleCropError(data);
            } else {
                if (data != null) {
                    handleCropResult(data);
                }
            }
        }
    }

    /**
     * Callback to handle the permission result and provide you the option the handle the permissin which are denied.
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

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
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
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
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Permissions Required")
                        .setMessage(context.getString(R.string.permission_message))

                        .setPositiveButton(context.getString(R.string.settings), (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        })
                        .setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> {
                            // CameraIntentTestActivity.this.finish();
                            dialog.dismiss();
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        } else {

            switch (requestCode) {
                case PersmissionUtils.REQUEST_CAMERA_PERMISSION:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        onLaunchCamera();
                    }
                    break;
                case PersmissionUtils.REQUEST_READ_EXTERNAL_STORAGE_PERMISSION:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        pickFromGallery();
                    }
                    break;
                case PersmissionUtils.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        saveCroppedImage();
                    }
                    break;
            }
        }

    }


    /**
     * Called to start the cropping activity it has the option for
     * basic and advanced cropping.
     */
    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = ".jpg";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(context.getCacheDir(), destinationFileName)));
        // adding the advanced features to the cropper
        uCrop = advancedConfig(uCrop);
        // adding the advanced features to the cropper
        uCrop = basisConfig(uCrop);


        if (isFragment) {
            uCrop.start(context, fragment);
        } else {
            uCrop.start(activity);
        }

    }

    /**
     * Callback for basic config of the ucrop.
     */

    private UCrop basisConfig(@NonNull UCrop uCrop) {

        if (isEnableBannerAspectRatio()) {
            uCrop = uCrop.withAspectRatio(3, 2);
        }

        if (isEnableProfileAspectRatio()) {
            uCrop = uCrop.withAspectRatio(1, 1);
        }

        if (isEnableSourceImageAspectRatio()) {
            uCrop = uCrop.useSourceImageAspectRatio();
        }
        return uCrop;
    }

    // crop adavanced configs
    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();

        options.setHideBottomControls(true);

        if (isFragment) {
            options.setToolbarTitle("Editar Foto");
            options.setToolbarColor(context.getResources().getColor(R.color.color_primary));
            options.setStatusBarColor(context.getResources().getColor(R.color.color_primary));
            options.setToolbarWidgetColor(context.getResources().getColor(R.color.color_on_primary));
        } else {
            options.setToolbarTitle("Editar Foto");
            options.setToolbarColor(context.getResources().getColor(R.color.color_primary));
            options.setStatusBarColor(context.getResources().getColor(R.color.color_primary));
            options.setToolbarWidgetColor(context.getResources().getColor(R.color.color_on_primary));
        }


        options.setFreeStyleCropEnabled(isEnableFreeStyleCrop());
        options.setCircleDimmedLayer(isEnableCircleCrop());

         /*
        If you want to configure how gestures work for all UCropActivity tabs
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        * */

        /*
        This sets max size for bitmap that will be decoded from source Uri.
        More size - more memory allocation, default implementation uses screen diagonal.
        options.setMaxBitmapSize(640);
        * */


       /*
        Tune everything (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧
        options.setMaxScaleMultiplier(5);
        options.setImageToCropBoundsAnimDuration(666);
        options.setDimmedLayerColor(Color.CYAN);
        options.setCircleDimmedLayer(true);
        options.setShowCropFrame(false);
        options.setCropGridStrokeWidth(20);
        options.setCropGridColor(Color.GREEN);
        options.setCropGridColumnCount(2);
        options.setCropGridRowCount(1);
        options.setToolbarCropDrawable(R.drawable.your_crop_icon);
        options.setToolbarCancelDrawable(R.drawable.your_cancel_icon);
        // Color palette
        options.setToolbarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.your_color_res));
        // Aspect ratio options
        options.setAspectRatioOptions(1,
            new AspectRatio("WOW", 1, 2),
            new AspectRatio("MUCH", 3, 4),
            new AspectRatio("RATIO", CropImageView.DEFAULT_ASPECT_RATIO, CropImageView.DEFAULT_ASPECT_RATIO),
            new AspectRatio("SO", 16, 9),
            new AspectRatio("ASPECT", 1, 1));
       */
        return uCrop.withOptions(options);
    }

    // handle the crop results after cropping
    private void handleCropResult(@NonNull Intent result) {
        resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            File dsf = new File(String.valueOf(resultUri));
            Log.e(TAG, "handleCropResult: " + getReadableFileSize(dsf.length()));
            saveCroppedImage();
        } else {
            Toast.makeText(context, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            Toast.makeText(context, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }


    private void saveCroppedImage() {
        if (persmissionUtils.requestWritePermission()) {
            try {
                copyFileToDownloads(resultUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * It saves the cropped results in local storage of the phone
     */
    private void copyFileToDownloads(Uri croppedFileUri) throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = timeStamp + context.getString(R.string.photo_jpg);

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                context.getString(R.string.app_name));

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.e(TAG, "failed to create directory");
        }

        saveFile = new File(mediaStorageDir, imageFileName);

        FileInputStream inStream = new FileInputStream(new File(croppedFileUri.getPath()));
        FileOutputStream outStream = new FileOutputStream(saveFile);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();

        Log.e(TAG, "file Storage: " + saveFile);
        Log.e(TAG, "file Storage: " + getReadableFileSize(saveFile.length()));

    }

    /**
     * To get the resultant image after copping from camera or gallery.
     */
    public File outputImageFile() {
        return saveFile;
    }


    /**
     * Get the size of the image
     */
    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        Log.e(TAG, "getReadableFileSize: - " + digitGroups);
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    /**
     * Getter setters to enable or disable the desired feature of cropping.
     */
    public boolean isEnableCircleCrop() {
        return enableCircleCrop;
    }

    public void setEnableCircleCrop(boolean enableCircleCrop) {
        this.enableCircleCrop = enableCircleCrop;
        if (enableCircleCrop) {
            this.enableFreeStyleCrop = false;
            this.enableProfileAspectRatio = false;
            this.enableBannerAspectRatio = false;
            this.enableSourceImageAspectRatio = false;
        }
    }

    public boolean isEnableSourceImageAspectRatio() {
        return enableSourceImageAspectRatio;
    }

    public void setEnableSourceImageAspectRatio(boolean enableSourceImageAspectRatio) {
        this.enableSourceImageAspectRatio = enableSourceImageAspectRatio;
        if (enableSourceImageAspectRatio) {
            this.enableFreeStyleCrop = false;
            this.enableProfileAspectRatio = false;
            this.enableBannerAspectRatio = false;
            this.enableCircleCrop = false;
        }
    }

    public boolean isEnableProfileAspectRatio() {
        return enableProfileAspectRatio;
    }

    public void setEnableProfileAspectRatio(boolean enableProfileAspectRatio) {
        this.enableProfileAspectRatio = enableProfileAspectRatio;
        if (enableProfileAspectRatio) {
            this.enableFreeStyleCrop = false;
            this.enableBannerAspectRatio = false;
            this.enableSourceImageAspectRatio = false;
            this.enableCircleCrop = false;
        }
    }

    public boolean isEnableBannerAspectRatio() {
        return enableBannerAspectRatio;
    }

    public void setEnableBannerAspectRatio(boolean enableBannerAspectRatio) {
        this.enableBannerAspectRatio = enableBannerAspectRatio;
        if (enableBannerAspectRatio) {
            this.enableFreeStyleCrop = false;
            this.enableSourceImageAspectRatio = false;
            this.enableProfileAspectRatio = false;
            this.enableCircleCrop = false;
        }
    }

    public boolean isEnableFreeStyleCrop() {
        return enableFreeStyleCrop;
    }

    public void setEnableFreeStyleCrop(boolean enableFreeStyleCrop) {
        this.enableFreeStyleCrop = enableFreeStyleCrop;

        if (enableFreeStyleCrop) {
            this.enableSourceImageAspectRatio = false;
            this.enableProfileAspectRatio = false;
            this.enableBannerAspectRatio = false;
            this.enableCircleCrop = false;
        }
    }

    // end of getter setter.

}
