package com.tbilisi.bus.fragments

import android.app.Fragment
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.*
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay
import com.mapbox.mapboxsdk.tileprovider.tilesource.WebSourceTileLayer
import com.mapbox.mapboxsdk.views.MapView
import com.tbilisi.bus.R
import com.tbilisi.bus.SearchActivity
import com.tbilisi.bus.util.BusMapListener
import com.tbilisi.bus.util.BusMapViewListener
import io.realm.Realm

public class MapFragment : Fragment() {
    var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val createdView = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = createdView.findViewById(R.id.mapview) as MapView
        if (mapView != null) {
            setupMap(mapView!!)
            setupZoomControls(mapView!!, createdView)
        }

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
                mapView?.goToUserLocation(true)
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

    fun setupMap(mv: MapView) {
        mv.setTileSource(WebSourceTileLayer("tiles", getString(R.string.tile_source)))
        mv.setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.FOLLOW)
        mv.setUserLocationRequiredZoom(17F)

        mv.addListener(BusMapListener(mv, Realm.getInstance(activity)))
        mv.setMapViewListener(BusMapViewListener(activity, mv.rootView))
    }

    fun setupZoomControls(mv: MapView, view: View) {
        val zoom_plus = view.findViewById(R.id.zoom_plus)
        val zoom_minus = view.findViewById(R.id.zoom_minus)

        zoom_plus.setOnClickListener {
            mv.zoomIn()
        }

        zoom_minus.setOnClickListener {
            mv.zoomOut()
        }
    }
}
