package com.tbilisi.bus;

import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tbilisi.bus.data.BusInfo;
import com.tbilisi.bus.data.HistoryItem;
import com.tbilisi.bus.util.BusListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import io.realm.Realm;

public class ScheduleFragment extends Fragment {
    public ArrayList<BusInfo> busList;
    private int stopId;
    private BusListAdapter adapter;
    public static final String STOP_ID_KEY = "stopId";
    private static final String API =
            "http://transit.ttc.com.ge/pts-portal-services/servlet/stopArrivalTimesServlet?stopId=";
    private String url;
    private final int delay = 30000; //Auto-update interval, seconds x 1000
    private Handler handler;
    private AutoUpdater autoUpdater;
    private Realm realm;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        realm = Realm.getInstance(getActivity());

        ListView listView = (ListView) view.findViewById(R.id.busSchedule);
        loadList();
        adapter = new BusListAdapter(getActivity(), busList);
        listView.setAdapter(adapter);

        handler = new Handler();
        autoUpdater = new AutoUpdater();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(autoUpdater, delay);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(autoUpdater);
    }

    @Override
    public void onDestroy() {
        if(realm != null) realm.close();
        super.onDestroy();
    }

    public void loadList() {
        if(busList == null) busList = new ArrayList<>();
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
                view.findViewById(R.id.nothing_found).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.nothing_found).setVisibility(View.GONE);
            }
        }
    }

    public void reload() {
        loadList();
        adapter.update(busList);
    }

    private void saveToHistory() {
        try {
            if(realm.where(HistoryItem.class).equalTo("id", stopId).count() == 0) {
                realm.beginTransaction();
                realm.createObject(HistoryItem.class).setId(stopId);
                realm.commitTransaction();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSchedule(int id) {
        stopId = id;
        loadList();
        saveToHistory();
    }
}
