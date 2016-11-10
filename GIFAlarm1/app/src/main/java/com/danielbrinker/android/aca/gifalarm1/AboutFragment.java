package com.danielbrinker.android.aca.gifalarm1;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by danielbrinker on 11/8/16.
 */

public class AboutFragment extends Fragment {


    TextView mAboutSectionTitle;
    TextView mAboutMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment, container, false);

        mAboutSectionTitle = (TextView) view.findViewById(R.id.textViewAboutTitle);
        mAboutMessage = (TextView) view.findViewById(R.id.aboutMessage);


        return view;

    }


}




