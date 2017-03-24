package com.tbilisi.bus.data

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class Favorite: RealmObject() {
    open var stop: BusStop? = null
}