package com.tbilisi.bus.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MapItem implements ClusterItem {
    private final LatLng position;
    public String id;
    public String name;

    public MapItem(double lat, double lon, String id, String name){
        position = new LatLng(lat, lon);
        this.id = id;
        this.name = name;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }
}
