package com.tbilisi.bus

import android.app.Activity
import android.support.v4.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.tbilisi.bus.data.BusStop
import com.tbilisi.bus.data.MapItem
import com.tbilisi.bus.util.MapItemRenderer

import io.realm.Realm

public class MapFragment : Fragment(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    private var googleMap: GoogleMap? = null
    private var clusterManager: ClusterManager<MapItem>? = null
    private var googleApiClient: GoogleApiClient? = null
    private var googleApiLoaded: Boolean = false

    private var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getInstance(activity)

        googleApiClient = GoogleApiClient.Builder(activity).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (realm != null) realm!!.close()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setUpMap() {
        if (googleMap == null) return

        clusterManager = ClusterManager<MapItem>(activity, googleMap)
        clusterManager!!.setRenderer(MapItemRenderer(activity, googleMap!!, clusterManager!!))

        clusterManager!!.setOnClusterItemClickListener(object : ClusterManager.OnClusterItemClickListener<MapItem> {
            override fun onClusterItemClick(mapItem: MapItem): Boolean {
                showSchedule(mapItem.id)
                return true
            }
        })
        clusterManager!!.setOnClusterClickListener(object : ClusterManager.OnClusterClickListener<MapItem> {
            override fun onClusterClick(mapItemCluster: Cluster<MapItem>): Boolean {
                googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        mapItemCluster.position,
                        googleMap!!.cameraPosition.zoom + 1))
                return true
            }
        })

        googleMap!!.setOnCameraChangeListener(clusterManager)
        googleMap!!.setOnMarkerClickListener(clusterManager)
        googleMap!!.setOnInfoWindowClickListener(clusterManager)

        googleMap!!.isMyLocationEnabled = true
        googleMap!!.isTrafficEnabled = false

        googleMap!!.uiSettings.isZoomControlsEnabled = true

        if (googleApiLoaded) return

        //Default location and zoom level (Tbilisi)
        val latitude = 41.7167
        val longitude = 44.7833
        val zoomLevel = 11

        val latLng = LatLng(latitude, longitude)
        googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap!!.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel.toFloat()))
    }

    override fun onConnected(connectionHint: Bundle) {
        googleApiLoaded = true
        Log.i("GoogleApiClient", "Connected")

        val lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient)
        if (lastLocation != null) {
            Log.i("Location", "Location acquired")
            val latitude = lastLocation.latitude
            val longitude = lastLocation.longitude
            val zoomLevel = 18

            val latLng = LatLng(latitude, longitude)
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            googleMap!!.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel.toFloat()))
        } else {
            Log.w("Location", "Location null")
        }
    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.w("GoogleApiClient", connectionResult.toString())
    }

    private fun showSchedule(stopId: Int) {
        (activity as MainActivity).showSchedule(stopId)
    }

    public fun populateMap() {
        if (googleMap == null) return
        for (stop in realm!!.allObjects(BusStop::class.java)) {
            clusterManager!!.addItem(MapItem(stop))
        }
        Log.i("Map", "Added items")
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setUpMap()
        populateMap()
    }
}// Required empty public constructor
