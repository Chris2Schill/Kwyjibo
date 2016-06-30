package com.seniordesign.kwyjibo.fragments.login_signup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.seniordesign.kwyjibo.validation.ValidatableEditText;
import com.seniordesign.kwyjibo.activities.ApplicationWrapper;
import com.seniordesign.kwyjibo.restapi.RestAPI;
import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.beans.SessionInfo;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements HasSessionInfo{

    private ValidatableEditText usernameEditText;
    private ValidatableEditText passwordEditText;

    private static final String TAG = "LoginFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment, container, false);

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
//                        getActivity().findViewById(R.id.login_fragment_login_button).setEnabled(false);
                        if (response.body().IS_AUTHENTICATED) {
                            ApplicationWrapper.storePreference(USER_ID, response.body().USER_ID);
                            ApplicationWrapper.storePreference(USER_NAME, response.body().USER_NAME);
                            ApplicationWrapper.storePreference(USER_EMAIL, response.body().USER_EMAIL);
                            ApplicationWrapper.storePreference(AUTH_TOKEN, response.body().AUTH_TOKEN);
                            ApplicationWrapper.storePreference(IS_AUTHENTICATED, true);

                            MainActivity.replaceScreen(MainActivity.Screens.MODE_SELECTION, "MODE_SELECTION");
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
        loginAsAdminButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                RestAPI.requestLogin("admin", "admin", new Callback<SessionInfo>() {
                    @Override
                    public void onResponse(Call<SessionInfo> call, Response<SessionInfo> response) {
//                        getActivity().findViewById(R.id.login_fragment_login_button).setEnabled(false);
                        if (response.body().IS_AUTHENTICATED) {
                            ApplicationWrapper.storePreference(USER_ID, response.body().USER_ID);
                            ApplicationWrapper.storePreference(USER_NAME, response.body().USER_NAME);
                            ApplicationWrapper.storePreference(USER_EMAIL, response.body().USER_EMAIL);
                            ApplicationWrapper.storePreference(AUTH_TOKEN, response.body().AUTH_TOKEN);
                            ApplicationWrapper.storePreference(IS_AUTHENTICATED, true);

                            MainActivity.destroyBackStack();
                            MainActivity.replaceScreen(MainActivity.Screens.MODE_SELECTION, "MODE_SELECTION");
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

        MainActivity.applyLayoutDesign(rootView);

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
                return;
            }
        }
    }
}
