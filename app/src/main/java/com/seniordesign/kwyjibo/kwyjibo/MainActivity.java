package com.seniordesign.kwyjibo.kwyjibo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static Map<Screens,Fragment> fragments = new HashMap<>();

    public enum Screens{
        LOGIN, MODE_SELECTION, RECORD_MODE, RADIO_MODE, CREATE_STATION
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        fragments.put(Screens.LOGIN, new LoginFragment());
        fragments.put(Screens.MODE_SELECTION, new ModeSelectionFragment());
        fragments.put(Screens.RECORD_MODE, new RecordModeFragment());
        fragments.put(Screens.RADIO_MODE, new RadioModeFragment());
        fragments.put(Screens.CREATE_STATION, new CreateStationFragment());

        if (findViewById(R.id.main_activity_fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_activity_fragment_container, getFragment(Screens.LOGIN)).commit();

        }
    }

    public static Fragment getFragment(Screens screen){
        return fragments.get(screen);
    }
}

