package com.seniordesign.kwyjibo.fragments.login_signup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seniordesign.kwyjibo.kwyjibo.R;

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




