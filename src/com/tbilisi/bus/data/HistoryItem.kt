package com.tbilisi.bus.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
public open class HistoryItem : RealmObject() {
    @PrimaryKey
    public open var id: Int = 0
}
