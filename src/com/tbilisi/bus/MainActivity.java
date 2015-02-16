package com.tbilisi.bus;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.j256.ormlite.dao.CloseableIterator;
import com.tbilisi.bus.data.BusStop;
import com.tbilisi.bus.data.MapItem;
import com.tbilisi.bus.util.MapItemRenderer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Pattern qr_pattern;
    private SearchFragment searchFragment;
    private GoogleMap googleMap;
    private ClusterManager<MapItem> clusterManager;
    private boolean mapPopulated = false;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;

    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        instance = this;
        setContentView(R.layout.activity_main);

        qr_pattern = Pattern.compile("smsto:([0-9]+):([0-9]+)", Pattern.CASE_INSENSITIVE);
        searchFragment = (SearchFragment) getFragmentManager().findFragmentById(R.id.search_fragment);

        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        setUpMap();

        if(A.dbLoaded && !mapPopulated) populateMap();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void setUpMap() {
        if(googleMap == null) return;

        clusterManager = new ClusterManager<>(this, googleMap);
        clusterManager.setRenderer(new MapItemRenderer(this, googleMap, clusterManager));

        clusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<MapItem>() {
            @Override
            public void onClusterItemInfoWindowClick(MapItem mapItem) {
                showSchedule(mapItem.id);
            }
        });

        googleMap.setOnCameraChangeListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
        googleMap.setOnInfoWindowClickListener(clusterManager);

        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(false);

        //Default location and zoom level (Tbilisi)
        double latitude = 41.7167f;
        double longitude = 44.7833f;
        int zoomLevel = 11;

        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i("GoogleApiClient", "Connected");
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        if (lastLocation != null) {
            Log.i("Location", "Location acquired");
            double latitude = lastLocation.getLatitude();
            double longitude = lastLocation.getLongitude();
            int zoomLevel = 18;

            LatLng latLng = new LatLng(latitude, longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
        } else {
            Log.w("Location", "Location null");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void populateMap() {
        if(googleMap == null) return;

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
//                googleMap.addMarker(new MarkerOptions()
//                                .position(new LatLng(items[0].lat, items[0].lon))
//                                .title(items[0].name)
//                                .snippet(String.valueOf(items[0].id))
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_icon))
//                                .anchor(0.5f, 1.0f) //bottom-center
//                );
                clusterManager.addItem(new MapItem(items[0].lat, items[0].lon,
                        String.valueOf(items[0].id),
                        items[0].name));
            }

            @Override
            protected void onPostExecute(Void result) {
                iterator.closeQuietly();
            }
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w("GoogleApiClient", connectionResult.toString());
    }
}
