package com.pcamarounds.controller;

import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.google.gson.Gson;
import com.pcamarounds.constants.AppConfig;

import listeners.CometChatCallListener;

public class Controller extends MultiDexApplication {
    private static final String TAG = "Controller";
    private static Gson gson;
    public static int count;
    public double curlat, curlon;
    public String location = "";
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        RealmController.InitRealm(this);
        AppSettings appSettings = new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(AppConfig.AppDetails.REGION).build();
        CometChat.init(this, AppConfig.AppDetails.APP_ID, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "onSuccess: "+s);
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: "+e.getMessage());
            }
        });
        CometChatCallListener.addCallListener(TAG,this);

    }

    public static Gson getGson() {
        gson = new Gson();
        return gson;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        CometChatCallListener.removeCallListener(TAG);
    }

}
