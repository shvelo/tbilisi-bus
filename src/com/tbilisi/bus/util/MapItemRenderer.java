package com.tbilisi.bus.util;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.tbilisi.bus.R;
import com.tbilisi.bus.data.MapItem;

public class MapItemRenderer extends DefaultClusterRenderer<MapItem>{

    public MapItemRenderer(Context context, GoogleMap map, ClusterManager<MapItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MapItem mapItem, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_icon));
        markerOptions.snippet(String.valueOf(mapItem.id))
                .anchor(0.5f, 1.0f)
                .title(mapItem.name);
    }
}