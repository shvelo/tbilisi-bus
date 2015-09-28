package com.tbilisi.bus.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
public open class BusStop : RealmObject() {
    @PrimaryKey
    public open var id: Int = 0
    public open var name: String = ""
    public open var lat: Double = 0.toDouble()
    public open var lon: Double = 0.toDouble()
}
