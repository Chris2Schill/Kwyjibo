package com.seniordesign.kwyjibo.fragments.radiomode;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.adapters.SoundClipInfoAdapter;
import com.seniordesign.kwyjibo.asynctasks.GetStationSoundClips;
import com.seniordesign.kwyjibo.beans.SoundClipInfo;
import com.seniordesign.kwyjibo.interfaces.ListViewHandler;
import com.seniordesign.kwyjibo.kwyjibo.R;

import java.util.ArrayList;

public class StationFragment extends Fragment{

    private SoundClipInfoAdapter listAdapter;
    private ListView clipListView;

    private Button addSoundButton;

    private static final String TAG = "StationFragment";
    private String stationName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.station_fragment, container, false);

        Bundle bundle = this.getArguments();
        stationName = bundle.getString("Name", "");

        initButtons(rootView);

        initCurrentSoundsListView(rootView);

        populateCurrentSoundsList();
        MainActivity.applyLayoutDesign(rootView);

        return rootView;
    }

    private void initButtons(View rootView){
        addSoundButton = (Button)rootView.findViewById(R.id.add_sound_to_station_button);
        addSoundButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setTitle("Add Sound to Station")
                        .setMessage("Where is your sound coming from?")
                        .setPositiveButton("File", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("New Recording", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                startActivity(new Intent(getActivity(), RecordActivity.class));
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();

                /*new AddSoundToStationAsyncTask(new ListViewHandler(){
                    @Override
                    public void updateListView(Object... items) {
                        if (items != null){
                            for (Object clip : items){
                                listAdapter.add((SoundClipInfo)clip);
                            }
                        }
                    }
                }).execute(stationName);
                */
            }
        });
    }

    private void initCurrentSoundsListView(View rootView){
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/ProximaNova-Semibold.otf");
        listAdapter = new SoundClipInfoAdapter(getActivity(), R.layout.sound_clip_list_item,
                new ArrayList<SoundClipInfo>(), font);

        clipListView = (ListView)rootView.findViewById(R.id.station_fragment_current_sounds_listview);
        clipListView.setAdapter(listAdapter);
    }

    private void populateCurrentSoundsList(){
        new GetStationSoundClips(new ListViewHandler(){
            @Override
            public void updateListView(Object[] items){
                if (items != null) {
                    listAdapter.clear();
                    for (Object clip : items) {
                        Log.d(TAG, clip.toString());
                        listAdapter.add((SoundClipInfo) clip);
                    }
                }
            }
        }).execute(stationName);
    }
}

