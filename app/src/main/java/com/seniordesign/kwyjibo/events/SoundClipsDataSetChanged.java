package com.seniordesign.kwyjibo.events;

import com.seniordesign.kwyjibo.beans.SoundClipInfo;

public class SoundClipsDataSetChanged {
    public SoundClipInfo[] newClips;

    public SoundClipsDataSetChanged(SoundClipInfo[] newClips){
        this.newClips = newClips;
    }
}
