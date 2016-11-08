package com.seniordesign.kwyjibo.core;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.seniordesign.kwyjibo.fragments.screens.StudioModeFragment;
import com.seniordesign.kwyjibo.database.restapi.RestAPI;
import com.seniordesign.kwyjibo.fragments.screens.ModeSelectionFragment;
import com.seniordesign.kwyjibo.fragments.screens.IntroTitleFragment;
import com.seniordesign.kwyjibo.fragments.login_signup.LoginFragment;
import com.seniordesign.kwyjibo.fragments.login_signup.SignupFragment;
import com.seniordesign.kwyjibo.fragments.screens.StationFragment;
import com.seniordesign.kwyjibo.fragments.screens.StationSelectionFragment;
import com.seniordesign.kwyjibo.kwyjibo.R;
import com.seniordesign.kwyjibo.fragments.screens.RecordModeFragment;
import com.seniordesign.kwyjibo.database.services.ObserverService;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

/*
 * This is the central activity class for the entire application. It contains a HashMap of all the
 * 'Screen' fragments. The screen fragments are different than regular fragments because they
 * have been designed to take up the entire screen and act as pseudo-activities for the different
 * screens the user will see when using the app. This class provides static methods which
 * allow you to switch screens anytime using a single line of code.
 */
public class MainActivity extends ApplicationWrapper implements HasSessionInfo, ConnectionCallbacks, OnConnectionFailedListener
{

    private static Map<Screens,Fragment> fragments = new HashMap<>();
    private static final String TAG = "MainActivity";
    private static boolean firstRun = true;
    public static GoogleApiClient mGoogleApiClient;


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
        fragments.put(Screens.RADIO_STATION, new StationFragment());
        fragments.put(Screens.STUDIO_MODE, new StudioModeFragment());

        EventBus.getDefault().register(getFragment(Screens.RADIO_STATION));

        if (findViewById(R.id.main_activity_fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }
            replaceScreen(Screens.INTRO_TITLE, null, android.R.anim.fade_in, android.R.anim.fade_out);
        }

        if (!isMyServiceRunning(ObserverService.class)){
            startService(new Intent(getBaseContext(), ObserverService.class));
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //create an instance of the GoogleAPIClient
        if(mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }



    @Override
    protected void onResume() {
        super.onResume();
        RestAPI.authenticateSession(getStringPreference(USER_ID), getStringPreference(AUTH_TOKEN),
                new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        boolean authenticated = false;
                        try{
                            authenticated = Boolean.parseBoolean(response.body().toString());
                        }catch(Exception e){
                            Log.e(TAG, e.getMessage());
                        }
                        if (authenticated) {
                            if (firstRun || getSupportFragmentManager().getBackStackEntryCount() == 0) {
                                replaceScreen(Screens.MODE_SELECTION, null,
                                        android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                            firstRun = false;
                            Log.d(TAG, "AUTHENTICATION SUCCESSFUL.");
                        } else {
                            destroyUserSession();
                            destroyBackStack();
                            replaceScreen(Screens.INTRO_TITLE, null,
                                    android.R.anim.fade_in, android.R.anim.fade_out);
                            Log.d(TAG, "AUTHENTICATION DENIED.");
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        destroyUserSession();
                        destroyBackStack();
                        replaceScreen(Screens.INTRO_TITLE, null,
                                android.R.anim.fade_in, android.R.anim.fade_out);
                        Log.d(TAG, "Authentication Request Failed.");
                    }
                });

        mGoogleApiClient.connect();
    }

    public static void destroyBackStack(){
        FragmentManager fm = context.getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); i++){
            fm.popBackStack();
        }
    }

    public static void replaceScreen(Screens screen, String tag, int animInResourceId, int animOutResourceId){
        FragmentTransaction transaction = context.getSupportFragmentManager() .beginTransaction();
        transaction.setCustomAnimations(animInResourceId, animOutResourceId, animInResourceId, animOutResourceId);
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
        storePreference(CURRENT_STATION, 0);
        storePreference(IS_AUTHENTICATED, false);
    }



    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    public static String getLocation()
    {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Geocoder geocoder = new Geocoder(ApplicationWrapper.context);
            if(mLastLocation != null){

                try{
                    List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(),1);
                    String city = addresses.get(0).getLocality();
                    return city;
                }
                catch(IOException e){

                }
            }
        }
        return null;
    }




    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }


}
