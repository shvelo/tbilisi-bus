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
            jsonStream.close()
        }
    }

    fun isInitialized(): Boolean {
        return Realm.getInstance(context).where(BusStop::class.java).count() > 0
    }
}