package com.seniordesign.kwyjibo.fragments.login_signup;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.seniordesign.kwyjibo.FlipListener;
import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.kwyjibo.R;

/*
 * This class is the screen fragment you will see when the application is started for the first time.
 * It provides the user with buttons to login or signup.
 */
public class IntroTitleFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.intro_title_fragment, container, false);
        MainActivity.applyLayoutDesign(rootView);
        setOnClickListeners(rootView);
        return rootView;
    }

    private void setOnClickListeners(View v){

        v.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment loginFragment = MainActivity.getFragment(MainActivity.Screens.LOGIN);
                Fragment signupFragment = MainActivity.getFragment(MainActivity.Screens.SIGNUP);

                FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                t.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top,
                        R.anim.slide_in_top, R.anim.slide_out_top);

                if (!loginFragment.isAdded()){
                    t.add(R.id.main_activity_fragment_container,loginFragment);
                    t.addToBackStack("LOGIN");
                }
                if (signupFragment.isAdded()){
                    getActivity().getSupportFragmentManager().popBackStack();
                    t.hide(signupFragment);
                }

                t.show(loginFragment);
                t.commit();
            }
        });

        v.findViewById(R.id.intro_fragment_signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment signupFragment = MainActivity.getFragment(MainActivity.Screens.SIGNUP);
                Fragment loginFragment = MainActivity.getFragment(MainActivity.Screens.LOGIN);

                FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                t.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top,
                        R.anim.slide_in_top, R.anim.slide_out_top);
                if (!signupFragment.isAdded()) {
                    t.add(R.id.main_activity_fragment_container, signupFragment);
                    t.addToBackStack("SIGNUP");
                }
                if (loginFragment.isAdded()){
                    getActivity().getSupportFragmentManager().popBackStack();
                    t.hide(loginFragment);
                }
                t.show(signupFragment);
                t.commit();
            }
        });
    }
}
