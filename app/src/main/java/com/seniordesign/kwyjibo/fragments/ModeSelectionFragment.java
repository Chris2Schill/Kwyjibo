package com.seniordesign.kwyjibo.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.kwyjibo.R;

public class ModeSelectionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mode_selection_fragment, container, false);

        rootView.findViewById(R.id.record_mode_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainActivity.replaceScreen(MainActivity.Screens.RECORD_MODE, true);
            }
        });

        rootView.findViewById(R.id.radio_mode_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                MainActivity.replaceScreen(MainActivity.Screens.STATION_SELECTION, true);
            }
        });

        rootView.findViewById(R.id.signout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.destroyUserSession();
                MainActivity.replaceScreen(MainActivity.Screens.INTRO_TITLE, false);
            }
        });

        MainActivity.applyLayoutDesign(rootView);
        return rootView;
    }
}
