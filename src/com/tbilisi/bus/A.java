package com.tbilisi.bus;

import android.app.Application;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

public class A extends Application implements Thread.UncaughtExceptionHandler {
    private static Context mContext;
    public static A instance;
    public static Camera camera;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mContext = getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static Context getContext() {
        return mContext;
    }

    public static void log(Object string){
        Log.d("BUS",String.valueOf(string));
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if(camera != null) {
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
        throwable.printStackTrace();
        System.exit(1);
    }
}
