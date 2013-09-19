package com.tbilisi.bus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.tbilisi.bus.data.BusStop;
import com.tbilisi.bus.data.HistoryItem;
import com.tbilisi.bus.util.StopListAdapter;

import java.sql.SQLException;
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

        try {
            for(HistoryItem item : A.db.historyItemDao.queryForAll()) {
                items.add(A.db.busStopDao.queryForId(item.id));
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
