package com.seniordesign.kwyjibo.activities;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.seniordesign.kwyjibo.asynctasks.AuthenticationTask;
import com.seniordesign.kwyjibo.fragments.CreateStationFragment;
import com.seniordesign.kwyjibo.fragments.ModeSelectionFragment;
import com.seniordesign.kwyjibo.interfaces.AuthenticationHandler;
import com.seniordesign.kwyjibo.kwyjibo.R;
import com.seniordesign.kwyjibo.fragments.radiomode.RadioModeFragment;
import com.seniordesign.kwyjibo.fragments.recordmode.RecordModeFragment;
import com.seniordesign.kwyjibo.fragments.login_signup.StartupFragment;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements HasSessionInfo {


    private static Context context;
    protected static SharedPreferences.Editor prefsEditor;

    private static Map<Screens,Fragment> fragments = new HashMap<>();
    private boolean fromOrientation = false;

    private static final String TAG = "MainActivity";

    public enum Screens{
        LOGIN_SIGNUP, MODE_SELECTION, RECORD_MODE, RADIO_MODE, CREATE_STATION
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        context = this;

        prefsEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();

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
        new AuthenticationTask(new AuthenticationHandler() {
            @Override
            public void isAuthenticated(boolean authenticated) {
                if (authenticated){
                    replaceScreen(Screens.MODE_SELECTION, false);
                    Log.d(TAG, "AUTHENTICATION SUCCESSFUL.");
                }else{
                    destroyUserSession();
                    replaceScreen(Screens.LOGIN_SIGNUP, false);
                    Log.d(TAG, "AUTHENTICATION FAILED.");
                }
            }
        }).execute(PreferenceManager.getDefaultSharedPreferences(this).getString(USER_ID, "userid missing"),
                PreferenceManager.getDefaultSharedPreferences(this).getString(AUTH_TOKEN, "authToken missing"));
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        prefsEditor.putBoolean("fromOrientation", true);
        prefsEditor.commit();
        return null;
    }

    @Override
    protected void onDestroy() {
        if (fromOrientation){
            prefsEditor.putBoolean("fromOrientation", false);
            prefsEditor.commit();
        }
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

    public static Fragment getFragment(Screens screen){
        return fragments.get(screen);
    }

    public static <T> void storePreference(String key, T value){
        if (value instanceof String){
            prefsEditor.putString(key, (String) value);
        }else if (value instanceof Boolean){
            prefsEditor.putBoolean(key, (Boolean)value);
        }else if (value instanceof Integer){
            prefsEditor.putInt(key, (Integer) value);
        }
        prefsEditor.apply();
    }

    public static void destroyUserSession(){
        storePreference(USER_ID, "");
        storePreference(AUTH_TOKEN, "");
        storePreference(USER_NAME, "");
        storePreference(USER_EMAIL, "");
        storePreference(IS_AUTHENTICATED, false);
    }

    public static void applyLayoutDesign(View rootView) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-Semibold.otf");
        ViewGroup v = (ViewGroup) rootView;
        for (int i = 0; i < v.getChildCount(); i++) {
            View child = v.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTypeface(font);
            }
            if (child instanceof EditText) {
                ((EditText) child).setTypeface(font);
                child.setBackgroundColor(context.getResources().getColor(R.color.darkGray));
            }
            if (child instanceof Button) {
                ((Button) child).setTypeface(font);
            }
            if (child instanceof RadioGroup){
                RadioGroup rg = (RadioGroup)child;
                for (int j = 0; j < rg.getChildCount(); j++){
                    RadioButton b = (RadioButton)rg.getChildAt(j);
                    b.setTypeface(font);
                }
            }
        }
    }


}
