package com.tbilisi.bus.util

import android.content.Context

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.tbilisi.bus.R
import com.tbilisi.bus.data.MapItem

public class MapItemRenderer(context: Context, map: GoogleMap, clusterManager: ClusterManager<MapItem>) : DefaultClusterRenderer<MapItem>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(mapItem: MapItem?, markerOptions: MarkerOptions?) {
        markerOptions!!.icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_icon))
        markerOptions.snippet(mapItem!!.id.toString()).anchor(0.5f, 1.0f).title(mapItem.name)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<MapItem>): Boolean {
        return cluster.getSize() > 2
    }
}