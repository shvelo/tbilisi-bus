package com.tbilisi.bus;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class A extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

    public static void log(Object string){
        Log.d("BUS",String.valueOf(string));
    }
}
