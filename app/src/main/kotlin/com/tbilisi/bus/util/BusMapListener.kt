package com.tbilisi.bus.util

import io.realm.Realm

class BusMapListener(val mapView: Any, val realm: Realm) {
    val MIN_ZOOM = 17F
    val markerHelper = MarkerHelper(mapView)

    fun onScroll(event: Any?) {
        if(event != null) {
            populate()
        }
    }

    fun onZoom(event: Any?) {
        if(event != null) {
            populate()
        }
    }

    fun onRotate(event: Any?) {
    }

    fun populate() {
        if(0 >= MIN_ZOOM) {
            markerHelper.populateBounds()
        }
    }
}