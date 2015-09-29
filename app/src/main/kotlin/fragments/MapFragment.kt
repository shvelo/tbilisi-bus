package com.tbilisi.bus.fragments

import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay
import com.mapbox.mapboxsdk.tileprovider.tilesource.WebSourceTileLayer
import com.mapbox.mapboxsdk.views.MapView
import com.tbilisi.bus.R

public class MapFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val createdView = inflater.inflate(R.layout.fragment_map, container, false)

        val mapView = createdView.findViewById(R.id.mapview) as MapView
        setupMap(mapView)

        return createdView
    }

    fun setupMap(mv: MapView) {
        mv.setTileSource(WebSourceTileLayer("tiles", getString(R.string.tile_source)))
        mv.setUserLocationEnabled(true)
        mv.setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.FOLLOW)
        mv.setUserLocationRequiredZoom(16F)
    }
}
