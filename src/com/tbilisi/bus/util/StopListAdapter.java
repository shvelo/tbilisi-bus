package com.tbilisi.bus.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tbilisi.bus.R;
import com.tbilisi.bus.data.BusStop;

import java.util.ArrayList;

public class StopListAdapter extends BaseAdapter {
    public ArrayList<BusStop> stops;
    private LayoutInflater inflater;

    public StopListAdapter(Context context, ArrayList<BusStop> stops) {
        this.stops = stops;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return stops.size();
    }

    @Override
    public Object getItem(int i) {
        return stops.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (long) i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = inflater.inflate(R.layout.stop_list_item, viewGroup, false);
        }
        BusStop stop = stops.get(i);

        TextView stopNumber = (TextView) view.findViewById(R.id.stopNumber);
        TextView stopName = (TextView) view.findViewById(R.id.stopName);

        stopNumber.setText(String.valueOf(stop.getId()));
        stopName.setText(String.valueOf(stop.getName()));

        view.setTag(String.valueOf(stop.getId()));

        return view;
    }
}
