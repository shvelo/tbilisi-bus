package com.tbilisi.bus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tbilisi.bus.data.BusStop;
import com.tbilisi.bus.data.HistoryItem;
import com.tbilisi.bus.util.StopListAdapter;

import java.util.ArrayList;

public class HistoryActivity extends ActionBarActivity {
    private ArrayList<BusStop> items;
    private StopListAdapter adapter;
    private AdView ad;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ListView list = (ListView) findViewById(R.id.listView);
        items = new ArrayList<>();
        adapter = new StopListAdapter(this, items);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = (String) view.getTag();
                Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                intent.putExtra(ScheduleActivity.STOP_ID_KEY, id);
                startActivity(intent);
            }
        });

        loadList();

        ad = (AdView)findViewById(R.id.ad_history);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("F405EEB4E7BFB13CFC1CD35E3688395F")
                .build();

        ad.loadAd(adRequest);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(ad != null) ad.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(ad != null) ad.pause();
    }

    @Override
    public void onDestroy() {
        if (ad != null) ad.destroy();

        super.onDestroy();
    }

    public void loadList() {
        items.clear();
        try {
            for(HistoryItem item : A.db.historyItemDao.queryForAll()) {
                items.add(A.db.busStopDao.queryForId(item.id));
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
        A.db.clearTable(HistoryItem.class);
        loadList();
    }
}
