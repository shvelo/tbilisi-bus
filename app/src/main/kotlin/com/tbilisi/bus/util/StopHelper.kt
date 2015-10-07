package com.tbilisi.bus.util

import com.tbilisi.bus.data.BusStop
import java.util.*

object StopHelper {
    fun getLocalizedName(busStop: BusStop): String {
        if (Locale.getDefault().equals(Locale("ka"))) {
            return busStop.name
        } else {
            return busStop.name_en
        }
    }
}