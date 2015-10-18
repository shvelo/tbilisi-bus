package com.tbilisi.bus.util

import android.content.Context
import com.tbilisi.bus.R
import com.tbilisi.bus.data.BusInfo
import com.tbilisi.bus.data.BusStop
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*

object StopHelper {
    fun getLocalizedName(busStop: BusStop): String {
        if (Locale.getDefault().equals(Locale("ka"))) {
            return busStop.name
        } else {
            return busStop.name_en
        }
    }

    fun getSchedule(busStop: BusStop, context: Context): ArrayList<BusInfo> {
        val doc = Jsoup.connect(context.getString(R.string.service_url, busStop.id)).get();
        val buses = ArrayList<BusInfo>()
        for(tr in doc.select(".arrivalTimesInnerTable tr")) {
            val bus = tr.child(0).text().toInt()
            val direction = tr.child(1).text()
            val time = tr.child(2).text().toInt()
            buses.add(BusInfo(bus, direction, time))
        }
        return buses
    }
}