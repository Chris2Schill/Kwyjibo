package com.seniordesign.kwyjibo.kwyjibo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ModeSelectionFragment extends Fragment {

    private Button goRecordMode;
    private Button goRadioMode;
    private Button goStudioMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mode_selection_fragment, container, false);

        goRecordMode = (Button)rootView.findViewById(R.id.record_mode_button);
        goRecordMode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_activity_fragment_container,
                        MainActivity.recordModeFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        goRadioMode = (Button)rootView.findViewById(R.id.radio_mode_button);
        goStudioMode = (Button)rootView.findViewById(R.id.studio_mode_button);
        return rootView;
    }
}
