package com.pcamarounds.firebase;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Group;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pcamarounds.R;
import com.pcamarounds.activities.MessagesDetailActivity;
import com.pcamarounds.constants.AppConfig;
import com.pcamarounds.controller.SessionManager;
import com.pcamarounds.utils.AppProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import constant.StringContract;

import static com.pcamarounds.utils.Utility.getBitmapFromURL;
import static com.pcamarounds.utils.Utility.logLargeString;


/**
 * Created by Argalon-PC on 19-01-2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private Context mContext;
    private NotificationHelper helper;
    private JSONObject json;
    private int count=0;
    private String name,decs,avtar,type,gid,gowner;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        mContext = getApplicationContext();
        helper = new NotificationHelper(mContext);

        if (NotificationHelper.isAppIsInBackground(mContext)) {
            Log.e(TAG, "onMessageReceived  data  if background: " + remoteMessage.getData().toString());
            if (remoteMessage.getData().get("alert") != null) {
/*
                try {
                    JSONObject jsonObject = new JSONObject(remoteMessage.getData());
                    String datat = jsonObject.getString("message");
                    JSONObject jsonObjectmess = new JSONObject(datat);
                    Log.e(TAG, "onMessageReceived: >>>>>>>>   " + jsonObjectmess.getString("sender"));
                    String comeitId = jsonObjectmess.getString("sender");
                    helper.makeNotify(remoteMessage.getData().get("title"), remoteMessage.getData().get("alert"), remoteMessage.getData().get("sound"), comeitId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
*/
                try {
                    count++;
                    json = new JSONObject(remoteMessage.getData());
                    Log.d(TAG, "JSONObject: "+json.toString());
                    JSONObject messageData = new JSONObject(json.getString("message"));
                    BaseMessage baseMessage = CometChatHelper.processMessage(new JSONObject(remoteMessage.getData().get("message")));
                    //showNotifcation(messageData,baseMessage);
                    String senderid = messageData.getString("sender");
                    if (!senderid.equals(SessionManager.getCometId(mContext))) {
                        helper.makeNotifyBackground("clickAction", json.getString("title"), json.getString("alert"), messageData, baseMessage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                String booking_status = remoteMessage.getData().get("booking_status");
                String tech_id = remoteMessage.getData().get("tech_id");
                String job_id = remoteMessage.getData().get("job_id");
                String status = remoteMessage.getData().get("status");
                String user_id = remoteMessage.getData().get("user_id");
                String body = remoteMessage.getData().get("body");
                String sound = remoteMessage.getData().get("sound");
                String title = remoteMessage.getData().get("title");
                String message = remoteMessage.getData().get("message");
                String service_name = remoteMessage.getData().get("service_name");
                String clickAction = remoteMessage.getData().get("click_action");
                String bid_status = remoteMessage.getData().get("bid_status");
                String toggle = remoteMessage.getData().get("toggle");
                String image_url = remoteMessage.getData().get("image_url");
                helper.notifyFromBackground(image_url,toggle,booking_status, tech_id, job_id, user_id, body, title, message, service_name, clickAction, bid_status);
            }
        } else {
            Log.e(TAG, "onMessageReceived  data else forground: " + remoteMessage.getData().toString());
            if (remoteMessage.getData().get("alert") != null) {
               /* try {
                    JSONObject jsonObject = new JSONObject(remoteMessage.getData());
                    String datat = jsonObject.getString("message");
                    JSONObject jsonObjectmess = new JSONObject(datat);
                    Log.e(TAG, "onMessageReceived: >>>>>>>>   " + jsonObjectmess.getString("sender"));
                    String comeitId = jsonObjectmess.getString("sender");
                    helper.makeNotify(remoteMessage.getData().get("title"), remoteMessage.getData().get("alert"), remoteMessage.getData().get("sound"), comeitId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                try {
                    count++;
                    json = new JSONObject(remoteMessage.getData());
                    Log.d(TAG, "JSONObject: "+json.toString());
                    JSONObject messageData = new JSONObject(json.getString("message"));
                    BaseMessage baseMessage = CometChatHelper.processMessage(new JSONObject(remoteMessage.getData().get("message")));
                    String senderid = messageData.getString("sender");
                    if (!senderid.equals(SessionManager.getCometId(mContext))) {
                        helper.makeNotify("clickAction", json.getString("title"), json.getString("alert"), messageData, baseMessage);
                    }
                    } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                String booking_status = remoteMessage.getData().get("booking_status");
                String tech_id = remoteMessage.getData().get("tech_id");
                String job_id = remoteMessage.getData().get("job_id");
                String status = remoteMessage.getData().get("status");
                String user_id = remoteMessage.getData().get("user_id");
                String body = remoteMessage.getData().get("body");
                String sound = remoteMessage.getData().get("sound");
                String title = remoteMessage.getData().get("title");
                String message = remoteMessage.getData().get("message");
                String service_name = remoteMessage.getData().get("service_name");
                String clickAction = remoteMessage.getData().get("click_action");
                String bid_status = remoteMessage.getData().get("bid_status");
                String toggle = remoteMessage.getData().get("toggle");
                String image_url = remoteMessage.getData().get("image_url");
                helper.make_notifiocation(image_url,toggle,booking_status, tech_id, job_id, user_id, body, title, message, service_name, clickAction, bid_status);
            }
        }
    }
    private void showNotifcation(JSONObject messageData,BaseMessage baseMessage) {

        try {
            int m = (int) ((new Date().getTime()));
            String GROUP_ID = "group_id";
            Log.e(TAG, "showNotifcation: "+ messageData.getString("receiver"));




         //   getGroupInfo(messageData.getString("receiver"));
            PendingIntent pendingIntent;
          /*  Intent intent = new Intent(mContext, MessagesDetailActivity.class);
            intent.putExtra(StringContract.IntentStrings.NAME, name);
            intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, gowner);
            intent.putExtra(StringContract.IntentStrings.GUID, gid);
            intent.putExtra(StringContract.IntentStrings.AVATAR, avtar);
            intent.putExtra(StringContract.IntentStrings.DESCRIPTION, decs);
            intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_GROUP);
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), count, intent, 0);
*/

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"2")
                    .setSmallIcon(R.drawable.noti_pro_icon)
                    .setContentTitle(json.getString("title"))
                    .setContentText(json.getString("alert"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setLargeIcon(getBitmapFromURL(baseMessage.getSender().getAvatar()))
                    .setGroup(GROUP_ID)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(this,"2")
                    .setContentTitle("CometChat")
                    .setContentText(count+" messages")
                    .setSmallIcon(R.drawable.cc)
                    .setGroup(GROUP_ID)
                    .setGroupSummary(true);
                    //.setContentIntent(pendingIntent);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(baseMessage.getId(), builder.build());
            notificationManager.notify(0, summaryBuilder.build());


        } catch (Exception e) {
            e.printStackTrace();
        }

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


}