package com.seniordesign.kwyjibo.services;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.beans.SoundClipInfo;
import com.seniordesign.kwyjibo.events.PauseObserverService;
import com.seniordesign.kwyjibo.events.SoundClipsDataSetChanged;
import com.seniordesign.kwyjibo.events.StopObserverService;
import com.seniordesign.kwyjibo.events.UnpauseObserverService;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ObserverService extends Service implements HasSessionInfo {

    private static final String TAG = "ObserverService";
    private boolean paused = true; // temporarily pause
    private boolean stopped = false; // exit loop to call stopStop()

    public static SoundClipInfo[] currentStationSoundClips;
    public static String[] stationsList;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EventBus.getDefault().register(this);

        new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    while (!stopped){
                        if(!paused){
                            Log.d(TAG, "onStartCommand()");
                            SoundClipInfo[] clips = getCurrentStationSoundClips();
                            if (dataSetChanged(currentStationSoundClips, clips)){
                                EventBus.getDefault().post(new SoundClipsDataSetChanged(clips));
                            }
                            currentStationSoundClips = clips;
                            Thread.sleep(1000);
                        }
                    }
                }catch(Exception e){
                    Log.e(TAG, e.getMessage());
                }
            }
        }).start();
        return START_NOT_STICKY;
    }

    @Subscribe
    public void onEvent(Object event){
        if (event instanceof PauseObserverService){
            this.paused = true;
        }
        if (event instanceof UnpauseObserverService){
            this.paused = false;
        }
        if (event instanceof StopObserverService){
            this.stopped = true;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private SoundClipInfo[] getCurrentStationSoundClips() {
        String baseURL = "http://motw.tech/api/GetStationSoundClips.aspx";
        Uri builtUri = Uri.parse(baseURL).buildUpon()
                .appendQueryParameter("stationName",
                        MainActivity.getStringPreference(CURRENT_STATION))
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

        } catch (Exception e) {
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

    private boolean dataSetChanged(SoundClipInfo[] clips1, SoundClipInfo[] clips2){
        if (clips1 == null || clips2 == null){
            Log.e(TAG, "A SoundClipInfo[] source passed in to dataSetChanged() was null");
            return false;
        }
        if (clips1.length != clips2.length){
            return false;
        }
        for (int i = 0; i < clips1.length && i < clips2.length; i++){
            if (clips1[i].getId() != clips2[i].getId()){
                return false;
            }
        }
        return true;
    }

}
