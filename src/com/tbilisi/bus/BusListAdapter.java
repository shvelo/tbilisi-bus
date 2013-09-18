package com.tbilisi.bus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BusListAdapter extends BaseAdapter {
    public ArrayList<BusInfo> busList;
    private Context context;

    public BusListAdapter(Context context, ArrayList<BusInfo> busList) {
        this.context = context;
        this.busList = busList;
    }

    public void update(ArrayList<BusInfo> newList) {
        busList = newList;
        notifyDataSetChanged();
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
        return (long) i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        BusInfo item = busList.get(i);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null) {
            view = inflater.inflate(R.layout.bus_info, viewGroup, false);
        }

        TextView busNumber = (TextView) view.findViewById(R.id.busNumber);
        TextView busDestination = (TextView) view.findViewById(R.id.busDestination);
        TextView busArrival = (TextView) view.findViewById(R.id.busArrival);

        busNumber.setTypeface(A.typeface);
        busDestination.setTypeface(A.typeface);
        busArrival.setTypeface(A.typeface);

        busNumber.setText("№" + String.valueOf(item.number));
        busDestination.setText(item.destination);
        busArrival.setText(String.valueOf(item.arrival) + "წთ");

        return view;
    }

    @Override
    public boolean isEmpty() {
        return busList.isEmpty();
    }
}
