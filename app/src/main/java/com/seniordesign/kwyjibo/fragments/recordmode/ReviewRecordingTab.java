package com.seniordesign.kwyjibo.fragments.recordmode;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.seniordesign.kwyjibo.restapi.RestAPI;
import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.beans.SoundClipInfo;
import com.seniordesign.kwyjibo.drawables.Triangle;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewRecordingTab extends Fragment implements HasSessionInfo{
    private Triangle playbackButton;
    private Button uploadSoundButton;
    private Spinner spinner;
    private ArrayAdapter<String> spinnerAdapter;
    private EditText clipNameEditText;
    private static final String TAG = "ReviewRecordingTab";
    private static final String outputFile = "/storage/emulated/0/recording.3gp";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record_mode_review_tab, container, false);

        enableButtons(rootView);
        updateSpinner(rootView);


        clipNameEditText = (EditText)rootView.findViewById(R.id.review_recording_soundclip_name_edittext);

        MainActivity.applyLayoutDesign(rootView);
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
        playbackButton = (Triangle) v.findViewById(R.id.playback_button);
        playbackButton.setOnClickListener(new Triangle.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(outputFile);
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
                String clipName = clipNameEditText.getText().toString();
                String category = (spinner.getSelectedItemPosition() + 1) + "";

                SoundClipInfo newClip = new SoundClipInfo();
                newClip.Name = clipNameEditText.getText().toString();
                newClip.CreatedBy = MainActivity.getStringPreference(USER_NAME);
                newClip.Location = "NO LOCATION SERVICE YET";
                newClip.Category = (spinner.getSelectedItemPosition() + 1) + "";


                RestAPI.uploadSoundClip(MainActivity.getStringPreference(CURRENT_STATION),
                        newClip, MainActivity.getStringPreference(USER_ID),
                        MainActivity.getStringPreference(AUTH_TOKEN),
                        new Callback<SoundClipInfo>() {
                            @Override
                            public void onResponse(Call<SoundClipInfo> call, Response<SoundClipInfo> response) {
                                Log.d(TAG, response.body().toString());
                                getActivity().getSupportFragmentManager().popBackStack();
                                MainActivity.replaceScreen(MainActivity.Screens.CURRENT_STATION, false);
                            }

                            @Override
                            public void onFailure(Call<SoundClipInfo> call, Throwable t) {
                                Log.e(TAG, t.getMessage());
                            }
                        }
                );
            }
        });
    }

    protected static void longInfo(String str) {
        if(str.length() > 4000) {
            Log.d(TAG, str.substring(0, 4000));
            longInfo(str.substring(4000));
        } else
            Log.i(TAG, str);
    }

    private void updateSpinner(View rootView){
        spinner = (Spinner)rootView.findViewById(R.id.review_recording_category_spinner);
        spinnerAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.station_selection_list_item, new ArrayList<String>());
        spinner.setAdapter(spinnerAdapter);

        RestAPI.getCategories(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                spinnerAdapter.clear();
                for (String category : response.body()) {
                    spinnerAdapter.add(category);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }
}
