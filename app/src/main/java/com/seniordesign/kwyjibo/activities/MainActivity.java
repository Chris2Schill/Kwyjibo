package com.seniordesign.kwyjibo.activities;


import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.seniordesign.kwyjibo.fragments.radiomode.CreateStationFragment;
import com.seniordesign.kwyjibo.fragments.ModeSelectionFragment;
import com.seniordesign.kwyjibo.interfaces.AuthenticationHandler;
import com.seniordesign.kwyjibo.kwyjibo.R;
import com.seniordesign.kwyjibo.fragments.radiomode.RadioModeFragment;
import com.seniordesign.kwyjibo.fragments.recordmode.RecordModeFragment;
import com.seniordesign.kwyjibo.fragments.login_signup.StartupFragment;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements HasSessionInfo {

    private static Map<Screens,Fragment> fragments = new HashMap<>();
    private static final String TAG = "MainActivity";

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
        new AuthCheckAsyncTask(new AuthenticationHandler() {
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


    private static class AuthCheckAsyncTask extends AsyncTask<String, Void, Boolean>{

        private static final String TAG = "AuthCheckAsyncTask";

        AuthenticationHandler handler;

        AuthCheckAsyncTask(AuthenticationHandler handler){
            this.handler = handler;
        }

        @Override
        protected void onPostExecute(Boolean authenticated) {
            handler.isAuthenticated(authenticated);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonResponse = "";
            try{
                Uri builtUri = Uri.parse("http://motw.tech/api/AuthenticateUser.aspx").buildUpon()
                        .appendQueryParameter("userId", params[0])
                        .appendQueryParameter("authToken", params[1])
                        .build();
                Log.v(TAG, builtUri.toString());
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null){
                    sb.append(line).append('\n');
                }
                jsonResponse = sb.toString();
            }catch (IOException e){
                Log.e(TAG, e.getMessage());
            }finally{
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try{
                        reader.close();
                    }catch(IOException e){
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            return parseJsonResponse(jsonResponse);
        }

        private Boolean parseJsonResponse(String jsonResponse) {
            try {
                JSONObject user = new JSONObject(jsonResponse);
                Log.d(TAG, "jsonResponse: " + user.getBoolean(IS_AUTHENTICATED));
                return user.getBoolean(IS_AUTHENTICATED);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
