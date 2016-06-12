package com.seniordesign.kwyjibo.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.seniordesign.kwyjibo.restapi.RestAPI;
import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.beans.SoundClipInfo;
import com.seniordesign.kwyjibo.events.PauseObserverService;
import com.seniordesign.kwyjibo.events.SoundClipsDataSetChanged;
import com.seniordesign.kwyjibo.events.StopObserverService;
import com.seniordesign.kwyjibo.events.UnpauseObserverService;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * This service is responsible for providing real-time data retrieval from the server.
 */
public class ObserverService extends Service implements HasSessionInfo {

    private static final String TAG = "ObserverService";
    private boolean paused = true; // temporarily pause
    private boolean stopped = false; // exit loop to call stopStop()

    public static SoundClipInfo[] currentStationSoundClips = new SoundClipInfo[0];
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
                            RestAPI.getStationSoundClips(MainActivity.getStringPreference(CURRENT_STATION),
                                    new Callback<List<SoundClipInfo>>() {
                                        @Override
                                        public void onResponse(Call<List<SoundClipInfo>> call, Response<List<SoundClipInfo>> response) {
                                            SoundClipInfo[] clips = response.body().toArray(new SoundClipInfo[response.body().size()]);
                                            if (dataSetChanged(currentStationSoundClips, clips)) {
                                                EventBus.getDefault().post(new SoundClipsDataSetChanged(clips));
                                                currentStationSoundClips = clips;
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<List<SoundClipInfo>> call, Throwable t) {

                                        }
                                    });
                        }
                        Thread.sleep(1000);
                    }
                }catch(Exception e){
                    Log.e(TAG, e.getMessage());
                }
            }
        }).start();
        return START_NOT_STICKY;
    }

    // This function is called when EventBus.post() is called.
    // It is wrong to call this function yourself.
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

    // This function is required when extending Service.  We are using EventBus instead of
    // an IBinder to provide communication to/from this service so we simply return null.
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean dataSetChanged(SoundClipInfo[] clips1, SoundClipInfo[] clips2){
        if (clips1 == null || clips2 == null){
            Log.e(TAG, "A SoundClipInfo[] source passed in to dataSetChanged() was null");
            return false;
        }
        if (clips1.length != clips2.length){
            return true;
        }
        for (int i = 0; i < clips1.length && i < clips2.length; i++){
            if (clips1[i].Id != clips2[i].Id){
                return false;
            }
        }
        return true;
    }

}
