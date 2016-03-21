package com.tbilisi.bus.fragments

import android.app.Fragment
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.tbilisi.bus.R
import com.tbilisi.bus.SearchActivity
import com.tbilisi.bus.util.BusMapListener
import com.tbilisi.bus.util.BusMapViewListener
import io.realm.Realm

class MapFragment : Fragment(), OnMapReadyCallback {
    var map: GoogleMap? = null

    override fun onMapReady(readyMap: GoogleMap?) {
        map = readyMap
        Toast.makeText(activity, "Map initialized", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val createdView = inflater.inflate(R.layout.fragment_map, container, false)

        var mapFragment = (activity as AppCompatActivity).supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment;
        mapFragment.getMapAsync(this);

        return createdView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.map, menu)

        if (menu != null)
            setupSearch(menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_locate -> {
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun setupSearch(menu: Menu) {
        val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                ComponentName(activity.applicationContext, SearchActivity::class.java)))
    }
}
