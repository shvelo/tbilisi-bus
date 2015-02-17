package com.tbilisi.bus.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MapItem implements ClusterItem {
    public final int id;
    public final String name;
    public final double lat;
    public final double lon;

    public MapItem(int id, String name, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public MapItem(BusStop stop) {
        this.id = stop.getId();
        this.name = stop.getName();
        this.lat = stop.getLat();
        this.lon = stop.getLon();
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(lat, lon);
    }
}
