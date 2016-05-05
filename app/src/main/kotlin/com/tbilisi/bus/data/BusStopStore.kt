package com.tbilisi.bus.data

import com.google.android.gms.maps.model.LatLngBounds
import io.realm.Realm

object BusStopStore {
    /**
     * Find stops in lat lng bounds
     */
    fun findInBounds(bounds: LatLngBounds): List<BusStop> {
        val realm = Realm.getDefaultInstance()
        val query = realm.where(BusStop::class.java)
                .between("lat", bounds.southwest.latitude, bounds.northeast.latitude)
                .between("lon", bounds.southwest.longitude, bounds.northeast.longitude)
        val foundStops = query.findAll()
        realm.close()
        return foundStops
    }
}