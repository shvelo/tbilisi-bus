package com.tbilisi.bus

import android.app.Fragment
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.tbilisi.bus.fragments.MapFragment
import kotlinx.android.synthetic.activity_main.*

public class MainActivity() : AppCompatActivity() {
    var drawerToggle: ActionBarDrawerToggle? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(toolbar)
        setupNavigationDrawer()

        navigate(MapFragment())
    }

    fun navigate(fragment: Fragment) {
        fragmentManager.beginTransaction().replace(R.id.content_layout, fragment).commit()
    }

    fun setupNavigationDrawer() {
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawerLayout.setDrawerListener(drawerToggle)
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeButtonEnabled(true);

        drawer.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.drawer_map -> navigate(MapFragment())
            }
            supportActionBar.title = it.title
            it.setChecked(true)
            drawerLayout.closeDrawers()
            true
        }
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