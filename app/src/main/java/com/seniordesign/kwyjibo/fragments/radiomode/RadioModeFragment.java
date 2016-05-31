package com.seniordesign.kwyjibo.fragments.radiomode;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seniordesign.kwyjibo.kwyjibo.R;

public class RadioModeFragment extends Fragment {

    private static final String TAG = "RadioModeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.radio_mode_fragment, container, false);

        if (rootView.findViewById(R.id.radio_mode_fragment_container) != null){
            if (savedInstanceState != null){
                return rootView;
            }
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.radio_mode_fragment_container, new StationSelectionFragment())
                    .commit();

        }
        return rootView;
    }
}




