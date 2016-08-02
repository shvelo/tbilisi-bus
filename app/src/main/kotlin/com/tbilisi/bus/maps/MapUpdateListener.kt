package com.tbilisi.bus.maps

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.tbilisi.bus.util.MarkerHelper

class MapUpdateListener(googleMap: GoogleMap, val context: Context): GoogleMap.OnCameraIdleListener {
    companion object {
        var lastPosition: LatLng? = null
    }

    val LOG_TAG = "MapUpdateListener"
    val MIN_ZOOM = 16F
    val markerHelper = MarkerHelper(googleMap, context)
    val map = googleMap

    override fun onCameraIdle() {
        val cameraPosition = map.cameraPosition
        if(cameraPosition != null && cameraPosition.zoom >= MIN_ZOOM) {
            markerHelper.populateBounds()
        }
        lastPosition = cameraPosition?.target

    }
}