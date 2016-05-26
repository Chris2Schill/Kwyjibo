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

    private TextView titleTextView;
    private Button goRecordMode;
    private Button goRadioMode;
    private Button goStudioMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mode_selection_fragment, container, false);

        titleTextView = (TextView)rootView.findViewById(R.id.mode_selection_title_textview);

        goRecordMode = (Button)rootView.findViewById(R.id.record_mode_button);
        goRecordMode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_activity_fragment_container,
                        MainActivity.getFragment(MainActivity.Screens.RECORD_MODE));
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        goRadioMode = (Button)rootView.findViewById(R.id.radio_mode_button);
        goRadioMode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_activity_fragment_container,
                        MainActivity.getFragment(MainActivity.Screens.RADIO_MODE));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        goStudioMode = (Button)rootView.findViewById(R.id.studio_mode_button);

        rootView.findViewById(R.id.signout_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).destroyUserSession();
                ((MainActivity)getActivity()).replaceScreen(MainActivity.Screens.LOGIN_SIGNUP, false);
            }
        });

        setFont();
        return rootView;
    }
    private void setFont(){
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNova-Semibold.otf");
        titleTextView.setTypeface(font);
        goRecordMode.setTypeface(font);
        goRadioMode.setTypeface(font);
        goStudioMode.setTypeface(font);
    }
}
