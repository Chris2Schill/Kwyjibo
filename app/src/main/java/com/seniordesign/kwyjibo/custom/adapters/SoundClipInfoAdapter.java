package com.seniordesign.kwyjibo.custom.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seniordesign.kwyjibo.kwyjibo.R;
import com.seniordesign.kwyjibo.database.models.SoundClipInfo;
import com.seniordesign.kwyjibo.database.restapi.RestAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SoundClipInfoAdapter extends ArrayAdapter<SoundClipInfo> {

    private List<SoundClipInfo> soundClipInfoList;
    private Typeface font = null;

    public SoundClipInfoAdapter(Context context, int resource, List<SoundClipInfo> clips) {
        super(context, resource, clips);
        this.soundClipInfoList = clips;
    }

    public SoundClipInfoAdapter(Context context, int resource, List<SoundClipInfo> clips, Typeface font){
        super(context, resource, clips);
        this.soundClipInfoList = clips;
        this.font = font;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.sound_clip_list_item,parent,false);
        }

        final SoundClipInfo clip = soundClipInfoList.get(position);
        if (clip != null){
            TextView soundName = (TextView) convertView
                    .findViewById(R.id.soundclip_listitem_soundname_textview);
            TextView contributor = (TextView) convertView
                    .findViewById(R.id.soundclip_listitem_contributorsname_textview);
            TextView location = (TextView) convertView
                    .findViewById(R.id.soundclip_listitem_location_textview);
            if (soundName != null){
                if (font != null){
                    soundName.setTypeface(font);
                }
                soundName.setText(clip.Name);
            }
            if (contributor != null){
                if (font != null){
                    contributor.setTypeface(font);
                }
                contributor.setText(clip.CreatedBy);
            }
            if (location != null){
                if (font != null){
                    location.setTypeface(font);
                }
                location.setText(clip.Location);
            }

            convertView.findViewById(R.id.soundclip_listitem_playsoundclip_button)
                    .setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
//                        Toast.makeText(getContext(), "Play button not implemented yet!", Toast.LENGTH_LONG).show();
                            RestAPI.getSoundClip(clip.Name, new Callback<ResponseBody>(){
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.body() != null){
                                        Log.d("SoundClipAdapter", "Successful Download");
                                        writeSoundClipToCategoryDir(clip.Category, clip.Name.replace(" ","_"), response.body());
                                        MediaPlayer mPlayer = new MediaPlayer();
                                        try {
                                            mPlayer.setDataSource(getContext().getExternalFilesDir(null) + File.separator + clip.Category +
                                                    File.separator + clip.Name.replace(" ","_") + ".wav");
                                        } catch (IOException e) {
                                            Log.e("SoundClipAdapter", e.getMessage());
                                        }
                                        try {
                                            mPlayer.prepare();
                                        } catch (IOException e) {
                                            Log.e("SoundClipAdapter", e.getMessage());
                                        }
                                        mPlayer.start();
                                        mPlayer.release();
                                    }
                                }
                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });
                        }
                    });
        }

        return convertView;
    }

    private boolean writeSoundClipToCategoryDir(String category, String filename, ResponseBody body) {
        try {
            String localFilepath = getContext().getExternalFilesDir(null) + File.separator + category +
                    File.separator + filename + ".wav";
            File soundClip = new File(localFilepath);

            Log.d("RestAPI", localFilepath);

            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                File f = new File(getContext().getExternalFilesDir(null), category);
                if (!f.exists()) {
                    f.mkdirs();
                }

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(soundClip);
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
        } catch (IOException e) {
            return false;
        }
    }
}
