package com.tbilisi.bus

import android.support.v4.app.Fragment
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView

import com.tbilisi.bus.data.BusInfo
import com.tbilisi.bus.data.HistoryItem
import com.tbilisi.bus.util.BusListAdapter

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.util.ArrayList

import io.realm.Realm

public class ScheduleFragment : Fragment() {
    public var busList: ArrayList<BusInfo>? = null
    private var stopId: Int = 0
    private var adapter: BusListAdapter? = null
    private var url: String? = null
    private val delay = 30000 //Auto-update interval, seconds x 1000
    private var handler: Handler? = null
    private var autoUpdater: AutoUpdater? = null
    private var realm: Realm? = null
    private var mView: View? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater!!.inflate(R.layout.fragment_schedule, container, false)
        return mView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        realm = Realm.getInstance(activity)

        val listView = mView!!.findViewById(R.id.busSchedule) as ListView
        loadList()
        adapter = BusListAdapter(activity, busList)
        listView.adapter = adapter

        handler = Handler()
        autoUpdater = AutoUpdater()
    }

    override fun onResume() {
        super.onResume()
        handler!!.postDelayed(autoUpdater, delay.toLong())
    }

    override fun onPause() {
        super.onPause()
        handler!!.removeCallbacks(autoUpdater)
    }

    override fun onDestroy() {
        if (realm != null) realm!!.close()
        super.onDestroy()
    }

    public fun loadList() {
        if (busList == null) busList = ArrayList<BusInfo>()
        url = API + stopId
        ListLoader().execute()
    }

    private inner class AutoUpdater : Runnable {
        override fun run() {
            reload()
            handler!!.postDelayed(this, delay.toLong())
        }
    }

    private inner class ListLoader : AsyncTask<Void, Int, Void>() {
        override fun doInBackground(vararg voids: Void?): Void? {
            val doc: Document
            val newBusList = ArrayList<BusInfo>()
            try {
                doc = Jsoup.connect(url).get()
                val elements = doc.select(".arrivalTimesScrol tr")
                for (element in elements) {
                    var i = 0

                    var busNumber = 0
                    var busDestination = ""
                    var busArrival = 0

                    for (td in element.children()) {
                        val text = td.text()
                        if (i == 0) {
                            busNumber = Integer.valueOf(text)!!
                        } else if (i == 1) {
                            busDestination = text
                        } else {
                            busArrival = Integer.valueOf(text)!!
                        }
                        i++
                    }

                    newBusList.add(BusInfo(busNumber, busDestination, busArrival))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (newBusList.size() > 0) {
                busList = newBusList
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            adapter!!.update(busList)
            if (busList!!.size() == 0) {
                mView!!.findViewById(R.id.nothing_found).visibility = View.VISIBLE
            } else {
                mView!!.findViewById(R.id.nothing_found).visibility = View.GONE
            }
        }
    }

    public fun reload() {
        loadList()
        adapter!!.update(busList)
    }

    private fun saveToHistory() {
        try {
            if (realm!!.where(HistoryItem::class.java).equalTo("id", stopId).count() == 0L) {
                realm!!.beginTransaction()
                realm!!.createObject(HistoryItem::class.java).id = stopId
                realm!!.commitTransaction()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    public fun showSchedule(id: Int) {
        stopId = id
        loadList()
        saveToHistory()
    }

    companion object {
        private val API = "http://transit.ttc.com.ge/pts-portal-services/servlet/stopArrivalTimesServlet?stopId="
    }
}
