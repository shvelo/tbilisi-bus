package com.tbilisi.bus;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.tbilisi.bus.data.BusInfo;
import com.tbilisi.bus.data.BusStop;
import com.tbilisi.bus.data.HistoryItem;
import com.tbilisi.bus.util.BusListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class ScheduleActivity extends ActionBarActivity {
    public ArrayList<BusInfo> busList;
    private ListView listView;
    private String stopId;
    private BusListAdapter adapter;
    public static final String STOP_ID_KEY = "stopId";
    private static final String API =
            "http://transit.ttc.com.ge/pts-portal-services/servlet/stopArrivalTimesServlet?stopId=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        if (getIntent().getExtras() == null){
            A.log("getIntent().getExtras() == null");
            return;
        }
        stopId = getIntent().getExtras().getString(STOP_ID_KEY);
        if (stopId == null){
            A.log("stopId == null");
            return;
        }
        listView = (ListView) findViewById(R.id.busSchedule);

        loadList();

        adapter = new BusListAdapter(this, busList);

        listView.setAdapter(adapter);

        if(A.dbLoaded) {
            try {
                BusStop busStop = A.db.busStopDao.queryForId(Integer.valueOf(stopId));
                if(busStop != null) {
                    setTitle(busStop.name);
                    A.db.historyItemDao.createIfNotExists(new HistoryItem(Integer.valueOf(stopId)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadList() {
        busList = new ArrayList<BusInfo>();
        String url = API + stopId;
        Document doc;
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

                busList.add(new BusInfo(busNumber,busDestination,busArrival));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(busList.size() == 0)
            busList.add(new BusInfo(0, getResources().getString(R.string.nothing_found), 0));
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
