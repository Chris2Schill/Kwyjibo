package com.seniordesign.kwyjibo.fragments.screens;

import android.content.DialogInterface;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.seniordesign.kwyjibo.core.ApplicationWrapper;
import com.seniordesign.kwyjibo.core.LoopMediaPlayer;
import com.seniordesign.kwyjibo.core.Screens;
import com.seniordesign.kwyjibo.database.restapi.RestAPI;
import com.seniordesign.kwyjibo.core.MainActivity;
import com.seniordesign.kwyjibo.custom.adapters.SoundClipInfoAdapter;
import com.seniordesign.kwyjibo.database.models.SoundClipInfo;
import com.seniordesign.kwyjibo.database.services.events.PauseObserverService;
import com.seniordesign.kwyjibo.database.services.events.SoundClipsDataSetChanged;
import com.seniordesign.kwyjibo.database.services.events.UnpauseObserverService;
import com.seniordesign.kwyjibo.core.HasSessionInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StationFragment extends Fragment implements HasSessionInfo{

    private SoundClipInfoAdapter listAdapter;
    private ListView clipListView;

    private Button addSoundButton;
    private ImageView playButton;

    private String songFilename;

    private View.OnClickListener playButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (songFilename != null){
                playSongClip();
                Drawable stopImage = ContextCompat.getDrawable(getContext(), R.drawable.ic_stop_circle_outline);
                playButton.setOnClickListener(stopSongListener);
                playButton.setImageDrawable(stopImage);
            }
        }
    };

    private View.OnClickListener stopSongListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if(songFilename != null){
                MainActivity.getLoopPlayer().stop();
                playButton.setOnClickListener(playButtonListener);
                playButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_circle_outline));
            }
        }
    };

    private static final String TAG = "StationFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.station_fragment, container, false);


        initButtons(rootView);

        // Populate the clips listview
        initCurrentSoundsListView(rootView);

        // Download the song
        downloadStationSong(playButtonListener);

        MainActivity.getLoopPlayer().setMode(LoopMediaPlayer.Modes.RADIO);

        // Start the service that refreshes the list in pseudo real-time
        EventBus.getDefault().post(new UnpauseObserverService());

        // Apply our theme settings
        ApplicationWrapper.applyLayoutDesign(rootView);
        return rootView;
    }

    private void downloadStationSong(final View.OnClickListener playButtonListener){
        RestAPI.getStationSong(MainActivity.getIntPreference(CURRENT_STATION), new Callback<ResponseBody>(){

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.body() != null){
                    saveStationSong(response.headers(), response.body());
                    // We only want the button to work after the song has finished downloading
                    if (MainActivity.getLoopPlayer().isPlaying()){
                        playButton.setOnClickListener(stopSongListener);
                        playButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_stop_circle_outline));

                    }else{
                        playButton.setOnClickListener(playButtonListener);
                        playButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_circle_outline));
                    }
                    playButton.setAlpha(1.0f);
//                    if (playButton != null) {
//                        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(ContextCompat.getColor(getContext(), R.color.blueGray_600),
//                                PorterDuff.Mode.SRC_ATOP);

//                        playButton.setColorFilter(porterDuffColorFilter);
//                    }
                }else{
                    Log.d(TAG, "getStationSong() response body is null");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "getStationSong() api call failed");
            }
        });

    }

    private void playSongClip(){
        try {
            MainActivity.getLoopPlayer().reset();
            MainActivity.getLoopPlayer().setDataSource(getContext().getExternalFilesDir(null) + "/station-songs/" + songFilename);
            MainActivity.getLoopPlayer().prepare();
            MainActivity.getLoopPlayer().start();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().post(new PauseObserverService());
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new UnpauseObserverService());
    }

    // This function is called when an event is posted to the EventBus. It is configured to run
    // on the main thread to allow UI updates.
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Object event){
        if (event instanceof SoundClipsDataSetChanged){
            listAdapter.clear();
            SoundClipInfo[] clips = ((SoundClipsDataSetChanged)event).newClips;
            for (SoundClipInfo clip : clips){
                listAdapter.add(clip);
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initButtons(View rootView){
        rootView.findViewById(R.id.add_sound_to_station_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setTitle("Add Sound to Station")
                        .setMessage("Where is your sound coming from?")
                        .setPositiveButton("File", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("New Recording", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ApplicationWrapper.storePreference("fromMode", 1);

                                MainActivity.replaceScreen(Screens.RECORD_MODE, "RECORD_MODE",
                                        android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
            }
        });

        // Set the play song button to the proper icon and transparency
        playButton = (ImageView) rootView.findViewById(R.id.station_fragment_play_stream_imagebutton);
        if (MainActivity.getLoopPlayer() != null && MainActivity.getLoopPlayer().isPlaying()){
            playButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_stop_circle_outline));
            playButton.setOnClickListener(stopSongListener);
            playButton.setAlpha(1f);
        }else{
            playButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_circle_outline));
            playButton.setOnClickListener(playButtonListener);
            playButton.setAlpha(0.2f);
        }
    }

    private void initCurrentSoundsListView(View rootView){
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/ProximaNova-Semibold.otf");
        listAdapter = new SoundClipInfoAdapter(getActivity(), R.layout.sound_clip_list_item,
                new ArrayList<SoundClipInfo>(), font);

        clipListView = (ListView)rootView.findViewById(R.id.station_fragment_current_sounds_listview);
        clipListView.setAdapter(listAdapter);
        clipListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String clipName = ((TextView) view.findViewById(R.id.soundclip_listitem_soundname_textview)).getText().toString();

                new AlertDialog.Builder(getActivity())
                        .setTitle(clipName)
                        .setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Do Nothing
                            }
                        }).create().show();
                return true;
            }
        });
    }



    private void populateCurrentSoundsList(){
        RestAPI.getStationSoundClips(ApplicationWrapper.getIntPreference(CURRENT_STATION), new Callback<List<SoundClipInfo>>() {
            @Override
            public void onResponse(Call<List<SoundClipInfo>> call, Response<List<SoundClipInfo>> response) {
                listAdapter.clear();
                for (SoundClipInfo clip : response.body()) {
                    Log.d(TAG, clip.toString());
                    listAdapter.add(clip);
                }
            }

            @Override
            public void onFailure(Call<List<SoundClipInfo>> call, Throwable t) {
                Toast.makeText(getContext(), "Sound clips failed to load.", Toast.LENGTH_LONG).show();
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private boolean saveStationSong(Headers headers, ResponseBody body){
        InputStream inputStream = null;
        OutputStream outputStream = null;
        songFilename = getSongFilename(headers);

        // Save the file
        File stationSong = new File(getContext().getExternalFilesDir(null) + "/station-songs/" + songFilename);
        try{
            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                // Create directory if it doesn't exist
                File f = new File(getContext().getExternalFilesDir(null), "station-songs");
                if (!f.exists()) {
                    f.mkdirs();
                }

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(stationSong);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                   // Log.d("API", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        }catch(IOException ex){
            Log.e(TAG, ex.getMessage());
            return false;
        }
        return true;
    }

    private String getSongFilename(Headers headers){
        String songFilenameHeader = headers.get("Content-Disposition");
        String songFilename = songFilenameHeader.substring(songFilenameHeader.indexOf("\"")+1, songFilenameHeader.lastIndexOf("\""));
        return songFilename;
    }



}

