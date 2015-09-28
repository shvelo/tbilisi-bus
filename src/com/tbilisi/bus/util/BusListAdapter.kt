package com.tbilisi.bus.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.tbilisi.bus.R
import com.tbilisi.bus.data.BusInfo

import java.util.ArrayList

public class BusListAdapter(private val context: Context, public var busList: ArrayList<BusInfo>?) : BaseAdapter() {

    public fun update(newList: ArrayList<BusInfo>?) {
        busList = newList!!
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return busList!!.size()
    }

    override fun getItem(i: Int): Any {
        return busList!!.get(i)
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup): View {
        var view: View? = convertView
        val item = busList!!.get(i)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if (view == null) {
            view = inflater.inflate(R.layout.bus_info, viewGroup, false)
        }

        val busNumber = view!!.findViewById(R.id.busNumber) as TextView
        val busDestination = view.findViewById(R.id.busDestination) as TextView
        val busArrival = view.findViewById(R.id.busArrival) as TextView

        busNumber.text = item.number.toString()
        busDestination.text = item.destination
        busArrival.text = item.arrival.toString() + "წთ"

        return view
    }

    override fun isEmpty(): Boolean {
        return busList!!.isEmpty()
    }
}
