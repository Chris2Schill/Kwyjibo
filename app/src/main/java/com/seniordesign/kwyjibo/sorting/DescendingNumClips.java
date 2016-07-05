package com.seniordesign.kwyjibo.sorting;

import android.support.annotation.NonNull;

import com.seniordesign.kwyjibo.beans.RadioStation;

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
