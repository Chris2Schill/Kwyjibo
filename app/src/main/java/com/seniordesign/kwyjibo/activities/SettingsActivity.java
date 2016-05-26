package com.seniordesign.kwyjibo.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.seniordesign.kwyjibo.kwyjibo.R;

public class SettingsActivity extends AppCompatActivity {

    private final static String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            PreferenceManager.setDefaultValues(getActivity(), R.xml.advanced_preferences, true);
            addPreferencesFromResource(R.xml.advanced_preferences);
            initializeSummaries(getPreferenceScreen());
        }

        private void initializeSummaries(Preference pref){
            if (pref instanceof PreferenceGroup){
                PreferenceGroup pGroup = (PreferenceGroup) pref;
                for (int i = 0; i < pGroup.getPreferenceCount(); i++){
                    initializeSummaries(pGroup.getPreference(i));
                }
            }else{
                updatePrefSummary(pref);
            }

        }

        private void updatePrefSummary(Preference pref){
            if (pref == null) return;
            if (pref instanceof EditTextPreference){
                EditTextPreference p = (EditTextPreference) pref;
                pref.setSummary(p.getText());
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePrefSummary(findPreference(key));
        }
    }

}
