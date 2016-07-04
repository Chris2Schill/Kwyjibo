package com.seniordesign.kwyjibo.sorting;

import com.seniordesign.kwyjibo.beans.SoundClipInfo;

import java.util.Comparator;

public class AscendingClipName implements Comparator<SoundClipInfo> {
    @Override
    public int compare(SoundClipInfo lhs, SoundClipInfo rhs) {
        return lhs.Name.compareTo(rhs.Name);
    }
}
