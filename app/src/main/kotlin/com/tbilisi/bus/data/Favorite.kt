package com.tbilisi.bus.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
public open class Favorite(): RealmObject() {
    @PrimaryKey
    public open var id = 0
    public open var stop: BusStop? = null
}