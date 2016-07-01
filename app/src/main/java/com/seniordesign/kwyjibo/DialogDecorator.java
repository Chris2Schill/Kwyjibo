package com.seniordesign.kwyjibo;

import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seniordesign.kwyjibo.kwyjibo.R;

public class DialogDecorator {

    private AlertDialog dialog;
    private View view;
    private ViewGroup container;

    public DialogDecorator(int layoutId, AlertDialog dialog, ViewGroup container){
        this.dialog = dialog;
        this.container = container;
        setView(layoutId);
    }

    public void setView(int layoutId){
        if (dialog != null){
            if (view != null && view.getParent() == null){
                dialog.setView(view);
            }else{
                view = null;
                inflateView(layoutId);
                dialog.setView(view);
            }
        }
    }

    public void inflateView(int layoutId){
        view = LayoutInflater.from(dialog.getContext())
                .inflate(layoutId, container, false);
    }

    public void setOnClickListener(int resourceId, View.OnClickListener listener){
        view.findViewById(resourceId).setOnClickListener(listener);
    }

    public View getView(){
        return view;
    }
}
