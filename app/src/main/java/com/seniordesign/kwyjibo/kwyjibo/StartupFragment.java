package com.seniordesign.kwyjibo.kwyjibo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.SignInButton;

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

class IntroTitleFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.intro_title_fragment, container, false);
        setOnClickListeners(rootView);
        initLayoutDesign(rootView);
        return rootView;
    }

    private void initLayoutDesign(View rootView) {
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNova-Semibold.otf");
        ViewGroup v = (ViewGroup)rootView;
        for (int i = 0; i < v.getChildCount(); i++){
            View child = v.getChildAt(i);
            if (child instanceof TextView){
                ((TextView) child).setTypeface(font);
            }
            if (child instanceof EditText){
                ((EditText) child).setTypeface(font);
                child.setBackgroundColor(getResources().getColor(R.color.darkGray));
            }
            if (child instanceof Button){
                ((Button) child).setTypeface(font);
            }
        }
    }

    private void setOnClickListeners(View v){
        ((Button)v.findViewById(R.id.login_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.login_signup_container, new LoginFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        ((Button)v.findViewById(R.id.intro_fragment_signup_button))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.login_signup_container, new SignupFragment())
                                .addToBackStack(null)
                                .commit();
                    }
                });
    }
}

class LoginFragment extends Fragment{

    private EditText usernameET;
    private EditText passwordET;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment, container, false);
        initGoogleLoginButton(rootView);

        usernameET = ((EditText) rootView.findViewById(R.id.login_fragment_username_edittext));
        passwordET = ((EditText) rootView.findViewById(R.id.login_fragment_password_edittext));

        Button submitLoginButton = (Button)rootView.findViewById(R.id.login_fragment_login_button);
        submitLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameET.getText().toString();
                String password = passwordET.getText().toString();

                new LoginAsyncTask(getActivity()).execute(username, password);

            }
        });

        initLayoutDesign(rootView);

        return rootView;
    }

    private void initGoogleLoginButton(View v){
        // This finds and centers the 'Sign in' textview on the button.
        SignInButton signInButton = (SignInButton)v.findViewById(R.id.login_fragment_google_login_button);
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View child = signInButton.getChildAt(i);
            if (child instanceof TextView) {
                TextView tv = (TextView)child;    // The default without the padding centers between
                tv.setPadding(0, 0, 20, 0);       // the right of icon to end of button rather than
                tv.setText("Log in with Google");// between start of button to end of button.
                return;
            }
        }
    }

    private void initLayoutDesign(View rootView){
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNova-Semibold.otf");
        ViewGroup v = (ViewGroup)rootView;
        for (int i = 0; i < v.getChildCount(); i++){
            View child = v.getChildAt(i);
            if (child instanceof TextView){
                ((TextView) child).setTypeface(font);
            }
            if (child instanceof EditText){
                ((EditText) child).setTypeface(font);
                child.setBackgroundColor(getResources().getColor(R.color.darkGray));
            }
            if (child instanceof Button){
                ((Button) child).setTypeface(font);
            }
        }
    }

    private class LoginAsyncTask extends AsyncTask<String,Void,Map<String,String>> {

        private Context context;

        private static final String TAG = "LoginAsyncTask";

        LoginAsyncTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPostExecute(Map<String,String> user) {
            boolean authenticated = Boolean.parseBoolean(user.get("Authenticated"));
            if (authenticated){
                ((MainActivity)getActivity()) .storePreference("userId", user.get("Id"))
                        .storePreference("username", user.get("Username"))
                        .storePreference("email", user.get("Email"))
                        .storePreference("authToken", user.get("AuthToken"))
                        .storePreference("authenticated", true);
                ((MainActivity)context).replaceScreen(MainActivity.Screens.MODE_SELECTION, true);
            }else{
                ((MainActivity)getActivity()).destroyUserSession();
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
                Log.e(TAG,builtUri.toString());
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
                map.put("Id", user.getString("Id"));
                map.put("Username",user.getString("Username"));
                map.put("Email",user.getString("Email"));
                map.put("AuthToken",user.getString("AuthToken"));
                map.put("Authenticated" , user.getString("Authenticated"));

            }catch(JSONException e){
                Log.e(TAG,e.getMessage());
            }
            return map;
        }
    }

}

class SignupFragment extends Fragment {

    private static final String TAG = "SignupFragment";

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.signup_fragment, container, false);

        usernameEditText = (EditText)rootView.findViewById(R.id.signup_fragment_username_edittext);
        emailEditText = (EditText)rootView.findViewById(R.id.signup_fragment_email_edittext);
        passwordEditText = (EditText)rootView.findViewById(R.id.signup_fragment_password_edittext);

        ((Button)rootView.findViewById(R.id.signup_fragment_signup_button)).setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        String username = usernameEditText.getText().toString();
                        String email = emailEditText.getText().toString();
                        String password = passwordEditText.getText().toString();
                        new SignupAsyncTask(getActivity()).execute(username, email, password);
                    }
                }
        );

        initGoogleSignInButton(rootView);
        initLayoutDesign(rootView);

        return rootView;
    }

    private void initGoogleSignInButton(View v) {
        // This finds and centers the 'Sign in' textview on the button.
        SignInButton signInButton = (SignInButton) v.findViewById(R.id.signup_fragment_google_signin_button);
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View child = signInButton.getChildAt(i);
            if (child instanceof TextView) {
                TextView tv = (TextView) child;    // The default without the padding centers between
                tv.setPadding(0, 0, 20, 0);       // the right of icon to end of button rather than
                tv.setText("Sign up with Google");// between start of button to end of button.
                return;
            }
        }
    }

    private void initLayoutDesign(View rootView) {
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNova-Semibold.otf");
        ViewGroup v = (ViewGroup) rootView;
        for (int i = 0; i < v.getChildCount(); i++) {
            View child = v.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTypeface(font);
            }
            if (child instanceof EditText) {
                ((EditText) child).setTypeface(font);
                child.setBackgroundColor(getResources().getColor(R.color.darkGray));
            }
            if (child instanceof Button) {
                ((Button) child).setTypeface(font);
            }
        }
    }

    private class SignupAsyncTask extends AsyncTask<String, Void, Map<String,String>> {

        private Context context;

        SignupAsyncTask(Context c) {
            context = c;
        }

        @Override
        protected void onPostExecute(Map<String,String> user) {
            boolean creationSuccessful = Boolean.parseBoolean(user.get("Authenticated"));
            if (creationSuccessful){
                ((MainActivity)getActivity()).storePreference("userId", user.get("Id"))
                        .storePreference("username", user.get("Username"))
                        .storePreference("email", user.get("Email"))
                        .storePreference("authToken", user.get("AuthToken"))
                        .storePreference("authenticated", true);

                ((MainActivity)context).replaceScreen(MainActivity.Screens.MODE_SELECTION, true);
                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
            } else{
                ((MainActivity)getActivity()).destroyUserSession();
                Toast.makeText(context, "Unsuccessful", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Map<String,String> doInBackground(String... params) {

            // The new station's parameters
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
    }

    private Map<String,String> parseJSONResponse(String response){
        Map<String,String> map = new HashMap<>();
        try {
            JSONObject user = new JSONObject(response);
            map.put("Id", user.getString("Id"));
            map.put("Username",user.getString("Username"));
            map.put("Email",user.getString("Email"));
            map.put("AuthToken",user.getString("AuthToken"));
            map.put("Authenticated" , user.getString("Authenticated"));
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
        }
        return map;
    }
}