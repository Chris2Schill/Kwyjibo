package com.seniordesign.kwyjibo.kwyjibo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;



import com.google.android.gms.common.SignInButton;

public class StartupFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.startup_fragment, container, false);
        if (rootView.findViewById(R.id.login_signup_container) != null){
            if (savedInstanceState == null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.login_signup_container, new IntroTitleFragment())
                        .commit();
            }
        }
        return rootView;
    }
}

class IntroTitleFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.intro_title_fragment, container, false);
        setOnClickListeners(rootView);
        initLayoutDesign(rootView);
        return rootView;
    }

    private void initLayoutDesign(View rootView) {
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNova-Semibold.otf");
        ViewGroup v = (ViewGroup)rootView;
        for (int i = 0; i < v.getChildCount(); i++){
            View child = v.getChildAt(i);
            if (child instanceof TextView){
                ((TextView) child).setTypeface(font);
            }
            if (child instanceof EditText){
                ((EditText) child).setTypeface(font);
                child.setBackgroundColor(getResources().getColor(R.color.darkGray));
            }
            if (child instanceof Button){
                ((Button) child).setTypeface(font);
            }
        }
    }

    private void setOnClickListeners(View v){
        ((Button)v.findViewById(R.id.login_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.login_signup_container, new LoginFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        ((Button)v.findViewById(R.id.intro_fragment_signup_button))
                .setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.login_signup_container, new SignupFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}

class LoginFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment, container, false);
        initGoogleLoginButton(rootView);

        Button submitLoginButton = (Button)rootView.findViewById(R.id.login_fragment_login_button);
        submitLoginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_activity_fragment_container,
                                MainActivity.getFragment(MainActivity.Screens.MODE_SELECTION))
                        .addToBackStack(null)
                        .commit();
            }
        });

        initLayoutDesign(rootView);

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

    private void initLayoutDesign(View rootView){
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNova-Semibold.otf");
        ViewGroup v = (ViewGroup)rootView;
        for (int i = 0; i < v.getChildCount(); i++){
            View child = v.getChildAt(i);
            if (child instanceof TextView){
                ((TextView) child).setTypeface(font);
            }
            if (child instanceof EditText){
                ((EditText) child).setTypeface(font);
                child.setBackgroundColor(getResources().getColor(R.color.darkGray));
            }
            if (child instanceof Button){
                ((Button) child).setTypeface(font);
            }
        }
    }

}

class SignupFragment extends Fragment {

    private TextView signupTextView;
    private TextView withEmailTextView;
    private TextView disclaimerTextView;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText usernameEditText;
    private Button submitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.signup_fragment, container, false);

        signupTextView = (TextView)rootView.findViewById(R.id.signup_fragment_signup_textview);
        withEmailTextView = (TextView)rootView.findViewById(R.id.signup_fragment_withemail_textview);
        disclaimerTextView = (TextView)rootView.findViewById(R.id.signup_fragment_disclaimer_textview);

        emailEditText = (EditText)rootView.findViewById(R.id.signup_fragment_email_edittext);
        passwordEditText = (EditText)rootView.findViewById(R.id.signup_fragment_password_edittext);
        usernameEditText = (EditText)rootView.findViewById(R.id.signup_fragment_username_edittext);

        submitButton = (Button)rootView.findViewById(R.id.signup_fragment_signup_button);

        initGoogleSignInButton(rootView);
        setEditTextBackground();

        setFont();
        return rootView;
    }

    private void setEditTextBackground() {
        emailEditText.setBackgroundColor(getResources().getColor(R.color.darkGray));
        passwordEditText.setBackgroundColor(getResources().getColor(R.color.darkGray));
        usernameEditText.setBackgroundColor(getResources().getColor(R.color.darkGray));
    }

    private void initGoogleSignInButton(View v){
        // This finds and centers the 'Sign in' textview on the button.
        SignInButton signInButton = (SignInButton)v.findViewById(R.id.signup_fragment_google_signin_button);
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View child = signInButton.getChildAt(i);
            if (child instanceof TextView) {
                TextView tv = (TextView)child;    // The default without the padding centers between
                tv.setPadding(0, 0, 20, 0);       // the right of icon to end of button rather than
                tv.setText("Sign up with Google");// between start of button to end of button.
                return;
            }
        }
    }

    private void setFont(){
        Typeface proximaNova = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNova-Semibold.otf");
        signupTextView.setTypeface(proximaNova);
        withEmailTextView.setTypeface(proximaNova);
        disclaimerTextView.setTypeface(proximaNova);
        emailEditText.setTypeface(proximaNova);
        passwordEditText.setTypeface(proximaNova);
        usernameEditText.setTypeface(proximaNova);
        submitButton.setTypeface(proximaNova);
    }
}
