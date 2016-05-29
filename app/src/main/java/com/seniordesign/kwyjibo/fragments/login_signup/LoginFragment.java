package com.seniordesign.kwyjibo.fragments.login_signup;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

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

    private class LoginAsyncTask extends AsyncTask<String,Void,Map<String,String>>
            implements HasSessionInfo {

        private Context context;

        private static final String TAG = "LoginAsyncTask";

        LoginAsyncTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPostExecute(Map<String,String> user) {
            boolean isAuthenticated = Boolean.parseBoolean(user.get(IS_AUTHENTICATED));
            if (isAuthenticated){
                ((MainActivity)getActivity()).storePreference(USER_ID, user.get(USER_ID))
                        .storePreference(USER_NAME, user.get(USER_NAME))
                        .storePreference(USER_EMAIL, user.get(USER_EMAIL))
                        .storePreference(AUTH_TOKEN, user.get(AUTH_TOKEN))
                        .storePreference(IS_AUTHENTICATED, true);
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
                Log.e(TAG,e.getMessage());
            }
            return map;
        }
    }

}
