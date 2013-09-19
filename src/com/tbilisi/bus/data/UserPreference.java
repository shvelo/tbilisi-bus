package com.tbilisi.bus.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "preferences")
public class UserPreference {
    @DatabaseField(id = true)
    public String key;
    @DatabaseField
    public String value;

    public UserPreference(){}

    public UserPreference(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
