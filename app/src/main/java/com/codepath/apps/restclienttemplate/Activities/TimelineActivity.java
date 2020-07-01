package com.codepath.apps.restclienttemplate.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.ColumnInfo;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.Activities.ComposeTweetActivity;
import com.codepath.apps.restclienttemplate.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.Adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.asynchttpclient.RequestParams;
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
    EndlessRecyclerViewScrollListener scrolling;
    TweetDao tweetDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        rv = findViewById(R.id.recycle);


        tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();

        twClient = TwitterApp.getRestClient(this);

        // Create an Adapter to manage the RecyclerView
        tweets = new ArrayList<>();
        twAdapter = new TweetAdapter(this, tweets, twClient, new TweetAdapter.OnClickReply() {
            @Override
            public void OnClick(Tweet tweet) {
                Intent i = new Intent(TimelineActivity.this, ComposeTweetActivity.class);
                i.putExtra("reply", true);
                i.putExtra("reply_to_id", tweet.getId());
                i.putExtra("reply_to", tweet.getPoster_username());
                startActivityForResult(i, 200);
            }
        });
        rv.setAdapter(twAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        // Initially, get a new feed
        newFeed();

        // As we scroll, keep loading the next page of 20 Tweets
        scrolling = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextPage();
            }
        };

        rv.addOnScrollListener(scrolling);

        // Prepare listeners for when we need to refresh
        final SwipeRefreshLayout fresh = findViewById(R.id.swipeContainer);
        fresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            // Clear out old Tweets and get new ones
            @Override
            public void onRefresh() {
                twAdapter.clear();
                newFeed();

                fresh.setRefreshing(false); // Need to ensure that this is false before notify change of tweets to adapter
            }
        });
    }

    // Asynch call the insert methods of tweetDao
    public void save(final List<Tweet> news) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                tweetDao.insertModel(news.toArray(new Tweet[0]));
            }
        });
    }

    private void loadNextPage() {
        twClient.loadNextPage(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray tw_arr = json.jsonArray;
                // Get each JSONObject from the response and pass it to method that turns JSONObject's data to a Tweet object
                List<Tweet> new_tweets = new ArrayList<>();
                for (int i = 0; i < tw_arr.length(); i++) {
                    try {
                        new_tweets.add(Tweet.fromJSONObject(tw_arr.getJSONObject(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                tweets.addAll(new_tweets);
                save(new_tweets);
                // Update the RecyclerView since Tweets were retrieved
                twAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e("Error", "Loading next page of Tweets", throwable);
            }
        }, tweets.get(tweets.size() - 1).getId());
    }

    public void newFeed() {
        twClient.getTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray tw_arr = json.jsonArray;
                // Get each JSONObject from the response and pass it to method that turns JSONObject's data to a Tweet object
                for (int i = 0; i < tw_arr.length(); i++) {
                    try {
                        System.out.println(tw_arr.getJSONObject(i).toString());
                        tweets.add(Tweet.fromJSONObject(tw_arr.getJSONObject(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // Update the RecyclerView since Tweets were retrieved
                save(tweets);
                twAdapter.notifyDataSetChanged();
            }

            // If we failed to connect to internet, fetch the saved db tweets
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        tweets.clear();
                        tweets.addAll(tweetDao.recent());
                        twAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu that includes the "compose new Tweet" icon
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If compose was pressed, go to ComposeTweetActivity
        if (item.getItemId() == R.id.compose) {
            Intent i = new Intent(TimelineActivity.this, ComposeTweetActivity.class);
            i.putExtra("reply", false);
            // NOTE: Put extra??
            startActivityForResult(i, 100);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // If request was successful
        if ((requestCode == 100 || requestCode == 200) && resultCode == RESULT_OK) {
            // Get the new Tweet and add to RecyclerView
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            twAdapter.notifyItemInserted(0);
            rv.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}