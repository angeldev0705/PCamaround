package com.pcamarounds.controller;

import android.content.Context;

import com.pcamarounds.R;
import com.pcamarounds.models.LoginModel;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmModel;

public class RealmController {

    private static final String TAG = "RealmController";
    public static RealmController realmController;
    public static Realm realm;

    // constructor
    public RealmController() {
        realm = Realm.getDefaultInstance();
    }

    // initialize in mains
    public static void InitRealm(Context context) {
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(context.getResources().getString(R.string.app_name))
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public static LoginModel getUser() {
        if (realmController == null) {
            realmController = new RealmController();
        }
        return realm.where(LoginModel.class).findFirst();
    }

    public static void setUser(LoginModel user) {
        if (realmController == null) {
            realmController = new RealmController();
        }
        realmController.getRealm().beginTransaction();
        realmController.getRealm().copyToRealmOrUpdate(user);
        realmController.getRealm().commitTransaction();
    }

    public static void closeRealm() {
        if (realmController == null) {
            realmController = new RealmController();
        }
        realm.close();
    }

    public static void clearDatabase() {
        if (realmController == null) {
            realmController = new RealmController();
        }
        realm.executeTransaction(realm -> realm.deleteAll());
    }

    public static void insertOrUpdate(RealmModel object) {
        realm.executeTransaction(realm -> realm.insertOrUpdate(object));
    }

    public static void insertOrUpdate(RealmList object) {
        realm.executeTransaction(realm -> realm.insertOrUpdate(object));
    }

    /****************** YOUR METHODS *************************************************************/
    public static RealmController realmControllerInIt() {
        if (realmController == null) {
            realmController = new RealmController();
        }
        return realmController;
    }

    public Realm getRealm() {
        return realm;
    }

    public static void copyToRealmOrUpdate(RealmModel object) {
        realm.executeTransaction(realm -> realm.copyToRealmOrUpdate(object));
    }

    public void copyToRealmOrUpdate(RealmList object) {
        realm.executeTransaction(realm -> realm.copyToRealmOrUpdate(object));
    }

}
