package com.tbilisi.bus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainMenuAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MainMenuItem> items;
    private LayoutInflater inflater;

    public MainMenuAdapter(Context context, ArrayList<MainMenuItem> items) {
        this.items = items;
        this.context = context;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (long) i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MainMenuItem item = items.get(i);

        if(! item.enabled) return new View(context);

        View view = convertView;

        if(view == null) {
            view = inflater.inflate(R.layout.main_menu_item, viewGroup, false);
        }

        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(item.label);
        textView.setTypeface(A.typeface);

        ((ImageView) view.findViewById(R.id.imageView)).setImageDrawable(item.icon);

        view.setTag(item.intent);

        return view;
    }
}
