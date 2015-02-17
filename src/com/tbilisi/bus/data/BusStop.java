package com.tbilisi.bus.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BusStop extends RealmObject implements ClusterItem {
    @PrimaryKey
    private int id;
    private String name;
    private double lat;
    private double lon;

    public BusStop() {}

    @Override
    public LatLng getPosition() {
        return new LatLng(lat, lon);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }
}
