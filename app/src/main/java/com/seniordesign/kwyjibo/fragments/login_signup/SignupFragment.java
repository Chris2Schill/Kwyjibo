package com.seniordesign.kwyjibo.fragments.login_signup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.asynctasks.SignupTask;
import com.seniordesign.kwyjibo.interfaces.AsyncTaskCallback;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;

import java.util.HashMap;
import java.util.Map;

public class SignupFragment extends Fragment implements HasSessionInfo{

    private static final String TAG = "SignupFragment";

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.signup_fragment, container, false);

        usernameEditText = (EditText)rootView.findViewById(R.id.signup_fragment_username_edittext);
        emailEditText = (EditText)rootView.findViewById(R.id.signup_fragment_email_edittext);
        passwordEditText = (EditText)rootView.findViewById(R.id.signup_fragment_password_edittext);

        rootView.findViewById(R.id.signup_fragment_signup_button).setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        String username = usernameEditText.getText().toString();
                        String email = emailEditText.getText().toString();
                        String password = passwordEditText.getText().toString();
                        new SignupTask(new AsyncTaskCallback() {
                            @Override
                            public void callback(Object obj) {
                                Map<String,String> user = (HashMap<String,String>)obj;
                                boolean creationSuccessful = Boolean.parseBoolean(user.get(IS_AUTHENTICATED));
                                if (creationSuccessful){
                                    MainActivity.storePreference(USER_ID, user.get(USER_ID));
                                    MainActivity.storePreference(USER_NAME, user.get(USER_NAME));
                                    MainActivity.storePreference(USER_EMAIL, user.get(USER_EMAIL));
                                    MainActivity.storePreference(AUTH_TOKEN, user.get(AUTH_TOKEN));
                                    MainActivity.storePreference(IS_AUTHENTICATED, true);

                                    MainActivity.replaceScreen(MainActivity.Screens.MODE_SELECTION, true);
                                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                } else{
                                    MainActivity.destroyUserSession();
                                    Toast.makeText(getActivity(), "Unsuccessful", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).execute(username, email, password);
                    }
                }
        );

        initGoogleSignInButton(rootView);
        MainActivity.applyLayoutDesign(rootView);

        return rootView;
    }

    private void initGoogleSignInButton(View v) {
        // This finds and centers the 'Sign in' textview on the button.
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
