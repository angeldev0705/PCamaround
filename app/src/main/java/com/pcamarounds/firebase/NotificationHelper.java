package com.pcamarounds.firebase;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Group;
import com.pcamarounds.R;
import com.pcamarounds.activities.MainActivity;
import com.pcamarounds.activities.MessagesDetailActivity;
import com.pcamarounds.activities.SearchDetailActivity;
import com.pcamarounds.activities.SearchDetailsCamaroundActivity;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import constant.StringContract;

import static com.pcamarounds.utils.Utility.getBitmapFromURL;
import static org.webrtc.ContextUtils.getApplicationContext;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";

    public static final String PRIMARY_CHANNEL = "DSIRRES";
    private Context context;
    private NotificationManagerCompat managerCompat;
    private NotificationCompat.Builder mBuilder;
    private Intent intent;
    private Uri defaultSoundUri;
    private String name,decs,avtar,type,gid,gowner;
    public NotificationHelper(Context context) {
        this.context = context;

        createNotificationChannel();

    }

    private void createNotificationChannel() {
        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            CharSequence name = "Bid";
            String description = "Job Bid";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.CYAN);
            channel.setShowBadge(true);
            channel.setSound(defaultSoundUri, attributes);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            mBuilder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL);
        } else {
            mBuilder = new NotificationCompat.Builder(context);
        }
    }

    public void make_notifiocation(String image_url, String toggle, String booking_status, String tech_id, String job_id, String user_id, String body, String title, String message, String service_name, String clickAction, String bid_status) {
        playNotificationSound();
        int res = Utility.getRandomId();
        Log.e(TAG, "make_notifiocation: " + booking_status + "  " + job_id);

        Locale locale = new Locale("es");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        PendingIntent pendingIntent;
        if (Utility.isCheckEmptyOrNull(clickAction) && clickAction.equals("SearchDetailsCamaroundActivity")) {
            Intent intent2 = new Intent(AppConstants.BROADCAST);
            intent2.putExtra(AppConstants.KEY, AppConstants.NOTIFICATION);
            intent2.putExtra(AppConstants.JOB_ID, job_id);
            intent2.putExtra(AppConstants.USER_ID, tech_id);
            intent2.putExtra(AppConstants.BID_STATUS, bid_status);
            intent2.putExtra(AppConstants.SERVICE_NAME, service_name);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);

            intent = new Intent(context, SearchDetailsCamaroundActivity.class);
            intent.putExtra(AppConstants.JOB_ID, job_id);
            intent.putExtra(AppConstants.USER_ID, user_id);
            intent.putExtra(AppConstants.BID_STATUS, bid_status);
            intent.putExtra(AppConstants.SERVICE_NAME, service_name);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(context, res, intent, 0);
        } else if (Utility.isCheckEmptyOrNull(clickAction) && clickAction.equals("SearchDetailActivity")) {

            Intent intent2 = new Intent(AppConstants.BROADCAST);
            intent2.putExtra(AppConstants.KEY, AppConstants.NOTIFICATION);
            intent2.putExtra(AppConstants.JOB_ID, job_id);
            intent2.putExtra(AppConstants.USER_ID, tech_id);
            intent2.putExtra(AppConstants.BID_STATUS, bid_status);
            intent2.putExtra(AppConstants.SERVICE_NAME, service_name);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);

            intent = new Intent(context, SearchDetailActivity.class);
            intent.putExtra(AppConstants.JOB_ID, job_id);
            intent.putExtra(AppConstants.USER_ID, user_id);
            intent.putExtra(AppConstants.BID_STATUS, bid_status);
            intent.putExtra(AppConstants.SERVICE_NAME, service_name);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(context, res, intent, 0);
        } else if (Utility.isCheckEmptyOrNull(clickAction) && clickAction.equals("MainActivity")) {
            intent = new Intent(context, MainActivity.class);
            intent.putExtra(AppConstants.JOB_ID, job_id);
            intent.putExtra(AppConstants.USER_ID, user_id);
            intent.putExtra(AppConstants.BID_STATUS, bid_status);
            intent.putExtra(AppConstants.SERVICE_NAME, service_name);
            intent.putExtra(AppConstants.TOGGLE, toggle);
            intent.putExtra(AppConstants.BODY, body);
            intent.putExtra(AppConstants.MESSAGE, message);
            intent.putExtra(AppConstants.IMAGE_URL, image_url);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(context, res, intent, 0);
        } else {
            intent = new Intent(context, MainActivity.class);
            intent.putExtra(AppConstants.JOB_ID, job_id);
            intent.putExtra(AppConstants.USER_ID, user_id);
            intent.putExtra(AppConstants.BID_STATUS, bid_status);
            intent.putExtra(AppConstants.SERVICE_NAME, service_name);
            intent.putExtra(AppConstants.TOGGLE, toggle);
            intent.putExtra(AppConstants.BODY, body);
            intent.putExtra(AppConstants.MESSAGE, message);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(context, res, intent, 0);
        }

        if (Utility.isCheckEmptyOrNull(image_url)) {
            mBuilder.setContentTitle(body)
                    //.setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getBitmapFromURL(AppConstants.NOTIFICATION_IMAGE_URL + image_url)))
                    .setNumber(1)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.drawable.noti_pro_icon)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(context, R.color.color_primary))
                    .setContentIntent(pendingIntent);
            getManager().notify(res, mBuilder.build());
        } else {
            mBuilder.setContentTitle(body)
                    //.setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setNumber(1)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.drawable.noti_pro_icon)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(context, R.color.color_primary))
                    .setContentIntent(pendingIntent);
            getManager().notify(res, mBuilder.build());
        }
    }

    public void notifyFromBackground(String image_url, String toggle, String booking_status, String tech_id, String job_id, String user_id, String body, String title, String message, String service_name, String clickAction, String bid_status) {
        playNotificationSound();
        int res = Utility.getRandomId();
        Log.e(TAG, "notifyFromBackground: " + booking_status + "  " + job_id);
        Locale locale = new Locale("es");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        PendingIntent pendingIntent;
        if (Utility.isCheckEmptyOrNull(clickAction) && clickAction.equals("SearchDetailsCamaroundActivity")) {

            Intent intent2 = new Intent(AppConstants.BROADCAST);
            intent2.putExtra(AppConstants.KEY, AppConstants.NOTIFICATION);
            intent2.putExtra(AppConstants.JOB_ID, job_id);
            intent2.putExtra(AppConstants.USER_ID, tech_id);
            intent2.putExtra(AppConstants.BID_STATUS, bid_status);
            intent2.putExtra(AppConstants.SERVICE_NAME, service_name);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
            intent = new Intent(clickAction);
            intent.putExtra(AppConstants.JOB_ID, job_id);
            intent.putExtra(AppConstants.USER_ID, user_id);
            intent.putExtra(AppConstants.BID_STATUS, bid_status);
            intent.putExtra(AppConstants.SERVICE_NAME, service_name);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(context, res, intent, 0);
        } else if (Utility.isCheckEmptyOrNull(clickAction) && clickAction.equals("SearchDetailActivity")) {

            Intent intent2 = new Intent(AppConstants.BROADCAST);
            intent2.putExtra(AppConstants.KEY, AppConstants.NOTIFICATION);
            intent2.putExtra(AppConstants.JOB_ID, job_id);
            intent2.putExtra(AppConstants.USER_ID, tech_id);
            intent2.putExtra(AppConstants.BID_STATUS, bid_status);
            intent2.putExtra(AppConstants.SERVICE_NAME, service_name);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);

            intent = new Intent(clickAction);
            intent.putExtra(AppConstants.JOB_ID, job_id);
            intent.putExtra(AppConstants.USER_ID, user_id);
            intent.putExtra(AppConstants.BID_STATUS, bid_status);
            intent.putExtra(AppConstants.SERVICE_NAME, service_name);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(context, res, intent, 0);
        } else if (Utility.isCheckEmptyOrNull(clickAction) && clickAction.equals("MainActivity")) {
            intent = new Intent(clickAction);
            intent.putExtra(AppConstants.JOB_ID, job_id);
            intent.putExtra(AppConstants.USER_ID, user_id);
            intent.putExtra(AppConstants.BID_STATUS, bid_status);
            intent.putExtra(AppConstants.SERVICE_NAME, service_name);
            intent.putExtra(AppConstants.TOGGLE, toggle);
            intent.putExtra(AppConstants.BODY, body);
            intent.putExtra(AppConstants.MESSAGE, message);
            intent.putExtra(AppConstants.IMAGE_URL, image_url);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(context, res, intent, 0);
        } else {
            intent = new Intent(clickAction);
            intent.putExtra(AppConstants.JOB_ID, job_id);
            intent.putExtra(AppConstants.USER_ID, user_id);
            intent.putExtra(AppConstants.BID_STATUS, bid_status);
            intent.putExtra(AppConstants.SERVICE_NAME, service_name);
            intent.putExtra(AppConstants.TOGGLE, toggle);
            intent.putExtra(AppConstants.BODY, body);
            intent.putExtra(AppConstants.MESSAGE, message);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(context, res, intent, 0);
        }
        if (Utility.isCheckEmptyOrNull(image_url)) {
            mBuilder.setContentTitle(body)
                    //.setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getBitmapFromURL(AppConstants.NOTIFICATION_IMAGE_URL + image_url)))
                    .setNumber(1)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.drawable.noti_pro_icon)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(context, R.color.color_primary))
                    .setContentIntent(pendingIntent);
            getManager().notify(res, mBuilder.build());
        }else {
            mBuilder.setContentTitle(body)
                    //.setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setNumber(1)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.drawable.noti_pro_icon)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(context, R.color.color_primary))
                    .setContentIntent(pendingIntent);
            getManager().notify(res, mBuilder.build());
        }
    }

    public void makeNotify(String clickAction,String title,String mag,JSONObject messageData, BaseMessage baseMessage) throws JSONException {
        playNotificationSound();
        getGroupInfo(messageData.getString("receiver"));
        int res = Utility.getRandomId();
        intent = new Intent(context, MessagesDetailActivity.class);
        intent.putExtra(StringContract.IntentStrings.NAME, name);
        intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, gowner);
        intent.putExtra(StringContract.IntentStrings.GUID, messageData.getString("receiver"));
        intent.putExtra(StringContract.IntentStrings.AVATAR, avtar);
        intent.putExtra(StringContract.IntentStrings.DESCRIPTION, decs);
        intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_GROUP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, res, intent, 0);
        mBuilder
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mag))
                .setNumber(1)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_foreground))
                .setSmallIcon(R.drawable.noti_pro_icon)
                .setAutoCancel(true)
                .setLights(Color.CYAN, 3000, 3000)
                .setSound(defaultSoundUri)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                .setContentIntent(pendingIntent);
        getManager().notify(res, mBuilder.build());

    }

    public void makeNotifyBackground(String clickAction,String title,String mag,JSONObject messageData, BaseMessage baseMessage) throws JSONException {
        playNotificationSound();
        getGroupInfo(messageData.getString("receiver"));
        int res = Utility.getRandomId();
        intent = new Intent(clickAction);
        intent.putExtra(StringContract.IntentStrings.NAME, name);
        intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, gowner);
        intent.putExtra(StringContract.IntentStrings.GUID, messageData.getString("receiver"));
        intent.putExtra(StringContract.IntentStrings.AVATAR, avtar);
        intent.putExtra(StringContract.IntentStrings.DESCRIPTION, decs);
        intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_GROUP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, res, intent, 0);
        mBuilder
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mag))
                .setNumber(1)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_foreground))
                .setSmallIcon(R.drawable.noti_pro_icon)
                .setAutoCancel(true)
                .setLights(Color.CYAN, 3000, 3000)
                .setSound(defaultSoundUri)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                .setContentIntent(pendingIntent);
        getManager().notify(res, mBuilder.build());

    }

    private void getGroupInfo(String GUID) {
        CometChat.getGroup(GUID, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                Log.e(TAG, "Group details fetched successfully: " + group.toString());
                name = group.getName();
                gowner = group.getOwner();
                gid = group.getGuid();
                avtar = group.getIcon();
                decs = group.getDescription();
                type = CometChatConstants.RECEIVER_TYPE_GROUP;

            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "Group details fetching failed with exception: " + e.getMessage());
            }
        });
    }

    private NotificationManagerCompat getManager() {
        if (managerCompat == null) {
            managerCompat = NotificationManagerCompat.from(context);
        }
        return managerCompat;
    }

    public void clearNotifications() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
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

    public void playNotificationSound() {
        try {
            Ringtone r = RingtoneManager.getRingtone(context, defaultSoundUri);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
