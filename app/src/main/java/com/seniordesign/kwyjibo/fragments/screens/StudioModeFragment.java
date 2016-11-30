package com.seniordesign.kwyjibo.fragments.screens;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.seniordesign.kwyjibo.core.ApplicationWrapper;
import com.seniordesign.kwyjibo.core.LoopMediaPlayer;
import com.seniordesign.kwyjibo.custom.adapters.SoundClipInfoAdapter;
import com.seniordesign.kwyjibo.custom.decorators.SwipeDetector;
import com.seniordesign.kwyjibo.database.models.SoundClipInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;
import com.seniordesign.kwyjibo.database.restapi.RestAPI;
import com.seniordesign.kwyjibo.custom.sorting.AscendingClipName;
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
    private ViewGroup mainFragViewGroup;
    private FloatingActionButton addSoundButton;
    private View helpOverlay;
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;

    private ImageView playButton;
    private View.OnClickListener playButtonListener;
    private View.OnClickListener stopSongListener;
    private LoopMediaPlayer loopPlayer;
    private String songFilename = "studioMode";

    private boolean firstTimeRunning = true;

    private static final String TAG = "StudioModeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.studio_mode_fragment, container, false);
        ApplicationWrapper.applyLayoutDesign(rootView);

        initViews(rootView);




        // Create list adapters
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNova-Semibold.otf");
        allClipsListAdapter = new SoundClipInfoAdapter(getActivity(), R.layout.sound_clip_list_item, new ArrayList<SoundClipInfo>(), font);
        currentSoundsListAdapter = new SoundClipInfoAdapter(getActivity(), R.layout.sound_clip_list_item, new ArrayList<SoundClipInfo>(), font);

        // Set Adapters
        allClipsListView.setAdapter(allClipsListAdapter);
        currentSoundsListView.setAdapter(currentSoundsListAdapter);

        setListeners();

        if (firstTimeRunning){
            helpOverlay = inflater.inflate(R.layout.studio_mode_help_overlay, mainFragViewGroup, false);
            addOverlayView(helpOverlay, mainFragViewGroup);
        }
        firstTimeRunning = false;

        RestAPI.getAllSoundClipInfo(new Callback<List<SoundClipInfo>>() {
            @Override
            public void onResponse(Call<List<SoundClipInfo>> call, Response<List<SoundClipInfo>> response) {
                if (response.body() != null) {
                    updateAllClipsListView(response.body());
                    playButton.setOnClickListener(playButtonListener);
                    playButton.setAlpha(1.0f);
                }else{
                    Log.d(TAG, response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<List<SoundClipInfo>> call, Throwable t) {

            }
        });

        return rootView;
    }



    private void playSongClip(){
        String songFilepath = getContext().getExternalFilesDir(null) + "/station-songs/" + songFilename;
        try {
            loopPlayer = LoopMediaPlayer.create(songFilepath);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void initViews(View rootView){
        mainFragViewGroup = (ViewGroup)rootView.findViewById(R.id.studio_mode_main_viewgroup);
        panel = (SlidingUpPanelLayout)rootView.findViewById(R.id.studio_mode_sliding_panel);
        allClipsListView = (ListView)rootView.findViewById(R.id.studio_mode_allclips_listview);
        currentSoundsListView = (ListView)rootView.findViewById(R.id.studio_mode_current_sounds_listview);
        addSoundButton = (FloatingActionButton)rootView.findViewById(R.id.studio_mode_add_sound_button);
        playButton = (ImageView) rootView.findViewById(R.id.studio_mode_play_stream_button);
        playButton.setAlpha(0.2f);
    }

    private void setListeners(){
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

        addSoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        currentSoundsListView.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SoundClipInfo itemClicked = currentSoundsListAdapter.getItem(position);
                currentSoundsListAdapter.remove(itemClicked);
                currentSoundsListAdapter.notifyDataSetChanged();
                return false;
            }
        });

        /*
        new SwipeDetector(currentSoundsListView).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
            @Override
            public void SwipeEventDetected(View v, SwipeDetector.SwipeType swipeType) {
                if(swipeType == SwipeDetector.SwipeType.RIGHT_TO_LEFT){
                    SoundClipInfo itemSwiped = currentSoundsListAdapter.getItem(0);
                    currentSoundsListAdapter.remove(itemSwiped);
                    currentSoundsListAdapter.notifyDataSetChanged();
                }
            }
        });
        */


        queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("onQueryTextChange", newText);

                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("onQueryTextSubmit", query);

                return true;
            }

        };
        searchView.setOnQueryTextListener(queryTextListener);

       new SwipeDetector(mainFragViewGroup).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
            @Override
            public void SwipeEventDetected(View v, SwipeDetector.SwipeType swipeType) {
                if (swipeType == SwipeDetector.SwipeType.LEFT_TO_RIGHT) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        stopSongListener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(songFilename != null){
                    loopPlayer.stop();
                    playButton.setOnClickListener(playButtonListener);
                    playButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_circle_outline));
                }
            }
        };

        // Define the action when the play button is clicked
        playButtonListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (songFilename != null){
                    playSongClip();
                    Drawable stopImage = ContextCompat.getDrawable(getContext(), R.drawable.ic_stop_circle_outline);
                    playButton.setOnClickListener(stopSongListener);
                    playButton.setImageDrawable(stopImage);


                }
            }
        };

    }

    private void addOverlayView(View overlay, ViewGroup parent){
        PorterDuffColorFilter blueGray_50 = new PorterDuffColorFilter(
                ContextCompat.getColor(getContext(), R.color.blueGray_50), PorterDuff.Mode.SRC_ATOP );
        ((ImageView)overlay.findViewById(R.id.studio_mode_overlay_icon)).setColorFilter(blueGray_50);
        panel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        overlay.setOnClickListener(nextHelpHighlight);
        parent.addView(overlay);

    }

    private void updateAllClipsListView(@NonNull List<SoundClipInfo> clips){
        Collections.sort(clips, new AscendingClipName());
        allClipsListAdapter.clear();
        for (SoundClipInfo clip : clips){
            allClipsListAdapter.add(clip);
        }
        allClipsListAdapter.notifyDataSetChanged();
    }

    private View.OnClickListener hideOverlay = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setDuration(500);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                    ((ViewGroup) v.getParent()).removeView(v);
                    panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
                public void onAnimationRepeat(Animation animation) {}
                public void onAnimationStart(Animation animation) {}
            });
            v.startAnimation(fadeOut);
            helpOverlay = null;
        }
    };

    private View.OnClickListener nextHelpHighlight = new View.OnClickListener(){
        @Override
        public void onClick(final View v) {
            final ImageView icon = (ImageView)v.findViewById(R.id.studio_mode_overlay_icon);
            final View divider = (View)v.findViewById(R.id.studio_mode_overlay_header_divider);
            final TextView pullDown = (TextView)v.findViewById(R.id.studio_mode_help_overlay_pulldown_textview);
            final TextView tapToStart = (TextView)v.findViewById(R.id.studio_mode_help_overlay_taptostart_textview);

            fadeOut(icon, 1, 0.1f);
            fadeOut(divider, 1, 0.1f);
            fadeOut(pullDown, 1, 0.1f);
            fadeIn(tapToStart, 0.1f, 1);
            helpOverlay.setOnClickListener(hideOverlay);
        }
    };

    private void fadeOut(final View v, float start, final float end){
        Animation fade = new AlphaAnimation(start, end);
        //fade.setInterpolator(new AccelerateInterpolator());
        fade.setDuration(250);
        fade.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) { v.setAlpha(end); }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });
        v.startAnimation(fade);
    }

    private void fadeIn(final View v, float start, final float end){
        Animation fadeIn = new AlphaAnimation(start, end);
        //fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(250);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) { v.setAlpha(end); }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });
        v.startAnimation(fadeIn);
    }

    private List<SoundClipInfo> searchByName(String name, List<SoundClipInfo> clips){
        List<SoundClipInfo> results = new ArrayList<SoundClipInfo>();
        for(SoundClipInfo clip: clips){
            if(clip.Name.equals(name))
                results.add(clip);
        }

        return results;
    }

    private List<SoundClipInfo> searchByLocation(String location, List<SoundClipInfo> clips){
        List<SoundClipInfo> results = new ArrayList<SoundClipInfo>();
        for(SoundClipInfo clip: clips){
            if(clip.Location.equals(location))
                results.add(clip);
        }

        return results;
    }

    private List<SoundClipInfo> searchByCategory(String category, List<SoundClipInfo> clips){
        List<SoundClipInfo> results = new ArrayList<SoundClipInfo>();
        for(SoundClipInfo clip: clips){
            if(clip.Category.equals(category))
                results.add(clip);


        }

        return results;
    }

    private List<SoundClipInfo> searchByCreator(String creator, List<SoundClipInfo> clips){
        List<SoundClipInfo> results = new ArrayList<SoundClipInfo>();
        for(SoundClipInfo clip: clips){
            if(clip.CreatedByName.equals(creator))
                results.add(clip);
        }

        return results;
    }


}
