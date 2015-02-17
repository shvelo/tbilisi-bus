package com.tbilisi.bus;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.tbilisi.bus.data.BusStop;
import com.tbilisi.bus.util.MapItemRenderer;

import io.realm.Realm;

public class MapFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap googleMap;
    private ClusterManager<BusStop> clusterManager;
    private GoogleApiClient googleApiClient;

    public static MapFragment instance;

    private Realm realm;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(getActivity());

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(realm != null) realm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_map, container, false);
        googleMap = ((com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        setUpMap();

        populateMap();
        return layout;
    }

    private void setUpMap() {
        if(googleMap == null) return;

        clusterManager = new ClusterManager<>(getActivity(), googleMap);
        clusterManager.setRenderer(new MapItemRenderer(getActivity(), googleMap, clusterManager));

        clusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<BusStop>() {
            @Override
            public void onClusterItemInfoWindowClick(BusStop mapItem) {
                showSchedule(String.valueOf(mapItem.getId()));
            }
        });

        googleMap.setOnCameraChangeListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
        googleMap.setOnInfoWindowClickListener(clusterManager);

        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(false);

        googleMap.getUiSettings().setZoomControlsEnabled(true);

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
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w("GoogleApiClient", connectionResult.toString());
    }

    private void showSchedule(String stopId) {
        Intent i = new Intent(getActivity(), ScheduleActivity.class);
        i.putExtra(ScheduleActivity.STOP_ID_KEY, stopId);
        startActivity(i);
    }

    public void populateMap() {
        if(googleMap == null) return;
        clusterManager.addItems(realm.allObjects(BusStop.class));
        Log.i("Map", "Added items");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
