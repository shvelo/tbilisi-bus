package com.tbilisi.bus;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.crashlytics.android.Crashlytics;
import com.tbilisi.bus.util.MainMenuAdapter;
import com.tbilisi.bus.util.MainMenuItem;
import java.util.ArrayList;

public class MenuActivity extends ActionBarActivity {
    private ListView listView;
    private View loadingView;
    private ArrayList<MainMenuItem> menu_items;

    public static MenuActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        instance = this;
        setContentView(R.layout.activity_menu);

        Resources res = getResources();

        listView = (ListView) findViewById(R.id.listView);

        loadingView = getLayoutInflater().inflate(R.layout.loading_db, null);
        listView.addHeaderView(loadingView, null, false);

        Intent intent_scan = new Intent(this, CameraActivity.class);
        Intent intent_search = new Intent(this, SearchActivity.class);
        Intent intent_history = new Intent(this, HistoryActivity.class);

        menu_items = new ArrayList<MainMenuItem>();
        menu_items.add(new MainMenuItem(res.getString(R.string.scan),
                res.getDrawable(R.drawable.qr), true, intent_scan));
//        menu_items.add(new MainMenuItem(res.getString(R.string.search),
//                res.getDrawable(R.drawable.search), true, intent_search));

        menu_items.add(new MainMenuItem(res.getString(R.string.history),
                res.getDrawable(R.drawable.time), false, intent_history));
//        menu_items.add(new MainMenuItem(res.getString(R.string.nearest),
//                res.getDrawable(R.drawable.location), false, null));
//
//        menu_items.add(new MainMenuItem(res.getString(R.string.help),
//                res.getDrawable(R.drawable.help), true, null));

        listView.setAdapter(new MainMenuAdapter(this, menu_items));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = (Intent) view.getTag();
                if(intent != null)
                startActivity(intent);
            }
        });

        if(A.dbLoaded) enableItems();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(A.dbLoaded) enableItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent(instance, SearchActivity.class);
                intent.putExtra("query", s);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void enableItems() {
        listView.removeHeaderView(loadingView);
        menu_items.get(1).enabled = true;
//        menu_items.get(3).enabled = true;
//        menu_items.get(4).enabled = true;
        listView.setAdapter(new MainMenuAdapter(this, menu_items));
    }
}
