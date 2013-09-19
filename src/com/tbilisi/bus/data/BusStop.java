package com.tbilisi.bus.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "stops")
public class BusStop {
    @DatabaseField(id = true)
    public int id;
    @DatabaseField
    public String name;
    @DatabaseField
    public boolean hasBoard;
    @DatabaseField
    public boolean hasData;
    @DatabaseField
    public double lat;
    @DatabaseField
    public double lon;

    public BusStop() {}

    public BusStop(int id, String name, boolean hasBoard, boolean hasData, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.hasBoard = hasBoard;
        this.hasData = hasData;
        this.lat = lat;
        this.lon = lon;
    }
}
