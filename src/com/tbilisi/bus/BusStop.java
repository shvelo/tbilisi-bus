package com.tbilisi.bus;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "stops")
public class BusStop {
    @DatabaseField(id = true)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField
    private boolean hasBoard;
    @DatabaseField
    private boolean hasData;
    @DatabaseField
    private double lat;
    @DatabaseField
    private double lon;

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
