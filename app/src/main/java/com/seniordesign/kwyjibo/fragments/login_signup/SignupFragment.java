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
import com.seniordesign.kwyjibo.core.Screens;
import com.seniordesign.kwyjibo.custom.decorators.SwipeDetector;
import com.seniordesign.kwyjibo.custom.validation.ValidatableEditText;
import com.seniordesign.kwyjibo.database.restapi.RestAPI;
import com.seniordesign.kwyjibo.core.MainActivity;
import com.seniordesign.kwyjibo.database.models.SessionInfo;
import com.seniordesign.kwyjibo.core.HasSessionInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupFragment extends Fragment implements HasSessionInfo{

    private static final String TAG = "SignupFragment";

    private ValidatableEditText usernameEditText;
    private ValidatableEditText emailEditText;
    private ValidatableEditText passwordEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.signup_fragment, container, false);

        usernameEditText = (ValidatableEditText)rootView.findViewById(R.id.signup_fragment_username_edittext);
        emailEditText = (ValidatableEditText)rootView.findViewById(R.id.signup_fragment_email_edittext);
        passwordEditText = (ValidatableEditText)rootView.findViewById(R.id.signup_fragment_password_edittext);

        Button signupButton = (Button)rootView.findViewById(R.id.signup_fragment_signup_button);
        signupButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        usernameEditText.validate();
                        emailEditText.validate();
                        passwordEditText.validate();
                        if (usernameEditText.getError() != null || emailEditText.getError() != null
                                || passwordEditText.getError() != null){
                            Toast.makeText(getActivity(), "Disabled until form is complete.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String username = usernameEditText.getText().toString();
                        String email = emailEditText.getText().toString();
                        String password = passwordEditText.getText().toString();

                        RestAPI.requestSignup(username, email, password, new Callback<SessionInfo>() {
                            @Override
                            public void onResponse(Call<SessionInfo> call, Response<SessionInfo> response) {
                                Log.e(TAG, response.body().toString());
                                boolean creationSuccessful = response.body().IS_AUTHENTICATED;
                                if (creationSuccessful) {
                                    MainActivity.storePreference(USER_ID, Integer.parseInt(response.body().USER_ID));
                                    MainActivity.storePreference(USER_NAME, response.body().USER_NAME);
                                    MainActivity.storePreference(USER_EMAIL, response.body().USER_EMAIL);
                                    MainActivity.storePreference(AUTH_TOKEN, response.body().AUTH_TOKEN);
                                    MainActivity.storePreference(IS_AUTHENTICATED, true);

                                    MainActivity.replaceScreen(Screens.MODE_SELECTION, "MODE_SELECTION",
                                            android.R.anim.fade_in, android.R.anim.fade_out);
                                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                } else {
                                    MainActivity.destroyUserSession();
                                    Toast.makeText(getActivity(), "Denied", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<SessionInfo> call, Throwable t) {
                                MainActivity.destroyUserSession();
                                Toast.makeText(getActivity(), "Unsuccessful", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
        );

        ViewGroup signupContainer = (ViewGroup)rootView.findViewById(R.id.signup_fragment_container);
        new SwipeDetector(signupContainer).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
            @Override
            public void SwipeEventDetected(View v, SwipeDetector.SwipeType swipeType) {
                if (swipeType == SwipeDetector.SwipeType.BOTTOM_TO_TOP){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        initGoogleSignInButton(rootView);
        MainActivity.applyLayoutDesign(rootView);

        return rootView;
    }

    private void initGoogleSignInButton(View v) {
        // This finds and horizontally centers the 'Sign in with Google' textview on the button.
        SignInButton signInButton = (SignInButton)v.findViewById(R.id.signup_fragment_google_signin_button);
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View child = signInButton.getChildAt(i);
            if (child instanceof TextView) {
                TextView tv = (TextView) child;   // The default without the padding centers between
                tv.setPadding(0, 0, 20, 0);       // the right of icon to end of button rather than
                tv.setText("Sign up with Google");// between start of button to end of button.
                return;
            }
        }
    }
}
