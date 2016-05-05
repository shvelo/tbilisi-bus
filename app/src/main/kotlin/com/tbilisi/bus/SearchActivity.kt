package com.tbilisi.bus

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.tbilisi.bus.data.BusStop
import com.tbilisi.bus.data.BusStopStore
import com.tbilisi.bus.util.BusStopAdapter
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*

class SearchActivity : AppCompatActivity() {
    val LOG_TAG = "SearchActivity"
    var searchButton: MenuItem? = null
    var query: String? = null
    val stopList = ArrayList<BusStop>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(toolbar)
        title = getString(R.string.search)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        list.adapter = BusStopAdapter(stopList)
        list.layoutManager = LinearLayoutManager(this)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH.equals(intent.action)) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            search(query)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)

        if (menu != null) {
            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchButton = menu.findItem(R.id.menu_search)
            val searchView = searchButton?.actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.setIconifiedByDefault(false)
            searchView.setQuery(query, true)
            MenuItemCompat.expandActionView(searchButton)
            searchView.isSubmitButtonEnabled = true

            if(query.isNullOrEmpty())
                searchView.requestFocus()
        }

        return super.onCreateOptionsMenu(menu)
    }

    fun search(queryString: String) {
        query = queryString
        title = queryString

        val results = BusStopStore.findByQuery(query!!)
        stopList.clear()
        stopList.addAll(results)
        list.adapter?.notifyDataSetChanged()
    }
}
