package com.tbilisi.bus.util

import com.tbilisi.bus.data.BusStop
import com.tbilisi.bus.data.HistoryItem
import io.realm.Realm
import java.util.*

object HistoryHelper {
    fun getHistory(): ArrayList<HistoryItem> {
        val realm = Realm.getDefaultInstance()

        val historyList = ArrayList<HistoryItem>()
        realm.allObjects(HistoryItem::class.java).toCollection(historyList)
        realm.close()

        return historyList
    }

    fun addToHistory(stop: BusStop) {
        val realm = Realm.getDefaultInstance()
        val inHistory = realm.where(HistoryItem::class.java).equalTo("stop.id", stop.id).count()
        if(inHistory == 0L) {
            realm.executeTransaction {
                val historyItem = HistoryItem()
                historyItem.stop = stop
                realm.copyToRealm(historyItem)
            }
        }
        realm.close()
    }

    fun clearHistory() {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            realm.clear(HistoryItem::class.java)
        }
        realm.close()
    }
}