package com.seniordesign.kwyjibo.database.services.events;

import com.seniordesign.kwyjibo.database.models.SoundClipInfo;

public class SoundClipsDataSetChanged {
    public SoundClipInfo[] newClips;

    public SoundClipsDataSetChanged(SoundClipInfo[] newClips){
        this.newClips = newClips;
    }
}
