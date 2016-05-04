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
import com.tbilisi.bus.util.StopHelper
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_schedule.*
import java.util.*

class ScheduleActivity : AppCompatActivity() {
    var stop: BusStop? = null
    val stopList = ArrayList<BusInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = BusInfoAdapter(stopList)

        handleIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.schedule, menu);

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_refresh -> {
                updateInfo()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    fun handleIntent(intent: Intent) {
        val stopId = intent.getIntExtra("id", -1)
        if (stopId == -1)
            return

        stop = Realm.getInstance(this).where(BusStop::class.java).equalTo("id", stopId).findFirst()

        if (stop == null)
            return

        title = StopHelper.getLocalizedName(stop!!)
        toolbar.subtitle = stop!!.id.toString()

        updateInfo()
    }

    fun updateInfo() {
        showProgress()
        ScheduleRetriever.retrieve(stop!!.id, this, {
            stopList.clear()
            stopList.addAll(it)

            runOnUiThread {
                list.adapter.notifyDataSetChanged()
                hideProgress()
            }
        })
    }

    fun showProgress() {
        progressDisplay.visibility = View.VISIBLE
    }

    fun hideProgress() {
        progressDisplay.visibility = View.GONE
    }
}
