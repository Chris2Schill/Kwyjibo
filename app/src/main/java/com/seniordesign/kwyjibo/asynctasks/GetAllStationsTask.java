package com.seniordesign.kwyjibo.asynctasks;

import android.os.AsyncTask;

import com.seniordesign.kwyjibo.interfaces.ListViewHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetAllStationsTask extends AsyncTask<Void, Void, String[]> {

    private static final String TAG = "GetAllStationsTask";

    private ListViewHandler handler;

    public GetAllStationsTask(ListViewHandler handler){
        this.handler = handler;
    }

    @Override
    protected void onPostExecute(String[] stations) {
        handler.updateListView(stations);
    }

    @Override
    protected String[] doInBackground(Void... params) {
        String baseURL = "http://motw.tech/api/GetStations.aspx";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResponse = null;
        try{
            URL url = new URL(baseURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
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
                } catch (IOException e) {
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

