package com.seniordesign.kwyjibo.custom.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.seniordesign.kwyjibo.custom.sorting.AscendingClipName;
import com.seniordesign.kwyjibo.kwyjibo.R;
import com.seniordesign.kwyjibo.database.models.SoundClipInfo;
import com.seniordesign.kwyjibo.database.restapi.RestAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SoundClipInfoAdapter extends ArrayAdapter<SoundClipInfo> {

    private List<SoundClipInfo> soundClipInfoList;
    private Typeface font = null;
    private String baseFilepathForClips;
    private static final String TAG = "SoundClipInfoAdapter";


    View.OnClickListener downloadClipListener;
    View.OnClickListener playSoundClipListener;



    public void sort(Comparator compare) {
        List<SoundClipInfo> clips = new ArrayList<SoundClipInfo>();
        for(int i = 0; i < soundClipInfoList.size(); i++){
            clips.add(soundClipInfoList.get(i));
        }
        Collections.sort(clips, new AscendingClipName());
        soundClipInfoList = clips;
        notifyDataSetChanged();
    }

    public SoundClipInfoAdapter(Context context, int resource, List<SoundClipInfo> clips) {
        super(context, resource, clips);
        this.soundClipInfoList = clips;
        baseFilepathForClips = context.getExternalFilesDir(null).toString();
    }

    public SoundClipInfoAdapter(Context context, int resource, List<SoundClipInfo> clips, Typeface font){
        super(context, resource, clips);
        this.soundClipInfoList = clips;
        this.font = font;
        baseFilepathForClips = context.getExternalFilesDir(null).toString();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // We are caching the views in the list view. A common pattern for android list views to improve performance.
        final ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sound_clip_list_item,parent,false);
            // cache views into view holder
            holder = new ViewHolder(convertView, downloadClipListener, playSoundClipListener);
            holder.soundName = (TextView) convertView.findViewById(R.id.soundclip_listitem_soundname_textview);
            holder.contributor = (TextView) convertView.findViewById(R.id.soundclip_listitem_contributorsname_textview);
            holder.location = (TextView) convertView.findViewById(R.id.soundclip_listitem_location_textview);
            holder.icon = (ImageView) convertView.findViewById(R.id.soundclip_listitem_playsoundclip_button);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        // We get the sound clip at this position and get a filepath for where it will be stored on the device.
        final SoundClipInfo clip = soundClipInfoList.get(position);
        final String clipFilepath = getLocalFilepathFor(clip);

        if (clip != null) {
            holder.soundName.setTypeface(font);
            holder.contributor.setTypeface(font);
            holder.location.setTypeface(font);
            holder.soundName.setText(clip.Name);
            holder.contributor.setText(clip.CreatedByName);
            holder.location.setText(clip.Location);
        }

        // Define two click listeners for the same button. We will use one for downloading
        // when the file doesn't exist, and one for playing the clip when it does.
        downloadClipListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Download started. Wait for the icon to change.",
                        Toast.LENGTH_LONG).show();
                RestAPI.getSoundClip(clip.Id, new Callback<ResponseBody>(){
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.body() != null){
                            String filepath = getLocalFilepathFor(clip);
                            writeSoundClipToCategoryDir(filepath, clip.Category, response.body());
                            if (holder.icon != null){
                                holder.icon.setOnClickListener(playSoundClipListener);
                                holder.icon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_play));
                                Log.d(TAG, "Successful Download");
                            }
                        }else{
                            Log.e(TAG, "Download Sound Clip responseBody() null.");
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "Sound clip download failed");
                    }
                });
            }
        };

        playSoundClipListener =  new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                playSoundClip(clip);
            }
        };

        String filepath = getLocalFilepathFor(clip);
        if (!new File(filepath).exists()){
            holder.icon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_download));
            holder.icon.setOnClickListener(downloadClipListener);
        }else{
            holder.icon.setOnClickListener(playSoundClipListener);
            holder.icon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_play));
        }
        return convertView;
    }

    private String getLocalFilepathFor(SoundClipInfo clip){
//        String categoryName = "";
//        switch(clip.Category){
//            case "1":
//                categoryName = "percussive"; break;
//            case "2":
//                categoryName = "drone"; break;
//            case "3":
//                categoryName = "ambient"; break;
//            case "4":
//                categoryName = "melodic"; break;
//            case "5":
//                categoryName = "other"; break;
//            default:
//                categoryName = "other";
//        }

        String fileExtension = clip.Filepath.substring(clip.Filepath.lastIndexOf('.'));

        return getContext().getExternalFilesDir(null) + "/" + clip.Category + "/" + clip.Name.replace(" ","_") +
                "_" + clip.CreatedById + fileExtension;
    }


    private void playSoundClip(SoundClipInfo clip){
        MediaPlayer mPlayer = new MediaPlayer();
        try {
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            String filepath = getLocalFilepathFor(clip);
            mPlayer.setDataSource(filepath);
            mPlayer.prepare();
        } catch (IOException e) {
            Log.e("SoundClipAdapter", e.getMessage());
        }
        mPlayer.start();
    }

    private boolean writeSoundClipToCategoryDir(String filepath, String category, ResponseBody body) {
        try {
            Log.d("RestAPI", filepath);

            // Make the file object for that file
            File soundClip = new File(filepath);

            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                // Make sure the directory for the sound clip exists and create it if not
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

    @Override
    public int getCount() {
        return soundClipInfoList.size();
    }

    @Override
    public SoundClipInfo getItem(int position) {
        return soundClipInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return soundClipInfoList.hashCode();
    }

    static class ViewHolder{
        public TextView soundName;
        public TextView location;
        public TextView contributor;
        public ImageView icon;

//        interface IOnClick{
//            void onItemClick(View view, int position);
//        }
//
//        public IOnClick listener;

        public ViewHolder(View itemView, View.OnClickListener downloadListener, View.OnClickListener playListener) {
            soundName = (TextView)itemView.findViewById(R.id.soundclip_listitem_soundname_textview);
            location = (TextView)itemView.findViewById(R.id.soundclip_listitem_location_textview);
            contributor = (TextView)itemView.findViewById(R.id.soundclip_listitem_contributorsname_textview);
            icon = (ImageView)itemView.findViewById(R.id.soundclip_listitem_playsoundclip_button);
        }

//        @Override
//        public void onClick(View v) {
//            listener.onItemClick(v, getLayoutPosition());
//        }

    }

    private String getSoundClipFilepath(Headers headers, String category){
        String songFilenameHeader = headers.get("Content-Disposition");
        String songFilename = songFilenameHeader.substring(songFilenameHeader.indexOf("\"")+2, songFilenameHeader.lastIndexOf("\""));
        return getContext().getExternalFilesDir(null) + File.separator + category + File.separator + songFilename;
    }


}
