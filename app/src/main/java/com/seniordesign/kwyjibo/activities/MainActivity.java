package com.seniordesign.kwyjibo.activities;


import android.support.v4.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.seniordesign.kwyjibo.fragments.radiomode.CreateStationFragment;
import com.seniordesign.kwyjibo.fragments.ModeSelectionFragment;
import com.seniordesign.kwyjibo.kwyjibo.R;
import com.seniordesign.kwyjibo.fragments.radiomode.RadioModeFragment;
import com.seniordesign.kwyjibo.fragments.recordmode.RecordModeFragment;
import com.seniordesign.kwyjibo.fragments.StartupFragment;
import com.seniordesign.kwyjibo.interfaces.HasUserInfo;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements HasUserInfo {

    private static Map<Screens,Fragment> fragments = new HashMap<>();

    public enum Screens{
        LOGIN_SIGNUP, MODE_SELECTION, RECORD_MODE, RADIO_MODE, CREATE_STATION
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        fragments.put(Screens.LOGIN_SIGNUP, new StartupFragment());
        fragments.put(Screens.MODE_SELECTION, new ModeSelectionFragment());
        fragments.put(Screens.RECORD_MODE, new RecordModeFragment());
        fragments.put(Screens.RADIO_MODE, new RadioModeFragment());
        fragments.put(Screens.CREATE_STATION, new CreateStationFragment());

        if (findViewById(R.id.main_activity_fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_activity_fragment_container, getFragment(Screens.LOGIN_SIGNUP))
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean authenticated = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(IS_AUTHENTICATED, false);
        if (authenticated){
            replaceScreen(Screens.MODE_SELECTION, false);
        }
    }

    public void replaceScreen(Screens screen, boolean addToBackStack){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_activity_fragment_container, getFragment(screen));
        if (addToBackStack){
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public static Fragment getFragment(Screens screen){
        return fragments.get(screen);
    }

    public void destroyUserSession(){
        storePreference(USER_ID, "");
        storePreference(AUTH_TOKEN, "");
        storePreference(USER_NAME, "");
        storePreference(USER_EMAIL, "");
        storePreference(IS_AUTHENTICATED, false);
    }

    public <T> MainActivity storePreference(String key, T value){
        SharedPreferences settings = PreferenceManager .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        if (value instanceof String){
            editor.putString(key, (String) value);
        }else if (value instanceof Boolean){
            editor.putBoolean(key,(Boolean)value);
        }
        editor.apply();
        return this;
    }

}
