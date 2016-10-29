package com.seniordesign.kwyjibo.fragments.login_signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.seniordesign.kwyjibo.core.Screens;
import com.seniordesign.kwyjibo.custom.decorators.SwipeDetector;
import com.seniordesign.kwyjibo.custom.validation.ValidatableEditText;
import com.seniordesign.kwyjibo.core.ApplicationWrapper;
import com.seniordesign.kwyjibo.database.restapi.RestAPI;
import com.seniordesign.kwyjibo.core.MainActivity;
import com.seniordesign.kwyjibo.database.models.SessionInfo;
import com.seniordesign.kwyjibo.core.HasSessionInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements HasSessionInfo{

    private ValidatableEditText usernameEditText;
    private ValidatableEditText passwordEditText;
    private String googleUsername = "";
    private String googlePassword = "";
    private String googleToken = "";

    private static final String TAG = "LoginFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment, container, false);
        MainActivity.applyLayoutDesign(rootView);

        initGoogleLoginButton(rootView);

        usernameEditText = ((ValidatableEditText) rootView.findViewById(R.id.login_fragment_username_edittext));
        passwordEditText = ((ValidatableEditText) rootView.findViewById(R.id.login_fragment_password_edittext));

        if (usernameEditText == null || passwordEditText == null){
            Log.e(TAG, "EDIT TEXT NULL");
        }

        Button submitLoginButton = (Button)rootView.findViewById(R.id.login_fragment_login_button);
        submitLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                usernameEditText.validate();
                passwordEditText.validate();
                if (usernameEditText.getError() != null || passwordEditText.getError() != null){
                    Toast.makeText(getActivity(), "Disabled until form complete.", Toast.LENGTH_SHORT).show();
                    return;
                }

                RestAPI.requestLogin(username, password, new Callback<SessionInfo>() {
                    @Override
                    public void onResponse(Call<SessionInfo> call, Response<SessionInfo> response) {
                        if (response.body().IS_AUTHENTICATED) {
                            ApplicationWrapper.storePreference(USER_ID, response.body().USER_ID);
                            ApplicationWrapper.storePreference(USER_NAME, response.body().USER_NAME);
                            ApplicationWrapper.storePreference(USER_EMAIL, response.body().USER_EMAIL);
                            ApplicationWrapper.storePreference(AUTH_TOKEN, response.body().AUTH_TOKEN);
                            ApplicationWrapper.storePreference(IS_AUTHENTICATED, true);

                            MainActivity.destroyBackStack();
                            MainActivity.replaceScreen(Screens.MODE_SELECTION, null,
                                    android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            MainActivity.destroyUserSession();
                            Toast.makeText(getActivity(), "Account Credentials Invalid.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SessionInfo> call, Throwable t) {
                        MainActivity.destroyUserSession();
                        Toast.makeText(getActivity(), "Login failed to complete", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        Button loginAsAdminButton = (Button)rootView.findViewById(R.id.login_as_admin_button);
        loginAsAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestAPI.requestLogin("admin", "admin", new Callback<SessionInfo>() {
                    @Override
                    public void onResponse(Call<SessionInfo> call, Response<SessionInfo> response) {
                        if (response.body().IS_AUTHENTICATED) {
                            ApplicationWrapper.storePreference(USER_ID, response.body().USER_ID);
                            ApplicationWrapper.storePreference(USER_NAME, response.body().USER_NAME);
                            ApplicationWrapper.storePreference(USER_EMAIL, response.body().USER_EMAIL);
                            ApplicationWrapper.storePreference(AUTH_TOKEN, response.body().AUTH_TOKEN);
                            ApplicationWrapper.storePreference(IS_AUTHENTICATED, true);

                            MainActivity.destroyBackStack();
                            MainActivity.replaceScreen(Screens.MODE_SELECTION, null,
                                    android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            MainActivity.destroyUserSession();
                            Toast.makeText(getActivity(), "Account Credentials Invalid.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SessionInfo> call, Throwable t) {
                        MainActivity.destroyUserSession();
                        Toast.makeText(getActivity(), "Login failed to complete", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        ViewGroup loginContainer = (ViewGroup)rootView.findViewById(R.id.login_fragment_container);
        new SwipeDetector(loginContainer).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
            @Override
            public void SwipeEventDetected(View v, SwipeDetector.SwipeType swipeType) {
                if (swipeType == SwipeDetector.SwipeType.BOTTOM_TO_TOP) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        return rootView;
    }

    private void initGoogleLoginButton(View v){
        // This finds and centers the 'Sign in' textview on the button.
        SignInButton signInButton = (SignInButton)v.findViewById(R.id.login_fragment_google_login_button);
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View child = signInButton.getChildAt(i);
            if (child instanceof TextView) {
                TextView tv = (TextView)child;    // The default without the padding centers between
                tv.setPadding(0, 0, 20, 0);       // the right of icon to end of button rather than
                tv.setText("Log in with Google");// between start of button to end of button.
            }
        }
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"test");
                googleSignIn();
                //use google login info for app database credentials
                //RestAPI.requestLogin(googleUsername, googlePassword,  new Callback<SessionInfo>()){



            }
        });

    }

    private void googleSignIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(MainActivity.mGoogleApiClient);

        startActivityForResult(signInIntent, 2);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //results returned from launching intent in getSignInIntent
        if(requestCode == 2) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount acct = result.getSignInAccount();
                googleUsername = acct.getDisplayName();
                googlePassword = acct.getEmail();
                googleToken = acct.getIdToken();
                //try passing token as account password so a new token is generated to be
                //the same format as other tokens in DB
            }
        }
    }





}
