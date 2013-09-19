package com.tbilisi.bus.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "history")
public class HistoryItem {
    @DatabaseField(id = true)
    public int id;

    public HistoryItem(){}

    public HistoryItem(int id) {
        this.id = id;
    }
}
