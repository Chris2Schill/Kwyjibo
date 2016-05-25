package com.seniordesign.kwyjibo.kwyjibo;


import android.app.Application;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

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
                .getBoolean("authenticated", false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Fragment getFragment(Screens screen){
        return fragments.get(screen);
    }

    public void destroyUserSession(){
        storePreference("userId", "");
        storePreference("authToken", "");
        storePreference("username", "");
        storePreference("email", "");
        storePreference("authenticated", false);
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

