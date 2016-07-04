package com.seniordesign.kwyjibo.fragments.radiomode;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.j256.ormlite.table.TableUtils;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.seniordesign.kwyjibo.DialogDecorator;
import com.seniordesign.kwyjibo.LocalDBManager;
import com.seniordesign.kwyjibo.activities.ApplicationWrapper;
import com.seniordesign.kwyjibo.adapters.SwipeDetector;
import com.seniordesign.kwyjibo.restapi.RestAPI;
import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.adapters.StationSelectListAdapter;
import com.seniordesign.kwyjibo.beans.RadioStation;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;
import com.seniordesign.kwyjibo.sorting.AscendingClipName;
import com.seniordesign.kwyjibo.sorting.DescendingNumClips;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StationSelectionFragment extends Fragment implements HasSessionInfo {

    private ListView stationsListView;
    private StationSelectListAdapter<RadioStation> listAdapter;
    private Parcelable state;
    private static final String TAG = "StationSelectionFrag";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.station_selection_fragment, container, false);
        ApplicationWrapper.applyLayoutDesign(rootView);
        enableStationListView(rootView);
        enableCreateStationButton(rootView);

        try {
            LocalDBManager dbManager = ApplicationWrapper.getDBManager(getActivity());
            List<RadioStation> stations = dbManager.getStationDao().queryForAll();
            updateStationsListView(stations);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final SwipyRefreshLayout swipeLayout = (SwipyRefreshLayout)rootView.findViewById(R.id.station_selection_swipe_refresh_layout);
        swipeLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                RestAPI.getStations(new Callback<List<RadioStation>>() {
                    @Override
                    public void onResponse(Call<List<RadioStation>> call, Response<List<RadioStation>> response) {
                        if (response.body() != null) {
                            updateStationsListView(response.body());
                            swipeLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RadioStation>> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                    }
                });
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        // Save ListView state @ onPause
        Log.d(TAG, "saving listview state @ onPause");
        state = stationsListView.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set new items
//        stationsListView.setAdapter(adapter);
        // Restore previous state (including selected item index and scroll position)
        if(state != null) {
            Log.d(TAG, "trying to restore listview state..");
            stationsListView.onRestoreInstanceState(state);
        }
    }

    private void enableStationListView(View v){
        listAdapter = new StationSelectListAdapter<>(getActivity(), R.layout.station_selection_list_item,
                new ArrayList<RadioStation>());

        stationsListView = (ListView) v.findViewById(R.id.radio_mode_list_view);
        stationsListView.setAdapter(listAdapter);
        stationsListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.storePreference(CURRENT_STATION, listAdapter.getItem(position).Name);
                MainActivity.replaceScreen(MainActivity.Screens.CURRENT_STATION, "CURRENT_STATION",
                        android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        new SwipeDetector(stationsListView).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
            @Override
            public void SwipeEventDetected(View v, SwipeDetector.SwipeType swipeType) {
                if (swipeType == SwipeDetector.SwipeType.LEFT_TO_RIGHT){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
    }

    private void enableCreateStationButton(View v){
        final ViewGroup container = (ViewGroup)v.findViewById(R.id.create_station_activity_container);

        final FloatingActionButton createStationButton = (FloatingActionButton) v.findViewById(R.id.create_station_button);
        createStationButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                final DialogDecorator decorator = new DialogDecorator(R.layout.create_station_fragment, alertDialog, container);
                decorator.setOnClickListenerFor(R.id.create_station_confirm_button, new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        EditText stationNameEditText = (EditText)decorator.getView().findViewById(R.id.create_station_name_edittext);
                        RadioGroup radioGroup = (RadioGroup) decorator.getView().findViewById(R.id.create_station_radio_group);
                        List<String> genres = new ArrayList<>();
                        genres.add("EDM");
                        genres.add("Dubstep");
                        genres.add("Jazz");

                        if (radioGroup != null) {
                            int genreIndex = radioGroup.indexOfChild(decorator.getView().findViewById(radioGroup.getCheckedRadioButtonId()));

                            final RadioStation newStation = new RadioStation();
                            newStation.Name = stationNameEditText.getText().toString();
                            newStation.CreatedBy = MainActivity.getStringPreference(USER_NAME);
                            newStation.Genre = genres.get(genreIndex);
                            String userId = MainActivity.getStringPreference(USER_ID);
                            String authToken = MainActivity.getStringPreference(AUTH_TOKEN);

                            RestAPI.createStation(newStation, userId, authToken, new Callback<Boolean>() {
                                @Override
                                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                    if (response.body() != null){
                                        if (response.body()){
                                            Toast.makeText(getActivity(), "Station Added", Toast.LENGTH_LONG).show();
                                            listAdapter.add(newStation);
                                            listAdapter.notifyDataSetChanged();
                                            alertDialog.dismiss();
                                        }
                                    }else{
                                        Log.e(TAG, "Response body is null.");
                                    }
                                }

                                @Override
                                public void onFailure(Call<Boolean> call, Throwable t) {
                                    Toast.makeText(getActivity(), "Station Not Added", Toast.LENGTH_LONG).show();
                                    Log.e(TAG,t.getMessage());

                                }
                            });
                        } else {
                            Log.e(TAG, "Reference to RadioGroup create_station_radio_group is null.");
                        }
                    }
                });
                alertDialog.show();
            }
        });
    }

    private void updateStationsListView(List<RadioStation> stations){
        listAdapter.clear();
        Collections.sort(stations, new DescendingNumClips());
        LocalDBManager db = ApplicationWrapper.getDBManager(getActivity());
        try {
            TableUtils.clearTable(db.getConnectionSource(), RadioStation.class);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
        for (RadioStation station : stations) {
            listAdapter.add(station);
            try {
                db.getStationDao().create(station);
            } catch (SQLException e) {
                Log.e(TAG,e.getMessage());
            }
        }
    }
}

