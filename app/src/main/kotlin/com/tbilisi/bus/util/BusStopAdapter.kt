package com.tbilisi.bus.util

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tbilisi.bus.R
import com.tbilisi.bus.data.BusStop

class BusStopAdapter(val dataset: List<BusStop>): RecyclerView.Adapter<BusStopAdapter.ViewHolder>() {
    var onClickListener: BusStopAdapter.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.stop_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val item = dataset[position]
        holder?.text_id?.text = item.id
        holder?.text_name?.text = LocalizationHelper.getLocalizedStopName(item)
        holder?.itemView?.setOnClickListener {
            onClickListener?.onClick(it, item)
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val text_id: TextView = view.findViewById(R.id.text_id) as TextView
        val text_name: TextView = view.findViewById(R.id.text_name) as TextView
    }

    interface OnClickListener {
        fun onClick(view: View, stop: BusStop)
    }
}