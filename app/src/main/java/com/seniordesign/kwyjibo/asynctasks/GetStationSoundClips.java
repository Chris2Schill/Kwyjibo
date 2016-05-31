package com.seniordesign.kwyjibo.asynctasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.seniordesign.kwyjibo.beans.SoundClipInfo;
import com.seniordesign.kwyjibo.interfaces.ListViewHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetStationSoundClips extends AsyncTask<String, Void, SoundClipInfo[]> {

    private static final String TAG = "StationCurrSoundsATask";
    private ListViewHandler handler;

    public GetStationSoundClips(ListViewHandler handler){
        this.handler = handler;
    }

    @Override
    protected void onPostExecute(SoundClipInfo[] items) {
        handler.updateListView(items);
    }


    @Override
    protected SoundClipInfo[] doInBackground(String... params) {
        String baseURL = "http://motw.tech/api/GetStationSoundClips.aspx";
        Uri builtUri = Uri.parse(baseURL).buildUpon()
                .appendQueryParameter("stationName", params[0])
                .build();
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResponse = null;

        try {
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder sb = new StringBuilder();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            if (sb.length() == 0) {
                return null;
            }
            jsonResponse = sb.toString();

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        try {
            Log.v(TAG, "jsonResponse: " + jsonResponse);
            return getSoundDataFromJson(jsonResponse);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    private SoundClipInfo[] getSoundDataFromJson(String jsonStr) throws JSONException {

        JSONArray itemsArray = new JSONArray(jsonStr);

        SoundClipInfo[] clips = new SoundClipInfo[itemsArray.length()];
        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemObj = (JSONObject) itemsArray.get(i);
            int id = itemObj.getInt("Id");
            String name = itemObj.getString("Name");
            String createdBy = itemObj.getString("CreatedBy");
            String location = itemObj.getString("Location");
            String category = itemObj.getString("Category");
            String filepath = itemObj.getString("Filepath");
            String uploadDate = itemObj.getString("UploadDate");
            clips[i] = new SoundClipInfo(id, name, createdBy, location, category, filepath, uploadDate);
        }

        return clips;
    }

}
