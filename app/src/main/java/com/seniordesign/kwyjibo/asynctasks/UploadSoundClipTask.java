package com.seniordesign.kwyjibo.asynctasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.beans.SoundClipInfo;
import com.seniordesign.kwyjibo.interfaces.AsyncTaskCallback;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;
import com.seniordesign.kwyjibo.interfaces.ListViewHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class UploadSoundClipTask extends AsyncTask<String, Void, SoundClipInfo>
        implements HasSessionInfo {

    private AsyncTaskCallback handler;
    private static final String TAG = "UploadSoundClipTask";

    public UploadSoundClipTask(AsyncTaskCallback handler) {
        this.handler = handler;
    }

    @Override
    protected void onPostExecute(SoundClipInfo clip) {
        handler.callback(clip);
    }

    @Override
    protected SoundClipInfo doInBackground(String... params) {

        // The new station's parameters
        Map<String,String> postDataMap = new LinkedHashMap<>();
        postDataMap.put("stationName", MainActivity.getStringPreference("currentStation"));
        postDataMap.put("clipName", params[0]);
        postDataMap.put("userId", MainActivity.getStringPreference(USER_ID));
        postDataMap.put("username", MainActivity.getStringPreference(USER_NAME));
        postDataMap.put("location", "Orlando");
        postDataMap.put("category", params[1]);
        postDataMap.put("authToken", MainActivity.getStringPreference(AUTH_TOKEN));

        // Get the post parameters as a byte buffer
        byte[] postDataBytes = null;
        try{
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String,String> param : postDataMap.entrySet()){
                if (postData.length() != 0) {
                    postData.append('&');
                }
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            postDataBytes = postData.toString().getBytes("UTF-8");
        }catch(UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }

        // Send Request and try to parse response.
        String jsonResponse = sendPostRequest(postDataBytes);
        try{
            return parseJSONResponse(jsonResponse);
        }catch(JSONException e){
            Log.e(TAG, e.getMessage());
        }
        Log.d(TAG, "doInBackground(String...) returned null");
        return null;
    }

    private String sendPostRequest(byte[] postDataBytes) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = "";
        try{
            // Open Connection
            Uri builtUri = Uri.parse("http://motw.tech/api/AddSoundToStation.aspx").buildUpon().build();
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataBytes.length));
            urlConnection.getOutputStream().write(postDataBytes);
            urlConnection.connect();

            // Build response
            StringBuilder sb = new StringBuilder();
            String line;
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((line = reader.readLine()) != null){
                sb.append(line).append('\n');
            }

            response = sb.toString();
        }catch(IOException e){
            Log.e(TAG, e.getMessage());
        }finally{
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try{
                    reader.close();
                }catch (final IOException e){
                    Log.e(TAG,e.getMessage());
                }
            }
        }
        return response;
    }

    private SoundClipInfo parseJSONResponse(String jsonStr) throws JSONException {
        Log.d(TAG, jsonStr);
        JSONObject clipJson = new JSONObject(jsonStr);


        int id = clipJson.getInt("Id");
        String name = clipJson.getString("Name");
        String createdBy = clipJson.getString("CreatedBy");
        String location = clipJson.getString("Location");
        String category = clipJson.getString("Category");
        String filepath = clipJson.getString("Filepath");
        String uploadDate = clipJson.getString("UploadDate");
        return new SoundClipInfo(id, name, createdBy, location, category, filepath, uploadDate);
    }
}

