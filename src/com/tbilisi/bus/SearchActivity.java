package com.tbilisi.bus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    private String query;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        list = (ListView) findViewById(R.id.list);
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

        Intent thisIntent = getIntent();
        query = thisIntent.getStringExtra("query");
        Log.d("BUS", query);
        if(query != null) updateList(query);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setSubmitButtonEnabled(false);
        if(query != null) {
            searchView.setQuery(query, false);
            searchView.setIconified(false);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                updateList(s);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
