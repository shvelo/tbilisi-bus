package com.tbilisi.bus.maps

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.tbilisi.bus.util.MarkerHelper

class MapUpdateListener(map: GoogleMap, val context: Context): GoogleMap.OnCameraChangeListener {
    companion object {
        var lastPosition: LatLng? = null
    }

    val LOG_TAG = "MapUpdateListener"
    val MIN_ZOOM = 17F
    val markerHelper = MarkerHelper(map, context)

    override fun onCameraChange(cameraPosition: CameraPosition?) {
        if(cameraPosition != null && cameraPosition.zoom >= MIN_ZOOM) {
            markerHelper.populateBounds()
        }
        lastPosition = cameraPosition?.target
    }
}