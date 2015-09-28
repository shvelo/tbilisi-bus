package com.tbilisi.bus.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.tbilisi.bus.R
import com.tbilisi.bus.data.MapItem

import java.util.ArrayList

public class StopListAdapter(context: Context, public var stops: ArrayList<MapItem>) : BaseAdapter() {
    private val inflater: LayoutInflater

    init {
        this.inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return stops.size()
    }

    override fun getItem(i: Int): Any {
        return stops.get(i)
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        var view = view
        if (view == null) {
            view = inflater.inflate(R.layout.stop_list_item, viewGroup, false)
        }
        val stop = stops.get(i)

        val stopNumber = view!!.findViewById(R.id.stopNumber) as TextView
        val stopName = view.findViewById(R.id.stopName) as TextView

        stopNumber.text = stop.id.toString()
        stopName.text = stop.name

        view.tag = stop.id

        return view
    }
}
