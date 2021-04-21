package com.pcamarounds.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.pcamarounds.constants.AppConstants;

public class SessionManager {

    public static final String MyPREFERENCES = "AppSession";
    private static SharedPreferences sharedpreferences;
    private static SharedPreferences.Editor editor;

    // clear sessions
    public static void clearSession(Context context) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }
    public static String getAuthenticationToken(Context context) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString(AppConstants.AUTHENTICATION, "");
    }

    public static void setAuthenticationToken(Context context, String token) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.putString(AppConstants.AUTHENTICATION, token);
        editor.apply();
    }
    public static String getAccessToken(Context context) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString(AppConstants.ACCESS_TOKEN, "");
    }

    public static void setAccessToken(Context context, String token) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.putString(AppConstants.ACCESS_TOKEN, token);
        editor.apply();
    }
    public static String getCometId(Context context) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString(AppConstants.COMET_ID, "");
    }

    public static void setCometId(Context context, String name) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.putString(AppConstants.COMET_ID, name);
        editor.apply();
    }

}
