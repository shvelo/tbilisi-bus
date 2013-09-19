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

import com.tbilisi.bus.data.BusStop;
import com.tbilisi.bus.data.DatabaseHelper;
import com.tbilisi.bus.data.UserPreference;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

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
        private Elements stops;
        private boolean clean = true;
        NotificationManager notificationManager;

        @Override
        protected Void doInBackground(Void... voids) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(instance)
                .setSmallIcon(R.drawable.refresh)
                .setContentTitle(getResources().getString(R.string.updating))
                .setContentText(getResources().getString(R.string.updating_info))
                .setProgress(0, 0, true)
                .setContentIntent(PendingIntent.getActivity(instance, 0,
                        new Intent(instance, MenuActivity.class), 0))
                .setOngoing(true);
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());

            db = new DatabaseHelper(mContext);
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(getAssets().open("stops.xml.md5")));
                final String checksum = reader.readLine();
                UserPreference saved_checksum = db.userPreferenceDao.queryForId("checksum");
                if(saved_checksum != null && saved_checksum.value.equals(checksum)) {
                    return null;
                }

                log("Parsing stops.xml");

                Document document = Jsoup.parse(getAssets().open("stops.xml"), "UTF-8", "");
                stops = document.select("Stops");

                log("Parsed stops.xml");

                db.busStopDao.callBatchTasks(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        String tagName; int id = -1; String name = ""; boolean hasBoard = false;
                        boolean hasData = false; double lat = 0.0; double lon = 0.0;
                        Pattern p = Pattern.compile(" - ");
                        for(Element stop : stops) {
                            log("Parsing record");
                            if(! stop.select("Type").first().text().equals("bus")) continue;
                            for(Element child: stop.children()) {
                                tagName = child.tagName();
                                if(tagName.equals("stopid")) {
                                    id = Integer.valueOf(child.text());
                                } else if(tagName.equals("name")) {
                                    name = child.text();
                                    name = p.split(name)[0];
                                } else if(tagName.equals("hasboard")) {
                                    hasBoard = Boolean.valueOf(child.text());
                                } else if(tagName.equals("virtual")) {
                                    hasData = Boolean.valueOf(child.text());
                                } else if(tagName.equals("lat")) {
                                    lat = Double.valueOf(child.text());
                                } else if(tagName.equals("lon")) {
                                    lon = Double.valueOf(child.text());
                                }
                            }
                            if(name.length() == 0 || id == -1) continue;
                            log("Inserting record");
                            db.busStopDao.create(new BusStop(id, name, hasBoard, hasData, lat, lon));
                        }
                        return null;
                    }
                });
                if(clean) {
                    log("Operation successful, saving checksum");
                    db.userPreferenceDao.createOrUpdate(new UserPreference("checksum", checksum));
                } else {
                    log("Operation failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                clean = false;
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
