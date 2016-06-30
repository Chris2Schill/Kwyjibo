package com.seniordesign.kwyjibo.fragments.login_signup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.kwyjibo.R;

/*
 * This class is the screen fragment you will see when the application is started for the first time.
 * It provides the user with buttons to login or signup.
 */
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
                FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                t.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top,
                                      R.anim.slide_in_top, R.anim.slide_out_top);
                t.add(R.id.main_activity_fragment_container,
                        MainActivity.getFragment(MainActivity.Screens.LOGIN));
                t.addToBackStack("LOGIN");
                t.commit();
//                MainActivity.replaceScreen(MainActivity.Screens.LOGIN, "LOGIN",
//                        R.anim.slide_in_top, R.anim.slide_out_top);
            }
        });

        v.findViewById(R.id.intro_fragment_signup_button) .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                t.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top,
                                      R.anim.slide_in_top, R.anim.slide_out_top);
                t.add(R.id.main_activity_fragment_container,
                        MainActivity.getFragment(MainActivity.Screens.SIGNUP));
                t.addToBackStack("SIGNUP");
                t.commit();
//                MainActivity.replaceScreen(MainActivity.Screens.SIGNUP, "SIGNUP",
//                        R.anim.slide_in_top, R.anim.slide_out_top);
            }
        });
    }
}
