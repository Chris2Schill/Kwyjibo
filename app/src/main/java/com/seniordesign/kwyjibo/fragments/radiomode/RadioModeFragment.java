package com.seniordesign.kwyjibo.fragments.radiomode;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.adapters.StationSelectListAdapter;
import com.seniordesign.kwyjibo.kwyjibo.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class RadioModeFragment extends Fragment {

    private static final String TAG = "RadioModeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.radio_mode_fragment, container, false);

        if (rootView.findViewById(R.id.radio_mode_fragment_container) != null){
            if (savedInstanceState != null){
                return rootView;
            }
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.radio_mode_fragment_container, new StationSelectionFragment())
                    .commit();

        }
        return rootView;
    }
}




