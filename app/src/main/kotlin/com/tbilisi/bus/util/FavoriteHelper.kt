package com.tbilisi.bus.util

import com.tbilisi.bus.data.BusStop
import com.tbilisi.bus.data.Favorite
import io.realm.Realm
import java.util.*

object FavoriteHelper {
    fun getFavorites(): ArrayList<Favorite> {
        val realm = Realm.getDefaultInstance()
        val favoriteList = ArrayList<Favorite>()
        realm.allObjects(Favorite::class.java).toCollection(favoriteList)
        realm.close()
        return favoriteList
    }

    fun addToFavorites(stop: BusStop) {
        val realm = Realm.getDefaultInstance()
        if(!isFavorited(stop)) {
            realm.executeTransaction {
                val historyItem = realm.createObject(Favorite::class.java)
                historyItem.stop = stop
            }
        }
        realm.close()
    }

    fun isFavorited(stop: BusStop): Boolean {
        val realm = Realm.getDefaultInstance()
        val inFavorites = realm.where(Favorite::class.java).equalTo("stop.id", stop.id).count()
        realm.close()
        return inFavorites > 0
    }

    fun clearFavorites() {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            it.clear(Favorite::class.java)
        }
        realm.close()
    }
}