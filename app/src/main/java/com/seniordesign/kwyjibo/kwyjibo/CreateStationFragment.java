package com.seniordesign.kwyjibo.kwyjibo;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class CreateStationFragment extends Fragment {

    private Button addStationButton;
    private EditText stationNameEditText;
    private EditText userNameEditText;

    private static final String TAG = "CreateStationFragment";

    List<String> genres;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_station_fragment, container, false);

        genres = new ArrayList<>();
        genres.add("EDM");
        genres.add("Dubstep");
        genres.add("Jazz");

        addStationButton = (Button)rootView.findViewById(R.id.create_station_confirm_button);
        addStationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String newStationName = stationNameEditText.getText().toString();
                String createdBy = userNameEditText.getText().toString();

                RadioGroup radioGroup = (RadioGroup)getActivity().findViewById(R.id.create_station_radio_group);
                if (radioGroup != null){
                    int index = radioGroup.indexOfChild(
                            getActivity().findViewById(radioGroup.getCheckedRadioButtonId()));
                    String genre = genres.get(index);
                    Log.e(TAG,genre);
                    new AddStationAsyncTask(getContext()).execute(newStationName, createdBy, genre);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_activity_fragment_container, MainActivity.getFragment(MainActivity.Screens.RADIO_MODE))
                            .commit();

                }else{
                    Log.e(TAG, "Reference to RadioGroup create_station_radio_group is null");
                }
            }
        });

        stationNameEditText = (EditText)rootView.findViewById(R.id.create_station_name_edittext);
        userNameEditText = (EditText)rootView.findViewById(R.id.create_station_yourname_edittext);

        initLayoutDesign(rootView);

        return rootView;
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
            if (child instanceof RadioGroup){
                RadioGroup rg = (RadioGroup)child;
                for (int j = 0; j < rg.getChildCount(); j++){
                    RadioButton b = (RadioButton)rg.getChildAt(j);
                    b.setTypeface(font);
                }
            }

        }
    }

    private void setEditTextBackground() {
        userNameEditText.setBackgroundColor(getResources().getColor(R.color.darkGray));
    }

    private static class AddStationAsyncTask extends AsyncTask<String, Void, String> {

        private Context context;

        AddStationAsyncTask(Context c){
            context = c;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(context, "Station Added", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {

            // The new station's parameters
            Map<String,String> postDataMap = new LinkedHashMap<>();
            postDataMap.put("stationName", params[0]);
            postDataMap.put("createdBy", params[1]);
            postDataMap.put("genre", params[2]);

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

            String jsonResponse = sendPostRequest(postDataBytes);

            try{
                return parseJSONResponse(jsonResponse);
            }catch(JSONException e){
                Log.e(TAG, e.getMessage());
            }
            return null;

        }

        private String sendPostRequest(byte[] postDataBytes){
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String response = "";
            try{
                // Open Connection
                Uri builtUri = Uri.parse("http://motw.tech/api/AddStation.aspx").buildUpon().build();
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

        private String parseJSONResponse(String jsonResponse) throws JSONException{
            return null;
        }
    }
}
