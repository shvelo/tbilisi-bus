package com.tbilisi.bus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.tbilisi.bus.data.BusStop;

import java.util.ArrayList;

public class InputActivity extends ActionBarActivity {
    private EditText input;
    private ListView list;
    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;
    private String stopId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        input = (EditText) findViewById(R.id.input);
        list = (ListView) findViewById(R.id.list);
        items = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.list_item_text, items);
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
                A.log("Showing schedule for " + stopId);
                Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                intent.putExtra(ScheduleActivity.STOP_ID_KEY, stopId);
                startActivity(intent);
            }
        });
    }

    public void updateList(String id) {
        stopId = id;
        try {
            items.clear();
            if(A.dbLoaded) {
                BusStop busStop = A.db.busStopDao.queryForId(Integer.valueOf(id));
                if(busStop != null) items.add(busStop.name);
            }
            if(items.size() == 0) items.add(String.valueOf(id));
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
