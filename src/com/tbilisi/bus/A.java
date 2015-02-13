package com.tbilisi.bus;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.tbilisi.bus.data.DatabaseHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public class A extends Application {
    public static DatabaseHelper db;
    public static boolean dbLoaded = false;

    @Override
    public void onCreate() {
        super.onCreate();

        new BaseLoader().execute();
    }

    private class BaseLoader extends AsyncTask<Void,Integer,Void> {
        NotificationManager notificationManager;

        @Override
        protected void onPreExecute() {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            db = new DatabaseHelper(A.this);

            try {
                if(db.busStopDao.countOf() > 0) return;
            } catch (Exception e) {
                e.printStackTrace();
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(A.this)
                    .setSmallIcon(R.drawable.refresh)
                    .setContentTitle(getResources().getString(R.string.updating))
                    .setContentText(getResources().getString(R.string.updating_info))
                    .setProgress(0, 0, true)
                    .setContentIntent(PendingIntent.getActivity(A.this, 0,
                            new Intent(A.this, MainActivity.class), 0))
                    .setOngoing(true);
            notificationManager.notify(0, builder.build());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if(db.busStopDao.countOf() > 0) return null;

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
            MainActivity.instance.populateMap();
            notificationManager.cancel(0);
        }
    }
}
