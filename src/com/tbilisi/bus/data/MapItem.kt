package com.tbilisi.bus.data

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

public class MapItem : ClusterItem {
    public val id: Int
    public val name: String
    public val lat: Double
    public val lon: Double

    public constructor(id: Int, name: String, lat: Double, lon: Double) {
        this.id = id
        this.name = name
        this.lat = lat
        this.lon = lon
    }

    public constructor(stop: BusStop) {
        this.id = stop.id
        this.name = stop.name
        this.lat = stop.lat
        this.lon = stop.lon
    }

    override fun getPosition(): LatLng {
        return LatLng(lat, lon)
    }
}
