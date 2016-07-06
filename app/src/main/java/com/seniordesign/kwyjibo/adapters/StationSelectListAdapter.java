package com.seniordesign.kwyjibo.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.beans.RadioStation;
import com.seniordesign.kwyjibo.interfaces.HasSessionInfo;
import com.seniordesign.kwyjibo.kwyjibo.R;
import com.seniordesign.kwyjibo.sorting.DescendingNumClips;

import java.util.Collections;
import java.util.List;

public class StationSelectListAdapter extends RecyclerView.Adapter<StationSelectListAdapter.ViewHolder>
    implements HasSessionInfo{

    private Context context;
    private List<RadioStation> stations;

    public StationSelectListAdapter(Context context, List<RadioStation> stations) {
        this.context = context;
        this.stations = stations;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View listItem = LayoutInflater.from(context).inflate(R.layout.station_selection_list_item, parent, false);
        return new ViewHolder(listItem, new ViewHolder.IOnClick() {
            @Override
            public void onItemClick(View view, int position) {
                MainActivity.storePreference(CURRENT_STATION, stations.get(position).Name);
                MainActivity.replaceScreen(MainActivity.Screens.CURRENT_STATION, "CURRENT_STATION",
                        android.R.anim.fade_in, android.R.anim.fade_out);
                Log.e("StationSelectAdapter", "onItemClick");
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Typeface proximaNova = Typeface.createFromAsset(context.getAssets(),"fonts/ProximaNova-Semibold.otf");
        RadioStation station = stations.get(position);
        holder.name.setText(station.Name);
        holder.name.setTypeface(proximaNova);
        holder.numClips.setText(String.valueOf(station.NumCurrentClips));
        holder.numClips.setTypeface(proximaNova);
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    public void updateData(List<RadioStation> list){
        if (list != null){
            stations.clear();
            Collections.sort(list, new DescendingNumClips());
            stations.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void add(RadioStation station){
        stations.add(station);
        notifyItemInserted(stations.size()-1);
    }

    public Context getContext(){
        return context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public TextView numClips;
        public ImageView icon;
        public IOnClick listener;

        public ViewHolder(View itemView, IOnClick listener) {
            super(itemView);
            this.listener = listener;
            name = (TextView)itemView.findViewById(R.id.station_selection_cardview_name_textview);
            numClips = (TextView)itemView.findViewById(R.id.station_selection_cardview_numclips_textview);
            icon = (ImageView)itemView.findViewById(R.id.station_selection_cardview_sounds_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getLayoutPosition());
        }

        interface IOnClick{
            void onItemClick(View view, int position);
        }
    }
}