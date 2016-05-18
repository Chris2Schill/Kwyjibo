package com.seniordesign.kwyjibo.kwyjibo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecordModeFragment extends Fragment {

    // We are saving a reference to rootView so we can restore the fragment's state
    // after this fragment leaves visibility and onDestroyView() is called.
    private View rootView;
    private ViewPager viewPager;
    private RecordModePagerAdapter pagerAdapter;
    private static final String outputFile = "/storage/emulated/0/recording.3gp";
    private static final int MY_PERMISSIONS_REQUEST_AUDIO_STORAGE = 1;
    private static final String TAG = "RecordModeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null){
            rootView = inflater.inflate(R.layout.record_mode_fragment, container, false);
            viewPager = (ViewPager)rootView.findViewById(R.id.record_mode_view_pager);
            pagerAdapter = new RecordModePagerAdapter(getActivity().getSupportFragmentManager());
            viewPager.setAdapter(pagerAdapter);
        }
        requestRecordAndStoragePermissions();
        return rootView;
    }

    private void requestRecordAndStoragePermissions(){
        List<String> permissionsList = new ArrayList<>();
        if (!havePermission(Manifest.permission.RECORD_AUDIO)){
            permissionsList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!havePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionsList.size() > 0){
            Toast.makeText(getContext(),
                    "This application requires the use of the microphone and device storage "
                    + "to function properly.",
                    Toast.LENGTH_LONG).show();
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    MY_PERMISSIONS_REQUEST_AUDIO_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_AUDIO_STORAGE){
            int numGranted = 0;
            for (int permission = 0; permission < permissions.length; permission++){
                if (grantResults[permission] == PackageManager.PERMISSION_GRANTED){
                    numGranted++;
                }
            }
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && numGranted == permissions.length){
                ((RecordTab)pagerAdapter.getItem(0)).enableButtons(rootView);
                ((ReviewRecordingTab)pagerAdapter.getItem(1)).enableButtons(rootView);
            }else{
                ((RecordTab)pagerAdapter.getItem(0)).disableButtons();
                ((ReviewRecordingTab)pagerAdapter.getItem(1)).disableButtons();
            }
        }
    }

    private boolean havePermission(String permission){
        return ContextCompat.checkSelfPermission(getActivity(), permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    // FragmentPagerAdapter allows horizontal swiping between Fragment views.
    class RecordModePagerAdapter extends FragmentPagerAdapter {
        private RecordTab recordTab;
        private ReviewRecordingTab reviewRecordingTab;

        public RecordModePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            if (position == 0){
                if (recordTab == null){
                    recordTab = new RecordTab();
                }
                return recordTab;
            }else if (position == 1){
                if (reviewRecordingTab == null){
                    reviewRecordingTab = new ReviewRecordingTab();
                }
                return reviewRecordingTab;
            }else{
                return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public static class RecordTab extends android.support.v4.app.Fragment{
        private ImageView recordButton;
        private MediaRecorder mRecorder;
        private TextView recordingTextView;

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
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(outputFile);
        }

    }

    public static class ReviewRecordingTab extends android.support.v4.app.Fragment{
        private Triangle playbackButton;
        private Button uploadSoundButton;
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
}
