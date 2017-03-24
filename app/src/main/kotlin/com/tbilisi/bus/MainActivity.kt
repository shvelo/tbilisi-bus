package com.tbilisi.bus

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.tbilisi.bus.fragments.BusMapFragment
import com.tbilisi.bus.fragments.FavoritesFragment
import com.tbilisi.bus.fragments.HistoryFragment
import com.tbilisi.bus.fragments.InfoFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    var activeFragmentId = R.id.drawer_map
    var drawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restoreLocale()
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        setupNavigationDrawer()
        setupAds()

        activeFragmentId = savedInstanceState?.getInt("fragment") ?: getSavedFragmentId() ?: activeFragmentId

        setActive(activeFragmentId)
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

    fun getSavedFragmentId(): Int? {
        val lastFragmentId = getSharedPreferences("default", 0).getInt("lastFragment", -1)
        if(lastFragmentId == -1)
            return null
        return lastFragmentId
    }

    fun saveFragmentId(id: Int) {
        getSharedPreferences("default", 0).edit().putInt("lastFragment", id).apply()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun setActive(fragmentId: Int): Boolean {
        drawer.setCheckedItem(fragmentId)
        when (fragmentId) {
            R.id.drawer_map -> {
                saveFragmentId(fragmentId)
                setFragment(BusMapFragment())
                supportActionBar?.title = getString(R.string.title_map)
                activeFragmentId = fragmentId
                return true
            }
            R.id.drawer_favorites -> {
                saveFragmentId(fragmentId)
                setFragment(FavoritesFragment())
                supportActionBar?.title = getString(R.string.title_favorites)
                activeFragmentId = fragmentId
                return true
            }
            R.id.drawer_history -> {
                saveFragmentId(fragmentId)
                setFragment(HistoryFragment())
                supportActionBar?.title = getString(R.string.title_history)
                activeFragmentId = fragmentId
                return true
            }
            R.id.drawer_search -> {
                startActivity(Intent(this, SearchActivity::class.java))
                return true
            }
            R.id.drawer_info -> {
                saveFragmentId(fragmentId)
                setFragment(InfoFragment())
                supportActionBar?.title = getString(R.string.title_info)
                activeFragmentId = fragmentId
                return true
            }
            R.id.drawer_language -> {
                switchLocale()
                return false
            }
            else -> return false
        }
    }

    fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_layout, fragment)
            .commit()
    }

    fun setupNavigationDrawer() {
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawerLayout.addDrawerListener(drawerToggle!!)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        drawer.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()
            setActive(it.itemId)
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt("fragment", activeFragmentId)
        super.onSaveInstanceState(outState)
    }

    fun restoreLocale() {
        val locale = getSharedPreferences("default", 0).getString("locale", null)
        if(locale != null) {
            setLocale(locale)
        }
    }

    fun switchLocale() {
        if (Locale.getDefault() == Locale("ka")) {
            setLocale("en")
        } else {
            setLocale("ka")
        }
        restartActivity()
    }

    fun setLocale(localeName: String) {
        val locale = Locale(localeName)

        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale

        val preferences = getSharedPreferences("default", 0)
        preferences.edit().putString("locale", localeName).apply()

        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    fun restartActivity() {
        recreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        drawerToggle?.onConfigurationChanged(newConfig)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle?.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle!!.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}