package com.tbilisi.bus.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.io.Serializable

@RealmClass
open class BusStop(): RealmObject(), Serializable {
    @PrimaryKey
    open var id = 0
    open var name = ""
    open var name_en = ""
    open var lat = 0.0
    open var lon = 0.0
}