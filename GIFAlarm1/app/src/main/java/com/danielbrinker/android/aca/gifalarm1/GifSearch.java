package com.danielbrinker.android.aca.gifalarm1;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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


public class GifSearch extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private GifDataFromGiphy mGifDataFromGiphy;
    private ImageView mImageView2;
    private Button mRefreshButton2;
    private EditText mUserInput2;
    //private SharedPreferences mPrefs;
   // private boolean mSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gif_search);

        mImageView2 = (ImageView) findViewById(R.id.gifsReloader);
        mRefreshButton2 = (Button) findViewById(R.id.refresher);
        mUserInput2 = (EditText) findViewById(R.id.userInput2);


        mRefreshButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refresh();
            }
        });
    }


///////////////////////////////////

    /*
    @Override
    protected void onResume(){
        super.onResume();

        mPrefs = getSharedPreferences("Note to self", MODE_PRIVATE);
        mSound  = mPrefs.getBoolean("sound", true);


    }
    */

    @Override
    protected void onPause() {
        super.onPause();

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

    ////////////////////////////////////////////////


    protected void onResume() {
        super.onResume();
        refresh();


        //mPrefs = getSharedPreferences("Note to self", MODE_PRIVATE);
        //mSound = mPrefs.getBoolean("sound", true);
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

        Glide.with(GifSearch.this)
                .load(gifUrl)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .into(new GlideDrawableImageViewTarget(mImageView2) {
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
        String userInput = mUserInput2.getText().toString().toLowerCase();

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
//                            toggleRefresh();
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
        } else {
            Toast.makeText(this, "Network not available!", Toast.LENGTH_LONG).show();
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
