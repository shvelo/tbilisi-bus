package com.tbilisi.bus;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class ScheduleActivity extends Activity{
    public ArrayList<BusInfo> busList;
    public ListView listView;
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
        String stopId = getIntent().getExtras().getString(STOP_ID_KEY);
        if (stopId == null){
            A.log("stopId == null");
            return;
        }
        listView = (ListView) findViewById(R.id.busSchedule);
        String url = API + stopId;

        busList = new ArrayList<BusInfo>();

        Document doc = null;
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        listView.setAdapter(new BusListAdapter(getApplicationContext(), busList));
    }

}
