package com.danielbrinker.android.aca.gifalarm1;

/**
 * Created by danielbrinker on 10/5/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class WelcomeScreen extends Activity {

    private Button mGoToTimer;
    private Button mGoToRefresher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        mGoToTimer = (Button) findViewById(R.id.btnGifAlarm);
        mGoToRefresher = (Button) findViewById(R.id.btnGifSearch);


        mGoToTimer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
                startActivity(intent);


            }
        });

        mGoToRefresher.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WelcomeScreen.this, GifSearch.class);
                startActivity(intent);


            }
        });

    }


}

