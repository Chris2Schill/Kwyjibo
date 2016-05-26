package com.seniordesign.kwyjibo.fragments.login_signup;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.seniordesign.kwyjibo.activities.MainActivity;
import com.google.android.gms.common.SignInButton;
import com.seniordesign.kwyjibo.interfaces.HasUserInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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




