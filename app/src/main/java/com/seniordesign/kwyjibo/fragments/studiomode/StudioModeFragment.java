package com.seniordesign.kwyjibo.fragments.studiomode;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.seniordesign.kwyjibo.activities.ApplicationWrapper;
import com.seniordesign.kwyjibo.adapters.SoundClipInfoAdapter;
import com.seniordesign.kwyjibo.beans.SoundClipInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;
import com.seniordesign.kwyjibo.restapi.RestAPI;
import com.seniordesign.kwyjibo.sorting.AscendingClipName;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudioModeFragment extends Fragment {

    private ListView allClipsListView;
    private ListView currentSoundsListView;
    private SoundClipInfoAdapter allClipsListAdapter;
    private SoundClipInfoAdapter currentSoundsListAdapter;
    private SlidingUpPanelLayout panel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.studio_mode_fragment, container, false);
        ApplicationWrapper.applyLayoutDesign(rootView);
        panel = (SlidingUpPanelLayout)rootView.findViewById(R.id.studio_mode_sliding_panel);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNova-Semibold.otf");
        allClipsListAdapter = new SoundClipInfoAdapter(getActivity(), R.layout.sound_clip_list_item,
                new ArrayList<SoundClipInfo>(), font);
        currentSoundsListAdapter = new SoundClipInfoAdapter(getActivity(), R.layout.sound_clip_list_item,
                new ArrayList<SoundClipInfo>(), font);

        allClipsListView = (ListView)rootView.findViewById(R.id.studio_mode_allclips_listview);
        allClipsListView.setAdapter(allClipsListAdapter);
        allClipsListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SoundClipInfo itemClicked = allClipsListAdapter.getItem(position);
                currentSoundsListAdapter.add(itemClicked);
                currentSoundsListAdapter.notifyDataSetChanged();

                allClipsListAdapter.remove(itemClicked);
                allClipsListAdapter.notifyDataSetChanged();

                panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        currentSoundsListView = (ListView)rootView.findViewById(R.id.studio_mode_current_sounds_listview);
        currentSoundsListView.setAdapter(currentSoundsListAdapter);

        rootView.findViewById(R.id.studio_mode_add_sound_button)
                .setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        panel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    }
                });

        RestAPI.getAllSoundClipInfo(new Callback<List<SoundClipInfo>>(){
            @Override
            public void onResponse(Call<List<SoundClipInfo>> call, Response<List<SoundClipInfo>> response) {
                if (response.body() != null){
                    updateAllClipsListView(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<SoundClipInfo>> call, Throwable t) {

            }
        });
        return rootView;
    }

    private void updateAllClipsListView(@NonNull List<SoundClipInfo> clips){
        Collections.sort(clips, new AscendingClipName());
        allClipsListAdapter.clear();
        for (SoundClipInfo clip : clips){
            allClipsListAdapter.add(clip);
        }
        allClipsListAdapter.notifyDataSetChanged();
    }
}
