package com.tbilisi.bus

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView

import com.tbilisi.bus.data.BusStop
import com.tbilisi.bus.data.HistoryItem
import com.tbilisi.bus.data.MapItem
import com.tbilisi.bus.util.StopListAdapter

import java.util.ArrayList

import io.realm.Realm

public class HistoryActivity : AppCompatActivity() {
    private var items: ArrayList<MapItem>? = null
    private var adapter: StopListAdapter? = null
    private var scheduleFragment: ScheduleFragment? = null
    private var scheduleButton: Button? = null
    private var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        realm = Realm.getInstance(this)

        val actionBar = supportActionBar
        actionBar.setDisplayHomeAsUpEnabled(true)

        val list = findViewById(R.id.listView) as ListView
        items = ArrayList<MapItem>()
        adapter = StopListAdapter(this, items!!)
        list.adapter = adapter

        list.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                showSchedule(view.tag as Int)
            }
        }

        loadList()

        scheduleFragment = supportFragmentManager.findFragmentById(R.id.schedule_fragment) as ScheduleFragment
        scheduleButton = findViewById(R.id.hide_schedule) as Button

        scheduleButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                hideSchedule()
            }
        })

        hideSchedule()
    }

    public fun showSchedule(stopId: Int) {
        findViewById(R.id.schedule_fragment).visibility = View.VISIBLE
        scheduleButton!!.visibility = View.VISIBLE
        val stopName = realm!!.where<BusStop>(BusStop::class.java).equalTo("id", stopId).findFirst().name
        scheduleButton!!.text = stopName
        scheduleFragment!!.showSchedule(stopId)
    }

    public fun hideSchedule() {
        findViewById(R.id.schedule_fragment).visibility = View.GONE
        scheduleButton!!.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        if (realm != null) realm!!.close()
    }

    public fun loadList() {
        items!!.clear()
        try {
            for (item in realm!!.allObjects<HistoryItem>(HistoryItem::class.java)) {
                Log.d("History", item.id.toString())
                val stop = realm!!.where<BusStop>(BusStop::class.java).equalTo("id", item.id).findFirst()
                items!!.add(MapItem(stop))
            }
            adapter!!.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_history, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                clear()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    public fun clear() {
        realm!!.beginTransaction()
        realm!!.where<HistoryItem>(HistoryItem::class.java).findAll().clear()
        realm!!.commitTransaction()
        loadList()
    }
}
