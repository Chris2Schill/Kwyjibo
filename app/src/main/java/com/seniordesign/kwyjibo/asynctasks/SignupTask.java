package com.seniordesign.kwyjibo.asynctasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;

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

public class SignupTask extends AsyncTask<String, Void, Map<String,String>>
        implements HasSessionInfo {

    private Context context;

    private static final String TAG = "SignupTask";

    public SignupTask(Context c) {
        context = c;
    }

    @Override
    protected void onPostExecute(Map<String,String> user) {
        boolean creationSuccessful = Boolean.parseBoolean(user.get(IS_AUTHENTICATED));
        if (creationSuccessful){
            MainActivity.storePreference(USER_ID, user.get(USER_ID));
            MainActivity.storePreference(USER_NAME, user.get(USER_NAME));
            MainActivity.storePreference(USER_EMAIL, user.get(USER_EMAIL));
            MainActivity.storePreference(AUTH_TOKEN, user.get(AUTH_TOKEN));
            MainActivity.storePreference(IS_AUTHENTICATED, true);

            MainActivity.replaceScreen(MainActivity.Screens.MODE_SELECTION, true);
            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
        } else{
            MainActivity.destroyUserSession();
            Toast.makeText(context, "Unsuccessful", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Map<String,String> doInBackground(String... params) {

        // The post request's parameters
        Map<String, String> postDataMap = new LinkedHashMap<>();
        postDataMap.put("username", params[0]);
        postDataMap.put("email", params[1]);
        postDataMap.put("password", params[2]);

        // Get the post parameters as a byte buffer
        byte[] postDataBytes = null;
        try {
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> param : postDataMap.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            postDataBytes = postData.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
        }

        String jsonResponse = sendPostRequest(postDataBytes);

        return parseJSONResponse(jsonResponse);

    }

    private String sendPostRequest(byte[] postDataBytes) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = "";
        try {
            // Open Connection
            Uri builtUri = Uri.parse("http://motw.tech/api/AddUser.aspx").buildUpon().build();
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
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
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            response = sb.toString();
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
        return response;
    }

    private Map<String,String> parseJSONResponse(String response){
        Map<String,String> map = new HashMap<>();
        try {
            JSONObject user = new JSONObject(response);
            map.put(USER_ID, user.getString(USER_ID));
            map.put(USER_NAME,user.getString(USER_NAME));
            map.put(USER_EMAIL,user.getString(USER_EMAIL));
            map.put(AUTH_TOKEN,user.getString(AUTH_TOKEN));
            map.put(IS_AUTHENTICATED, user.getString(IS_AUTHENTICATED));
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return map;
    }
}


