package com.tbilisi.bus

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.tbilisi.bus.data.BusInfo
import com.tbilisi.bus.data.BusStop
import com.tbilisi.bus.data.FavoriteStore
import com.tbilisi.bus.data.HistoryStore
import com.tbilisi.bus.util.BusInfoAdapter
import com.tbilisi.bus.util.LocalizationHelper
import com.tbilisi.bus.util.ScheduleRetriever
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_schedule.*
import java.util.*

class ScheduleActivity : AppCompatActivity() {
    val LOG_TAG = "ScheduleActivity"
    val FAVORITE_REMOVED = 1
    val FAVORITE_ADDED = 2
    // auto-refresh every 30 seconds
    val REFRESH_DELAY = 30 * 1000L

    // info about current bus stop
    var stop: BusStop? = null
    // bus info list
    val busList = ArrayList<BusInfo>()
    var menu: Menu? = null

    val handler = Handler()
    val refreshRunnable = { refresh() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        // use Toolbar as ActionBar
        setSupportActionBar(toolbar)
        // show up button in actionbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // initialize RecyclerView
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = BusInfoAdapter(busList, this)

        button_refresh.setOnClickListener {
            refresh()
        }

        refresh_layout.setOnRefreshListener {
            refresh()
        }

        // handle intent to get schedule
        handleIntent(intent)

        setupAds()
    }

    fun setupAds() {
        if(BuildConfig.FLAVOR == "adfree") {
            adView.visibility = View.GONE
            return
        }
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: Int) {
                adView.visibility = View.GONE
                super.onAdFailedToLoad(p0)
            }

            override fun onAdLoaded() {
                adView.visibility = View.VISIBLE
                super.onAdLoaded()
            }
        }
        val adRequest = AdRequest.Builder()
                .addTestDevice(resources.getString(R.string.ads_test_device))
                .build()
        adView.loadAd(adRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.schedule, menu)

        if(stop != null && FavoriteStore.isFavorite(stop!!)) {
            menu?.findItem(R.id.menu_favorite)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_36dp)
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
                    item.icon = ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_white_36dp)
                if(result == FAVORITE_ADDED)
                    item.icon = ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_36dp)
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
        val stopId = intent.getStringExtra("id") ?: return

        stop = Realm.getDefaultInstance().where(BusStop::class.java).equalTo("id", stopId).findFirst() ?: return

        title = LocalizationHelper.getLocalizedStopName(stop!!)
        toolbar.subtitle = stop!!.id

        HistoryStore.addToHistory(stop!!)

        if(FavoriteStore.isFavorite(stop!!)) {
            menu?.findItem(R.id.menu_favorite)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_36dp)
        }

        refresh()
    }

    /**
     * Refresh schedule
     */
    fun refresh() {
        showProgress()

        ScheduleRetriever.retrieve(stop!!.id, this, {
            runOnUiThread {
                hideProgress()
                updateList(it)
                handler.removeCallbacks(refreshRunnable)
                handler.postDelayed(refreshRunnable, REFRESH_DELAY)
            }
        }, {
            runOnUiThread {
                hideProgress()
                showError()
                Log.e(LOG_TAG, "Error retrieving schedule for ${stop?.id}")
                it?.printStackTrace()
                handler.removeCallbacks(refreshRunnable)
                handler.postDelayed(refreshRunnable, REFRESH_DELAY)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(refreshRunnable)
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
        val snackbar = Snackbar.make(findViewById(android.R.id.content)!!, R.string.error, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction("OK", {
            snackbar.dismiss()
        }).show()
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
        refresh_layout.isRefreshing = true
    }

    /**
     * Hide progressbar
     */
    fun hideProgress() {
        refresh_layout.isRefreshing = false
    }
}
