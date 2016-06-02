package com.seniordesign.kwyjibo.asynctasks;

import org.json.JSONArray;


import android.os.AsyncTask;
import android.util.Log;

import com.seniordesign.kwyjibo.interfaces.AsyncTaskCallback;
import com.seniordesign.kwyjibo.interfaces.ListViewHandler;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetCategoriesTask extends AsyncTask<Void, Void, String[]> {

    private static final String TAG = "GetCategoriesTask";

    private AsyncTaskCallback handler;

    public GetCategoriesTask(AsyncTaskCallback handler){
        this.handler = handler;
    }

    @Override
    protected void onPostExecute(String[] categories) {
        handler.callback(categories);
    }

    @Override
    protected String[] doInBackground(Void... params) {
        String baseURL = "http://motw.tech/api/GetCategories.aspx";
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
        Log.d(TAG, jsonStr);
        JSONArray categoriesArray = new JSONArray(jsonStr);
        Log.d(TAG, categoriesArray.toString());
        String[] categoriesList = new String[categoriesArray.length()];
        for (int i = 0; i < categoriesArray.length(); i++){
            categoriesList[i] = categoriesArray.get(i).toString();
        }
        return categoriesList;
    }
}

