package com.tbilisi.bus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MenuActivity extends Activity {
    private ListView listView;
    private ArrayList<MainMenuItem> menu_items;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menu_items = new ArrayList<MainMenuItem>();

        menu_items.add(new MainMenuItem("სკანირება", null, null));
        menu_items.add(new MainMenuItem("შეყვანა", null, null));

        listView = (ListView) findViewById(R.id.listView);

        listView.setAdapter(new MainMenuAdapter(getApplicationContext(), menu_items));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = (Intent) view.getTag();
                if(intent != null)
                startActivity(intent);
            }
        });
    }
}
