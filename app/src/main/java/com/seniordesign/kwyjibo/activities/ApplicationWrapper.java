package com.seniordesign.kwyjibo.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.seniordesign.kwyjibo.kwyjibo.R;

public class ApplicationWrapper extends AppCompatActivity{
    protected static MainActivity context;
    protected static SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefsEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
    }

    public static void applyLayoutDesign(View rootView) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-Semibold.otf");
        ViewGroup v = (ViewGroup) rootView;
        for (int i = 0; i < v.getChildCount(); i++) {
            View child = v.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTypeface(font);
                ((TextView) child).setTextColor(Color.WHITE);
            }
            if (child instanceof EditText) {
                ((EditText) child).setTypeface(font);
                child.setBackgroundColor(context.getResources().getColor(R.color.darkGray));
            }
            if (child instanceof Button) {
                ((Button) child).setTypeface(font);
            }
            if (child instanceof RadioGroup){
                RadioGroup rg = (RadioGroup)child;
                for (int j = 0; j < rg.getChildCount(); j++){
                    RadioButton b = (RadioButton)rg.getChildAt(j);
                    b.setTypeface(font);
                }
            }
        }
    }


    public static <T> void storePreference(String key, T value){
        if (value instanceof String){
            prefsEditor.putString(key, (String) value);
        }else if (value instanceof Boolean){
            prefsEditor.putBoolean(key, (Boolean)value);
        }else if (value instanceof Integer){
            prefsEditor.putInt(key, (Integer) value);
        }
        prefsEditor.apply();
    }

    public static String getStringPreference(String key){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }
    public static boolean getBooleanPreference(String key){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }
    public static int getIntPreference(String key){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0);
    }

    protected static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
