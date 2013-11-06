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
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.tbilisi.bus.data.BusStop;
import com.tbilisi.bus.util.StopListAdapter;
import java.util.ArrayList;

public class SearchActivity extends ActionBarActivity {
    private EditText input;
    private ListView list;
    private ArrayList<BusStop> items;
    private StopListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
            if(A.dbLoaded) {
                items.clear();
                id = id.trim();
                QueryBuilder<BusStop, Integer> qb = A.db.busStopDao.queryBuilder();
                Where<BusStop, Integer> query = qb.where().like("name", "%" + id + "%");
                query.or().like("name_en", "%" + id + "%");
                if(id.matches("[0-9]+")) {
                    query.or().idEq(Integer.valueOf(id));
                }
                items.addAll(query.query());
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
