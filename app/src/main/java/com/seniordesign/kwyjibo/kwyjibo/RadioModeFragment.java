package com.seniordesign.kwyjibo.kwyjibo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

class StationSelectionFragment extends Fragment{
    private ListView stationsListView;
    private ArrayAdapter<String> listAdapter;
    private Button createStationButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.station_selection_fragment, container, false);
        enableStationListView(rootView);
        enableCreateStationButton(rootView);
        new StationPopulatorAsyncTask().execute();
        return rootView;
    }

    private void enableStationListView(View v){
        listAdapter = new ArrayAdapter<>(getActivity(), R.layout.radio_mode_list_item,
                R.id.radio_mode_list_item_textview,
                new ArrayList<String>());

        stationsListView = (ListView) v.findViewById(R.id.radio_mode_list_view);
        stationsListView.setAdapter(listAdapter);

        stationsListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.radio_mode_fragment_container, new StationFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private void enableCreateStationButton(View v){
        createStationButton = (Button)v.findViewById(R.id.create_station_button);
        createStationButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.main_activity_fragment_container,
                        MainActivity.getFragment(MainActivity.Screens.CREATE_STATION));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    public class StationPopulatorAsyncTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected void onPostExecute(String[] stations) {
            if (stations != null){
                listAdapter.clear();
                for (String s : stations){
                    listAdapter.add(s);
                }
            }
        }

        @Override
        protected String[] doInBackground(Void... params) {
            if (params.length == 0){
//                  return null;
            }

            String baseURL = "http://motw.tech/code/GetStations.aspx";
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonResponse = null;

            try{
                URL url = new URL(baseURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                }
                if (buffer.length() == 0){
                    return null;
                }
                jsonResponse = buffer.toString();
            }catch(IOException e){
//                Log.e(TAG, "doInBackground(): " + e.getMessage());
            }finally{
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try{
                        reader.close();
                    } catch (final IOException e) {
//                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            try {
                return getStationDataFromJson(jsonResponse);
            }catch(JSONException e){
//                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        private String[] getStationDataFromJson(String jsonStr) throws JSONException {

            JSONArray stationsArray = new JSONArray(jsonStr);

            String[] stationsList = new String[stationsArray.length()];
            for (int i = 0; i < stationsArray.length(); i++){
                stationsList[i] = stationsArray.getJSONObject(i).getString("Name");
            }
            return stationsList;
        }
    }
}

class StationFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.station_fragment, container, false);
        return rootView;
    }

}
