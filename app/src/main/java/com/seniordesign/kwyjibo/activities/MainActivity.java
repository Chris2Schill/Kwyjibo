package com.seniordesign.kwyjibo.activities;


import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.seniordesign.kwyjibo.asynctasks.AuthenticationTask;
import com.seniordesign.kwyjibo.fragments.CreateStationFragment;
import com.seniordesign.kwyjibo.fragments.ModeSelectionFragment;
import com.seniordesign.kwyjibo.fragments.login_signup.IntroTitleFragment;
import com.seniordesign.kwyjibo.fragments.login_signup.LoginFragment;
import com.seniordesign.kwyjibo.fragments.login_signup.SignupFragment;
import com.seniordesign.kwyjibo.fragments.radiomode.StationFragment;
import com.seniordesign.kwyjibo.fragments.radiomode.StationSelectionFragment;
import com.seniordesign.kwyjibo.interfaces.AuthenticationHandler;
import com.seniordesign.kwyjibo.kwyjibo.R;
import com.seniordesign.kwyjibo.fragments.recordmode.RecordModeFragment;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;
import com.seniordesign.kwyjibo.services.ObserverService;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ApplicationWrapper implements HasSessionInfo {

    private static Map<Screens,Fragment> fragments = new HashMap<>();

    private static final String TAG = "MainActivity";

    public enum Screens{
        INTRO_TITLE, LOGIN, SIGNUP, MODE_SELECTION, RECORD_MODE, STATION_SELECTION, CREATE_STATION,
        CURRENT_STATION
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        fragments.put(Screens.INTRO_TITLE, new IntroTitleFragment());
        fragments.put(Screens.LOGIN, new LoginFragment());
        fragments.put(Screens.SIGNUP, new SignupFragment());
        fragments.put(Screens.MODE_SELECTION, new ModeSelectionFragment());
        fragments.put(Screens.RECORD_MODE, new RecordModeFragment());
        fragments.put(Screens.STATION_SELECTION, new StationSelectionFragment());
        fragments.put(Screens.CREATE_STATION, new CreateStationFragment());
        fragments.put(Screens.CURRENT_STATION, new StationFragment());

        EventBus.getDefault().register(getFragment(Screens.CURRENT_STATION));

        if (findViewById(R.id.main_activity_fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_activity_fragment_container, getFragment(Screens.INTRO_TITLE))
                    .commit();
        }

        startService(new Intent(getBaseContext(), ObserverService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AuthenticationTask(new AuthenticationHandler() {
            @Override
            public void isAuthenticated(boolean authenticated) {
                if (authenticated){
                    replaceScreen(Screens.MODE_SELECTION, false);
                    Log.d(TAG, "AUTHENTICATION SUCCESSFUL.");
                }else{
                    destroyUserSession();
                    replaceScreen(Screens.INTRO_TITLE, false);
                    Log.d(TAG, "AUTHENTICATION FAILED.");
                }
            }
        }).execute(getStringPreference(USER_ID), getStringPreference(AUTH_TOKEN));
    }

    @Override
    protected void onDestroy() {
        prefsEditor.putBoolean("fromOrientation", false);
        prefsEditor.commit();
        super.onDestroy();
    }

    public static void replaceScreen(Screens screen, boolean addToBackStack){
        FragmentTransaction transaction = ((MainActivity)context).getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.main_activity_fragment_container, getFragment(screen));
        if (addToBackStack){
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
    public static void replaceScreen(Screens screen, boolean addToBackStack, String tag){
        FragmentTransaction transaction = ((MainActivity)context).getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.main_activity_fragment_container, getFragment(screen));
        if (addToBackStack){
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    public static Fragment getFragment(Screens screen){
        return fragments.get(screen);
    }

    public static void destroyUserSession(){
        storePreference(USER_ID, "");
        storePreference(AUTH_TOKEN, "");
        storePreference(USER_NAME, "");
        storePreference(USER_EMAIL, "");
        storePreference(CURRENT_STATION, "");
        storePreference(IS_AUTHENTICATED, false);
    }

    public void setCurrentStation(Fragment fragment){
        fragments.put(Screens.CURRENT_STATION, fragment);
    }

}
