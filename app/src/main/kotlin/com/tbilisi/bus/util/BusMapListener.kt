package com.tbilisi.bus.util

import android.content.Context
import android.util.Log
import com.mapbox.mapboxsdk.events.MapListener
import com.mapbox.mapboxsdk.events.RotateEvent
import com.mapbox.mapboxsdk.events.ScrollEvent
import com.mapbox.mapboxsdk.events.ZoomEvent
import com.mapbox.mapboxsdk.views.MapView
import io.realm.Realm

class BusMapListener(val context: Context, val realm: Realm): MapListener {
    val MIN_ZOOM = 16

    override fun onScroll(event: ScrollEvent?) {
        if(event != null) {
            populate(event.source)
        }
    }

    override fun onZoom(event: ZoomEvent?) {
        if(event != null) {
            populate(event.source)
        }
    }

    override fun onRotate(event: RotateEvent?) {
    }

    fun populate(mapView: MapView) {
        if(mapView.zoomLevel >= MIN_ZOOM) {
            Log.d("BusMapListener", "Populating map bounds ${mapView.boundingBox.toString()}")
            MarkerHelper.populateBounds(mapView.boundingBox, mapView)
        }
    }
}