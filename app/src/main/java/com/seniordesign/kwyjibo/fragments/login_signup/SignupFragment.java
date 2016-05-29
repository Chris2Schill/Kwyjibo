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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SignupFragment extends Fragment {

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

    private class SignupAsyncTask extends AsyncTask<String, Void, Map<String,String>>
            implements HasSessionInfo {

        private Context context;

        SignupAsyncTask(Context c) {
            context = c;
        }

        @Override
        protected void onPostExecute(Map<String,String> user) {
            boolean creationSuccessful = Boolean.parseBoolean(user.get(IS_AUTHENTICATED));
            if (creationSuccessful){
                ((MainActivity)getActivity()).storePreference(USER_ID, user.get(USER_ID))
                        .storePreference(USER_NAME, user.get(USER_NAME))
                        .storePreference(USER_EMAIL, user.get(USER_EMAIL))
                        .storePreference(AUTH_TOKEN, user.get(AUTH_TOKEN))
                        .storePreference(IS_AUTHENTICATED, true);

                ((MainActivity)context).replaceScreen(MainActivity.Screens.MODE_SELECTION, true);
                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
            } else{
                ((MainActivity)getActivity()).destroyUserSession();
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
                Log.e(TAG,e.getMessage());
            }
            return map;
        }
    }

}
