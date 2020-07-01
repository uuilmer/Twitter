package com.codepath.apps.restclienttemplate.Activities;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeTweetActivity extends AppCompatActivity {

    ImageView cancel_tweet;
    ImageView profile_pic;
    TextView username;
    EditText new_tweet_text;
    ImageView create_tweet;
    TwitterClient twitterClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);

        twitterClient = TwitterApp.getRestClient(this);

        // Declare all Views
        cancel_tweet = findViewById(R.id.cancel_tweet);
        profile_pic = findViewById(R.id.profile_pic_compose);
        username = findViewById(R.id.username_compose);
        new_tweet_text = findViewById(R.id.new_tweet_text);
        create_tweet = findViewById(R.id.create_tweet);

        new_tweet_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            // Everytime the new_tweet_text is changed and there is too many chars, make the commit Tweet button Visibility GONE
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                create_tweet.setVisibility(charSequence.length() > 280 || charSequence.length() == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        create_tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = new_tweet_text.getText().toString();
                if (s.length() == 0) {
                    Toast.makeText(ComposeTweetActivity.this, "Enter text", Toast.LENGTH_SHORT).show();
                    return;
                }
                // When commit is clicked, call tweet() on inputed String
                twitterClient.tweet(s, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        try {
                            // On successful Tweet, go back to TimelineActivity
                            Tweet tweet = Tweet.fromJSONObject(json.jsonObject);
                            Intent i = new Intent();
                            i.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, i);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e("Error", "Error creating tweet", throwable);
                    }
                });
            }
        });

        // If cancelled, go back to previous Activity
        cancel_tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}