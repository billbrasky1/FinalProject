package com.danielbrinker.android.aca.gifalarm1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by danielbrinker on 11/6/16.
 */

public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {

                Intent i = new Intent(SplashScreen.this, WelcomeScreen.class);
                startActivity(i);

                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}