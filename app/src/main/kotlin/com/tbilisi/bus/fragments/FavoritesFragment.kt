package com.tbilisi.bus.fragments

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Toast
import com.tbilisi.bus.R
import com.tbilisi.bus.ScheduleActivity
import com.tbilisi.bus.data.BusStop
import com.tbilisi.bus.data.FavoriteStore
import com.tbilisi.bus.util.BusStopAdapter
import java.util.*

class FavoritesFragment : Fragment() {
    val stopList = ArrayList<BusStop>()
    var listView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val createdView = inflater.inflate(R.layout.fragment_favorites, container, false)

        listView = createdView.findViewById(R.id.list) as RecyclerView

        val adapter = BusStopAdapter(stopList)
        listView?.layoutManager = LinearLayoutManager(context)
        listView?.adapter = adapter

        adapter.onClickListener = object: BusStopAdapter.OnClickListener {
            override fun onClick(view: View, stop: BusStop) {
                showSchedule(stop)
            }
        }

        getFavorites()

        return createdView
    }

    fun getFavorites() {
        val result = FavoriteStore.getFavorites()
        stopList.clear()
        stopList.addAll(result.map({ it.stop!! }))
        listView?.adapter?.notifyDataSetChanged()
    }

    fun showSchedule(stop: BusStop) {
        val intent = Intent(context, ScheduleActivity::class.java)
        intent.putExtra("id", stop.id)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.favorites, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_clear -> {
                clearFavorites()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun clearFavorites() {
        FavoriteStore.clearFavorites()
        stopList.clear()
        listView?.adapter?.notifyDataSetChanged()
        Toast.makeText(context, R.string.favorites_cleared, Toast.LENGTH_SHORT).show()
    }
}
