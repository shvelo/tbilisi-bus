package com.tbilisi.bus.util

import com.tbilisi.bus.R
import com.tbilisi.bus.data.BusStop
import io.realm.Realm
import java.util.*

class MarkerHelper(val mapView: Any) {
    var loadedMarkers: ArrayList<Int> = ArrayList()

    fun addMarker(stop: BusStop) {
        if(loadedMarkers.contains(stop.id))
            return

        val marker = createMarker(stop)
//        mapView.addMarker(marker)
        loadedMarkers.add(stop.id)
    }

    fun createMarker(stop: BusStop) {
//        val marker = Marker(StopHelper.getLocalizedName(stop), stop.id.toString(), LatLng(stop.lat, stop.lon))
//        marker.setIcon(Icon(mapView.context.resources.getDrawable(R.drawable.stop_icon)))
//        marker.relatedObject = stop
//        return marker
    }

    fun populateBounds() {
//        val realm = Realm.getInstance(mapView.context)
//        val boundingBox = mapView.boundingBox
//
//        val query = realm.where(BusStop::class.java)
//                .between("lat", boundingBox.latSouth, boundingBox.latNorth)
//                .between("lon", boundingBox.lonWest, boundingBox.lonEast)
//
//        for(stop in query.findAll()) {
//            addMarker(stop)
//        }
    }
}