package com.seniordesign.kwyjibo.fragments.screens;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seniordesign.kwyjibo.core.MainActivity;
import com.seniordesign.kwyjibo.core.Screens;
import com.seniordesign.kwyjibo.kwyjibo.R;

public class ModeSelectionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mode_selection_fragment, container, false);
        MainActivity.applyLayoutDesign(rootView);

        rootView.findViewById(R.id.record_mode_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainActivity.replaceScreen(Screens.RECORD_MODE, "RECORD_MODE",
                        android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        rootView.findViewById(R.id.radio_mode_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Not using MainActivity.replaceScreen() because of the custom animation order.
                Fragment radioModeFragment = MainActivity.getFragment(Screens.STATION_SELECTION);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                                R.anim.slide_in_from_left, R.anim.slide_out_right)
                        .replace(R.id.main_activity_fragment_container, radioModeFragment)
                        .addToBackStack("STATION_SELECTION")
                        .commit();
            }
        });

        rootView.findViewById(R.id.studio_mode_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Not using MainActivity.replaceScreen() because of the custom animation order.
                Fragment studioModeFragment = MainActivity.getFragment(Screens.STUDIO_MODE);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                                R.anim.slide_in_from_left, R.anim.slide_out_right)
                        .replace(R.id.main_activity_fragment_container, studioModeFragment)
                        .addToBackStack("STUDIO_MODE")
                        .commit();
            }
        });

        rootView.findViewById(R.id.signout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.destroyUserSession();
                MainActivity.destroyBackStack();
                MainActivity.replaceScreen(Screens.INTRO_TITLE, null,
                        android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        return rootView;
    }
}
