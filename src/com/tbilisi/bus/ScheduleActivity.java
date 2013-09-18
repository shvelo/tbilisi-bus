package com.tbilisi.bus;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class ScheduleActivity extends Activity{
    public ArrayList<BusInfo> busList;
    private ListView listView;
    private String stopId;
    private BusListAdapter adapter;
    private ProgressBar progressBar;
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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        loadList();

        adapter = new BusListAdapter(this, busList);

        listView.setAdapter(adapter);

        Button reloadButton = (Button) findViewById(R.id.reload);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reload();
            }
        });

        ((TextView) findViewById(R.id.header)).setTypeface(A.typeface);
    }

    public void loadList() {
        busList = new ArrayList<BusInfo>();
        String url = API + stopId;
        Document doc = null;
        progressBar.setVisibility(View.VISIBLE);
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
        if(busList.size() == 0)
            busList.add(new BusInfo(0, getResources().getString(R.string.nothing_found), 0));
        progressBar.setVisibility(View.GONE);
    }

    public void reload() {
        loadList();
        adapter.update(busList);
    }
}
