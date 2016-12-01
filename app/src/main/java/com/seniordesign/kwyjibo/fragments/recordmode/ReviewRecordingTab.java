package com.seniordesign.kwyjibo.fragments.recordmode;

import android.media.MediaPlayer;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.table.TableUtils;
import com.seniordesign.kwyjibo.core.ApplicationWrapper;
import com.seniordesign.kwyjibo.core.MainActivity;
import com.seniordesign.kwyjibo.core.Screens;
import com.seniordesign.kwyjibo.database.SQLiteHelper;
import com.seniordesign.kwyjibo.database.models.RadioStation;
import com.seniordesign.kwyjibo.database.restapi.RestAPI;
import com.seniordesign.kwyjibo.database.models.SoundClipInfo;
import com.seniordesign.kwyjibo.core.HasSessionInfo;
import com.seniordesign.kwyjibo.fragments.screens.StationSelectionFragment;
import com.seniordesign.kwyjibo.kwyjibo.R;
import com.seniordesign.kwyjibo.custom.validation.ValidatableEditText;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewRecordingTab extends Fragment implements HasSessionInfo{
    private ImageView playbackButton;
    private Button uploadSoundButton;
    private Spinner spinner;
    private ArrayAdapter<String> spinnerAdapter;
    private ValidatableEditText clipNameEditText;
    private String tempOutputFile;
    private static final String TAG = "ReviewRecordingTab";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record_mode_review_tab, container, false);

        enableButtons(rootView);
        updateSpinner(rootView);

        clipNameEditText = (ValidatableEditText)rootView.findViewById(R.id.review_recording_soundclip_name_edittext);
//        tempOutputFile = getActivity().getFilesDir().toString() + "/recording.3gp";
        tempOutputFile = Environment.getExternalStorageDirectory().toString() + "/recording.wav";

        ApplicationWrapper.applyLayoutDesign(rootView);
        return rootView;
    }

    public void enableButtons(View v){
        enablePlaybackButton(v);
        enableUploadSoundButton(v);
    }

    public void disableButtons(){
        playbackButton.setOnClickListener(null);
        uploadSoundButton.setOnClickListener(null);
    }

    private void enablePlaybackButton(View v){
        playbackButton = (ImageView) v.findViewById(R.id.playback_button);
        playbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mPlayer = new MediaPlayer();
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });
                try {
                    mPlayer.setDataSource(tempOutputFile);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
                try {
                    mPlayer.prepare();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
                mPlayer.start();
            }
        });
    }

    private void enableUploadSoundButton(View v){
        uploadSoundButton = (Button) v.findViewById(R.id.upload_sound_to_server_button);
        uploadSoundButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipNameEditText.validate();
                if (clipNameEditText.getError() != null){
                    Toast.makeText(getActivity(), "Disabled until form complete.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                SoundClipInfo clipInfo = gatherSoundClipInfo();
                int stationId = ApplicationWrapper.getIntPreference(CURRENT_STATION);
                String userId = ApplicationWrapper.getIntPreference(USER_ID) + "";
                String authToken = ApplicationWrapper.getStringPreference(AUTH_TOKEN);
                Log.e(TAG, tempOutputFile);


                //check if there is a current station in use
                if(stationId == 0){
                    //prompt user with station list
                    try {//try to connect to database
                        SQLiteHelper dbManager = ApplicationWrapper.getDBManager(getActivity());
                        List<RadioStation> stations = dbManager.getStationDao().queryForAll();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                RestAPI.uploadSoundClip(tempOutputFile, clipInfo, stationId, userId, authToken,
                        new Callback<SoundClipInfo>() {
                            @Override
                            public void onResponse(Call<SoundClipInfo> call, Response<SoundClipInfo> response) {
                                if (response.body() != null) {
                                    Log.d(TAG, "" + response.body().toString());
                                    deleteFile(tempOutputFile);
                                    getActivity().getSupportFragmentManager().popBackStack();
                                    MainActivity.replaceScreen(Screens.RADIO_STATION, "RADIO_STATION",
                                            android.R.anim.fade_in, android.R.anim.fade_out);
                                } else{
                                        Log.e(TAG, "Http status:" + response.code());
                                }
                            }


                            @Override
                            public void onFailure(Call<SoundClipInfo> call, Throwable t) {
                                Log.e(TAG, t.getMessage());
                            }
                        });
            }
        });
    }

    private SoundClipInfo gatherSoundClipInfo(){
        SoundClipInfo clip = new SoundClipInfo();
        clip.Name = clipNameEditText.getText().toString();
        clip.CreatedById = ApplicationWrapper.getIntPreference(USER_ID);
        clip.Location = MainActivity.getLocation();
        clip.Category = (spinner.getSelectedItemPosition() + 1) + "";
        return clip;
    }



    private boolean deleteFile(String filePath){
        String dirPath = getActivity().getFilesDir().toString();
        return new File(dirPath+filePath).delete();
    }


    private void updateSpinner(View rootView){
        spinner = (Spinner)rootView.findViewById(R.id.review_recording_category_spinner);
        spinnerAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>());
        spinner.setAdapter(spinnerAdapter);

        RestAPI.getCategories(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.body() != null){
                    spinnerAdapter.clear();
                    for (String category : response.body()) {
                        spinnerAdapter.add(category);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }
}
