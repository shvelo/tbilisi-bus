package com.tbilisi.bus

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button

import com.crashlytics.android.Crashlytics
import com.google.zxing.integration.android.IntentIntegrator
import com.tbilisi.bus.data.BusStop

import java.io.IOException
import java.util.regex.Pattern

import io.realm.Realm

public class MainActivity : AppCompatActivity() {
    private var qr_pattern: Pattern? = null
    private var searchFragment: SearchFragment? = null
    private var scheduleFragment: ScheduleFragment? = null
    private var scheduleButton: Button? = null
    private var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Crashlytics.start(this)
        realm = Realm.getInstance(this)
        loadRealm()
        setContentView(R.layout.activity_main)

        qr_pattern = Pattern.compile("smsto:([0-9]+):([0-9]+)", Pattern.CASE_INSENSITIVE)
        searchFragment = supportFragmentManager.findFragmentById(R.id.search_fragment) as SearchFragment
        scheduleFragment = supportFragmentManager.findFragmentById(R.id.schedule_fragment) as ScheduleFragment
        scheduleButton = findViewById(R.id.hide_schedule) as Button

        scheduleButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                hideSchedule()
            }
        })

        hideSchedule()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun loadRealm() {
        try {
            if (realm!!.where(BusStop::class.java).count() == 0L) {
                var elapsed = System.nanoTime()
                Log.i("Realm", "Initializing DB")
                val `is` = assets.open("db.json")
                realm!!.beginTransaction()
                realm!!.createAllFromJson(BusStop::class.java, `is`)
                realm!!.commitTransaction()
                elapsed = (System.nanoTime() - elapsed) / 1000000
                Log.i("Realm", "Initialized DB in " + elapsed.toString() + "ms")
            }
        } catch (e: IOException) {
            realm!!.cancelTransaction()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_app, menu)
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.isSubmitButtonEnabled = false

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                searchFragment!!.updateList(s)
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                searchFragment!!.updateList(s)
                return false
            }
        })

        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                searchFragment!!.clearList()
                return false
            }
        })

        val scan = menu.findItem(R.id.scan)
        scan.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                val integrator = IntentIntegrator(this@MainActivity)
                integrator.initiateScan()
                return true
            }
        })

        val history = menu.findItem(R.id.history)
        history.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                val intent_history = Intent(this@MainActivity, HistoryActivity::class.java)
                startActivity(intent_history)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    public fun showSchedule(stopId: Int) {
        findViewById(R.id.schedule_fragment).visibility = View.VISIBLE
        scheduleButton!!.visibility = View.VISIBLE
        val stopName = realm!!.where(BusStop::class.java).equalTo("id", stopId).findFirst().name
        scheduleButton!!.text = stopName
        scheduleFragment!!.showSchedule(stopId)
    }

    public fun hideSchedule() {
        findViewById(R.id.schedule_fragment).visibility = View.GONE
        scheduleButton!!.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent)
        if (scanResult != null) {
            val scanned = scanResult.contents ?: return

            val m = qr_pattern!!.matcher(scanned)
            if (m.find()) showSchedule(Integer.parseInt(m.group(2)))
        }
    }
}
