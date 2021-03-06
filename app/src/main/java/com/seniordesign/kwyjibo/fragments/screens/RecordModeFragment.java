package com.seniordesign.kwyjibo.fragments.screens;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.seniordesign.kwyjibo.core.ApplicationWrapper;
import com.seniordesign.kwyjibo.custom.adapters.RecordModePagerAdapter;
import com.seniordesign.kwyjibo.fragments.recordmode.RecordTab;
import com.seniordesign.kwyjibo.fragments.recordmode.ReviewRecordingTab;
import com.seniordesign.kwyjibo.kwyjibo.R;

import java.util.ArrayList;
import java.util.List;

public class RecordModeFragment extends Fragment {

    // We are saving a reference to rootView so we can restore the fragment's state
    // after this fragment leaves visibility and onDestroyView() is called.
    private View rootView;
    private ViewPager viewPager;
    private RecordModePagerAdapter pagerAdapter;
    private static final int MY_PERMISSIONS_REQUEST_AUDIO_STORAGE = 1;
    private static final String TAG = "RecordModeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.record_mode_fragment, container, false);
            viewPager = (ViewPager) rootView.findViewById(R.id.record_mode_view_pager);
            pagerAdapter = new RecordModePagerAdapter(getActivity().getSupportFragmentManager());
            viewPager.setAdapter(pagerAdapter);
        }

        requestRecordAndStoragePermissions();
        return rootView;
    }

    private void requestRecordAndStoragePermissions() {
        List<String> permissionsList = new ArrayList<>();
        if (!ApplicationWrapper.haveDevicePermission(Manifest.permission.RECORD_AUDIO)) {
            permissionsList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!ApplicationWrapper.haveDevicePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!ApplicationWrapper.haveDevicePermission(Manifest.permission.ACCESS_COARSE_LOCATION)){
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (permissionsList.size() > 0) {
            Toast.makeText(getContext(), "This application requires the use of the microphone and " +
                            "device storage to function properly.", Toast.LENGTH_LONG).show();
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    MY_PERMISSIONS_REQUEST_AUDIO_STORAGE);
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_AUDIO_STORAGE) {
            int numGranted = 0;
            for (int permission = 0; permission < permissions.length; permission++) {
                if (grantResults[permission] == PackageManager.PERMISSION_GRANTED) {
                    numGranted++;
                }
            }
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && numGranted == permissions.length) {
                ((RecordTab) pagerAdapter.getItem(0)).enableButtons(rootView);
                ((ReviewRecordingTab) pagerAdapter.getItem(1)).enableButtons(rootView);
            } else {
                ((RecordTab) pagerAdapter.getItem(0)).disableButtons();
                ((ReviewRecordingTab) pagerAdapter.getItem(1)).disableButtons();
            }
        }
    }





}
