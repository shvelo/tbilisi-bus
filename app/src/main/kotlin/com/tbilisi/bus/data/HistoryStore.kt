package com.tbilisi.bus.data

import io.realm.Realm

object HistoryStore {
    fun getHistory(): List<HistoryItem> {
        val realm = Realm.getDefaultInstance()
        val historyList = realm.allObjects(HistoryItem::class.java)
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