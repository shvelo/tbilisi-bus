package com.tbilisi.bus.util

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tbilisi.bus.R
import com.tbilisi.bus.data.BusInfo

class BusInfoAdapter(val dataset: List<BusInfo>, val context: Context): RecyclerView.Adapter<BusInfoAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.bus_info, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val item = dataset[position]
        holder?.text_bus?.text = item.bus.toString()
        holder?.text_direction?.text = item.direction
        holder?.text_time?.text = LocalizationHelper.getLocalizedTime(item.time, context)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val text_bus: TextView = view.findViewById(R.id.text_bus) as TextView
        val text_direction: TextView = view.findViewById(R.id.text_direction) as TextView
        val text_time: TextView = view.findViewById(R.id.text_time) as TextView
    }
}