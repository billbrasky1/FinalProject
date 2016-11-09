package com.danielbrinker.android.aca.gifalarm1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;

    private boolean mSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mPrefs = getSharedPreferences("Note to self", MODE_PRIVATE);
        mEditor = mPrefs.edit();


        mSound = mPrefs.getBoolean("sound", true);

        CheckBox checkBoxSound = (CheckBox) findViewById(R.id.checkBoxSound);

        if (mSound) {
            checkBoxSound.setChecked(true);
        } else {
            checkBoxSound.setChecked(false);
        }

        checkBoxSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(
                    CompoundButton buttonView, boolean isChecked) {
                Log.i("sound = ", "" + mSound);
                Log.i("isChecked = ", "" + isChecked);

                // If mSound is true make it false
                // If mSound is false make it true
                mSound = !mSound;
                mEditor.putBoolean("sound", mSound);

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save the settings here
        mEditor.commit();
    }



}
