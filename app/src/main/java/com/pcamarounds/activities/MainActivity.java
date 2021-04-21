package com.pcamarounds.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pcamarounds.retrofit.Environment;
import com.squareup.picasso.Picasso;
import com.pcamarounds.R;
import com.pcamarounds.constants.AppConfig;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.controller.SessionManager;
import com.pcamarounds.databinding.ActivityMainBinding;
import com.pcamarounds.firebase.NotificationHelper;
import com.pcamarounds.fragments.CamaroundFragment;
import com.pcamarounds.fragments.MenuFragment;
import com.pcamarounds.fragments.MessageFragment;
import com.pcamarounds.fragments.ProfileFragment;
import com.pcamarounds.fragments.SearchFragment;
import com.pcamarounds.utils.GPSTracker;
import com.pcamarounds.utils.PersmissionUtils;
import com.pcamarounds.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Context context;
    private ActivityMainBinding binding;
    private PersmissionUtils persmissionUtils;
    private Dialog dialog;
    private double curlat, curlon;
    private GPSTracker gps;
    private Controller controller;
    private NotificationHelper helper;
    private String flaglogin = "";
    private List<String> unreadCount = new ArrayList<>();
    private BadgeDrawable badgeDrawable;
    private BroadcastReceiver broadcastReceiver;
    private String toggle = "",body = "",message = "",image_url = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        context = this;
        helper  = new NotificationHelper(context);
        controller = (Controller) getApplicationContext();
        persmissionUtils = new PersmissionUtils(context, MainActivity.this);
        persmissionUtils.askPermissionAtStart();
        initView();
    }

    private void initView() {

        toggle = getIntent().getStringExtra(AppConstants.TOGGLE);
        body = getIntent().getStringExtra(AppConstants.BODY);
        message = getIntent().getStringExtra(AppConstants.MESSAGE);
        image_url = getIntent().getStringExtra(AppConstants.IMAGE_URL);

        if (Utility.isCheckEmptyOrNull(toggle) && toggle.equals("1"))
        {
            notification_popup(body,message,image_url);
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(AppConstants.UNREADCOUNT);
                badgeDrawable.setVisible(false);
                if (message != null && message.equals(AppConstants.FLAG)) {
                    getUnreadConversationCount();
                }
            }
        };

        badgeDrawable = binding.navigation.getOrCreateBadge(R.id.itemMessages);
        badgeDrawable.setBackgroundColor(context.getResources().getColor(R.color.color_primary));

        getIntentData();
        if (Utility.isCheckEmptyOrNull(SessionManager.getCometId(context))) {
            getUserCometChat();
        }
        helper.clearNotifications();
        Locale locale = new Locale("es");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setupButtombar();

        if (RealmController.getUser() != null) {
            FirebaseMessaging.getInstance().subscribeToTopic(Environment.getTopicId() + RealmController.getUser().getId() + "");
            Log.e(TAG, "initView: " + Environment.getTopicId() + RealmController.getUser().getId() + "" );
        }
        if (persmissionUtils.checkLocationPermissions()) {
            gps = new GPSTracker(context);
            curlat = gps.getLatitude();
            curlon = gps.getLongitude();
            String address = Utility.getAddressFromLatLong(context, curlat, curlon);
           // country = Utility.getCountryFromLatLong(context, lat, longi);
            controller.curlat = curlat;
            controller.curlon = curlon;
            controller.location = address;
            Log.e(TAG, "initView: " + curlat + "  " + curlon);
        }


        Intent intent = getIntent();
        if (intent != null)
        {
            String fragmentFrom = intent.getStringExtra(AppConstants.FRAGMENTFROM);
            if (Utility.isCheckEmptyOrNull(fragmentFrom) && fragmentFrom.equals("MenuFragment"))
            {
                setter(1);
                pushFragment(new SearchFragment());
            }
        }


    }
    private void getIntentData() {
        if (getIntent() != null) {
            flaglogin = getIntent().getStringExtra(AppConstants.FLAGLOGIN);
            if (Utility.isCheckEmptyOrNull(flaglogin))
            {
                setter(0);
            }
        }
    }

    private void getUserCometChat() {
        Log.e(TAG, "getUserCometChat: "+ SessionManager.getCometId(context) );
        CometChat.getUser(SessionManager.getCometId(context), new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.e(TAG, "onSuccess: subscribeToTopic " +AppConfig.AppDetails.APP_ID+"_"+ CometChatConstants.RECEIVER_TYPE_USER+"_"+user.getUid() );
                FirebaseMessaging.getInstance().subscribeToTopic( AppConfig.AppDetails.APP_ID+"_"+ CometChatConstants.RECEIVER_TYPE_USER+"_"+user.getUid());
                Log.e(TAG, "User details fetched for user: " + user.toString());
            }
            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "User details fetching failed with exception: " + e.getMessage());
            }
        });
    }
    private void notification_popup(String body, String message,String imageurl) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.notification_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView imageView = dialog.findViewById(R.id.ivv);
        TextView textView = dialog.findViewById(R.id.tvmsggggg);
        TextView desc = dialog.findViewById(R.id.tvmsgggggDes);
        textView.setText(body);
        desc.setText(message);
        if (Utility.isCheckEmptyOrNull(imageurl)) {
            imageView.setVisibility(View.VISIBLE);
            Picasso.get().load(AppConstants.NOTIFICATION_IMAGE_URL + imageurl).placeholder(R.color.colorShimmer).into(imageView);
        } else {
            imageView.setVisibility(View.GONE);
        }
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void setupButtombar() {
        pushFragment(new MenuFragment());
        binding.navigation.setItemIconTintList(null);
        setter(0);
        binding.navigation.setElevation(8);
        binding.navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.itemMenu:
                        pushFragment(new MenuFragment());
                        return true;
                    case R.id.itemSearch:
                        if (persmissionUtils.checkLocationPermissions()) {
                            gps = new GPSTracker(context);
                            curlat = gps.getLatitude();
                            curlon = gps.getLongitude();
                            String address = Utility.getAddressFromLatLong(context, curlat, curlon);
                            controller.curlat = curlat;
                            controller.curlon = curlon;
                            controller.location = address;
                            Log.e(TAG, "initView: " + curlat + "  " + curlon);
                        }
                        pushFragment(new SearchFragment());
                        return true;
                    case R.id.itemCamarounds:
                        if (RealmController.getUser() != null) {
                            pushFragment(new CamaroundFragment());
                        } else {
                            loginSignupPopup();
                        }

                        return true;
                    case R.id.itemMessages:
                        if (RealmController.getUser() != null) {
                            pushFragment(new MessageFragment());
                        } else {
                            loginSignupPopup();
                        }
                        return true;
                    case R.id.itemProfile:
                        if (RealmController.getUser() != null) {
                            pushFragment(new ProfileFragment());
                        } else {
                            loginSignupPopup();
                        }
                        return true;

                }
                return false;

            }
        });

    }

    private void setter(int i) {
        binding.navigation.getMenu().getItem(i).setChecked(true);
    }

    public void pushFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (fragmentTransaction != null) {
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.addToBackStack(fragment.getClass().toString());
                fragmentTransaction.commit();
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            Log.e(TAG, "onBackPressed: if " + count);
            super.onBackPressed();
        } else {
            Log.e(TAG, "onBackPressed: else " + count);
            back(getCurrentFragment());
        }
    }

    private void back(String ss) {
        Log.e(TAG, "back: " + ss);
        switch (ss) {
            case "MenuFragment":
                setter(0);
                break;
            case "SearchFragment":
                setter(1);
                break;
            case "CamaroundFragment":
                setter(2);
                break;
            case "MessageFragment":
                setter(3);
                break;
            case "ProfileFragment":
                setter(4);
                break;

        }
    }

    public String getCurrentFragment() {
        try {
            String ss = getSupportFragmentManager().findFragmentById(R.id.container).getClass().getSimpleName();
            if (ss.equals("")) {
                ss = "MenuFragment";
                if (ss != null) {
                    return getSupportFragmentManager().findFragmentById(R.id.container).getClass().getSimpleName();
                } else {
                    return "MenuFragment";
                }
            } else {
                return getSupportFragmentManager().findFragmentById(R.id.container).getClass().getSimpleName();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void loginSignupPopup() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.login_signuo_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(context, WebViewSignupActivity.class);
                intent.putExtra(AppConstants.FLAGLOGIN, AppConstants.GUEST);
                startActivity(intent);
               // finish();
            }
        });
        dialog.findViewById(R.id.btnAccess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra(AppConstants.FLAGLOGIN, AppConstants.GUEST);
                startActivity(intent);
               // finish();

            }
        });

        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
        if (dialog != null)
        {
            dialog.hide();
        }
        unreadCount.clear();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        onRequestPermissionsRes(requestCode, permissions, grantResults);
    }

    public void onRequestPermissionsRes(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

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
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
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

                        .setPositiveButton(context.getString(R.string.settings), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        } else {
            switch (requestCode) {
                case PersmissionUtils.REQUEST_LOCATION_PERMISSION:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        addConversationListener();    //Enable Listener when app starts

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter(AppConstants.BROADCAST));
        getUnreadConversationCount();    // To get unread conversations count
    }

    public void getUnreadConversationCount() {
        CometChat.getUnreadMessageCount(new CometChat.CallbackListener<HashMap<String, HashMap<String, Integer>>>() {
            @Override
            public void onSuccess(HashMap<String, HashMap<String, Integer>> stringHashMapHashMap) {

                //unreadCount.addAll(stringHashMapHashMap.get("user").keySet());    //Add users whose messages are unread.
                // unreadCount.addAll(stringHashMapHashMap.get("group").keySet());    //Add groups whose messages are unread.

                if (unreadCount.size() == 0) {
                    badgeDrawable.setVisible(false);
                } else {
                    badgeDrawable.setVisible(true);
                }
                if (unreadCount.size()!=0)
                    Log.e(TAG, "onSuccessssssssssssssss: " + unreadCount.size() );
                badgeDrawable.setNumber(unreadCount.size());  //add total count of users and groups whose messages are unread in BadgeDrawable
            }

            @Override
            public void onError(CometChatException e) {
                Log.e("onError: ", e.getMessage());     //Logs the error if the error occurs.
            }
        });
    }
    private void setUnreadCount(BaseMessage message) {

        if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
            if (!unreadCount.contains(message.getReceiverUid())) {
                unreadCount.add(message.getReceiverUid());
                setBadge();
            }
        } else {
            if (!unreadCount.contains(message.getSender().getUid())) {
                unreadCount.add(message.getSender().getUid());
                setBadge();
            }
        }
    }
    private void setBadge(){
        if (badgeDrawable.getNumber()==0){
            badgeDrawable.setVisible(false);
        }else {
            badgeDrawable.setVisible(true);
            badgeDrawable.setNumber(badgeDrawable.getNumber() + 1);
        }
    }
    public void addConversationListener() {
        CometChat.addMessageListener(TAG, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage message) {
                Log.e(TAG, "onTextMessageReceived: " + message );
                setUnreadCount(message);

            }

            @Override
            public void onMediaMessageReceived(MediaMessage message) {
                Log.e(TAG, "onMediaMessageReceived: " + message );
                setUnreadCount(message);
            }

            @Override
            public void onCustomMessageReceived(CustomMessage message) {
                Log.e(TAG, "onCustomMessageReceived: " + message );
                setUnreadCount(message);
            }
        });
    }
}
