package com.seniordesign.kwyjibo.fragments.login_signup;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.asynctasks.SignupTask;
import com.seniordesign.kwyjibo.kwyjibo.R;

public class SignupFragment extends Fragment {

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
                        new SignupTask(getActivity()).execute(username, email, password);
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
