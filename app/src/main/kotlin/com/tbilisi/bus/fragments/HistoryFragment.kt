package com.tbilisi.bus.fragments

import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay
import com.mapbox.mapboxsdk.tileprovider.tilesource.WebSourceTileLayer
import com.mapbox.mapboxsdk.views.MapView
import com.tbilisi.bus.R

public class HistoryFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val createdView = inflater.inflate(R.layout.fragment_history, container, false)
        return createdView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.history, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_clear -> {
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
