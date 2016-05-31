package com.seniordesign.kwyjibo.asynctasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.seniordesign.kwyjibo.interfaces.AuthenticationHandler;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthenticationTask extends AsyncTask<String, Void, Boolean> implements HasSessionInfo {

    private static final String TAG = "AuthenticationTask";

    AuthenticationHandler handler;

    public AuthenticationTask(AuthenticationHandler handler){
        this.handler = handler;
    }

    @Override
    protected void onPostExecute(Boolean authenticated) {
        handler.isAuthenticated(authenticated);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResponse = "";
        try{
            Uri builtUri = Uri.parse("http://motw.tech/api/AuthenticateUser.aspx").buildUpon()
                    .appendQueryParameter("userId", params[0])
                    .appendQueryParameter("authToken", params[1])
                    .build();
            Log.v(TAG, builtUri.toString());
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                sb.append(line).append('\n');
            }
            jsonResponse = sb.toString();
        }catch (IOException e){
            Log.e(TAG, e.getMessage());
        }finally{
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try{
                    reader.close();
                }catch(IOException e){
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        return parseJsonResponse(jsonResponse);
    }

    private Boolean parseJsonResponse(String jsonResponse) {
        try {
            JSONObject user = new JSONObject(jsonResponse);
            Log.d(TAG, "jsonResponse: " + user.getBoolean(IS_AUTHENTICATED));
            return user.getBoolean(IS_AUTHENTICATED);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}

