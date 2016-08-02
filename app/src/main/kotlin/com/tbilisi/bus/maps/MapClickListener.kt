package com.tbilisi.bus.maps

import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.tbilisi.bus.ScheduleActivity
import com.tbilisi.bus.data.BusStop
import com.tbilisi.bus.util.MarkerHelper
import com.tbilisi.bus.util.LocalizationHelper

class MapClickListener(val context: Context): GoogleMap.OnInfoWindowClickListener {
    override fun onInfoWindowClick(marker: Marker?) {
        if(marker == null)
            return

        val stopId = marker.tag as String

        val intent = Intent(context, ScheduleActivity::class.java)
        intent.putExtra("id", stopId)
        context.startActivity(intent)
    }
}