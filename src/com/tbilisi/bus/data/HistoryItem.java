package com.tbilisi.bus.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class HistoryItem extends RealmObject {
    @PrimaryKey
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
