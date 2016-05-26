package com.seniordesign.kwyjibo.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.seniordesign.kwyjibo.fragments.recordmode.RecordTab;
import com.seniordesign.kwyjibo.fragments.recordmode.ReviewRecordingTab;

// FragmentPagerAdapter allows horizontal swiping between Fragment views.
public class RecordModePagerAdapter extends FragmentPagerAdapter {
    private RecordTab recordTab;
    private ReviewRecordingTab reviewRecordingTab;

    public RecordModePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        if (position == 0) {
            if (recordTab == null) {
                recordTab = new RecordTab();
            }
            return recordTab;
        } else if (position == 1) {
            if (reviewRecordingTab == null) {
                reviewRecordingTab = new ReviewRecordingTab();
            }
            return reviewRecordingTab;
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}