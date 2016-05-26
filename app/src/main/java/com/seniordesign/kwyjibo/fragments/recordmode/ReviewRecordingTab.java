package com.seniordesign.kwyjibo.fragments.recordmode;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.seniordesign.kwyjibo.drawables.Triangle;
import com.seniordesign.kwyjibo.kwyjibo.R;

import java.io.IOException;

public class ReviewRecordingTab extends Fragment {
    private Triangle playbackButton;
    private Button uploadSoundButton;
    private static final String TAG = "ReviewRecordingTab";
    private static final String outputFile = "/storage/emulated/0/recording.3gp";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record_mode_review_tab, container, false);
        enableButtons(rootView);
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
        uploadSoundButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "This button does not work yet!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
