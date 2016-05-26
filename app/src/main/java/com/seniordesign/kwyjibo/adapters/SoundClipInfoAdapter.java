package com.seniordesign.kwyjibo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seniordesign.kwyjibo.kwyjibo.R;
import com.seniordesign.kwyjibo.beans.SoundClipInfo;

import java.util.List;

public class SoundClipInfoAdapter extends ArrayAdapter<SoundClipInfo> {

    private List<SoundClipInfo> soundClipInfoList;

    SoundClipInfoAdapter(Context context, int resource, List<SoundClipInfo> clips) {
        super(context, resource, clips);
        this.soundClipInfoList = clips;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.sound_clip_list_item,parent,false);
        }

        SoundClipInfo clip = soundClipInfoList.get(position);
        if (clip != null){
            TextView soundName = (TextView) convertView
                    .findViewById(R.id.studio_mode_list_item_soundname_textview);
            TextView contributor = (TextView) convertView
                    .findViewById(R.id.studio_mode_list_item_contributors_name_textview);
            TextView location = (TextView) convertView
                    .findViewById(R.id.studio_mode_list_item_location_textview);
            if (soundName != null){
                soundName.setText(clip.getSoundClipName());
            }
            if (contributor != null){
                contributor.setText(clip.getContributor());
            }
            if (location != null){
                location.setText(clip.getLocation());
            }
        }
        return convertView;
    }
}
