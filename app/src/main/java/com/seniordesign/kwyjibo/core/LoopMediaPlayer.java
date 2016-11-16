package com.seniordesign.kwyjibo.core;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by jamie on 11/12/2016.
 */

public class LoopMediaPlayer {
    public static final String TAG = LoopMediaPlayer.class.getSimpleName();

    private int mCounter = 1;
    private String mSongFilePath = null;
    private MediaPlayer mCurrentPlayer = null;
    private MediaPlayer mNextPlayer = null;

    public static LoopMediaPlayer create(String songFilePath) {
        return new LoopMediaPlayer(songFilePath);
    }

    private LoopMediaPlayer(String songFilePath) {
        mSongFilePath = songFilePath;

        try{
            mCurrentPlayer = new MediaPlayer();
            mCurrentPlayer.setDataSource(mSongFilePath);
            mCurrentPlayer.prepare();
            mCurrentPlayer.start();
            /*mCurrentPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mCurrentPlayer.start();
                }
            });*/
        } catch(Exception e){
            Log.e(TAG, e.getMessage());
        }



        createNextMediaPlayer();
    }

    private void createNextMediaPlayer() {
        try{
            mNextPlayer = new MediaPlayer();
            mNextPlayer.setDataSource(mSongFilePath);

            mNextPlayer.prepare();
            mCurrentPlayer.setNextMediaPlayer(mNextPlayer);
            mCurrentPlayer.setOnCompletionListener(onCompletionListener);

        } catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    public void stop(){
        mCurrentPlayer.stop();
        mNextPlayer.stop();

    }


    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.release();
            mCurrentPlayer = mNextPlayer;

            createNextMediaPlayer();

            Log.d(TAG, String.format("Loop #%d", ++mCounter));
        }
    };
}
