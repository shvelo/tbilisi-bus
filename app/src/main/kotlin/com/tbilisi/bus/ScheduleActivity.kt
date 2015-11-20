package com.tbilisi.bus

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.tbilisi.bus.util.BusInfoAdapter
import kotlinx.android.synthetic.activity_schedule.*

class ScheduleActivity : AppCompatActivity() {
    var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        setSupportActionBar(toolbar)
        supportActionBar.setDisplayHomeAsUpEnabled(true)

        list.layoutManager = LinearLayoutManager(this)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    fun handleIntent(intent: Intent) {
        id = intent.getIntExtra("id", 0)
        title = id.toString()

        updateInfo()
    }

    fun updateInfo() {
        ScheduleRetriever.retrieve(id, this, {
            runOnUiThread {
                list.adapter = BusInfoAdapter(it)
            }
        })
    }
}
