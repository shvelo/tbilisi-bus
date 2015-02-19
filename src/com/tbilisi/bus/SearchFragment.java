package com.tbilisi.bus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tbilisi.bus.data.BusStop;
import com.tbilisi.bus.data.MapItem;
import com.tbilisi.bus.util.StopListAdapter;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class SearchFragment extends Fragment {
    private ListView list;
    private ArrayList<MapItem> items;
    private StopListAdapter adapter;
    private Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_search, container);
        list = (ListView) view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        realm = Realm.getInstance(getActivity());
        items = new ArrayList<>();
        adapter = new StopListAdapter(getActivity(), items);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int id = (int) view.getTag();
                clearList();
                showSchedule(id);
            }
        });
    }

    private void showSchedule(int stopId) {
        ((MainActivity)getActivity()).showSchedule(stopId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(realm != null) realm.close();
    }

    public void updateList(String id) {
        try {
            items.clear();
            id = id.trim();
            RealmResults<BusStop> results = realm.where(BusStop.class)
                    .contains("name", id)
                    .findAll();
            for(BusStop result : results){
                items.add(new MapItem(result));
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearList() {
        items.clear();
        adapter.notifyDataSetChanged();
    }
}
