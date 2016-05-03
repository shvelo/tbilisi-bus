package com.tbilisi.bus.util

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.tbilisi.bus.R
import com.tbilisi.bus.data.BusStop
import io.realm.Realm
import java.util.*

class MarkerHelper(val map: GoogleMap, val context: Context) {
    companion object {
        val stopsForMarkers = HashMap<Marker, BusStop>()

        fun getStopForMarker(marker: Marker): BusStop? {
            return stopsForMarkers.get(marker)
        }
    }

    val LOG_TAG = "MarkerHelper"
    val loadedMarkers: ArrayList<Int> = ArrayList()

    fun addMarker(stop: BusStop) {
        if(loadedMarkers.contains(stop.id))
            return

        val markerOptions = createMarkerOptions(stop)
        val marker = map.addMarker(markerOptions)
        stopsForMarkers.put(marker, stop)
        loadedMarkers.add(stop.id)
    }

    fun createMarkerOptions(stop: BusStop): MarkerOptions {
        val markerOptions = MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_icon))
                .position(LatLng(stop.lat, stop.lon))
                .title(StopHelper.getLocalizedName(stop))
                .snippet(stop.id.toString())
        return markerOptions
    }

    fun populateBounds() {
        val realm = Realm.getInstance(context)
        val boundingBox = map.projection.visibleRegion.latLngBounds

        val query = realm.where(BusStop::class.java)
                .between("lat", boundingBox.southwest.latitude, boundingBox.northeast.latitude)
                .between("lon", boundingBox.southwest.longitude, boundingBox.northeast.longitude)

        Log.d(LOG_TAG, "lat ${boundingBox.southwest.latitude}, ${boundingBox.northeast.latitude}\n" +
                "lng ${boundingBox.southwest.longitude}, ${boundingBox.northeast.longitude}")

        for(stop in query.findAll()) {
            Log.d(LOG_TAG, "Adding stop $stop")
            addMarker(stop)
        }
    }

    fun getStopForMarker(marker: Marker) {

    }
}