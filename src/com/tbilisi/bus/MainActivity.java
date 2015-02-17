package com.tbilisi.bus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends ActionBarActivity {
    private Pattern qr_pattern;
    private SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_main);

        qr_pattern = Pattern.compile("smsto:([0-9]+):([0-9]+)", Pattern.CASE_INSENSITIVE);
        searchFragment = (SearchFragment) getFragmentManager().findFragmentById(R.id.search_fragment);
    }

    @Override
    public void onResume() {
        super.onResume();
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

    private void showSchedule(String stopId) {
        Intent i = new Intent(this, ScheduleActivity.class);
        i.putExtra(ScheduleActivity.STOP_ID_KEY, stopId);
        startActivity(i);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String scanned = scanResult.getContents();

            if (scanned == null) return;

            Matcher m = qr_pattern.matcher(scanned);
            if (m.find()) showSchedule(m.group(2));
        }
    }
}
