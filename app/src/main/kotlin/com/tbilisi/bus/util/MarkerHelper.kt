package com.tbilisi.bus.util

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tbilisi.bus.R
import com.tbilisi.bus.data.BusStop
import com.tbilisi.bus.data.BusStopStore
import java.util.*

class MarkerHelper(val map: GoogleMap, val context: Context) {
    val LOG_TAG = "MarkerHelper"
    val loadedMarkers: ArrayList<String> = ArrayList()

    fun addMarker(stop: BusStop) {
        if(loadedMarkers.contains(stop.id))
            return

        val markerOptions = createMarkerOptions(stop)
        val marker = map.addMarker(markerOptions)
        marker.tag = stop.id
        loadedMarkers.add(stop.id)
    }

    fun createMarkerOptions(stop: BusStop): MarkerOptions {
        val markerOptions = MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_icon))
                .position(LatLng(stop.lat, stop.lon))
                .title(LocalizationHelper.getLocalizedStopName(stop))
                .snippet(stop.id)
        return markerOptions
    }

    fun populateBounds() {
        val boundingBox = map.projection.visibleRegion.latLngBounds

        val stops = BusStopStore.findInBounds(boundingBox)

        for(stop in stops) {
            addMarker(stop)
        }
    }
}