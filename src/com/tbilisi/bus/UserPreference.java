package com.tbilisi.bus;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "preferences")
public class UserPreference {
    @DatabaseField(id = true)
    private String key;
    @DatabaseField
    private String value;

    public UserPreference(){}

    public UserPreference(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
