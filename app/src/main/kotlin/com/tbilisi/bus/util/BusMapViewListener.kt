package com.tbilisi.bus.util

import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.view.View
import com.mapbox.mapboxsdk.api.ILatLng
import com.mapbox.mapboxsdk.overlay.Marker
import com.mapbox.mapboxsdk.views.MapView
import com.mapbox.mapboxsdk.views.MapViewListener
import com.tbilisi.bus.ScheduleActivity
import com.tbilisi.bus.data.BusStop

class BusMapViewListener(val context: Context, val rootView: View): MapViewListener {
    override fun onHideMarker(pMapView: MapView?, pMarker: Marker?) {
    }

    override fun onTapMap(pMapView: MapView?, pPosition: ILatLng?) {
    }

    override fun onLongPressMap(pMapView: MapView?, pPosition: ILatLng?) {
    }

    override fun onLongPressMarker(pMapView: MapView?, pMarker: Marker?) {
    }

    override fun onTapMarker(pMapView: MapView?, pMarker: Marker?) {
        val stop = pMarker?.relatedObject as BusStop
        val name = StopHelper.getLocalizedName(stop)
        Snackbar.make(rootView, "${stop.id} $name", Snackbar.LENGTH_INDEFINITE)
                .setAction("GO", {
                    val intent = Intent(context, ScheduleActivity::class.java)
                    intent.putExtra("id", stop.id)
                    context.startActivity(intent)
                })
                .show()
    }

    override fun onShowMarker(pMapView: MapView?, pMarker: Marker?) {
    }
}