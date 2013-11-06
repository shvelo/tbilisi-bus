package com.tbilisi.bus;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.tbilisi.bus.data.DatabaseHelper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public class A extends Application implements Thread.UncaughtExceptionHandler {
    public static Context mContext;
    public static A instance;
    public static Camera camera;
    public static Typeface typeface;
    public static DatabaseHelper db;
    public static boolean dbLoaded = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this);
        instance = this;
        mContext = getApplicationContext();
        typeface = Typeface.createFromAsset(getAssets(), "DejaVuSans.ttf");

        new BaseLoader().execute();
    }

    public static void log(Object string){
        Log.i("BUS",String.valueOf(string));
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

    private class BaseLoader extends AsyncTask<Void,Integer,Void> {
        NotificationManager notificationManager;

        @Override
        protected Void doInBackground(Void... voids) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            db = new DatabaseHelper(mContext);
            try {
                if(db.busStopDao.countOf() > 0) return null;

                NotificationCompat.Builder builder = new NotificationCompat.Builder(instance)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getResources().getString(R.string.updating))
                        .setContentText(getResources().getString(R.string.updating_info))
                        .setProgress(0, 0, true)
                        .setContentIntent(PendingIntent.getActivity(instance, 0,
                                new Intent(instance, MenuActivity.class), 0))
                        .setOngoing(true);
                notificationManager.notify(0, builder.build());

                db.busStopDao.callBatchTasks(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        BufferedReader reader = new BufferedReader(
                            new InputStreamReader(getAssets().open("db.sql")));
                        String line = reader.readLine();
                        while (line != null) {
                            db.busStopDao.executeRawNoArgs(line);
                            line = reader.readLine();
                        }
                        reader.close();
                        return null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            dbLoaded = true;
            MenuActivity.instance.enableItems();
            notificationManager.cancel(0);
        }
    }
}
