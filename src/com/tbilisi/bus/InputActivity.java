package com.tbilisi.bus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.tbilisi.bus.data.BusStop;
import com.tbilisi.bus.util.StopListAdapter;

import java.util.ArrayList;

public class InputActivity extends ActionBarActivity {
    private EditText input;
    private ListView list;
    private ArrayList<BusStop> items;
    private StopListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        input = (EditText) findViewById(R.id.input);
        list = (ListView) findViewById(R.id.list);
        items = new ArrayList<BusStop>();
        adapter = new StopListAdapter(this, items);
        list.setAdapter(adapter);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            @Override
            public void afterTextChanged(Editable editable) {
                updateList(editable.toString());
            }
        });

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
    }

    public void updateList(String id) {
        try {
            items.clear();
            if(A.dbLoaded) {
                BusStop busStop = A.db.busStopDao.queryForId(Integer.valueOf(id));
                if(busStop != null) items.add(busStop);
            }
            if(items.size() == 0)
                items.add(new BusStop(Integer.valueOf(id), "გაჩერება "+id, false, false, 0.0, 0.0));
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
