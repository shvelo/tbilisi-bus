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

        menu_items.add(new MainMenuItem(res.getString(R.string.scan), res.getDrawable(R.drawable.camera), intent_scan));
        menu_items.add(new MainMenuItem(res.getString(R.string.input), res.getDrawable(R.drawable.input), null));
        menu_items.add(new MainMenuItem(res.getString(R.string.history), res.getDrawable(R.drawable.time), null));
        menu_items.add(new MainMenuItem(res.getString(R.string.search), res.getDrawable(R.drawable.search), null));
        menu_items.add(new MainMenuItem(res.getString(R.string.nearest), res.getDrawable(R.drawable.location), null));
        menu_items.add(new MainMenuItem(res.getString(R.string.help), res.getDrawable(R.drawable.help), null));

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
