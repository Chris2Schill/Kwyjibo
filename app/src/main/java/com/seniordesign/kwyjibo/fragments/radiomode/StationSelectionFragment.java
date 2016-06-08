package com.seniordesign.kwyjibo.fragments.radiomode;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.seniordesign.kwyjibo.restapi.RestAPI;
import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.adapters.StationSelectListAdapter;
import com.seniordesign.kwyjibo.beans.RadioStation;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StationSelectionFragment extends Fragment implements HasSessionInfo {

    private StationSelectListAdapter<String> listAdapter;

    private static final String TAG = "StationSelectionFrag";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.station_selection_fragment, container, false);
        enableStationListView(rootView);
        enableCreateStationButton(rootView);

        RestAPI.getStations(new Callback<List<RadioStation>>() {
            @Override
            public void onResponse(Call<List<RadioStation>> call, Response<List<RadioStation>> response) {
                listAdapter.clear();
                for (RadioStation station : response.body()){
                    listAdapter.add(station.Name);
                }
            }

            @Override
            public void onFailure(Call<List<RadioStation>> call, Throwable t) {

            }
        });

        MainActivity.applyLayoutDesign(rootView);
        return rootView;
    }


    private void enableStationListView(View v){
        listAdapter = new StationSelectListAdapter<>(getActivity(), R.layout.station_selection_list_item,
                new ArrayList<String>());

        ListView stationsListView = (ListView) v.findViewById(R.id.radio_mode_list_view);
        stationsListView.setAdapter(listAdapter);
        stationsListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.storePreference(CURRENT_STATION, listAdapter.getItem(position));
                MainActivity.replaceScreen(MainActivity.Screens.CURRENT_STATION, true);
            }
        });
    }

    private void enableCreateStationButton(View v){
        Button createStationButton = (Button) v.findViewById(R.id.create_station_button);
        createStationButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.replaceScreen(MainActivity.Screens.CREATE_STATION, true);
            }
        });
    }
}

