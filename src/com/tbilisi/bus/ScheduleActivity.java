package com.tbilisi.bus;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tbilisi.bus.data.BusInfo;
import com.tbilisi.bus.data.HistoryItem;
import com.tbilisi.bus.util.BusListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import io.realm.Realm;

public class ScheduleActivity extends ActionBarActivity {
    public ArrayList<BusInfo> busList;
    private String stopId;
    private BusListAdapter adapter;
    public static final String STOP_ID_KEY = "stopId";
    private static final String API =
            "http://transit.ttc.com.ge/pts-portal-services/servlet/stopArrivalTimesServlet?stopId=";
    private String url;
    private final int delay = 60000; //Auto-update interval, seconds x 1000
    private Handler handler;
    private AutoUpdater autoUpdater;
    private AdView ad;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        if (getIntent().getExtras() == null){
            finish();
        }
        stopId = getIntent().getExtras().getString(STOP_ID_KEY);
        if (stopId == null){
            finish();
        }

        realm = Realm.getInstance(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ListView listView = (ListView) findViewById(R.id.busSchedule);

        loadList();

        adapter = new BusListAdapter(this, busList);

        listView.setAdapter(adapter);

        try {
            if(realm.where(HistoryItem.class).equalTo("id", Integer.parseInt(stopId)).count() == 0) {
                realm.beginTransaction();
                realm.createObject(HistoryItem.class).setId(Integer.parseInt(stopId));
                realm.commitTransaction();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        handler = new Handler();
        autoUpdater = new AutoUpdater();

        ad = (AdView)findViewById(R.id.ad_schedule);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("F405EEB4E7BFB13CFC1CD35E3688395F")
                .build();

        ad.loadAd(adRequest);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(ad != null) ad.resume();

        handler.postDelayed(autoUpdater, delay);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(ad != null) ad.pause();

        handler.removeCallbacks(autoUpdater);
    }

    @Override
    public void onDestroy() {
        if (ad != null) {
            ad.destroy();
        }
        super.onDestroy();
    }

    public void loadList() {
        if(busList == null) busList = new ArrayList<BusInfo>();
        url = API + stopId;
        new ListLoader().execute();
    }

    private class AutoUpdater implements Runnable {
        public void run(){
            reload();
            handler.postDelayed(this, delay);
        }
    }

    private class ListLoader extends AsyncTask<Void,Integer,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Document doc;
            ArrayList<BusInfo> newBusList = new ArrayList<BusInfo>();
            try {
                doc = Jsoup.connect(url).get();
                Elements elements = doc.select(".arrivalTimesScrol tr");
                for (Element element : elements) {
                    int i = 0;

                    int busNumber = 0;
                    String busDestination = "";
                    int busArrival = 0;

                    for(Element td : element.children()) {
                        String text = td.text();
                        if(i == 0) {
                            busNumber = Integer.valueOf(text);
                        } else if(i == 1) {
                            busDestination = text;
                        } else {
                            busArrival = Integer.valueOf(text);
                        }
                        i++;
                    }

                    newBusList.add(new BusInfo(busNumber,busDestination,busArrival));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(newBusList.size() > 0) {
                busList = newBusList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter.update(busList);
            if(busList.size() == 0) {
                findViewById(R.id.nothing_found).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.nothing_found).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload:
                reload();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void reload() {
        loadList();
        adapter.update(busList);
    }
}
