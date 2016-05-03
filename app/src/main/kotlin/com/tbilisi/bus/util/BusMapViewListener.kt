package com.tbilisi.bus.util

import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.view.View
import com.tbilisi.bus.ScheduleActivity
import com.tbilisi.bus.data.BusStop

class BusMapViewListener(val context: Context, val rootView: View) {

    fun onTapMarker(pMapView: Any?, pMarker: Any?) {
        val stop = pMarker as BusStop
        val name = StopHelper.getLocalizedName(stop)
        Snackbar.make(rootView, "${stop.id} $name", Snackbar.LENGTH_INDEFINITE)
                .setAction("GO", {
                    val intent = Intent(context, ScheduleActivity::class.java)
                    intent.putExtra("id", stop.id)
                    context.startActivity(intent)
                })
                .show()
    }
}