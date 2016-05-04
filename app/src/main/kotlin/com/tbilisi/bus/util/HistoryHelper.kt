package com.tbilisi.bus.util

import com.tbilisi.bus.data.BusStop
import com.tbilisi.bus.data.HistoryItem
import io.realm.Realm
import java.util.*

object HistoryHelper {
    fun getHistory(): ArrayList<HistoryItem> {
        val realm = Realm.getDefaultInstance()

        return realm.allObjects(HistoryItem::class.java).toCollection(destination = ArrayList<HistoryItem>())
    }

    fun addToHistory(stop: BusStop) {
        val realm = Realm.getDefaultInstance()
        val alreadyInHistory = realm.where(HistoryItem::class.java).equalTo("stop.id", stop.id).findFirst()
        if(alreadyInHistory == null) {
            realm.executeTransaction {
                val historyItem = realm.createObject(HistoryItem::class.java)
                historyItem.stop = stop
            }
        }
    }

    fun clearHistory() {
        Realm.getDefaultInstance().executeTransaction {
            it.clear(HistoryItem::class.java)
        }
    }
}