package com.seniordesign.kwyjibo.kwyjibo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static Fragment loginFragment;
    public static Fragment modeSelectionFragment;
    public static Fragment recordModeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        loginFragment = new LoginFragment();
        modeSelectionFragment = new ModeSelectionFragment();
        recordModeFragment = new RecordModeFragment();

        if (findViewById(R.id.main_activity_fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_activity_fragment_container,loginFragment).commit();

        }
    }
}

