package com.tbilisi.bus.util

import android.content.Context
import com.tbilisi.bus.R
import com.tbilisi.bus.data.BusStop
import java.util.*

object LocalizationHelper {
    fun getLocalizedStopName(busStop: BusStop): String {
        if (Locale.getDefault() == Locale("ka")) {
            return busStop.name
        } else {
            return busStop.name_en
        }
    }

    fun getLocalizedTime(time: Int, context: Context): String {
        val timeFormat = context.resources.getString(R.string.time_format)
        return timeFormat.format(time)
    }
}