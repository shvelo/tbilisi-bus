package com.tbilisi.bus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.tbilisi.bus.data.BusStop;
import com.tbilisi.bus.data.HistoryItem;
import com.tbilisi.bus.util.StopListAdapter;

import java.util.ArrayList;

public class HistoryActivity extends ActionBarActivity {
    private EditText input;
    private ListView list;
    private ArrayList<BusStop> items;
    private StopListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        list = (ListView) findViewById(R.id.listView);
        items = new ArrayList<BusStop>();
        adapter = new StopListAdapter(this, items);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = (String) view.getTag();
                A.log("Showing schedule for " + id);
                Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                intent.putExtra(ScheduleActivity.STOP_ID_KEY, id);
                startActivity(intent);
            }
        });

        loadList();
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
