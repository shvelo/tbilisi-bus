package com.tbilisi.bus.fragments

import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay
import com.mapbox.mapboxsdk.tileprovider.tilesource.WebSourceTileLayer
import com.mapbox.mapboxsdk.views.MapView
import com.tbilisi.bus.R

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
        setupMap(mapView!!)

        return createdView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.map, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_locate -> {
                mapView?.goToUserLocation(true)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun setupMap(mv: MapView) {
        mv.setTileSource(WebSourceTileLayer("tiles", getString(R.string.tile_source)))
        mv.setUserLocationEnabled(true)
        mv.setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.FOLLOW)
        mv.setUserLocationRequiredZoom(17F)
    }
}
