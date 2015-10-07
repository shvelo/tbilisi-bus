package com.tbilisi.bus.util

import com.mapbox.mapboxsdk.events.MapListener
import com.mapbox.mapboxsdk.events.RotateEvent
import com.mapbox.mapboxsdk.events.ScrollEvent
import com.mapbox.mapboxsdk.events.ZoomEvent
import com.mapbox.mapboxsdk.views.MapView
import io.realm.Realm

class BusMapListener(val mapView: MapView, val realm: Realm): MapListener {
    val MIN_ZOOM = 17F
    val markerHelper = MarkerHelper(mapView)

    override fun onScroll(event: ScrollEvent?) {
        if(event != null) {
            populate()
        }
    }

    override fun onZoom(event: ZoomEvent?) {
        if(event != null) {
            populate()
        }
    }

    override fun onRotate(event: RotateEvent?) {
    }

    fun populate() {
        if(mapView.zoomLevel >= MIN_ZOOM) {
            markerHelper.populateBounds()
        }
    }
}