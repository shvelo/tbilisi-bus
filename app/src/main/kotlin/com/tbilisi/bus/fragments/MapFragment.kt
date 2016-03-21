package com.tbilisi.bus.fragments

import android.app.Fragment
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.*
import com.tbilisi.bus.R
import com.tbilisi.bus.SearchActivity
import com.tbilisi.bus.util.BusMapListener
import com.tbilisi.bus.util.BusMapViewListener
import io.realm.Realm

class MapFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val createdView = inflater.inflate(R.layout.fragment_map, container, false)


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
