package com.tbilisi.bus;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.tbilisi.bus.data.BusStop;
import com.tbilisi.bus.data.HistoryItem;
import com.tbilisi.bus.util.StopListAdapter;

import java.util.ArrayList;

import io.realm.Realm;

public class HistoryActivity extends ActionBarActivity {
    private ArrayList<BusStop> items;
    private StopListAdapter adapter;
    private ScheduleFragment scheduleFragment;
    private Button scheduleButton;
    private Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        realm = Realm.getInstance(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ListView list = (ListView) findViewById(R.id.listView);
        items = new ArrayList<>();
        adapter = new StopListAdapter(this, items);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showSchedule((int) view.getTag());
            }
        });

        loadList();

        scheduleFragment = (ScheduleFragment) getSupportFragmentManager().findFragmentById(R.id.schedule_fragment);
        scheduleButton = (Button) findViewById(R.id.hide_schedule);

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSchedule();
            }
        });

        hideSchedule();
    }

    public void showSchedule(int stopId) {
        findViewById(R.id.schedule_fragment).setVisibility(View.VISIBLE);
        scheduleButton.setVisibility(View.VISIBLE);
        String stopName = realm.where(BusStop.class).equalTo("id", stopId).findFirst().getName();
        scheduleButton.setText(stopName);
        scheduleFragment.showSchedule(stopId);
    }

    public void hideSchedule() {
        findViewById(R.id.schedule_fragment).setVisibility(View.GONE);
        scheduleButton.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(realm != null) realm.close();
    }

    public void loadList() {
        items.clear();
        try {
            for(HistoryItem item : realm.allObjects(HistoryItem.class)){
                Log.d("History", String.valueOf(item.getId()));
                BusStop stop = realm.where(BusStop.class).equalTo("id", item.getId()).findFirst();
                items.add(stop);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                clear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void clear() {
        realm.beginTransaction();
        realm.where(HistoryItem.class).findAll().clear();
        realm.commitTransaction();
        loadList();
    }
}
