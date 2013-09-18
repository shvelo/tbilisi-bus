package com.tbilisi.bus;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MenuActivity extends Activity {
    private ListView listView;
    private ArrayList<MainMenuItem> menu_items;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        res = getResources();
        menu_items = new ArrayList<MainMenuItem>();

        Intent intent_scan = new Intent(this, CameraActivity.class);

        menu_items.add(new MainMenuItem("სკანირება", res.getDrawable(R.drawable.camera), intent_scan));
        menu_items.add(new MainMenuItem("შეყვანა", res.getDrawable(R.drawable.input), null));

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
