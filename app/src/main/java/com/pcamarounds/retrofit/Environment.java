package com.pcamarounds.retrofit;

import com.pcamarounds.BuildConfig;

public class Environment {
    /**
     * change the environment of the application
     * for Development it is false
     * for Staging it is true
     */
    public static boolean environment = false;

    public static String getBaseUrl() {
        if (environment) {
            return "https://app.camarounds.com/";
        } else {
             return "http://192.168.101.225:3005/";
//            return "https://dev.camarounds.com/";
        }
    }
    public static String getTopicId() {
        if (environment) {
            return "CamaroundP_";
        } else {
            return "CamaroundPDev_";
        }
    }
    public static String getCometttttId() {
        if (environment) {
            return "camaround";
        } else {
            return "camarounddev";
        }
    }
    public static String getVerisonName() {
        if (environment) {
            return "App Version : S :- " + BuildConfig.VERSION_NAME;
        } else {
            return "App Version : D :- " + BuildConfig.VERSION_NAME;
        }
    }
}

