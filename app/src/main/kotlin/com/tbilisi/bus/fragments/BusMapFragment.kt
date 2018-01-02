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
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.tbilisi.bus.R
import com.tbilisi.bus.SearchActivity
import com.tbilisi.bus.maps.MapClickListener
import com.tbilisi.bus.maps.MapUpdateListener
import pl.tajchert.nammu.Nammu
import pl.tajchert.nammu.PermissionCallback

class BusMapFragment : Fragment(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {
    val LOG_TAG = "MapFragment"

    var map: GoogleMap? = null
    var googleApiClient: GoogleApiClient? = null

    val TBILISI = LatLngBounds(LatLng(41.460397169473524, 44.49836120009422), LatLng(42.00661755761343, 45.06872121244669))
    val MIN_ZOOM = 10F

    override fun onConnected(bundle: Bundle?) {
        askForLocation()
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onMapReady(readyMap: GoogleMap?) {
        map = readyMap
        if(map != null) {
            map?.setOnCameraIdleListener(MapUpdateListener(map!!, context!!))
            map?.setOnInfoWindowClickListener(MapClickListener(context!!))
            map?.setLatLngBoundsForCameraTarget(TBILISI)
            map?.setMinZoomPreference(MIN_ZOOM)

            askForLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        googleApiClient = GoogleApiClient.Builder(context!!)
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
        gotoSavedLocation()
        Nammu.askForPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION, object:PermissionCallback {
            override fun permissionRefused() {
            }

            override fun permissionGranted() {
                map?.isMyLocationEnabled = true
                if(!gotoSavedLocation())
                    gotoMyLocation()
            }
        })
    }

    fun gotoSavedLocation(): Boolean {
        if(MapUpdateListener.lastPosition != null) {
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(MapUpdateListener.lastPosition, 18F))
            return true
        }
        return false
    }

    fun gotoMyLocation() {
        val location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        Log.d(LOG_TAG, "$location")
        if(location != null)
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 18F))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val createdView = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
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
        }

        return super.onOptionsItemSelected(item)
    }

    fun setupSearch(menu: Menu) {
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                ComponentName(activity?.applicationContext, SearchActivity::class.java)))
        searchView.isSubmitButtonEnabled = true
    }
}
