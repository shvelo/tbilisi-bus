package com.tbilisi.bus.data

import io.realm.Realm

object FavoriteStore {
    fun getFavorites(): List<Favorite> {
        val realm = Realm.getDefaultInstance()
        val favoriteList = realm.allObjects(Favorite::class.java)
        realm.close()
        return favoriteList
    }

    fun addToFavorites(stop: BusStop) {
        val realm = Realm.getDefaultInstance()
        if(!isFavorite(stop)) {
            realm.executeTransaction {
                val historyItem = realm.createObject(Favorite::class.java)
                historyItem.stop = stop
            }
        }
        realm.close()
    }

    fun removeFromFavorites(stop: BusStop) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            realm.where(Favorite::class.java).equalTo("stop.id", stop.id).findFirst()?.removeFromRealm()
        }
        realm.close()
    }

    fun isFavorite(stop: BusStop): Boolean {
        val realm = Realm.getDefaultInstance()
        val inFavorites = realm.where(Favorite::class.java).equalTo("stop.id", stop.id).count()
        realm.close()
        return inFavorites > 0
    }

    fun clearFavorites() {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            realm.clear(Favorite::class.java)
        }
        realm.close()
    }
}