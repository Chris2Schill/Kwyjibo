package com.seniordesign.kwyjibo.custom.sorting;

import android.support.annotation.NonNull;

import com.seniordesign.kwyjibo.database.models.RadioStation;

import java.util.Comparator;

public class DescendingNumClips implements Comparator<RadioStation> {
    @Override
    public int compare(@NonNull RadioStation lhs,@NonNull RadioStation rhs) {
        if (lhs.NumCurrentClips < rhs.NumCurrentClips){
            return 1;
        }else if (lhs.NumCurrentClips > rhs.NumCurrentClips){
            return -1;
        }else{
            return 0;
        }
    }
}
