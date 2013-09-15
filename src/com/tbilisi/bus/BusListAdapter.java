package com.tbilisi.bus;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BusListAdapter implements ListAdapter {
    public ArrayList<BusInfo> busList;
    private Context context;

    public BusListAdapter(Context context, ArrayList<BusInfo> busList) {
        this.context = context;
        this.busList = busList;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return busList.size();
    }

    @Override
    public Object getItem(int i) {
        return busList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        BusInfo item = busList.get(i);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout rowView = (LinearLayout) inflater.inflate(R.layout.bus_info, viewGroup, false);

        TextView busNumber = (TextView) rowView.findViewById(R.id.busNumber);
        TextView busDestination = (TextView) rowView.findViewById(R.id.busDestination);
        TextView busArrival = (TextView) rowView.findViewById(R.id.busArrival);

        busNumber.setText("№" + String.valueOf(item.number));
        busDestination.setText(item.destination);
        busArrival.setText(String.valueOf(item.arrival) + "წთ");

        return rowView;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return busList.isEmpty();
    }
}
