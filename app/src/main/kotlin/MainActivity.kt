package com.tbilisi.bus

import android.app.Fragment
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import com.tbilisi.bus.fragments.HistoryFragment
import com.tbilisi.bus.fragments.InfoFragment
import com.tbilisi.bus.fragments.MapFragment
import kotlinx.android.synthetic.activity_main.*
import java.util.*

public class MainActivity() : AppCompatActivity() {
    var activeFragmentId = R.id.drawer_map
    var drawerToggle: ActionBarDrawerToggle? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        restoreLocale()
        setContentView(R.layout.activity_main);

        setSupportActionBar(toolbar)
        setupNavigationDrawer()

        activeFragmentId = savedInstanceState?.getInt("fragment") ?: activeFragmentId

        Log.d("activeFragmentId", activeFragmentId.toString())
        Log.d("mapFragmentId", R.id.drawer_map.toString())


        setActive(activeFragmentId)
    }

    fun setActive(fragmentId: Int): Boolean {
        when (fragmentId) {
            R.id.drawer_map -> {
                setFragment(MapFragment())
                supportActionBar.title = getString(R.string.title_map)
                activeFragmentId = fragmentId
                return true
            }
            R.id.drawer_history -> {
                setFragment(HistoryFragment())
                supportActionBar.title = getString(R.string.title_history)
                activeFragmentId = fragmentId
                return true
            }
            R.id.drawer_info -> {
                setFragment(InfoFragment())
                supportActionBar.title = getString(R.string.title_info)
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
        fragmentManager.beginTransaction().replace(R.id.content_layout, fragment).commit()
    }

    fun setupNavigationDrawer() {
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawerLayout.setDrawerListener(drawerToggle)
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeButtonEnabled(true);

        drawer.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()
            setActive(it.itemId)
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        Log.d("save activeFragmentId", activeFragmentId.toString())

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
        if (Locale.getDefault().equals(Locale("ka"))) {
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
        super.onConfigurationChanged(newConfig);
        drawerToggle?.onConfigurationChanged(newConfig);
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState);
        drawerToggle?.syncState();
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle!!.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}