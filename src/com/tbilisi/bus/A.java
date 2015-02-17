package com.tbilisi.bus;

import android.app.Application;

import com.tbilisi.bus.data.BusStop;

import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;

public class A extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        loadRealm();
    }

    private void loadRealm() {
        Realm realm = Realm.getInstance(this);
        if(realm.where(BusStop.class).count() == 0) {
            try {
                InputStream is = getAssets().open("db.json");
                realm.beginTransaction();
                realm.createAllFromJson(BusStop.class, is);
                realm.commitTransaction();
            } catch (IOException e) {
                realm.cancelTransaction();
            }
        }
    }
}
