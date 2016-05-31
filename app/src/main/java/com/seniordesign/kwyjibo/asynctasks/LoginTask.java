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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoginTask extends AsyncTask<String,Void,Map<String,String>>
        implements HasSessionInfo {

    private Context context;

    private static final String TAG = "LoginTask";

    public LoginTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPostExecute(Map<String,String> user) {
        boolean isAuthenticated = Boolean.parseBoolean(user.get(IS_AUTHENTICATED));
        if (isAuthenticated){
            MainActivity.storePreference(USER_ID, user.get(USER_ID));
            MainActivity.storePreference(USER_NAME, user.get(USER_NAME));
            MainActivity.storePreference(USER_EMAIL, user.get(USER_EMAIL));
            MainActivity.storePreference(AUTH_TOKEN, user.get(AUTH_TOKEN));
            MainActivity.storePreference(IS_AUTHENTICATED, true);

            MainActivity.replaceScreen(MainActivity.Screens.MODE_SELECTION, true);
        }else{
            MainActivity.destroyUserSession();
            Toast.makeText(context, "Account Credentials Invalid.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Map<String,String> doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResponse = "";
        try{
            Uri builtUri = Uri.parse("http://motw.tech/api/Login.aspx").buildUpon()
                    .appendQueryParameter("username", params[0])
                    .appendQueryParameter("password", params[1])
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

    private Map<String,String> parseJsonResponse(String jsonResponse){
        Map<String,String> map = new HashMap<>();
        try{
            JSONObject user = new JSONObject(jsonResponse);
            map.put(USER_ID, user.getString(USER_ID));
            map.put(USER_NAME,user.getString(USER_NAME));
            map.put(USER_EMAIL,user.getString(USER_EMAIL));
            map.put(AUTH_TOKEN,user.getString(AUTH_TOKEN));
            map.put(IS_AUTHENTICATED, user.getString(IS_AUTHENTICATED));

            Log.v(TAG, "User Id: " + user.getString(USER_ID));
            Log.v(TAG, "Username: " + user.getString(USER_NAME));
            Log.v(TAG, "User Email: " + user.getString(USER_EMAIL));
            Log.v(TAG, "AuthToken: " + user.getString(AUTH_TOKEN));
            Log.v(TAG, "Authenticated: " + user.getString(IS_AUTHENTICATED));
        }catch(JSONException e){
            Log.e(TAG, e.getMessage());
        }
        return map;
    }
}


