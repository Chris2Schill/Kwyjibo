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

import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.kwyjibo.R;

public class IntroTitleFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.intro_title_fragment, container, false);
        setOnClickListeners(rootView);
        MainActivity.applyLayoutDesign(rootView);
        return rootView;
    }

    private void setOnClickListeners(View v){
        v.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.login_signup_container, new LoginFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        v.findViewById(R.id.intro_fragment_signup_button)
                .setOnClickListener(new View.OnClickListener() {
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
