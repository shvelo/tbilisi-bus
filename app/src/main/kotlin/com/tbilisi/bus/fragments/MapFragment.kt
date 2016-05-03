package com.tbilisi.bus.fragments

import android.Manifest
import android.support.v4.app.Fragment
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.tbilisi.bus.R
import com.tbilisi.bus.SearchActivity
import pl.tajchert.nammu.Nammu
import pl.tajchert.nammu.PermissionCallback

class MapFragment : Fragment(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {
    override fun onConnected(p0: Bundle?) {
        gotoMyLocation()
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    val LOG_TAG = "MapFragment"

    var map: GoogleMap? = null
    var googleApiClient: GoogleApiClient? = null

    override fun onMapReady(readyMap: GoogleMap?) {
        map = readyMap
        Toast.makeText(activity, "Map initialized", Toast.LENGTH_SHORT).show()
        askForLocation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        googleApiClient = GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build()
    }

    override fun onStart() {
        googleApiClient?.connect()
        super.onStart()
    }

    override fun onStop() {
        googleApiClient?.disconnect()
        super.onStop()
    }

    fun askForLocation() {
        Nammu.askForPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION, object:PermissionCallback {
            override fun permissionRefused() {
            }

            override fun permissionGranted() {
                map?.isMyLocationEnabled = true
                gotoMyLocation()
            }
        })
    }

    fun gotoMyLocation() {
        val location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        Log.d(LOG_TAG, "$location")
        if(location != null)
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 18f))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val createdView = inflater.inflate(R.layout.fragment_map, container, false)

        var mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return createdView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.map, menu)

        if (menu != null)
            setupSearch(menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_locate -> {
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun setupSearch(menu: Menu) {
        val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                ComponentName(activity.applicationContext, SearchActivity::class.java)))
    }
}
