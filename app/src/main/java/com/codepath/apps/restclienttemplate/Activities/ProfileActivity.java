package com.codepath.apps.restclienttemplate.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.Adapters.FriendAdapter;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ProfileActivity extends AppCompatActivity {

    TwitterClient twClient;
    RecyclerView rvFollowing;
    RecyclerView rvFollowers;
    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final List<Friend> following = new ArrayList<>();
        final List<Friend> followers = new ArrayList<>();

        // Get both RecyclerViews
        rvFollowing = findViewById(R.id.following);
        rvFollowers = findViewById(R.id.followers);

        twClient = TwitterApp.getRestClient(this);

        // Get values passed from TimelineActivity for this user
        String username = getIntent().getStringExtra("username");
        user_id = getIntent().getIntExtra("user_id", 0);

        // Set the current User we are viewing's name
        ((TextView) findViewById(R.id.friend_username)).setText("" + username);

        // This is what happens if we click on a follower or following
        FriendAdapter.OnClickUser ocu = new FriendAdapter.OnClickUser() {
            @Override
            public void onClick(Friend friend) {
                // Go to their ProfileActivity
                Intent i = new Intent(ProfileActivity.this, ProfileActivity.class);
                i.putExtra("username", friend.getName());
                i.putExtra("user_id", friend.getUser_id());
                startActivity(i);
            }
        };

        // Set up two adapters
        FriendAdapter followingAdapter = new FriendAdapter(ProfileActivity.this, following, ocu);
        FriendAdapter followersAdapter = new FriendAdapter(ProfileActivity.this, followers, ocu);
        rvFollowing.setAdapter(followingAdapter);
        rvFollowers.setAdapter(followersAdapter);

        rvFollowing.setLayoutManager(new LinearLayoutManager(this));
        rvFollowers.setLayoutManager(new LinearLayoutManager(this));

        // Get the data we need for both
        getPeople(following, true, followingAdapter);
        getPeople(followers, false, followersAdapter);


    }

    public void getPeople(final List<Friend> toAddTo, boolean getFollowing, final FriendAdapter adapter) {
        // Perform the search and update into the RecyclerView
        twClient.getFriends(user_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray users = json.jsonObject.getJSONArray("users");
                    for (int i = 0; i < users.length(); i++) {
                        // Populate the ArrayLists and notify Adapters
                        JSONObject friend = users.getJSONObject(i);
                        Friend curr = new Friend();
                        curr.name = friend.getString("name");
                        curr.username = friend.getString("screen_name");
                        curr.user_id = friend.getInt("id");
                        toAddTo.add(curr);
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e("Error", "Error retrieving people list", throwable);
            }
            // getFollowing triggers message being sent to a different endpoint
        }, getFollowing);
    }

    public class Friend {
        String name;
        String username;
        int user_id;

        public String getName() {
            return name;
        }

        public String getUsername() {
            return username;
        }

        public int getUser_id() {
            return user_id;
        }
    }
}