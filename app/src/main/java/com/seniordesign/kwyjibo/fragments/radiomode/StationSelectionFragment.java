package com.seniordesign.kwyjibo.fragments.radiomode;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.adapters.StationSelectListAdapter;
import com.seniordesign.kwyjibo.asynctasks.GetAllStationsTask;
import com.seniordesign.kwyjibo.interfaces.ListViewHandler;
import com.seniordesign.kwyjibo.kwyjibo.R;

import java.util.ArrayList;

public class StationSelectionFragment extends Fragment {

    private ListView stationsListView;
    private StationSelectListAdapter listAdapter;
    private Button createStationButton;

    private static final String TAG = "StationSelectionFrag";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.station_selection_fragment, container, false);
        enableStationListView(rootView);
        enableCreateStationButton(rootView);
        setFont();
        new GetAllStationsTask(new ListViewHandler(){
            @Override
            public void updateListView(Object[] stations){
                if (stations != null){
                    listAdapter.clear();
                    for (Object s : stations){
                        listAdapter.add(s);
                    }
                }
            }
        }).execute();
        return rootView;
    }

    private void enableStationListView(View v){
        listAdapter = new StationSelectListAdapter(getActivity(), R.layout.radio_mode_list_item,
                new ArrayList<String>());

        stationsListView = (ListView) v.findViewById(R.id.radio_mode_list_view);
        stationsListView.setAdapter(listAdapter);

        stationsListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment stationFragment = new StationFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Name", listAdapter.getItem(position).toString());
                stationFragment.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.radio_mode_fragment_container, stationFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private void enableCreateStationButton(View v){
        createStationButton = (Button)v.findViewById(R.id.create_station_button);
        createStationButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.main_activity_fragment_container,
                        MainActivity.getFragment(MainActivity.Screens.CREATE_STATION));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private void setFont(){
        Typeface proximaNova = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNova-Semibold.otf");
        createStationButton.setTypeface(proximaNova);
    }

}

