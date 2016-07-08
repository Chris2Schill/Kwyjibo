package com.seniordesign.kwyjibo.fragments.screens;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.j256.ormlite.table.TableUtils;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.seniordesign.kwyjibo.database.SQLiteHelper;
import com.seniordesign.kwyjibo.custom.decorators.DialogDecorator;
import com.seniordesign.kwyjibo.core.ApplicationWrapper;
import com.seniordesign.kwyjibo.custom.decorators.SwipeDetector;
import com.seniordesign.kwyjibo.database.restapi.RestAPI;
import com.seniordesign.kwyjibo.core.MainActivity;
import com.seniordesign.kwyjibo.custom.adapters.StationSelectListAdapter;
import com.seniordesign.kwyjibo.database.models.RadioStation;
import com.seniordesign.kwyjibo.core.HasSessionInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO: Set TRIGGER_DISTANCE to a proportion of the height of the list viewport.
public class StationSelectionFragment extends Fragment implements HasSessionInfo {

    private RecyclerView recyclerView;
    private StationSelectListAdapter listAdapter;
    private Parcelable state;
    private SwipyRefreshLayout swipeRefreshLayout;
    private static int TRIGGER_DISTANCE = 150;
    private static final String TAG = "StationSelectionFrag";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.station_selection_fragment, container, false);
        ApplicationWrapper.applyLayoutDesign(rootView);
        enableStationListView(rootView);
        enableCreateStationButton(rootView);

        try {
            SQLiteHelper dbManager = ApplicationWrapper.getDBManager(getActivity());
            List<RadioStation> stations = dbManager.getStationDao().queryForAll();
            updateStationsListView(stations);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        swipeRefreshLayout = (SwipyRefreshLayout)rootView.findViewById(R.id.station_selection_swipe_refresh_layout);
        swipeRefreshLayout.setDistanceToTriggerSync(TRIGGER_DISTANCE);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.blueGray_700, null));
        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                RestAPI.getStations(new Callback<List<RadioStation>>() {
                    @Override
                    public void onResponse(Call<List<RadioStation>> call, Response<List<RadioStation>> response) {
                        if (response.body() != null) {
                            updateStationsListView(response.body());
                            swipeRefreshLayout.setRefreshing(false);
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
//        state = recyclerView.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set new items
//        recyclerView.setAdapter(adapter);
        // Restore previous state (including selected item index and scroll position)
        if(state != null) {
            Log.d(TAG, "trying to restore listview state..");
//            recyclerView.onRestoreInstanceState(state);
        }
    }

    private void enableStationListView(View v){
        listAdapter = new StationSelectListAdapter(getActivity(), new ArrayList<RadioStation>());

        recyclerView = (RecyclerView) v.findViewById(R.id.radio_mode_list_view);
        recyclerView.setAdapter(listAdapter); // This adapter sets an implicit onItemClick listener.
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        new SwipeDetector(recyclerView).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
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
        listAdapter.updateData(stations);
        // Save a local copy to sqlite database
        SQLiteHelper db = ApplicationWrapper.getDBManager(getActivity());
        try {
            TableUtils.clearTable(db.getConnectionSource(), RadioStation.class);
            for (RadioStation station : stations) {
                db.getStationDao().create(station);
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}

