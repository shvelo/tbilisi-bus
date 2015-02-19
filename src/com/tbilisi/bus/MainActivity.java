package com.tbilisi.bus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tbilisi.bus.data.BusStop;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;

public class MainActivity extends ActionBarActivity {
    private Pattern qr_pattern;
    private SearchFragment searchFragment;
    private ScheduleFragment scheduleFragment;
    private Button scheduleButton;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        realm = Realm.getInstance(this);
        loadRealm();
        setContentView(R.layout.activity_main);

        qr_pattern = Pattern.compile("smsto:([0-9]+):([0-9]+)", Pattern.CASE_INSENSITIVE);
        searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.search_fragment);
        scheduleFragment = (ScheduleFragment) getSupportFragmentManager().findFragmentById(R.id.schedule_fragment);
        scheduleButton = (Button) findViewById(R.id.hide_schedule);

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSchedule();
            }
        });

        hideSchedule();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void loadRealm() {
        try {
            if(realm.where(BusStop.class).count() == 0) {
                Log.i("Realm", "Initializing DB");
                InputStream is = getAssets().open("db.json");
                realm.beginTransaction();
                realm.createAllFromJson(BusStop.class, is);
                realm.commitTransaction();
            }
        } catch (IOException e) {
            realm.cancelTransaction();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setSubmitButtonEnabled(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchFragment.updateList(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchFragment.updateList(s);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchFragment.clearList();
                return false;
            }
        });

        MenuItem scan = menu.findItem(R.id.scan);
        scan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.initiateScan();
                return true;
            }
        });

        MenuItem history = menu.findItem(R.id.history);
        history.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent_history = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent_history);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void showSchedule(int stopId) {
        findViewById(R.id.schedule_fragment).setVisibility(View.VISIBLE);
        scheduleButton.setVisibility(View.VISIBLE);
        String stopName = realm.where(BusStop.class).equalTo("id", stopId).findFirst().getName();
        scheduleButton.setText(stopName);
        scheduleFragment.showSchedule(stopId);
    }

    public void hideSchedule() {
        findViewById(R.id.schedule_fragment).setVisibility(View.GONE);
        scheduleButton.setVisibility(View.GONE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String scanned = scanResult.getContents();

            if (scanned == null) return;

            Matcher m = qr_pattern.matcher(scanned);
            if (m.find()) showSchedule(Integer.parseInt(m.group(2)));
        }
    }
}
