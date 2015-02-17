package com.tbilisi.bus.util;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.tbilisi.bus.R;
import com.tbilisi.bus.data.BusStop;

public class MapItemRenderer extends DefaultClusterRenderer<BusStop>{

    public MapItemRenderer(Context context, GoogleMap map, ClusterManager<BusStop> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(BusStop mapItem, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_icon));
        markerOptions.snippet(String.valueOf(mapItem.getId()))
                .anchor(0.5f, 1.0f)
                .title(mapItem.getName());
    }
}