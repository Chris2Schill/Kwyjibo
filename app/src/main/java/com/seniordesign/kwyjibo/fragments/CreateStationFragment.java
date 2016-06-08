package com.seniordesign.kwyjibo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.seniordesign.kwyjibo.restapi.RestAPI;
import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.beans.RadioStation;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateStationFragment extends Fragment implements HasSessionInfo{

    private EditText stationNameEditText;
    private EditText userNameEditText;

    private static final String TAG = "CreateStationFragment";

    List<String> genres;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_station_fragment, container, false);

        genres = new ArrayList<>();
        genres.add("EDM");
        genres.add("Dubstep");
        genres.add("Jazz");


        initButtons(rootView);

        stationNameEditText = (EditText)rootView.findViewById(R.id.create_station_name_edittext);
        userNameEditText = (EditText)rootView.findViewById(R.id.create_station_yourname_edittext);

        MainActivity.applyLayoutDesign(rootView);

        return rootView;
    }

    private void initButtons(View rootView){
        Button addStationButton = (Button) rootView.findViewById(R.id.create_station_confirm_button);
        addStationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup radioGroup = (RadioGroup) getActivity().findViewById(R.id.create_station_radio_group);
                if (radioGroup != null) {
                    int genreIndex = radioGroup.indexOfChild(getActivity().
                            findViewById(radioGroup.getCheckedRadioButtonId()));

                    RadioStation newStation = new RadioStation();
                    newStation.Name = stationNameEditText.getText().toString();
                    newStation.CreatedBy = userNameEditText.getText().toString();
                    newStation.Genre = genres.get(genreIndex);
                    String userId = MainActivity.getStringPreference(USER_ID);
                    String authToken = MainActivity.getStringPreference(AUTH_TOKEN);

                    RestAPI.createStation(newStation, userId, authToken, new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            Toast.makeText(getActivity(), "Station Added", Toast.LENGTH_LONG).show();
                            MainActivity.replaceScreen(MainActivity.Screens.STATION_SELECTION, false);
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
    }
}
