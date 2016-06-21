package com.seniordesign.kwyjibo.activities;


import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.seniordesign.kwyjibo.events.UnpauseObserverService;
import com.seniordesign.kwyjibo.restapi.RestAPI;
import com.seniordesign.kwyjibo.fragments.CreateStationFragment;
import com.seniordesign.kwyjibo.fragments.ModeSelectionFragment;
import com.seniordesign.kwyjibo.fragments.login_signup.IntroTitleFragment;
import com.seniordesign.kwyjibo.fragments.login_signup.LoginFragment;
import com.seniordesign.kwyjibo.fragments.login_signup.SignupFragment;
import com.seniordesign.kwyjibo.fragments.radiomode.StationFragment;
import com.seniordesign.kwyjibo.fragments.radiomode.StationSelectionFragment;
import com.seniordesign.kwyjibo.kwyjibo.R;
import com.seniordesign.kwyjibo.fragments.recordmode.RecordModeFragment;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;
import com.seniordesign.kwyjibo.services.ObserverService;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

 /*
  * This is the central activity class for the entire application. It contains a HashMap of all the
  * 'Screen' fragments. The screen fragments are different than regular fragments because they
  * have been designed to take up the entire screen and act as pseudo-activities for the different
  * screens the user will see when using the app. This class provides static methods which
  * allow you to switch screens anytime using a single line of code.
  */
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
        context = this;

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
            replaceScreen(Screens.INTRO_TITLE, null);
        }

        if (!isMyServiceRunning(ObserverService.class)){
            startService(new Intent(getBaseContext(), ObserverService.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        RestAPI.authenticateSession(getStringPreference(USER_ID), getStringPreference(AUTH_TOKEN),
                new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        boolean authenticated = Boolean.parseBoolean(response.body().toString());
                        if (authenticated) {
                            Log.d(TAG, "AUTHENTICATION SUCCESSFUL.");
                        } else {
                            destroyUserSession();
                            destroyBackStack();
                            replaceScreen(Screens.INTRO_TITLE, "INTRO_TITLE");
                            Log.d(TAG, "AUTHENTICATION DENIED.");
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        destroyUserSession();
                        destroyBackStack();
                        replaceScreen(Screens.INTRO_TITLE, "INTRO_TITLE");
                        Log.d(TAG, "Authentication Request Failed.");
                    }
                });
    }

    public static void destroyBackStack(){
        FragmentManager fm = context.getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); i++){
            fm.popBackStack();
        }
    }

    public static void replaceScreen(Screens screen, String tag){
        FragmentTransaction transaction = context.getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.main_activity_fragment_container, getFragment(screen));
        if (tag != null){
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

    public static void setCurrentStation(Fragment fragment){
        fragments.put(Screens.CURRENT_STATION, fragment);
    }
}
