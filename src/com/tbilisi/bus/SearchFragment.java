package com.tbilisi.bus;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.tbilisi.bus.data.BusStop;
import com.tbilisi.bus.util.StopListAdapter;
import java.util.ArrayList;

public class SearchFragment extends Fragment {
    private ListView list;
    private ArrayList<BusStop> items;
    private StopListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.activity_search, container);

        list = (ListView) view.findViewById(R.id.list);
        items = new ArrayList<BusStop>();
        adapter = new StopListAdapter(getActivity(), items);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = (String) view.getTag();
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                intent.putExtra(ScheduleActivity.STOP_ID_KEY, id);
                startActivity(intent);
            }
        });

        return view;
    }

    public void updateList(String id) {
        try {
            if(A.dbLoaded) {
                items.clear();
                id = id.trim();
                QueryBuilder<BusStop, Integer> qb = A.db.busStopDao.queryBuilder();
                Where<BusStop, Integer> query = qb.where().like("name", "%" + id + "%");
                query.or().like("name_en", "%" + id + "%");
                if(id.matches("[0-9]+")) {
                    query.or().idEq(Integer.valueOf(id));
                }
                items.addAll(query.query());
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearList() {
        items.clear();
        adapter.notifyDataSetChanged();
    }
}
