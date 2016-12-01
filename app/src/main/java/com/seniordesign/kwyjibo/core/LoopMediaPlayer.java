package com.seniordesign.kwyjibo.core;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.seniordesign.kwyjibo.database.restapi.RestAPI;
import com.seniordesign.kwyjibo.fragments.screens.StudioModeFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import okhttp3.ResponseBody;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.seniordesign.kwyjibo.core.HasSessionInfo.CURRENT_STATION;
import static com.seniordesign.kwyjibo.core.LoopMediaPlayer.Modes.RADIO;

/**
 * Created by jamie on 11/12/2016.
 */

public class LoopMediaPlayer {
    public static final String TAG = LoopMediaPlayer.class.getSimpleName();
    private Context mContext;

    private StudioModeFragment studioFragment;

    public void setStudioFragmentReference(StudioModeFragment studioFragmentReference) {
        this.studioFragment = studioFragmentReference;
    }

    private enum State{
        IDLE, INITIALIZED, PREPARED, STARTED, STOPPED, PAUSED, PLAYBACK_COMPLETED, PREPARING, END
    }

    public enum Modes{
        RADIO, STUDIO
    }

    private State state;
    private Modes mode;

    private int mCounter = 1;

    private String mSongFilePath1 = null;
    private String mSongFilePath2 = null;
    private String currentFilePath = null;

    private MediaPlayer mCurrentPlayer = null;
    private MediaPlayer mNextPlayer = null;

    public boolean isPlaying(){
        if (mCurrentPlayer != null && mNextPlayer != null){
            return mCurrentPlayer.isPlaying()
                    || mNextPlayer.isPlaying();
        }
        return false;
    }

    public static LoopMediaPlayer create(Context context) {
        return new LoopMediaPlayer(context);
    }

    public void setMode(Modes m){
        mode = m;
    }

    public void setDataSource(String filepath) throws IOException, IllegalStateException{
        if (currentFilePath != null){
            return;
        }

        if (state == State.IDLE){
            currentFilePath = filepath;
            mSongFilePath1 = currentFilePath;
            mSongFilePath2 = filepath.substring(0, filepath.lastIndexOf(".wav")) + "_temp.wav";
            mCurrentPlayer.setDataSource(currentFilePath);
            createNextMediaPlayer();
            state = State.INITIALIZED;
        }else{
            throw new IllegalStateException("Can only call setDataSource() from the Idle state. Current state: " + state.toString());
        }
    }

    public LoopMediaPlayer(Context context) {
        mContext = context;
        state = State.IDLE;
        try{
            mCurrentPlayer = new MediaPlayer();
        } catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    public void start(){
        if (state == State.PREPARED){
            mCurrentPlayer.start();
            state = State.STARTED;
        }else{
            throw new IllegalStateException("The player is not in the prepared state. Current state: " + state.toString());
        }
    }

    private void createNextMediaPlayer() {
        try{
            mNextPlayer = new MediaPlayer();
            mNextPlayer.setDataSource(currentFilePath);
            mNextPlayer.prepare();

            mCurrentPlayer.setOnCompletionListener(onCompletionListener);
            mCurrentPlayer.setNextMediaPlayer(mNextPlayer);
        } catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    public void reset(){
        mCurrentPlayer = new MediaPlayer();
        mNextPlayer = null;
        currentFilePath = null;
        mSongFilePath1 = null;
        mSongFilePath2 = null;
        state = State.IDLE;
    }

    public void seekTo(int msec) throws IllegalStateException{
        if (state == State.PREPARED || state == State.PLAYBACK_COMPLETED){
            mCurrentPlayer.seekTo(msec);
        }else{
            throw new IllegalStateException("The player must be in the prepared state to call seekTo(). Current state: " + state.toString());
        }
    }

    public void prepare() throws IllegalStateException, IOException{
        if (state == State.INITIALIZED || state == State.STOPPED){
            state = State.PREPARED;
            mCurrentPlayer.prepare();
        }else{
            throw new IllegalStateException("The player must be in the initialized or started state first to be prepared. Current State: " + state.toString());
        }
    }

    public void pause() throws IllegalStateException{
        if (state == State.STARTED || state == State.PAUSED){
            mCurrentPlayer.pause();
            state = State.PAUSED;
        }else{
            throw new IllegalStateException("The player must be started first to be paused. Current State: " + state.toString());
        }
    }

    public void prepareAsync(){
        if (state == State.INITIALIZED || state == State.STOPPED){
            mCurrentPlayer.prepareAsync();
            state = State.PREPARING;
        }else{
            throw new IllegalStateException("Must be initialized or stopped to call prepareAysnc(). Current state: " + state.toString());
        }
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener){
        mCurrentPlayer.setOnPreparedListener(listener);
    }

    public void stop() throws IllegalStateException{
        if (state == State.STARTED
                || state == State.PREPARED
                || state == State.PAUSED
                || state == State.PLAYBACK_COMPLETED
                || state == State.STOPPED){
            mCurrentPlayer.stop();
            mNextPlayer.stop();
            state = State.STOPPED;
        }else{
            throw new IllegalStateException("The player must be started first to be paused. Current State: " + state.toString());
        }
    }

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.release();
            mCurrentPlayer = mNextPlayer;

            // After n loops, we want to download the song again and set it to the currently unused mediaplayer
            // This way, any changes to the song on the server will be reflected here seamlessly
            if (mCounter == 2){
                mCounter = 1;
                if (mode == Modes.RADIO){
                    int stationId = ApplicationWrapper.getIntPreference(CURRENT_STATION);
                    RestAPI.getStationSong(stationId, new Callback<ResponseBody>(){
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.body() != null){
                                if (currentFilePath.equals(mSongFilePath1)){
                                    saveStationSong(response.headers(),response.body(), mSongFilePath2);
                                    currentFilePath = mSongFilePath2;
                                }else if (currentFilePath.equals(mSongFilePath2)){
                                    saveStationSong(response.headers(),response.body(), mSongFilePath1);
                                    currentFilePath = mSongFilePath1;
                                }
                                Log.d(TAG, "Station song download successful.");
                            }else{
                                Log.e(TAG, "Station song download failed. getStationSong() response.body() == null.");
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e(TAG, "Station song download failed. No response from server.");
                        }
                    });
                }
                if (mode == Modes.STUDIO){
                   studioFragment.getStudioModeSong();
                }

            }
            mCounter++;

            createNextMediaPlayer();

            Log.d(TAG, String.format("Gapless playback loop #%d.", mCounter));
        }
    };

    private boolean saveStationSong(Headers headers, ResponseBody body, String saveFilename){
        InputStream inputStream = null;
        OutputStream outputStream = null;

        // Save the file
        File stationSong = new File(saveFilename);
        try{
            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                // Create directory if it doesn't exist
                File f = new File(mContext.getExternalFilesDir(null), "station-songs");
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
                   // Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
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



}
