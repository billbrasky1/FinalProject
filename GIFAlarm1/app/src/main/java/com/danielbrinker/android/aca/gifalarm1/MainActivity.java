package com.danielbrinker.android.aca.gifalarm1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.danielbrinker.android.aca.gifalarm1.R.id.refresh;
import static com.danielbrinker.android.aca.gifalarm1.R.id.userInput;

public class MainActivity extends AppCompatActivity {

    ///////// Giphy API calls & ImageView for GIF /////////
    public static final String TAG = MainActivity.class.getSimpleName();
    private GifDataFromGiphy mGifDataFromGiphy;
    private ImageView mImageView;
    private Button mRefreshButton;
    private EditText mUserInput;

    ///////// User Timer Inputs /////////
    private EditText mUserHoursInput;
    private EditText mUserMinutesInput;
    private EditText mUserSecondsInput;

    ////// Timer Buttons /////////
    Button mBtnStart, mBtnStop;
    TextView mTextViewTime;

    //////// Beep sound for Alarm /////////
    int mIdBeep = -1;
    SoundPool mSp;

    ////////// Shared Prefs ////////
    private SharedPreferences mPrefs;
    private boolean mSound;



    //  create a textWatcher member
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // check Fields For Empty Values
            checkFieldsForEmptyValues();
        }
    };

    void checkFieldsForEmptyValues() {
        Button mBtnStart = (Button) findViewById(R.id.btnStart);

        String s1 = mUserInput.getText().toString();

        String s2 = mUserHoursInput.getText().toString();
        String s3 = mUserMinutesInput.getText().toString();
        String s4 = mUserSecondsInput.getText().toString();

        if (s1.equals("") || s2.equals("") || s3.equals("") || s4.equals("")) {
            mBtnStart.setEnabled(false);
        } else {
            mBtnStart.setEnabled(true);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.gifsFromGlide);
        mRefreshButton = (Button) findViewById(refresh);
        mUserInput = (EditText) findViewById(userInput);

        mUserHoursInput = (EditText) findViewById(R.id.hours);
        mUserMinutesInput = (EditText) findViewById(R.id.minutes);
        mUserSecondsInput = (EditText) findViewById(R.id.seconds);

        mPrefs = getSharedPreferences("Note to self", MODE_PRIVATE);
        mSound = mPrefs.getBoolean("sound", true);


        //////////// Instantiate our sound pool ///////////////////////////////
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSp = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }


        try {
            // Create objects of the 2 required classes
            AssetManager assetManager = this.getAssets();
            AssetFileDescriptor descriptor;

            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("alarm13.ogg");
            mIdBeep = mSp.load(descriptor, 0);


        } catch (IOException e) {
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }
        /////////////////////////////////////////////////////////


        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refresh();
            }

            private void refresh() {
            }
        });


        mBtnStart = (Button) findViewById(R.id.btnStart);
        mTextViewTime = (TextView) findViewById(R.id.textViewTime);


        mTextViewTime.setText("00:00:00");


        mBtnStart.setOnClickListener(new View.OnClickListener() {
            CounterClass timer;

            @Override
            public void onClick(View v) {
                if (!mUserSecondsInput.getText().toString().isEmpty() && !mUserMinutesInput.getText().toString().isEmpty() && !mUserHoursInput.getText().toString().isEmpty()) {


                    int secondsValue = Integer.parseInt(String.valueOf(mUserSecondsInput.getText())) * 1000;

                    int minutesValue = Integer.parseInt(String.valueOf(mUserMinutesInput.getText())) * 60 * 1000;

                    int hoursValue = Integer.parseInt(String.valueOf(mUserHoursInput.getText())) * 60 * 60 * 1000;


                    timer = new CounterClass((secondsValue + minutesValue + hoursValue), 1000);
                }
                timer.start();
            }
        });



        /*
        mBtnStop.setOnClickListener(new View.OnClickListener() {
            public CounterClass timer;

            @Override
            public void onClick(View v) {
                timer.cancel();
            }
        });
        */

    }

    public class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }


        public void onTick(long millisUntilFinished) {

            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

            System.out.println(hms);
            mTextViewTime.setText(hms);

        }


        protected void onResume() {
            this.onResume();

            mPrefs = getSharedPreferences("Settings", MODE_PRIVATE);
        }

        //@Override
        protected void onPause() {
            this.onPause();
        }

        @Override
        public void onFinish() {

            if (mSound) {
                mSp.play(mIdBeep, 1, 1, 0, 0, 1);
            }

            this.start();
            refresh();
        }

        private void refresh() {
            getGifsFromGiphy();
        }


        private GifDataFromGiphy getGif(String jsonData) throws JSONException {
            JSONObject giphy = new JSONObject(jsonData);
            JSONObject data = giphy.getJSONObject("data");

            GifDataFromGiphy gif = new GifDataFromGiphy();
            gif.setUrl(data.getString("image_url"));
            Log.i(TAG, "Gif JSON Data - GIF URL: " + gif);

            return gif;
        }

        private void updateDisplay() {
            String gifUrl = mGifDataFromGiphy.getUrl();
            Log.i(TAG, "updateDisplay GIF URL: " + gifUrl);

            Glide.with(MainActivity.this)
                    .load(gifUrl)
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .into(new GlideDrawableImageViewTarget(mImageView) {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                            //check isRefreshing
                        }
                    });
        }

        private void getGifsFromGiphy() {
            //URL Format: http://api.giphy.com/v1/gifs/search?q=cute+cat&api_key=dc6zaTOxFJmzC&limit=1&offset=0
            //Random Search URL: http://api.giphy.com/v1/gifs/random?api_key=dc6zaTOxFJmzC&tag=cute+funny+cat+kitten
            String apiKey = "dc6zaTOxFJmzC"; //Giphy's Public API Key
            String userInput = mUserInput.getText().toString().toLowerCase();

            String giphyUrl =
                    "http://api.giphy.com/v1/gifs/random" +
                            "?api_key=" +
                            apiKey +
                            "&tag=" +
                            userInput;


            if (isNetworkAvailable()) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(giphyUrl)
                        .build();

                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                        Log.i(TAG, "Request Failure");
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                        try {
                            String jsonData = response.body().string();
                            if (response.isSuccessful()) {
                                mGifDataFromGiphy = getGif(jsonData);
                                Log.v(TAG, "Giphy Gif Data from Response: " + mGifDataFromGiphy);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateDisplay();
                                    }
                                });
                            } else {
                                Log.i(TAG, "Response Unsuccessful");
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Exception Caught: ", e);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        private boolean isNetworkAvailable() {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            boolean isAvailable = false;
            if (networkInfo != null && networkInfo.isConnected()) {
                isAvailable = true;
            }
            return isAvailable;
        }

    }

    ///////// Options menu for selecting sound on or off and About Section ///////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.activity_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.about) {
            Intent intent = new Intent(getApplicationContext(), AboutSection.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}