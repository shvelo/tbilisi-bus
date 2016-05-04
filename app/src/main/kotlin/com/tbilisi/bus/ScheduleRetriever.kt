package com.tbilisi.bus

import android.content.Context
import android.util.Log
import com.squareup.okhttp.*
import com.tbilisi.bus.data.BusInfo
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*

object ScheduleRetriever {
    val LOG_TAG = "ScheduleRetriever"
    val client: OkHttpClient = OkHttpClient()

    /**
     * Retrieve schedule for the stop
     * @param id stop ID
     */
    fun retrieve(id: Int, context: Context, callback: (ArrayList<BusInfo>) -> Unit, errorCallback: (IOException?) -> Unit) {
        Log.d(LOG_TAG, "Retrieving schedule for $id")

        val request = Request.Builder()
                .url(getUrlForStop(id, context))
                .build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(request: Request?, e: IOException?) {
                errorCallback(e)
            }

            override fun onResponse(response: Response?) {
                val responseBody = response?.body()?.string()
                if(responseBody != null) {
                    val document = Jsoup.parse(responseBody)
                    val elements = document.select(".arrivalTimesScrol tr")
                    var buses = ArrayList<BusInfo>()

                    elements.forEach {
                        val busNum = it.child(0).text().toInt()
                        val direction = it.child(1).text()
                        val time = it.child(2).text().toInt()

                        buses.add(BusInfo(busNum, direction, time))
                    }

                    callback(buses)
                }
            }
        })
    }

    /**
     * Get schedule service URL for stop
     * @param id stop ID
     */
    fun getUrlForStop(id: Int, context: Context): HttpUrl {
        return HttpUrl.parse(context.getString(R.string.service_url)).newBuilder()
                .addQueryParameter("stopId", id.toString())
                .build()
    }
}