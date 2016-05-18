package com.seniordesign.kwyjibo.kwyjibo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class LoginFragment extends Fragment {

    private Button guestLoginButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // In android, all ui-elements extend the 'View' class.
        // rootView is the RelativeLayout from login_fragment.xml
        View rootView = inflater.inflate(R.layout.login_fragment, container, false);


        // This retrieves the ui-element reference pointer from the rootViews view hierarchy.
        guestLoginButton = (Button)rootView.findViewById(R.id.guest_login_button);

        // This assigns the View.OnClickListener interface to this button.
        // This syntax is weird. All that is happening here is overriding
        // the onClick method of the instantiated object on the fly.
        guestLoginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_activity_fragment_container,
                        ((MainActivity)getActivity()).modeSelectionFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return rootView;
    }
}
