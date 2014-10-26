package com.tbilisi.bus;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.j256.ormlite.dao.CloseableIterator;
import com.tbilisi.bus.data.BusStop;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuActivity extends ActionBarActivity {
    private Pattern qr_pattern;
    private GoogleMap googleMap;
    private boolean mapPopulated = false;

    public static MenuActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        instance = this;
        setContentView(R.layout.activity_menu);

        qr_pattern = Pattern.compile("smsto:([0-9]+):([0-9]+)", Pattern.CASE_INSENSITIVE);

        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        setUpMap();

        if(A.dbLoaded && !mapPopulated) populateMap();
    }

    private void setUpMap() {
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(false);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location myLocation = locationManager.getLastKnownLocation(provider);
        double latitude = myLocation.getLatitude();
        double longitude = myLocation.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(19));

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                showSchedule(marker.getSnippet());
            }
        });
    }

    public void populateMap() {
        mapPopulated = true;

        new AsyncTask<Void, BusStop, Void>() {
            private CloseableIterator<BusStop> iterator;

            @Override
            protected void onPreExecute() {
                iterator = A.db.busStopDao.iterator();
            }

            @Override
            protected Void doInBackground(Void... params) {
                while(iterator.hasNext()) {
                    publishProgress(iterator.next());
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(BusStop... items) {
                googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(items[0].lat, items[0].lon))
                                .title(items[0].name)
                                .snippet(String.valueOf(items[0].id))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_icon))
                                .anchor(0.5f, 1.0f) //bottom-center
                );
            }

//            @Override
//            protected void onPostExecute(Void result) {
//            }
        }.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
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

        MenuItem scan = menu.findItem(R.id.scan);
        scan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                IntentIntegrator integrator = new IntentIntegrator(instance);
                integrator.initiateScan();
                return true;
            }
        });

        MenuItem history = menu.findItem(R.id.history);
        history.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent_history = new Intent(instance, HistoryActivity.class);
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
