package com.seniordesign.kwyjibo.fragments.login_signup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.seniordesign.kwyjibo.restapi.RestAPI;
import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.beans.SessionInfo;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements HasSessionInfo{

    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment, container, false);
        initGoogleLoginButton(rootView);

        usernameEditText = ((EditText) rootView.findViewById(R.id.login_fragment_username_edittext));
        passwordEditText = ((EditText) rootView.findViewById(R.id.login_fragment_password_edittext));

        Button submitLoginButton = (Button)rootView.findViewById(R.id.login_fragment_login_button);
        submitLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                RestAPI.requestLogin(username, password, new Callback<SessionInfo>() {
                    @Override
                    public void onResponse(Call<SessionInfo> call, Response<SessionInfo> response) {
                        if (response.body().IS_AUTHENTICATED) {
                            MainActivity.storePreference(USER_ID, response.body().USER_ID);
                            MainActivity.storePreference(USER_NAME, response.body().USER_NAME);
                            MainActivity.storePreference(USER_EMAIL, response.body().USER_EMAIL);
                            MainActivity.storePreference(AUTH_TOKEN, response.body().AUTH_TOKEN);
                            MainActivity.storePreference(IS_AUTHENTICATED, true);

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
