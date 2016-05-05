package com.tbilisi.bus.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class BusStop(): RealmObject() {
    @PrimaryKey
    open var id = "000"
    open var name = ""
    open var name_en = ""
    open var lat = 0.0
    open var lon = 0.0
}