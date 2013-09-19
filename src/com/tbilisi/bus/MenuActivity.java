package com.tbilisi.bus;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MenuActivity extends Activity {
    private ListView listView;
    private View loadingView;
    private ArrayList<MainMenuItem> menu_items;
    private Resources res;

    public static MenuActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_menu);

        res = getResources();

        ((TextView) findViewById(R.id.header)).setTypeface(A.typeface);
        listView = (ListView) findViewById(R.id.listView);

        loadingView = getLayoutInflater().inflate(R.layout.loading_db, null);
        listView.addHeaderView(loadingView, null, false);

        Intent intent_scan = new Intent(this, CameraActivity.class);
        Intent intent_input = new Intent(this, InputActivity.class);

        menu_items = new ArrayList<MainMenuItem>();
        menu_items.add(new MainMenuItem(res.getString(R.string.scan),
                res.getDrawable(R.drawable.qr), true, intent_scan));
        menu_items.add(new MainMenuItem(res.getString(R.string.input),
                res.getDrawable(R.drawable.input), true, intent_input));

        menu_items.add(new MainMenuItem(res.getString(R.string.history),
                res.getDrawable(R.drawable.time), false, null));
        menu_items.add(new MainMenuItem(res.getString(R.string.search),
                res.getDrawable(R.drawable.search), false, null));
        menu_items.add(new MainMenuItem(res.getString(R.string.nearest),
                res.getDrawable(R.drawable.location), false, null));

        menu_items.add(new MainMenuItem(res.getString(R.string.help),
                res.getDrawable(R.drawable.help), true, null));

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

    public void enableItems() {
        listView.removeHeaderView(loadingView);
        menu_items.get(2).enabled = true;
        menu_items.get(3).enabled = true;
        menu_items.get(4).enabled = true;
        listView.setAdapter(new MainMenuAdapter(this, menu_items));
    }
}
