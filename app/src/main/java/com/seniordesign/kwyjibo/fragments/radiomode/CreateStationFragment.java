package com.seniordesign.kwyjibo.fragments.radiomode;

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


public class CreateStationFragment extends Fragment implements HasSessionInfo{

    private Button addStationButton;
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

        addStationButton = (Button)rootView.findViewById(R.id.create_station_confirm_button);
        addStationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String newStationName = stationNameEditText.getText().toString();
                String createdBy = userNameEditText.getText().toString();

                RadioGroup radioGroup = (RadioGroup)getActivity().findViewById(R.id.create_station_radio_group);
                if (radioGroup != null){
                    int index = radioGroup.indexOfChild(
                            getActivity().findViewById(radioGroup.getCheckedRadioButtonId()));

                    String authToken = PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .getString(AUTH_TOKEN, "");
                    String userId = PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .getString(USER_ID, "");
                    String genre = genres.get(index);

                    new CreateStationTask(getContext())
                            .execute(newStationName, createdBy, genre, authToken, userId);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_activity_fragment_container, MainActivity.getFragment(MainActivity.Screens.RADIO_MODE))
                            .commit();

                }else{
                    Log.e(TAG, "Reference to RadioGroup create_station_radio_group is null");
                }
            }
        });

        stationNameEditText = (EditText)rootView.findViewById(R.id.create_station_name_edittext);
        userNameEditText = (EditText)rootView.findViewById(R.id.create_station_yourname_edittext);

        initLayoutDesign(rootView);

        return rootView;
    }

    private void initLayoutDesign(View rootView){
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNova-Semibold.otf");
        ViewGroup v = (ViewGroup)rootView;
        for (int i = 0; i < v.getChildCount(); i++){
            View child = v.getChildAt(i);
            if (child instanceof TextView){
                ((TextView) child).setTypeface(font);
            }
            if (child instanceof EditText){
               ((EditText) child).setTypeface(font);
                child.setBackgroundColor(getResources().getColor(R.color.darkGray));
            }
            if (child instanceof Button){
                ((Button) child).setTypeface(font);
            }
            if (child instanceof RadioGroup){
                RadioGroup rg = (RadioGroup)child;
                for (int j = 0; j < rg.getChildCount(); j++){
                    RadioButton b = (RadioButton)rg.getChildAt(j);
                    b.setTypeface(font);
                }
            }

        }
    }


}
