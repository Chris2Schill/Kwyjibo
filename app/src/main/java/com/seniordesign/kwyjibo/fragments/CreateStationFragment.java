package com.seniordesign.kwyjibo.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.asynctasks.CreateStationTask;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
                String newStationName = stationNameEditText.getText().toString();
                String createdBy = userNameEditText.getText().toString();

                RadioGroup radioGroup = (RadioGroup) getActivity().findViewById(R.id.create_station_radio_group);
                if (radioGroup != null) {
                    int index = radioGroup.indexOfChild(
                            getActivity().findViewById(radioGroup.getCheckedRadioButtonId()));

                    String authToken = PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .getString(AUTH_TOKEN, "");
                    String userId = PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .getString(USER_ID, "");
                    String genre = genres.get(index);

                    new CreateStationTask(getContext())
                            .execute(newStationName, createdBy, genre, authToken, userId);

                    MainActivity.replaceScreen(MainActivity.Screens.STATION_SELECTION, false);

                } else {
                    Log.e(TAG, "Reference to RadioGroup create_station_radio_group is null");
                }
            }
        });
    }
}
