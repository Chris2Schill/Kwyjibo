package com.seniordesign.kwyjibo.fragments.screens;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.seniordesign.kwyjibo.core.ApplicationWrapper;
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
import okhttp3.internal.framed.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StationFragment extends Fragment implements HasSessionInfo{

    private SoundClipInfoAdapter listAdapter;
    private ListView clipListView;

    private Button addSoundButton;

    private static final String TAG = "StationFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.station_fragment, container, false);

        initButtons(rootView);
        initCurrentSoundsListView(rootView);



        EventBus.getDefault().post(new UnpauseObserverService());
        MainActivity.applyLayoutDesign(rootView);
        return rootView;
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
                                ApplicationWrapper.storePreference("fromStation", true);
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

        rootView.findViewById(R.id.station_fragment_play_stream_imagebutton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String s = MainActivity.getStringPreference(CURRENT_STATION);
                RestAPI.getStationSong(MainActivity.getStringPreference(CURRENT_STATION), new Callback<ResponseBody>(){

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (response.body() != null){
                            MediaPlayer mPlayer = new MediaPlayer();
                            String songFilepath = getContext().getExternalFilesDir(null) + "/station-songs/" + getSongFilename(response.headers());
                            saveStationSong(response.headers(), response.body());
                            try {
                                mPlayer.setDataSource(songFilepath);
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }
                            try {
                                mPlayer.prepare();
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }
                            mPlayer.start();
                            //mPlayer.release();
                            Toast.makeText(getContext(), "Play button clicked.", Toast.LENGTH_LONG);
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
        });
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
//                SQLiteHelper db = ApplicationWrapper.getDBManager(getActivity());
//                SoundClipInfo station = null;
//                try {
//                    QueryBuilder<RadioStation, Integer> queryBuilder = db.getStationDao().queryBuilder();
//                    queryBuilder.where().eq(RadioStation.NAME, clipName);
//                    station = queryBuilder.query().get(0);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//                if (station == null){
//                    return true;
//                }

                new AlertDialog.Builder(getActivity())
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
        RestAPI.getStationSoundClips(CURRENT_STATION, new Callback<List<SoundClipInfo>>() {
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
        String songFilename = getSongFilename(headers);

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
                    Log.d("API", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
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
    }

    private String getSongFilename(Headers headers){
        String songFilenameHeader = headers.get("Content-Disposition");
        String songFilename = songFilenameHeader.substring(songFilenameHeader.indexOf("\"")+1, songFilenameHeader.lastIndexOf("\""));
        return songFilename;
    }
}

