package com.tbilisi.bus

import android.content.Context
import android.util.Log
import com.tbilisi.bus.data.BusStop
import io.realm.Realm
import io.realm.exceptions.RealmException

class DatabaseManager(val context: Context) {
    fun initialize() {
        if(!isInitialized()) {
            Log.d("DatabaseManager", "Populating database")
            populate()
            setInitialized()
        }
    }

    fun populate() {
        val realm = Realm.getInstance(context)
        val jsonStream = context.assets.open("db.json")

        realm.beginTransaction()
        try {
            realm.createAllFromJson(BusStop::class.java, jsonStream)
        } catch(exception: RealmException) {
            realm.cancelTransaction()
        } finally {
            realm.commitTransaction()
        }
    }

    fun isInitialized(): Boolean {
        val preferences = context.getSharedPreferences("databasemanager", 0)
        return preferences.contains("initialized")
    }

    fun setInitialized() {
        val preferences = context.getSharedPreferences("databasemanager", 0)
        preferences.edit().putBoolean("initialized", true).apply()
    }
}