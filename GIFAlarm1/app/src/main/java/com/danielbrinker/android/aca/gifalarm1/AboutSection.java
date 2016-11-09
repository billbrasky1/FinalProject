package com.danielbrinker.android.aca.gifalarm1;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by danielbrinker on 11/7/16.
 */

public class AboutSection extends AppCompatActivity {

    TextView mAboutSectionTitle;
    TextView mAboutMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        FragmentManager fManager = getFragmentManager();

        Fragment frag = fManager.findFragmentById(R.id.fragmentHolder);

        if (frag == null) {
            frag = new AboutFragment();
            fManager.beginTransaction()
                    .add(R.id.fragmentHolder, frag)
                    .commit();
        }
    }
}

