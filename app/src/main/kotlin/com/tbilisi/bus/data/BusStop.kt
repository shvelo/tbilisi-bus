package com.tbilisi.bus.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
public open class BusStop(): RealmObject() {
    @PrimaryKey
    public open var id = 0
    public open var name = ""
    public open var name_en = ""
    public open var lat = 0.0
    public open var lon = 0.0
}