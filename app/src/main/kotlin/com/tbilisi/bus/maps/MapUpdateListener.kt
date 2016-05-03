package com.tbilisi.bus.maps

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.tbilisi.bus.util.MarkerHelper

class MapUpdateListener(map: GoogleMap, val context: Context): GoogleMap.OnCameraChangeListener {
    val LOG_TAG = "MapUpdateListener"
    val MIN_ZOOM = 17F
    val markerHelper = MarkerHelper(map, context)

    override fun onCameraChange(cameraPosition: CameraPosition?) {
        Log.d(LOG_TAG, "OnCameraChange $cameraPosition")
        if(cameraPosition != null && cameraPosition.zoom >= MIN_ZOOM) {
            markerHelper.populateBounds()
        }
    }
}