package com.tbilisi.bus

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.tbilisi.bus.data.BusInfo
import com.tbilisi.bus.data.BusStop
import com.tbilisi.bus.util.BusInfoAdapter
import com.tbilisi.bus.data.FavoriteStore
import com.tbilisi.bus.data.HistoryStore
import com.tbilisi.bus.util.ScheduleRetriever
import com.tbilisi.bus.util.StopHelper
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_schedule.*
import java.util.*

class ScheduleActivity : AppCompatActivity() {
    // info about current bus stop
    var stop: BusStop? = null
    // bus info list
    val busList = ArrayList<BusInfo>()
    var menu: Menu? = null

    val FAVORITE_REMOVED = 1
    val FAVORITE_ADDED = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        // use Toolbar as ActionBar
        setSupportActionBar(toolbar)
        // show up button in actionbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // initialize RecyclerView
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = BusInfoAdapter(busList)

        // handle intent to get schedule
        handleIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.schedule, menu);

        if(stop != null && FavoriteStore.isFavorite(stop!!)) {
            menu?.findItem(R.id.menu_favorite)?.icon = resources.getDrawable(R.drawable.ic_favorite_white_36dp)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_refresh -> {
                refresh()
                return true
            }
            R.id.menu_favorite -> {
                val result = toggleFavorite()
                if(result == FAVORITE_REMOVED)
                    item?.icon = resources.getDrawable(R.drawable.ic_favorite_border_white_36dp)
                if(result == FAVORITE_ADDED)
                    item?.icon = resources.getDrawable(R.drawable.ic_favorite_white_36dp)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    /**
     * Handle new intent
     */
    fun handleIntent(intent: Intent) {
        val stopId = intent.getIntExtra("id", -1)
        if (stopId == -1)
            return

        stop = Realm.getDefaultInstance().where(BusStop::class.java).equalTo("id", stopId).findFirst()

        if (stop == null)
            return

        title = StopHelper.getLocalizedName(stop!!)
        toolbar.subtitle = stop!!.id.toString()

        HistoryStore.addToHistory(stop!!)

        if(FavoriteStore.isFavorite(stop!!)) {
            menu?.findItem(R.id.menu_favorite)?.icon = resources.getDrawable(R.drawable.ic_favorite_white_36dp)
        }

        refresh()
    }

    /**
     * Refresh schedule
     */
    fun refresh() {
        hideError()
        showProgress()

        ScheduleRetriever.retrieve(stop!!.id, this, {
            runOnUiThread {
                hideProgress()
                updateList(it)
            }
        }, {
            runOnUiThread {
                hideProgress()
                showError()
            }
        })
    }

    /**
     * Update displayed list with new data
     */
    fun updateList(newList: ArrayList<BusInfo>) {
        busList.clear()
        busList.addAll(newList)

        if(busList.isEmpty()) {
            showNoData()
        } else {
            hideNoData()
        }

        list.adapter.notifyDataSetChanged()
    }

    fun toggleFavorite(): Int? {
        if(stop == null)
            return null

        if(FavoriteStore.isFavorite(stop!!)) {
            FavoriteStore.removeFromFavorites(stop!!)
            return FAVORITE_REMOVED
        } else {
            FavoriteStore.addToFavorites(stop!!)
            return FAVORITE_ADDED
        }
    }

    /**
     * Show error view
     */
    fun showError() {
        errorView.visibility = View.VISIBLE
    }

    /**
     * Hide error view
     */
    fun hideError() {
        errorView.visibility = View.GONE
    }

    /**
     * Show "no data" view
     */
    fun showNoData() {
        list.visibility = View.GONE
        noData.visibility = View.VISIBLE
    }

    /**
     * Hide "no data" view
     */
    fun hideNoData() {
        list.visibility = View.VISIBLE
        noData.visibility = View.GONE
    }

    /**
     * Show progressbar
     */
    fun showProgress() {
        progressDisplay.visibility = View.VISIBLE
    }

    /**
     * Hide progressbar
     */
    fun hideProgress() {
        progressDisplay.visibility = View.GONE
    }
}
