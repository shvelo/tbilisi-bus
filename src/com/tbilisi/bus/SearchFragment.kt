package com.tbilisi.bus

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView

import com.tbilisi.bus.data.BusStop
import com.tbilisi.bus.data.MapItem
import com.tbilisi.bus.util.StopListAdapter

import java.util.ArrayList

import io.realm.Realm

public class SearchFragment : Fragment() {
    private var list: ListView? = null
    private var items: ArrayList<MapItem>? = null
    private var adapter: StopListAdapter? = null
    private var realm: Realm? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_search, container) as ViewGroup
        list = view.findViewById(R.id.list) as ListView
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        realm = Realm.getInstance(activity)
        items = ArrayList<MapItem>()
        adapter = StopListAdapter(activity, items!!)
        list!!.adapter = adapter

        list!!.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                val id = view.tag as Int
                clearList()
                showSchedule(id)
            }
        }
    }

    private fun showSchedule(stopId: Int) {
        (activity as MainActivity).showSchedule(stopId)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (realm != null) realm!!.close()
    }

    public fun updateList(id: String) {
        var id = id
        try {
            items!!.clear()
            id = id.trim()
            val results = realm!!.where(BusStop::class.java).contains("name", id).findAll()
            for (result in results) {
                items!!.add(MapItem(result))
            }
            adapter!!.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    public fun clearList() {
        items!!.clear()
        adapter!!.notifyDataSetChanged()
    }
}
