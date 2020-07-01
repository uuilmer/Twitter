package com.codepath.apps.restclienttemplate.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.Activities.ComposeTweetActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.Adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    List<Tweet> tweets;
    TwitterClient twClient;
    TweetAdapter twAdapter;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        rv = findViewById(R.id.recycle);

        twClient = TwitterApp.getRestClient(this);

        //Create an Adapter to manage the RecyclerView
        tweets = new ArrayList<>();
        twAdapter = new TweetAdapter(this, tweets, twClient);
        rv.setAdapter(twAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        twClient.getTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray tw_arr = json.jsonArray;
                //Get each JSONObject from the response and pass it to method that turns JSONObject's data to a Tweet object
                for(int i = 0; i < tw_arr.length(); i++){
                    try {
                        tweets.add(Tweet.fromJSONObject(tw_arr.getJSONObject(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //Update the RecyclerView since Tweets were retrieved
                twAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                System.out.println(throwable.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu that includes the "compose new Tweet" icon
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //If compose was pressed, go to ComposeTweetActivity
        if(item.getItemId() == R.id.compose){
            Intent i = new Intent(TimelineActivity.this, ComposeTweetActivity.class);
            //NOTE: Put extra??
            startActivityForResult(i, 100);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //If request was successful
        if(requestCode == 100 && resultCode == RESULT_OK){
            //Get the new Tweet and add to RecyclerView
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            twAdapter.notifyItemInserted(0);
            rv.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}