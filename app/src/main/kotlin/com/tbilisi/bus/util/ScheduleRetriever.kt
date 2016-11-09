package com.tbilisi.bus.util

import android.content.Context
import android.util.Log
import com.squareup.okhttp.*
import com.tbilisi.bus.R
import com.tbilisi.bus.data.BusInfo
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*

/**
 * Helper for retrieving bus schedules
 */
object ScheduleRetriever {
    val LOG_TAG = "ScheduleRetriever"
    val client: OkHttpClient = OkHttpClient()

    /**
     * Retrieve schedule for the stop
     * @param id stop ID
     */
    fun retrieve(id: String, context: Context, callback: (ArrayList<BusInfo>) -> Unit, errorCallback: (IOException?) -> Unit) {
        Log.d(LOG_TAG, "Retrieving schedule for $id")

        // build bus schedule request
        val request = Request.Builder()
                .url(getUrlForStop(id, context))
                .build()

        // use async call to retrieve info
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(request: Request?, e: IOException?) {
                // error received, call error callback
                errorCallback(e)
            }

            override fun onResponse(response: Response?) {
                val responseBody = response?.body()?.string()
                if(responseBody != null) {
                    // parse document
                    val document = Jsoup.parse(responseBody)
                    // get all bus info elements
                    val elements = document.select(".arrivalTimesScrol tr")

                    // create BusInfo list with parsed data
                    val buses = ArrayList<BusInfo>()
                    elements.forEach {
                        val busNum = it.child(0).text().toInt()
                        val direction = it.child(1).text()
                        val time = it.child(2).text().toInt()

                        buses.add(BusInfo(busNum, direction, time))
                    }

                    // call callback with BusInfo list
                    callback(buses)
                } else {
                    // if response not received, call error callback
                    errorCallback(null)
                }
            }
        })
    }

    /**
     * Get schedule service URL for stop
     * @param id stop ID
     */
    fun getUrlForStop(id: String, context: Context): HttpUrl {
        // build stop schedule url
        return HttpUrl.parse(context.getString(R.string.service_url)).newBuilder()
                .addQueryParameter("stopId", id)
                .build()
    }
}