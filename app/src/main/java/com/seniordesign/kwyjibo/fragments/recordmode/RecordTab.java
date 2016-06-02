package com.seniordesign.kwyjibo.fragments.recordmode;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seniordesign.kwyjibo.kwyjibo.R;

import java.io.IOException;

public class RecordTab extends android.support.v4.app.Fragment{
    private ImageView recordButton;
    private MediaRecorder mRecorder;
    private TextView recordingTextView;

    private static final String TAG = "RecordTab";
    private static final String outputFile = "/storage/emulated/0/recording.3gp";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record_mode_record_tab, container, false);
        recordingTextView = (TextView) rootView.findViewById(R.id.record_mode_recording_textview);
        enableButtons(rootView);
        return rootView;
    }

    public void enableButtons(View v){
        enableRecordButton(v);
    }

    public void disableButtons(){
        recordButton.setOnTouchListener(null);
    }

    private void enableRecordButton(View rootView){
        recordButton = (ImageView) rootView.findViewById(R.id.record_button_image_view);
        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        initMediaRecorder();
                        try {
                            mRecorder.prepare();
                            mRecorder.start();
                            recordButton.setBackgroundResource(R.drawable.record_button_pressed);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                            recordButton.setBackgroundResource(R.drawable.record_button_unpressed);
                            mRecorder = null;
                        } finally {
                            recordingTextView.setText("Recording");
                        }
                        return true;
                    case (MotionEvent.ACTION_UP):
                        try {
                            mRecorder.stop();
                            recordButton.setBackgroundResource(R.drawable.record_button_unpressed);
                            ((ViewPager) getActivity().findViewById(R.id.record_mode_view_pager)).setCurrentItem(1);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        } finally {
                            recordingTextView.setText("Stopped recording");
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private void initMediaRecorder(){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncodingBitRate(16);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(outputFile);
    }

}
