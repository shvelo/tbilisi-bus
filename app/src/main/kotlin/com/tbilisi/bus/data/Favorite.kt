package com.tbilisi.bus.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Favorite(): RealmObject() {
    @PrimaryKey
    open var id = 0
    open var stop: BusStop? = null
}