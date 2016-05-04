package com.tbilisi.bus

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.tbilisi.bus.data.BusStop
import com.tbilisi.bus.util.BusInfoAdapter
import com.tbilisi.bus.util.StopHelper
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_schedule.*

class ScheduleActivity : AppCompatActivity() {
    var stop: BusStop? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        list.layoutManager = LinearLayoutManager(this)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    fun handleIntent(intent: Intent) {
        val stopId = intent.getIntExtra("id", -1)
        if(stopId == -1)
            return

        stop = Realm.getInstance(this).where(BusStop::class.java).equalTo("id", stopId).findFirst()

        if(stop == null)
            return

        title = StopHelper.getLocalizedName(stop!!)
        toolbar.subtitle =  stop!!.id.toString()

        updateInfo()
    }

    fun updateInfo() {
        showProgress()
        ScheduleRetriever.retrieve(stop!!.id, this, {
            runOnUiThread {
                hideProgress()
                list.adapter = BusInfoAdapter(it)
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
