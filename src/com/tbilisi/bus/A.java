package com.tbilisi.bus;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class A extends Application implements Thread.UncaughtExceptionHandler {
    public static Context mContext;
    public static A instance;
    public static Camera camera;
    public static Typeface typeface;
    public static DatabaseHelper db;

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

    private class BaseLoader extends AsyncTask<Void,Integer,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            db = new DatabaseHelper(mContext);
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(getAssets().open("stops.xml.md5")));
                String checksum = reader.readLine();
                UserPreference saved_checksum = db.userPreferenceDao.queryForId("checksum");
                if(saved_checksum != null && saved_checksum.value.equals(checksum)) {
                    return null;
                }

                Document document = Jsoup.parse(getAssets().open("stops.xml"), "UTF-8", "");
                Elements stops = document.select("Stops");
                for(Element stop : stops) {
                    int id = Integer.valueOf(stop.select("StopId").text());
                    String name = stop.select("Name").text();
                    if(name.length() == 0) continue;
                    boolean hasBoard = Boolean.valueOf(stop.select("HasBoard").text());
                    boolean hasData = Boolean.valueOf(stop.select("Virtual").text());
                    double lat = Double.valueOf(stop.select("Lat").text());
                    double lon = Double.valueOf(stop.select("Lon").text());
                    db.busStopDao.create(new BusStop(id, name, hasBoard, hasData, lat, lon));
                }
                db.userPreferenceDao.createOrUpdate(new UserPreference("checksum", checksum));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            MenuActivity.instance.enableItems();
        }
    }
}
