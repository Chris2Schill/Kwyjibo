package com.seniordesign.kwyjibo.fragments.screens;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.seniordesign.kwyjibo.core.ApplicationWrapper;
import com.seniordesign.kwyjibo.core.LoopMediaPlayer;
import com.seniordesign.kwyjibo.core.MainActivity;
import com.seniordesign.kwyjibo.core.Screens;
import com.seniordesign.kwyjibo.custom.adapters.SoundClipInfoAdapter;
import com.seniordesign.kwyjibo.custom.decorators.SwipeDetector;
import com.seniordesign.kwyjibo.database.models.SoundClipInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;
import com.seniordesign.kwyjibo.database.restapi.RestAPI;
import com.seniordesign.kwyjibo.custom.sorting.AscendingClipName;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Headers;
import okhttp3.ResponseBody;
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
    private Spinner spinnerBPM;
    private Spinner spinnerTimeSignature;
    private ArrayAdapter<String> spinnerAdapterBPM;
    private ArrayAdapter<String> spinnerAdapterTimeSignature;
    private EditText searchQuery;
    private String songFilepath;

    private boolean firstTimeRunning = true;

    private static final String TAG = "StudioModeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.studio_mode_fragment, container, false);
        ApplicationWrapper.applyLayoutDesign(rootView);

        initViews(rootView);
        updateSpinners(rootView);

        initButtons(rootView);

        MainActivity.getLoopPlayer().setMode(LoopMediaPlayer.Modes.STUDIO);

        songFilepath = getContext().getExternalFilesDir(null) + "/" + "studio-song.wav";

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

    private void addOverlayView(View overlay, ViewGroup parent){
        PorterDuffColorFilter blueGray_50 = new PorterDuffColorFilter(
                ContextCompat.getColor(getContext(), R.color.blueGray_50), PorterDuff.Mode.SRC_ATOP );
        ((ImageView)overlay.findViewById(R.id.studio_mode_overlay_icon)).setColorFilter(blueGray_50);
        panel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        overlay.setOnClickListener(nextHelpHighlight);
        parent.addView(overlay);

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

    private void fadeOut(final View v, float start, final float end) {
        Animation fade = new AlphaAnimation(start, end);
        //fade.setInterpolator(new AccelerateInterpolator());
        fade.setDuration(250);
        fade.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                v.setAlpha(end);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });
        v.startAnimation(fade);
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

    private void initButtons(View rootView){
        rootView.findViewById(R.id.studio_mode_add_sound_button).setOnClickListener(new View.OnClickListener() {
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
                                ApplicationWrapper.storePreference("fromStation", false);

                                MainActivity.replaceScreen(Screens.RECORD_MODE, "RECORD_MODE",
                                        android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
            }
        });

        // Set the button to be almost transparent to indicate that it isn't ready to be clicked.
        // Will be set to no transparency when song for the station is finished downloading
        //playButton = (ImageView) rootView.findViewById(R.id.station_fragment_play_stream_imagebutton);
        //playButton.setAlpha(0.2f);
    }


    public void getStudioModeSong(){
        List<Integer> clipIds = new ArrayList<Integer>();
        for(int i = 0; i < currentSoundsListAdapter.getCount(); i++){
            clipIds.add(currentSoundsListAdapter.getItem(i).Id);
        }
        int[] clipsIds = new int[clipIds.size()];
        for(int i = 0; i < clipsIds.length; i++){
            clipsIds[i] = clipIds.get(i);
        }

        int bpm = Integer.parseInt(spinnerBPM.getSelectedItem().toString());
        int timeSig = Integer.parseInt(spinnerTimeSignature.getSelectedItem().toString());
        RestAPI.getStudioModeSong(clipsIds, bpm, timeSig, new Callback<ResponseBody>(){

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body() != null){
                    saveStationSong(response.headers(), response.body());
                    if (!MainActivity.getLoopPlayer().isPlaying()){
                        playSong();
                        Drawable stopImage = ContextCompat.getDrawable(getContext(), R.drawable.ic_stop_circle_outline);
                        playButton.setOnClickListener(stopSongListener);
                        playButton.setImageDrawable(stopImage);
                    }
                }
                else{
                    Log.d(TAG, "getStudioModeSong() response body is null");
                    Toast.makeText(getContext(), "getStudioModeSong() response body is null",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "getStudioModeSong() api call failed");
            }
        });

    }

    private void playSong(){
        try {
            MainActivity.getLoopPlayer().reset();
            MainActivity.getLoopPlayer().setDataSource(songFilepath);
            MainActivity.getLoopPlayer().setStudioFragmentReference(this);
            MainActivity.getLoopPlayer().prepare();
            MainActivity.getLoopPlayer().start();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private boolean saveStationSong(Headers headers, ResponseBody body){
        InputStream inputStream = null;
        OutputStream outputStream = null;

        // Save the file
        File stationSong = new File(songFilepath);
        try{
            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(stationSong);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    //Log.d("API", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        }catch(IOException ex){
            Log.e(TAG, ex.getMessage());
            return false;
        }
        return true;
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

        /*addSoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });*/

        currentSoundsListView.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SoundClipInfo itemClicked = currentSoundsListAdapter.getItem(position);
                currentSoundsListAdapter.remove(itemClicked);
                currentSoundsListAdapter.notifyDataSetChanged();

                allClipsListAdapter.add(itemClicked);
                allClipsListAdapter.sort(new AscendingClipName());
                return true;
            }
        });


//        searchView.setOnQueryTextListener(queryTextListener);

//       new SwipeDetector(mainFragViewGroup).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
//            @Override
//            public void SwipeEventDetected(View v, SwipeDetector.SwipeType swipeType) {
//                if (swipeType == SwipeDetector.SwipeType.LEFT_TO_RIGHT) {
//                    getActivity().getSupportFragmentManager().popBackStack();
//                }
//            }
//        });

        stopSongListener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(songFilepath != null){
                    MainActivity.getLoopPlayer().stop();
                    playButton.setOnClickListener(playButtonListener);
                    playButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_circle_outline));
                }
            }
        };

        // Define the action when the play button is clicked
        playButtonListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (songFilepath != null){
                    getStudioModeSong();
                }
            }
        };

        /*
        queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("onQueryTextChange", newText);

                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("onQueryTextSubmit", query);
                updateAllClipsListViewSearch(query);
                return true;
            }

        };

        searchView.setOnQueryTextListener(queryTextListener);*/

    }

    private void updateAllClipsListViewSearch(String query){
        List<SoundClipInfo> clips = new ArrayList<SoundClipInfo>();
        clips = searchByName(query, clips);
        Collections.sort(clips, new AscendingClipName());
        allClipsListAdapter.clear();
        for(SoundClipInfo clip: clips){
            allClipsListAdapter.add(clip);
        }
        allClipsListAdapter.notifyDataSetChanged();


    }

    private void updateAllClipsListView(@NonNull List<SoundClipInfo> clips){
        Collections.sort(clips, new AscendingClipName());
        allClipsListAdapter.clear();
        for (SoundClipInfo clip : clips){
            allClipsListAdapter.add(clip);
        }
        allClipsListAdapter.notifyDataSetChanged();
    }

    private void updateSpinners(View rootview){
        spinnerBPM = (Spinner) rootview.findViewById(R.id.studio_mode_bpm);
        spinnerAdapterBPM = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>() );
        spinnerBPM.setAdapter(spinnerAdapterBPM);
        spinnerAdapterBPM.clear();
        for(int i = 45; i <= 180; i++){
            spinnerAdapterBPM.add(Integer.toString(i));
        }

        spinnerTimeSignature = (Spinner) rootview.findViewById(R.id.studio_mode_time_signature);
        spinnerAdapterTimeSignature = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>());
        spinnerTimeSignature.setAdapter(spinnerAdapterTimeSignature);
        spinnerAdapterTimeSignature.clear();
        for(int i = 3; i <= 9; i++){
            spinnerAdapterTimeSignature.add(Integer.toString(i));
        }
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
