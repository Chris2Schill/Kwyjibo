package com.seniordesign.kwyjibo.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seniordesign.kwyjibo.beans.RadioStation;
import com.seniordesign.kwyjibo.kwyjibo.R;

import java.util.List;

public class StationSelectListAdapter<T> extends ArrayAdapter<T> {

    private Context context;
    private int id;
    private List<T> items;

    public StationSelectListAdapter(Context context, int id, List<T> items) {
        super(context, id, items);
        this.context = context;
        this.id = id;
        this.items = items;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        View mView = v ;
        if(mView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = inflater.inflate(id, parent, false);
        }

        TextView stationName = (TextView)mView.findViewById(R.id.station_selection_cardview_textview);
        TextView numClips = (TextView)mView.findViewById(R.id.station_selection_cardview_numclips_textview);
        T item = items.get(position);

        if(item != null && item instanceof RadioStation) {
            RadioStation station = (RadioStation)item;
            Typeface proximaNova = Typeface.createFromAsset(context.getAssets(),"fonts/ProximaNova-Semibold.otf");

            stationName.setTypeface(proximaNova);
            stationName.setText(station.Name);

            numClips.setTypeface(proximaNova);
            numClips.setText(station.NumCurrentClips + "");
        }

        return mView;
    }
}