package com.tbilisi.bus.util

import android.util.Log
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.overlay.Icon
import com.mapbox.mapboxsdk.overlay.Marker
import com.mapbox.mapboxsdk.views.MapView
import com.tbilisi.bus.R
import com.tbilisi.bus.data.BusStop
import io.realm.Realm
import java.util.*

class MarkerHelper(val mapView: MapView) {
    var loadedMarkers: ArrayList<Int> = ArrayList()

    fun addMarker(stop: BusStop) {
        if(loadedMarkers.contains(stop.id))
            return

        Log.d("MarkerHelper", "Adding stop ${stop.name_en}")
        val marker = createMarker(stop)
        mapView.addMarker(marker)
        loadedMarkers.add(stop.id)
    }

    fun createMarker(stop: BusStop): Marker {
        val marker = Marker(StopHelper.getLocalizedName(stop), stop.id.toString(), LatLng(stop.lat, stop.lon))
        marker.setIcon(Icon(mapView.context.resources.getDrawable(R.drawable.stop_icon)))
        return marker
    }

    fun populateBounds() {
        val realm = Realm.getInstance(mapView.context)
        val boundingBox = mapView.boundingBox

        val query = realm.where(BusStop::class.java)
                .between("lat", boundingBox.latSouth, boundingBox.latNorth)
                .between("lon", boundingBox.lonWest, boundingBox.lonEast)

        Log.d("MarkerHelper", "Found ${query.count()} objects")
        Log.d("MarkerHelper", "All objects ${realm.where(BusStop::class.java).count()}")

        for(stop in query.findAll()) {
            addMarker(stop)
        }
    }
}