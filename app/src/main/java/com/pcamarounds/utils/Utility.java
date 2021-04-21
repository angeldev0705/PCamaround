package com.pcamarounds.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pcamarounds.R;
import com.pcamarounds.activities.SplashActivity;
import com.pcamarounds.constants.AppConfig;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.controller.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utility {
    public static final String Maestro_Card = "^(5018|5020|5038|6304|6759|6761|6763)[0-9]{8,15}$";
    public static final int SPLASH_TTIMEOUT = 3000;
    public static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final String TAG = "Utility";
    //for date comparsion
    public static String DATEAFTER = "after";
    public static String DATEBEFORE = "before";
    public static String DATEEQUAL = "equal";
    public static String FORMAT_MMddYYYY = "MM-dd-yyyy";
    public static String FORMAT_HHmmss = "HH:mm:ss";

    // String givenDateString = "Tue Apr 23 16:08:28 GMT+05:30 2013";
    // SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy")
    public static String FORMAT_YYYYMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
    public static String FORMAT_YYYYMMddHHmm = "yyyy-MM-dd HH:mm";
    public static String FORMAT_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static String FORMAT_YYYYMMdd = "yyyy-MM-dd";
    public static String FORMAT_MMMddYYYY = "MMM dd, yyyy";
    public static String FORMAT_ddMMMYYYY = "dd MMM yyyy";
    public static String FORMAT_MMddYY = "MM-dd-yy";
    public static String FORMAT_HHmm = "HH:mm";
    public static String FORMAT_YYYYMMMMMdd = "yyyyy-MMMMM-dd"; //  2001.July.04
    public static String FORMAT_EEEMMMdd = "EEE MMM dd HH:mm:ss z yyyy"; //   Wed Oct 30 00:00:00 GMT+05:30 2019
    private static boolean hasImmersive;
    private static boolean cached = false;

    /********************************** ALL FUNCTIONS ****************************************************** */

    // Logout when Invalid access key
    public static void logout(Activity activity) {
        if (RealmController.getUser() != null) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(com.pcamarounds.retrofit.Environment.getTopicId() + RealmController.getUser().getId() + "");
        }
        RealmController.clearDatabase();
        SessionManager.clearSession(activity);
        Intent intent = new Intent(activity, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    // hide keybaord
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void hideKeyboard(Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = ((Activity) mContext).getCurrentFocus();
        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    public static boolean isCheckEmptyOrNull(String text) {
        if (text != null && !text.equals("") && !text.isEmpty() && !text.equals("0") && !text.equals("0.00"))
        {
            return true;
        }else {
            return false;
        }

    }
    public static double roundOffTwoDecimal(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String printInitials(String name) {
        char first = 0;
        if (isCheckEmptyOrNull(name))
        {
            first =  name.charAt(0);
        }
        if (first != ' ')
        {
            return String.valueOf(first+".");
        }else {
            return String.valueOf(" ");
        }

    }
    public static File bitmapToFile(Context context, Bitmap bitmap, String fileNameToSave) { // File name like "image.png"
        //create a file to write bitmap data
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory() + File.separator + System.currentTimeMillis()+fileNameToSave);
            file.createNewFile();

//Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        }catch (Exception e){
            e.printStackTrace();
            return file; // it will return null
        }
    }

    // get a random id
    public static Integer getRandomId() {
        int random = (int) (Math.random() * 50 + 1);
        return random;
    }

    // get a random uuid
    public static String getUUID() {
        return java.util.UUID.randomUUID().toString();
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());

            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }


    public static File copyFileToDownloads(Context context,Uri croppedFileUri) throws Exception {

      // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = System.currentTimeMillis()+context.getString(R.string.photo_jpg);

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),context. getString(R.string.app_name));

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.e(TAG, "failed to create directory");
        }

        File saveFile = new File(mediaStorageDir, imageFileName);

        File dfsdf = new File(String.valueOf(saveFile));

        FileInputStream inStream = new FileInputStream(new File(Objects.requireNonNull(croppedFileUri.getPath())));
        FileOutputStream outStream = new FileOutputStream(saveFile);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();

        Log.e(TAG, "copyFileToDownloads: " + saveFile+"   " + dfsdf );
        return  saveFile;


    }



    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    public static String createGroupId(String job_id,String bid, String userid, String techid) {
        String jobidd = "job" + job_id;
        String bidd = "bid" + bid;
        //String useriddd = AppConstants.CAMAROUND + "_" + userid;
        String useriddd = com.pcamarounds.retrofit.Environment.getCometttttId() + "_" + userid;
        //String techiddd = AppConstants.CAMAROUND + "_" + techid;
        String techiddd = com.pcamarounds.retrofit.Environment.getCometttttId() + "_" + techid;
        String correctId = jobidd + "_"+bidd+"_" + useriddd + "_" + techiddd;
        Log.e(TAG, "getCometId: " + correctId);
        return correctId;
    }

    public static Bitmap stringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
    public static String getCometId(String id){
        //String newusername = AppConstants.CAMAROUND;
        String newusername = com.pcamarounds.retrofit.Environment.getCometttttId();
        String correctId = newusername+"_"+id;
        Log.e(TAG, "getCometId: "+correctId );
        return correctId;
    }

    public static HashMap<String, String> getHeaderAuthentication(Context context) {
        HashMap<String, String> headerHashMap = new HashMap<>();
        headerHashMap.put(AppConstants.AUTHENTICATION, "Bearer "+SessionManager.getAuthenticationToken(context));
        return headerHashMap;
    }


    public static HashMap<String, String> getHeadersone() {
        HashMap<String, String> headerHashMap = new HashMap<>();
        headerHashMap.put("appId", AppConfig.AppDetails.APP_ID);
        headerHashMap.put("apiKey",AppConfig.AppDetails.API_KEY);
        return headerHashMap;
    }

    public static HashMap<String, String> getHeaders() {
        HashMap<String, String> headerHashMap = new HashMap<>();
        headerHashMap.put("appId",AppConfig.AppDetails.APP_ID);
        headerHashMap.put("apiKey",AppConfig.AppDetails.API_KEY);
        headerHashMap.put("content-type", "application/json");
        headerHashMap.put("accept", "application/json");
        return headerHashMap;
    }
    public static void infoPopup(Context context,String info) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.info_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = dialog.findViewById(R.id.tvInfo);
        textView.setText(info);
        dialog.findViewById(R.id.ivClose12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.show();
    }
    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    public static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 6;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return false;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return false;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public static String getTwoDigitDecimal(double d) {
        //return new DecimalFormat("##.##").format(d);
        //String price = String.format(Locale.getDefault(), "%.2f", d);
        String price = new DecimalFormat("##.##").format(d);
        /*String[] split = price.split(".");
        int a = Integer.parseInt(split[0]);
        int b = Integer.parseInt(split[0]);
        if (b > 0) {
            price = a + "." + b;
        } else {
            price = String.valueOf(a);
        }*/
        return price;
    }

    public static String timeLeft(int delivery_time, String updatedDate) {

        try {

            int minutes = delivery_time;
            long millis_minutes = minutes * 60 * 1000;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = sdf.parse(updatedDate);
            long totalMinutes = millis_minutes + date.getTime();
            SimpleDateFormat sdf1 = new SimpleDateFormat();
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
            long leftMinutes = totalMinutes - getUTCDateTimeAslong();
            return leftMinutes + "";
        } catch (Exception e) {
            Log.e("Error in Utility 63", "" + e);
            e.printStackTrace();
        }
        return "";
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public static void logLargeString(String str) {
        if (str.length() > 3000) {
            System.out.print(str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            System.out.print(str);
        }
    }

    public static int getNavBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NewApi")
    public static boolean hasImmersive(Context ctx) {

        if (!cached) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                hasImmersive = false;
                cached = true;
                return false;
            }
            Display d = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            hasImmersive = (realWidth > displayWidth) || (realHeight > displayHeight);
            cached = true;
        }

        return hasImmersive;
    }

    public static String getDeviceType(Context mContext) {
        String ua = new WebView(mContext).getSettings().getUserAgentString();
        if (ua.contains("Mobile")) {
            System.out.println("Type:Mobile");
            return "ANDROID MOBILE";
            // Your code for Mobile
        } else {
            // Your code for TAB
            System.out.println("Type:TAB");
            return "ANDROID TAB";
        }
    }

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = "1755018674765832";
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    public static boolean connectionStatus(Context mContext) {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {

            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            if (activeNetwork != null) {
                Log.e(TAG, "activeNetwork.getTypeName(); " + activeNetwork.getTypeName());
                if (activeNetwork.getTypeName().equalsIgnoreCase("WIFI")) {


                    if (activeNetwork.isConnectedOrConnecting()) {
                        return true;
                    } else {
                        return false;
                    }

                } else if (activeNetwork.getTypeName().equalsIgnoreCase("MOBILE")) {

                    if (activeNetwork.isConnectedOrConnecting()) {
                        return true;
                    } else {
                        return false;
                    }

                } else {

                    return false;

                }
            }
        }
        return false;
    }

    /**
     * String myBase64Image = encodeToBase64(myBitmap, Bitmap.CompressFormat.JPEG, 100);
     * Bitmap myBitmapAgain = decodeBase64(myBase64Image);
     *
     * @param compressFormat
     * @param quality
     * @return
     */
    public static String encodeToBase64(Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.NO_WRAP);
    }

    /**
     * String myBase64Image = encodeToBase64(myBitmap, Bitmap.CompressFormat.JPEG, 100);
     * Bitmap myBitmapAgain = decodeBase64(myBase64Image);
     *
     * @param input
     * @return
     */
    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static String utcDateTimeZone(String dateFormat, String datetime, Context mContext) {
        String formattedDate = "";
        try {
            //String dateStr = "2001.07.04T12:08";
            SimpleDateFormat df = new SimpleDateFormat(dateFormat, Locale.getDefault());
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(datetime);
            df.setTimeZone(TimeZone.getDefault());
            if (date != null) {
                formattedDate = df.format(date);
                Log.e("TimeZone:- ", formattedDate);
                Date newDate = spf.parse(formattedDate);
                spf = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());
                if (newDate != null) {
                    formattedDate = spf.format(newDate);
                }
            } else {
                Utility.toast(mContext, "Date not found");
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            Log.e("Exception:- ", e.getMessage());
        }
        //String utcTime = formattedDate.split("T")[1];
        return formattedDate;
    }

    public static String makeSHA1Hash(String input)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.reset();
        byte[] buffer = input.getBytes("UTF-8");
        md.update(buffer);
        byte[] digest = md.digest();

        String hexStr = "";
        for (int i = 0; i < digest.length; i++) {
            hexStr += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
        }
        return hexStr;
    }

    public static void fullScreen(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

//    public static void nointernetmsg(final View v, final Activity activity) {
//        Snackbar snackbar = Snackbar
//                .make(v, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
//                .setAction("RETRY", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(Intent.ACTION_MAIN);
//                        intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
//                        activity.startActivity(intent);
//                    }
//                });
//        snackbar.setActionTextColor(Color.RED);
//        View sbView = snackbar.getView();
//        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
//        textView.setTextColor(Color.YELLOW);
//        snackbar.show();
//    }

    public static boolean isFirst(Context context) {
        final SharedPreferences reader = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        final boolean first = reader.getBoolean("is_first", true);
        if (first) {
            final SharedPreferences.Editor editor = reader.edit();
            editor.putBoolean("is_first", false);
            editor.apply();
            editor.commit();
        }
        return first;
    }


    // round off
    public static String roundOff(String value) {
        double datavalue = Double.parseDouble(value);
        double newKB = Math.round(datavalue * 100D) / 100D;
        String correctvalue = String.valueOf(newKB);
        return correctvalue;
    }

    public static String roundOff(Double amount) {
        return String.format("%.2f", amount);
    }

    public static String roundOffFloatWithDecimal(float d, int decimalPlace) {
        String value = "";
        value = String.valueOf(BigDecimal.valueOf(d).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue());
        return value;
    }


    public static String capitalize(String capString) {
        // String chars = capitalize("hello dream world");  Output: Hello Dream World
        if (capString != null && !capString.equals("")) {
            StringBuffer capBuffer = new StringBuffer();
            Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
            while (capMatcher.find()) {
                capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
            }
            return capMatcher.appendTail(capBuffer).toString();
        } else {
            return "";
        }
    }

    public static String reverseAString(String string) {
        String str[] = string.split("\\s*,\\s*");
        String finalStr = "";
        for (int i = str.length - 1; i >= 0; i--) {
            finalStr += str[i] + ",";
        }
        return finalStr;
    }

    public static String extractYoutubeVideoId(String ytUrl) {
        String vId = null;
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(ytUrl);

        if (matcher.find()) {
            vId = matcher.group();
        }
        Log.e(TAG, "extractYoutubeVideoId: " + vId);
        return vId;
    }

    public static int setval(int min) {
        int correct_min = 0;

        if (min > 0 && min < 5) {
            correct_min = 0;
        } else if (min > 5 && min < 10) {
            correct_min = 1;
        } else if (min > 10 && min < 15) {
            correct_min = 2;
        } else if (min > 15 && min < 20) {
            correct_min = 3;
        } else if (min > 20 && min < 25) {
            correct_min = 4;
        } else if (min > 25 && min < 30) {
            correct_min = 5;
        } else if (min > 30 && min < 35) {
            correct_min = 6;
        } else if (min > 35 && min < 40) {
            correct_min = 7;
        } else if (min > 40 && min < 45) {
            correct_min = 8;
        } else if (min > 45 && min < 50) {
            correct_min = 9;
        } else if (min > 50 && min < 55) {
            correct_min = 10;
        } else if (min > 55 && min < 60) {
            correct_min = 11;
        }
        Log.e(TAG, "setval: " + correct_min);
        return correct_min;

    }

    public static String getGooglePlayStoreUrl(Activity activity) {
        String id = activity.getApplicationInfo().packageName; // current google
        // play is using
        // package name
        // as id
        return "market://details?id=" + id;
    }

    public static String getCurrentVersion(Activity activity) {
        // int currentVersionCode = 0;
        String versionname = "";
        PackageInfo pInfo;
        try {
            pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            // currentVersionCode = pInfo.versionName;
            versionname = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // return 0
        }
        return versionname;
    }

    public static void showLoader(View layout) {
        layout.setVisibility(View.VISIBLE);
    }

    public static void hideLoader(View layout) {
        layout.setVisibility(View.GONE);
    }

    public static boolean checkGPSStatus(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;
    }

    ;

    public static void strikethrough(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public static void underline(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public static boolean isDigit(String str) {
        if (str.substring(0, 1).matches("\\d") || str.substring(0, 1).matches("[0-9]")) {
            return true;
        } else {
            return false;
        }
    }

    public static String removeComma(String s) {
        if (s.endsWith(",")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public static String getLastSeenDatenew(long timeStamp, Context context) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStamp * 1000L);
        String date = android.text.format.DateFormat.format("dd/MM/yyyy", cal).toString();
        String lastSeenTime = new SimpleDateFormat("HH:mm a").format(cal.getTime());

        long currentTimeStamp = System.currentTimeMillis();

        long diffTimeStamp = (currentTimeStamp - timeStamp) / 1000;

        if (diffTimeStamp < 24 * 60 * 60) {
            return "Last seen today at " + lastSeenTime;
        } else if (diffTimeStamp < 48 * 60 * 60) {
            return "Last seen yesterday at " + lastSeenTime;
        } else {
            return "Last seen at " + date + " on " + lastSeenTime;
        }

    }

    public static String getLeftTimeCountDownTimer(Long millisUntilFinished) {

        long seconds = millisUntilFinished / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (minutes > 0) {
            seconds = seconds % 60;
        }
        if (hours > 0) {
            minutes = minutes % 60;
        }

        StringBuilder strhour = new StringBuilder();
        StringBuilder strminutee = new StringBuilder();
        StringBuilder strsec = new StringBuilder();

        if (hours < 10) {
            strhour.append("0" + hours);
        } else {
            strhour.append(hours);
        }
        if (minutes < 10) {
            strminutee.append("0" + minutes);
        } else if (minutes == 0) {
            strminutee.append("00");
        } else {
            strminutee.append(minutes);
        }

        if (seconds < 10) {
            strsec.append("0" + seconds);
        } else if (seconds == 0) {
            strsec.append("00");
        } else {
            strsec.append(seconds);
        }

        String time = strhour + ":" + strminutee + ":" + strsec;
        return time;
    }

    public static void clearAllActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static String hourMinBuilder(int hour, int minute) {
        StringBuilder strhour = new StringBuilder();
        StringBuilder strminutee = new StringBuilder();
        if (hour < 10) {
            strhour.append("0" + hour);
        } else {
            strhour.append(hour);
        }
        if (minute < 10) {
            strminutee.append("0" + minute);
        } else if (minute == 0) {
            strminutee.append("00");
        } else {
            strminutee.append(minute);
        }
        String ttttt = strhour + ":" + strminutee;
        return ttttt;
    }


    public static int dpToPx(int dp, Context context) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static void showTransparentStatusBar(AppCompatActivity activity) {
        if (activity != null) {
            if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
                setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
            }
            if (Build.VERSION.SDK_INT >= 19) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            //make fully Android Transparent Status bar
            if (Build.VERSION.SDK_INT >= 21) {
                setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }

    }

    public static void setWindowFlag(AppCompatActivity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    // hide toolbar in fragments
/*

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

*/

    /*****************************  ALL VALIDATIONS  ********************************************************/

    public static boolean isValidPhone(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 8 || phone.length() > 13) {
                check = false;
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    public static boolean isValidEmailId(String emailAddress) {
        String expression = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        CharSequence inputStr = emailAddress;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();


    }

    public static boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public static boolean isValidPassword(String Password) {
        String expression = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$";  //--TOdo ws like --> "(?=.*[0-9@#$%^&+=])"  "^(?=.*[a-zA-Z])(?=.*[0-9@#$%^&+=]).{8,}$"
        CharSequence inputStr = Password;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }

 /*       public static boolean isValidPassword(final String password) {

            Pattern pattern;
            Matcher matcher;

            final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(password);

            return matcher.matches();

        }*/

    public static boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    public static String isValid(String passwordhere) {
        Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
        Pattern lowerCasePatten = Pattern.compile("[a-z ]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");
        String error = "";

        if (passwordhere.length() < 8) {
            error = "Password lenght must have alleast 8 character !!";
        }
        if (!specailCharPatten.matcher(passwordhere).find()) {
            error = "Password must have atleast one specail character !!";
        }
        if (!UpperCasePatten.matcher(passwordhere).find()) {
            error = "Password must have atleast one uppercase character !!";
        }
        if (!lowerCasePatten.matcher(passwordhere).find()) {
            error = "Password must have atleast one lowercase character !!";
        }
        if (!digitCasePatten.matcher(passwordhere).find()) {
            error = "Password must have atleast one digit character !!";
        }
        return error;
    }

    public static boolean isSpecailCharPatten(String paswword) {
        Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        return !specailCharPatten.matcher(paswword).find();
    }

    public static boolean isSpecialCharacter(Character c) {
        return c.toString().matches("[^a-z A-Z0-9]");
    }

    public static boolean isUpperCasePatten(String paswword) {
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
        return !UpperCasePatten.matcher(paswword).find();
    }

    public static boolean isLowerCasePatten(String password) {
        Pattern lowerCasePatten = Pattern.compile("[a-z]");
        return !lowerCasePatten.matcher(password).find();
    }

    public static boolean isDigitCasePatten(String password) {
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");
        return !digitCasePatten.matcher(password).find();
    }

    public static boolean isContainspain(String password) {
        Pattern digitCasePatten = Pattern.compile("[ ^([6-9]{1})([0-9]{9})]");
        return !digitCasePatten.matcher(password).find();
    }

    public static void nointernettoast(Context context) {
        Toast.makeText(context, "No internet!", Toast.LENGTH_SHORT).show();
    }

    public static boolean checkChar(EditText editText) {
        char x = editText.getText().toString().charAt(0);
        if (x == '6' || x == '7' || x == '9')
            return true;

        return false;
    }

    /************************** GEO LOCATIONS AND ADRESS *********************************************************************/
    public static LatLng getLatLongFromAddress(final Context context, final String locationAddress) {
        Double latitude = 0.0, longitude = 0.0;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        LatLng latLng = new LatLng(0.0, 0.0);
        try {
            List addressList = geocoder.getFromLocationName(locationAddress, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = (Address) addressList.get(0);
                StringBuilder sb = new StringBuilder();
                sb.append(address.getLatitude()).append("\n");
                sb.append(address.getLongitude()).append("\n");
                latitude = address.getLatitude();
                longitude = address.getLongitude();
                result = sb.toString();
                LatLng latLg = new LatLng(latitude, longitude);
                return latLg;
            } else {
                return null;
            }

        } catch (IOException e) {
            Log.e(TAG, "Unable to connect to Geocoder", e);
        }
        return latLng;
    }


    public static void getAddressFromLocation(final String locationAddress, final Context context, final Handler handler) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                Double latitude = 0.0, longitude = 0.0;
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List addressList = geocoder.getFromLocationName(locationAddress, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = (Address) addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        sb.append(address.getLatitude()).append("\n");
                        sb.append(address.getLongitude()).append("\n");
                        latitude = address.getLatitude();
                        longitude = address.getLongitude();
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable to connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Address: " + locationAddress +
                                "\n\nLatitude and Longitude :\n" + result;
                        bundle.putString("address", result);

                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Address: " + locationAddress +
                                "\n Unable to get Latitude and Longitude for this address location.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }

//    public static String getAddressFromLatLong(final double latitude, final double longitude, Context context) {
//        final StringBuilder result = new StringBuilder();
//        try {
//            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
//            if (addresses.size() > 0) {
//
//                Address address = addresses.get(0);
//
//                for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
//                    if (i == addresses.get(0).getMaxAddressLineIndex()) {
//                        result.append(addresses.get(0).getAddressLine(i));
//                    } else {
//                        result.append(addresses.get(0).getAddressLine(i) + ",");
//
//
//                    }
//                }
//
//             /*   addresssss = result.toString();
//                Log.e(TAG, "getaddress: "+ addresssss);
//                // binding.etLocationHome.setText(addresssss);
//                sessionFilter.setMplacename(addresssss);*/
//            }
//        } catch (Exception e) {
//
//        }
//        return result.toString();
//    }


    /***************************** DATE TIME FUNCTIONS *********************************************/

    public static String change_format(String timeeee, String input_frotmat, String output_fromat) {
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat(input_frotmat).parse(timeeee);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        // DateFormat df = new SimpleDateFormat(output_fromat);
        Locale localeSpanish = new Locale("es", "ES");
        SimpleDateFormat df = new SimpleDateFormat(output_fromat, localeSpanish);
        // TimeZone zone = TimeZone.getTimeZone("GMT+5:30");
        TimeZone zone = TimeZone.getDefault();
        df.setTimeZone(zone);
        return df.format(date1).replace(".","");
    }

    public static String datePlusMinus(String dates, int days) {
        try {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(dates);
            long datemili = date1.getTime();
            long newDate = new Date(datemili + days * 24 * 60 * 60 * 1000).getTime();
            Date date = new Date(newDate);
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
            String dateText = df2.format(date);
            return dateText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String dateaszone(String pattern, String timezone) {
        String datye = "";
        Date date = new Date();
        DateFormat df = new SimpleDateFormat(pattern);
        df.setTimeZone(TimeZone.getTimeZone(timezone));
        Log.e(TAG, "Date and time in place: " + df.format(date));
        return datye = df.format(date);
    }

    public static long getUTCDateTimeAslong() {

        Date dateTime1 = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm");
            format.setTimeZone(TimeZone.getTimeZone("IST"));
            Date date = new Date();
            SimpleDateFormat dateParser = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm");
            dateTime1 = null;
            try {
                dateTime1 = dateParser.parse(format.format(date));
            } catch (ParseException e) {
                Log.e("Error in Utility 84", "" + e);
                e.printStackTrace();
            }
        } catch (java.text.ParseException e) {
            Log.e("Error in Utility 88", "" + e);
            e.printStackTrace();
        }

        return dateTime1.getTime();
    }

    public static long getConvertUTCDateTimeAslong(Context context) {

        String bookingDateTime = getCurrentDate() + "T" + getCurrentTime();
        String strOrderBookingDate = Utility.utcDateTimeZone("yyyy-MM-dd'T'HH:mm", bookingDateTime, context, "yyyy-MM-dd'T'HH:mm");
        String date = strOrderBookingDate.split("T")[0];
        String timee = strOrderBookingDate.split("T")[1];
        String dd = date + " " + timee;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dateeeeee = null;
        try {
            dateeeeee = df.parse(dd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateeeeee.getTime();
    }


    public static String changeDateFormat(String strDate, String inputFormat, String outputFormat) {
        SimpleDateFormat df;
        df = new SimpleDateFormat(inputFormat);
        Date date = null;
        try {
            date = df.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        df = new SimpleDateFormat(outputFormat);
        return df.format(date);
    }


    public static String getCurrentDate() {
        return new SimpleDateFormat(FORMAT_YYYYMMdd).format(new Date());
    }

    public static String getCurrentDateFORMAT_YYYYMMddHHmm() {
        return new SimpleDateFormat(FORMAT_YYYYMMddHHmm).format(new Date());
    }

    public static String getCurrentDateFORMAT_YYYYMMddHHmmss() {
        return new SimpleDateFormat(FORMAT_YYYYMMddHHmmss).format(new Date());
    }

    public static String getCurrentDateFORMAT_Z() {
        return new SimpleDateFormat(FORMAT_Z).format(new Date());
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("HH:mm").format(new Date());
    }

    public static String getCurrentTimeBefore1Hour() {
        return new SimpleDateFormat("HH:mm").format(new Date().getTime() - 3600000);
    }


    public static String getCurrentDateTime(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    public static String serverFormat(String dddd) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(Date.parse(dddd));
        return date;
    }

    public static String appFormat(String dddd) {
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("yyyy-MM-dd").parse(dddd);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(date1);
        return date;
    }

    public static String days() {
        String correct_day = "";
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
        int intdays = calendar.get(Calendar.DAY_OF_WEEK);
        switch (intdays) {
            case 1:
                correct_day = "7";
                break;
            case 2:
                correct_day = "1";
                break;
            case 3:
                correct_day = "2";
                break;
            case 4:
                correct_day = "3";
                break;
            case 5:
                correct_day = "4";
                break;
            case 6:
                correct_day = "5";
                break;
            case 7:
                correct_day = "6";
                break;
        }

        return correct_day;

    }

    public static String getWeekdayFromDate(String datee) {
        String correct_day = "";
        Calendar calendar = Calendar.getInstance();
        String dobSplit[] = datee.split("-");
        calendar.set(Integer.parseInt(dobSplit[0]), Integer.parseInt(dobSplit[1]) - 1, Integer.parseInt(dobSplit[2]));
        Log.e(TAG, "days_from_date: " + Integer.parseInt(dobSplit[0]) + Integer.parseInt(dobSplit[1]) + Integer.parseInt(dobSplit[2]));
        int intdays = calendar.get(Calendar.DAY_OF_WEEK);
        switch (intdays) {
            case 1:
                correct_day = "7";
                break;
            case 2:
                correct_day = "1";
                break;
            case 3:
                correct_day = "2";
                break;
            case 4:
                correct_day = "3";
                break;
            case 5:
                correct_day = "4";
                break;
            case 6:
                correct_day = "5";
                break;
            case 7:
                correct_day = "6";
                break;
        }
        Log.e(TAG, "days_from_date: " + correct_day);
        return correct_day;

    }


    public static String getDay(String day, String month, String year) {
        String[] dates = new String[]{"SUNDAY", "MONDAY", "TUESDAY", //
                "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(year), //
                Integer.parseInt(month) - 1, // <-- add -1
                Integer.parseInt(day));
        int date_of_week = cal.get(Calendar.DAY_OF_WEEK);
        return dates[date_of_week - 1];
    }

    public static String getCorrectGmt(String dates, String times) {
        String utcTime = dates + " " + times + ":00";
        String timeinGmt[] = Utility.utcToGmt(utcTime).split(" ");
        return timeinGmt[1].substring(0, 5);
    }

    public static String gmtToUtc(String dddd) {
        String dtStart = dddd;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(dtStart);
            System.out.println(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Log.e(TAG, "timezone:date "+date);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String localtoUtc = df.format(date);
        Log.e(TAG, "time in GMT " + localtoUtc);
        return df.format(date);
    }

    public static String utcToGmt(String timeinUtc) {
        SimpleDateFormat oldFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        oldFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        String dueDateAsNormal = "";
        try {
            value = oldFormatter.parse(timeinUtc);
            SimpleDateFormat newFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            newFormatter.setTimeZone(TimeZone.getDefault());
            dueDateAsNormal = newFormatter.format(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "utcToGmt: " + timeinUtc + " : " + dueDateAsNormal);
        return dueDateAsNormal;
    }

    public static String getDateFromLong(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String compateTime(String startf, String endd) {
        Log.e(TAG, "compateTime() called with: startf = [" + startf + "], endd = [" + endd + "]");
        Date start = null;
        Date end = null;
        String fact = "";
        try {
            if (startf != null && !startf.isEmpty()) {
                start = new SimpleDateFormat("HH:mm").parse(startf);
            }

            if (endd != null && !endd.isEmpty()) {
                end = new SimpleDateFormat("HH:mm").parse(endd);
            }

            if (start.after(end)) {
                return fact = "after";
            }

            if (start.before(end)) {
                return fact = "before";
            }

            if (start.equals(end)) {
                return fact = "equal";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fact;
    }

    public static String compareTime(String startTime, String endTime) {
        Log.e(TAG, "compareTime() called with: startTime = [" + startTime + "], endTime = [" + endTime + "]");
        Date start = null;
        Date end = null;
        String fact = "";
        try {
            if (startTime != null && !startTime.isEmpty()) {
                start = new SimpleDateFormat("HH:mm").parse(startTime);
            }

            if (endTime != null && !endTime.isEmpty()) {
                end = new SimpleDateFormat("HH:mm").parse(endTime);
            }

            if (start.compareTo(end) > 0) {
                //Log.e(TAG,"Date1 is after Date2");
                fact = "past";
            } else if (start.compareTo(end) < 0) {
                //  Log.e(TAG,"Date1 is before Date2");
                fact = "future";
            } else if (start.compareTo(end) == 0) {
                //  Log.e(TAG,"Date1 is equal to Date2");
                fact = "equals";
            } else {
                Log.e(TAG, "How to get here?");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "compareTime: " + fact);
        return fact;
    }

    public static String utcDateTimeZone(String dateFormat, String datetime, Context mContext, String outputFormat) {
        String formattedDate = "";
        try {
            //String dateStr = "2001.07.04T12:08";
            SimpleDateFormat df = new SimpleDateFormat(dateFormat, Locale.getDefault());
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(datetime);
            df.setTimeZone(TimeZone.getDefault());
            if (date != null) {
                formattedDate = df.format(date);
                Log.e("TimeZone:- ", formattedDate);
                Date newDate = spf.parse(formattedDate);
                spf = new SimpleDateFormat(outputFormat, Locale.getDefault());
                if (newDate != null) {
                    formattedDate = spf.format(newDate);
                }
            } else {
                Utility.toast(mContext, "Date not found");
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            Log.e("Exception:- ", e.getMessage());
        }
        //String utcTime = formattedDate.split("T")[1];
        return formattedDate;
    }

//    public static String compareDates(String startdate, String enddate){
//        String type = "";
//        Date start = null;
//        Date end = null;
//        // start is a present date and end is tomorrow date
//        //  0 comes when two date are same,
//        //  1 comes when date1 is higher then date2
//        // -1 comes when date1 is lower then date2
//
//        try {
//            start = new SimpleDateFormat("yyyy-MM-dd").parse(startdate);
//            end = new SimpleDateFormat("yyyy-MM-dd").parse(enddate);
//            Log.e(TAG, "comparedates: >>> "+start.compareTo(end));
//
//            switch (start.compareTo(end)){
//                case 0:
//                    type = DATEEQUAL;
//                    break;
//                case 1:
//                    type = DATEAFTER;
//                    break;
//                case -1:
//                    type = DATEBEFORE;
//                    break;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return  type;
//    }


    public static String checktime(String startf, String endd) {
        Log.e(TAG, "checktime() called with: startf = [" + startf + "], endd = [" + endd + "]");
        Date start = null;
        Date end = null;
        String fact = "";
        try {
            if (startf != null && !startf.isEmpty()) {
                start = new SimpleDateFormat("HH:mm").parse(startf);
            }

            if (endd != null && !endd.isEmpty()) {
                end = new SimpleDateFormat("HH:mm").parse(endd);
            }

            if (start.after(end)) {
                return fact = "after";
            }

            if (start.before(end)) {
                return fact = "before";
            }

            if (start.equals(end)) {
                return fact = "equal";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fact;
    }

    public static String compareDates(String startdate, String enddate) {
        String type = "";
        Date start = null;
        Date end = null;
        // start is a present date and end is tomorrow date
        //  0 comes when two date are same,
        //  1 comes when date1 is higher then date2
        // -1 comes when date1 is lower then date2

        try {
            start = new SimpleDateFormat("yyyy-MM-dd").parse(startdate);
            end = new SimpleDateFormat("yyyy-MM-dd").parse(enddate);

            if (start.compareTo(end) > 0) {
                Log.e(TAG, "Date1 is after Date2");
                type = "past";
            } else if (start.compareTo(end) < 0) {
                Log.e(TAG, "Date1 is before Date2");
                type = "future";
            } else if (start.compareTo(end) == 0) {
                Log.e(TAG, "Date1 is equal to Date2");
                type = "equals";
            } else {
                Log.e(TAG, "How to get here?");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

/*
    public static LatLng getLatLongFromAddress(final Context context, final String locationAddress) {
        Double latitude = 0.0, longitude = 0.0;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        LatLng latLng = new LatLng(0.0, 0.0);
        try {
            List addressList = geocoder.getFromLocationName(locationAddress, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = (Address) addressList.get(0);
                StringBuilder sb = new StringBuilder();
                sb.append(address.getLatitude()).append("\n");
                sb.append(address.getLongitude()).append("\n");
                latitude = address.getLatitude();
                longitude = address.getLongitude();
                result = sb.toString();
                LatLng latLg = new LatLng(latitude, longitude);
                return latLg;
            } else {
                return null;
            }

        } catch (IOException e) {
            Log.e(TAG, "Unable to connect to Geocoder", e);
        }
        return latLng;
    }
*/

    public static Date getTimeFromString(String time) {
        Date date = null;
        Date end = null;
        String fact = "";
        try {
            date = new SimpleDateFormat("HH:mm:ss").parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getTimeFromString(String time, String inputFormat) {
        Date date = null;
        Date end = null;
        String fact = "";
        try {
            date = new SimpleDateFormat(inputFormat).parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getDateFromString(String datestr) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datestr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getDateFromString(String datestr, String inputFormat) {
        Date date = null;
        try {
            date = new SimpleDateFormat(inputFormat).parse(datestr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static void setTimePickerInterval(TimePicker timePicker) {
        final Calendar c = Calendar.getInstance();
        final int minute = c.get(Calendar.MINUTE);
        Log.e(TAG, "setTimePickerInterval: " + minute);

        try {
            int TIME_PICKER_INTERVAL = 5;
            NumberPicker minutePicker = (NumberPicker) timePicker.findViewById(Resources.getSystem().getIdentifier(
                    "minute", "id", "android"));
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            minutePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            int cbc = setval(minute);
            minutePicker.setValue(cbc);
            List<String> displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            minutePicker.setDisplayedValues(displayedValues.toArray(new String[0]));
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e);
        }
    }
    public static String getAddressFromLatLong(final double latitude, final double longitude,Context context) {
        final StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {

                Address address = addresses.get(0);

                for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                    if (i == addresses.get(0).getMaxAddressLineIndex()) {
                        result.append(addresses.get(0).getAddressLine(i));
                    } else {
                        result.append(addresses.get(0).getAddressLine(i) + ",");


                    }
                }


             /*   addresssss = result.toString();
                Log.e(TAG, "getaddress: "+ addresssss);
                // binding.etLocationHome.setText(addresssss);
                sessionFilter.setMplacename(addresssss);*/
            }
        } catch (Exception e) {

        }

        return String.valueOf(result.toString());
    }

    public static String getAddressFromLatLong(Context context, final double latitude, final double longitude) {
        final StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {

                Address address = addresses.get(0);

                for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                    if (i == addresses.get(0).getMaxAddressLineIndex()) {
                        result.append(addresses.get(0).getAddressLine(i));
                    } else {
                        result.append(addresses.get(0).getAddressLine(i) + ",");


                    }
                }


             /*   addresssss = result.toString();
                Log.e(TAG, "getaddress: "+ addresssss);
                // binding.etLocationHome.setText(addresssss);
                sessionFilter.setMplacename(addresssss);*/
            }
        } catch (Exception e) {

        }

        return String.valueOf(result.toString());
    }
    public static void dropDownVisibleAnimation(View layout, View dropDown) {
        if (layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.GONE);
            dropDown.animate().setDuration(150).rotationBy(180);
        } else {
            dropDown.animate().setDuration(150).rotationBy(-180);
            layout.setVisibility(View.VISIBLE);
        }
    }

    public static String getCountryFromLatLong(Context context, final double latitude, final double longitude) {
        final StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                /*for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                    if (i == addresses.get(0).getMaxAddressLineIndex()) {
                        result.append(addresses.get(0).getCountryName());
                    } else {
                        result.append(addresses.get(0).getAddressLine(i) + ",");
                    }
                }*/

                result.append(addresses.get(0).getCountryCode());

             /*   addresssss = result.toString();
                Log.e(TAG, "getaddress: "+ addresssss);
                // binding.etLocationHome.setText(addresssss);
                sessionFilter.setMplacename(addresssss);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    public static void hideKeyboardNew(AppCompatActivity activity) {
        setupParent(activity.getWindow().getDecorView().findViewById(android.R.id.content), activity);
    }

    //Hide Keyboard
    public static void setupParent(View view, Context context) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                Utility.hideKeyboard(context);
                //view.performClick();
                return false;
            });
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupParent(innerView, context);
            }
        }
    }

/*
    public static String add30MinutesInTime(String strOrderDateTime, int arrivingMinutes, int totalMin) {
        //arrivingMinutes = 30;
        //totalMin = 80;
        String diffType = "";
        try {
            String strCurrentDate = Utility.getCurrentDateTime("yyyy-MM-dd HH:mm");
            //String strCurrentDate = "20 Jan 2020, 06:05 PM";
            // strOrderDateTime = "20 Jan 2020, 07:30 PM";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date currentDate = format.parse(strCurrentDate);
            SimpleDateFormat orderDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date oldOrderDate = orderDateFormat.parse(strOrderDateTime);
            Date newOrderDate = getArrivingTime(oldOrderDate, arrivingMinutes);
            DateTime currentDateTime = new DateTime(currentDate), bookingDateTime = new DateTime(newOrderDate);
            Period p = new Period(currentDateTime, bookingDateTime);
            int hours = p.getHours();
            int minutes = p.getMinutes();
            int totalMinutes = (hours * 60) + minutes;
            if (totalMinutes == 0) {
                diffType = "hide";
            } else {
                if (totalMinutes > arrivingMinutes) {
                    diffType = "hide";
                } else {
                    diffType = "show";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return diffType;
    }
*/

/*
    public static String addMinutesInTime(String strOrderDateTime, int arrivingMinutes, int totalMin) {
        //arrivingMinutes = 30;
        //totalMin = 80;
        String diffType = "";
        try {
            String strCurrentDate = Utility.getCurrentDateTime("yyyy-MM-dd HH:mm");
            //String strCurrentDate = "20 Jan 2020, 06:05 PM";
            // strOrderDateTime = "20 Jan 2020, 07:30 PM";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date currentDate = format.parse(strCurrentDate);
            SimpleDateFormat orderDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date oldOrderDate = orderDateFormat.parse(strOrderDateTime);
            Date newOrderDate = getArrivingTime(oldOrderDate, arrivingMinutes);
            DateTime currentDateTime = new DateTime(currentDate), bookingDateTime = new DateTime(newOrderDate);
            Period p = new Period(currentDateTime, bookingDateTime);
            int hours = p.getHours();
            int minutes = p.getMinutes();
            int totalMinutes = (hours * 60) + minutes;
            if (totalMinutes < 0) {
                diffType = "show";
            } else if (totalMinutes <= totalMin) {
                diffType = "show";
            } else {
                diffType = "hide";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return diffType;
    }
*/


    private static Date getArrivingTime(Date wakingTime, int minutesToSleep) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(wakingTime);
        calendar.add(Calendar.HOUR, 0);
        calendar.add(Calendar.MINUTE, minutesToSleep);
        return calendar.getTime();
    }

    public static String numberFormate(long num) {
        if (num >= 10) {
            return "" + num;
        } else {
            return "0" + num;
        }
    }
/*
    public static ClockModel getTechLateTime(String strOrderDateTime, int arrivingMinutes) {
        ClockModel clockModel = new ClockModel();
        //arrivingMinutes = 1800;
        try {
            //String strCurrentDate = strOrderDateTime;
              String strCurrentDate = Utility.getCurrentDateTime("dd MMM yyyy, hh:mm:ss a");
           // String strCurrentDate = "26 Feb 2020, 06:27:00 PM";
            */
/*String strCurrentDate = "25 Jan 2020, 06:00 PM";*//*

           // strOrderDateTime = "26 Feb 2020, 06:27:00 PM";
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault());
            Date currentDate = format.parse(strCurrentDate);
            SimpleDateFormat orderDateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault());
            Date oldOrderDate = orderDateFormat.parse(strOrderDateTime);
            Date newOrderDate = getArrivingTime(oldOrderDate, arrivingMinutes);

            Log.e(TAG, "getWarrantyStartTime: oldOrderDate " + oldOrderDate);
            Log.e(TAG, "getWarrantyStartTime newOrderDate: " + newOrderDate);

            Log.e(TAG, "getWarrantyStartTime: oldOrderDate " + oldOrderDate.getTime());
            Log.e(TAG, "getWarrantyStartTime newOrderDate after add minutes: " + newOrderDate.getTime());

            long diff = currentDate.getTime() - newOrderDate.getTime();
            long diffSeconds = diff / 1000;
            long h = (diffSeconds / (60 * 60));
            long m = (diffSeconds / 60) % 60;
            long s = diffSeconds % 60;
            String timer = h + ":" + m + ":" + s;
            clockModel = new ClockModel(h, m, s, diff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clockModel;
    }
*/

/*
    public static ClockModel getWarrantyStartTime(String strOrderDateTime, int arrivingMinutes) {
        ClockModel clockModel = new ClockModel();
        //arrivingMinutes = 1800;
        try {
            //String strCurrentDate = strOrderDateTime;
            String strCurrentDate = Utility.getCurrentDateTime("dd MMM yyyy, hh:mm:ss a");
          //  String strCurrentDate = "26 Feb 2020, 06:27:00 PM";
        */
/*String strCurrentDate = "25 Jan 2020, 06:00 PM";*//*

       // strOrderDateTime = "26 Feb 2020, 06:27:00 PM";
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault());
            Date currentDate = format.parse(strCurrentDate);
            SimpleDateFormat orderDateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault());
            Date oldOrderDate = orderDateFormat.parse(strOrderDateTime);
            Date newOrderDate = getArrivingTime(oldOrderDate, arrivingMinutes);

            Log.e(TAG, "getWarrantyStartTime: oldOrderDate " + oldOrderDate);
            Log.e(TAG, "getWarrantyStartTime newOrderDate: " + newOrderDate);

            Log.e(TAG, "getWarrantyStartTime: oldOrderDate " + oldOrderDate.getTime());
            Log.e(TAG, "getWarrantyStartTime newOrderDate after add minutes: " + newOrderDate.getTime());

            long diff = newOrderDate.getTime() - currentDate.getTime();
            long diffSeconds = diff / 1000;
            long h = (diffSeconds / (60 * 60));
            long m = (diffSeconds / 60) % 60;
            long s = diffSeconds % 60;
            String timer = h + ":" + m + ":" + s;
            clockModel = new ClockModel(h, m, s, diff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clockModel;
    }
*/

    public static String utcDateTimeZone123(String dateFormat, String datetime, Context mContext, String outputFormat) {
        String formattedDate = "";
        try {
            //String dateStr = "2001.07.04T12:08";
            SimpleDateFormat df = new SimpleDateFormat(dateFormat, Locale.getDefault());
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(datetime);
            df.setTimeZone(TimeZone.getDefault());
            if (date != null) {
                formattedDate = df.format(date);
                Log.e("TimeZone:- ", formattedDate);
                Date newDate = spf.parse(formattedDate);
                spf = new SimpleDateFormat(outputFormat, Locale.getDefault());
                if (newDate != null) {
                    formattedDate = spf.format(newDate);
                }
            } else {
                Utility.toast(mContext, "Date not found");
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            Log.e("Exception:- ", e.getMessage());
        }
        //String utcTime = formattedDate.split("T")[1];
        return formattedDate;
    }

  /*  public static Timepoint[] generateTimepoints(int minutesInterval) {
        List<Timepoint> timepoints = new ArrayList<>();

        for (int minute = 0; minute <= 1440; minute += minutesInterval) {
            int currentHour = minute / 60;
            int currentMinute = minute - (currentHour > 0 ? (currentHour * 60) : 0);
            if (currentHour == 24)
                continue;
            timepoints.add(new Timepoint(currentHour, currentMinute));
        }
        return timepoints.toArray(new Timepoint[timepoints.size()]);
    }*/

     /* public static HashMap<String, Object> getModeltoMap(Object object) {
        Gson gson = new Gson();
        String temp = gson.toJson(object);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map = (HashMap<String, Object>) gson.fromJson(temp, map.getClass());
        return map;
    }*/

    /**************************************************************************************************
     ---------------------Broad cast reciever work -------------------------------------------==

     ------------------------------Intialization proccess
     private BroadcastReceiver broadcastReceiver;

     broadcastReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {

    }
    };

     @Override public void onPause() {
     super.onPause();
     LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);

     }

     @Override public void onResume() {
     super.onResume();
     LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter("checkout_update"));
     }
     ------------------------  senng process
     Intent itent = new Intent("checkout_update");
     itent.putExtra("abc", "update");
     LocalBroadcastManager.getInstance(mContext.getApplicationContext()).sendBroadcast(itent);
     ---------------------------------------------------------------------------------------------------------------------------
     ------- picaso ----------------------------------------
     while loading image in imageview do not set image view scale type swt it from icasoo
     Picasso.get()
     .load(url)
     .fit()
     .placeholder(R.drawable.no_image_available1)
     .centerCrop()
     .into(holder.binding.ivTop);

     ------------------------------------ gson list ---------------------------------------------------
     pointHistoryModelList = gson.fromJson(jsonObject.getJSONArray("data").toString(),
     new TypeToken<ArrayList<PointHistoryModel>>() {
     }.getType());


     ------------------------------------spinner work-------------------------------------------------
     using to stop spinner selection from selecting ******************************************
     private Boolean mIsSpinnerFirstCall = true;

     binding.toolbarimagebtn.spOrderStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
     @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

     if(!mIsSpinnerFirstCall) {

     }else {

     mIsSpinnerFirstCall = false;

     }

      *****************************************  END OF CLASS PLEASE ADD YOUR CUSTOMS FUNCTIONS BELOW ********************************************************************************/

}
