package com.codepath.apps.restclienttemplate.models;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetAdapter;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    List<Tweet> tweets;
    TwitterClient twClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        RecyclerView rv = findViewById(R.id.recycle);

        twClient = TwitterApp.getRestClient(this);

        tweets = new ArrayList<>();
        final TweetAdapter twAdapter = new TweetAdapter(this, tweets, twClient);
        rv.setAdapter(twAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        twClient.getTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray tw_arr = json.jsonArray;
                for(int i = 0; i < tw_arr.length(); i++){
                    try {
                        tweets.add(Tweet.fromJSONObject(tw_arr.getJSONObject(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                twAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                System.out.println(throwable.toString());
            }
        });
    }
}