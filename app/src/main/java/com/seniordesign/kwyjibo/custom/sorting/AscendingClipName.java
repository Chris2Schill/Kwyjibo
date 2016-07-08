package com.seniordesign.kwyjibo.custom.sorting;

import com.seniordesign.kwyjibo.database.models.SoundClipInfo;

import java.util.Comparator;

public class AscendingClipName implements Comparator<SoundClipInfo> {
    @Override
    public int compare(SoundClipInfo lhs, SoundClipInfo rhs) {
        return lhs.Name.compareTo(rhs.Name);
    }
}
