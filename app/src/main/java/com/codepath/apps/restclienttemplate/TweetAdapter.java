package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    List<Tweet> tweets;
    Context context;
    TwitterClient twClient;

    JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
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
            notifyDataSetChanged();
        }

        @Override
        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
            System.out.println("ERROR GETTING TIMELINE");
        }
    };


    public TweetAdapter(Context context, List<Tweet> tweets, TwitterClient twClient){
        this.context = context;
        this.tweets = tweets;
        this.twClient = twClient;
    }
    @NonNull
    @Override
    public TweetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View to_make = LayoutInflater.from(context).inflate(R.layout.tweet, parent, false);
        return new ViewHolder(to_make);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(tweets.get(position));
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView poster;
        TextView poster_username;
        TextView text;
        TextView likes;
        TextView retweets;
        ImageView profile_pic;
        ImageView content;
        ImageView like;
        ImageView retweet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.poster = itemView.findViewById(R.id.poster);
            this.poster_username = itemView.findViewById(R.id.poster_username);
            this.text = itemView.findViewById(R.id.tweet_text);
            this.likes = itemView.findViewById(R.id.likes);
            this.retweets = itemView.findViewById(R.id.retweets);
            this.profile_pic = itemView.findViewById(R.id.profile_pic);
            this.content = itemView.findViewById(R.id.picture);
            this.like = itemView.findViewById(R.id.like);
            this.retweet = itemView.findViewById(R.id.retweet);
        }
        public void bind(final Tweet tweet){
            like.setImageResource(tweet.isFavorited() ? R.drawable.like_1 : R.drawable.like_0);
            retweet.setImageResource(tweet.isRetweeted() ? R.drawable.retweet_1 : R.drawable.retweet_0);
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tweet.isFavorited()){
                        twClient.unlike(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                twClient.getTimeline(handler);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                        tweet.setFavorited(false);
                        like.setImageResource(R.drawable.like_0);
                        tweet.setLikes(tweet.getLikes()-1);
                    }
                    else{
                        twClient.like(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                twClient.getTimeline(handler);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                        tweet.setFavorited(true);
                        like.setImageResource(R.drawable.like_1);
                        tweet.setLikes(tweet.getLikes()+1);
                    }
                    likes.setText("" + tweet.getLikes());
                }
            });
            retweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tweet.isRetweeted()){
                        twClient.unretweet(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Toast.makeText(context, "Retweet deleted!", Toast.LENGTH_SHORT).show();
                                retweet.setImageResource(R.drawable.retweet_0);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                        tweet.setRetweeted(false);
                        tweet.setRetweets(tweet.getRetweets()+1);
                    }
                    else{
                        twClient.retweet(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Toast.makeText(context, "Retweeted!", Toast.LENGTH_SHORT).show();
                                tweets = new ArrayList<>();
                                twClient.getTimeline(handler);
                                retweet.setImageResource(R.drawable.retweet_1);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                        tweet.setRetweeted(true);
                        tweet.setRetweets(tweet.getRetweets()+1);
                    }
                }
            });


            poster.setText(tweet.getPoster());
            poster_username.setText(tweet.getPoster_username());
            text.setText(tweet.getText());
            likes.setText("" + tweet.getLikes());
            retweets.setText("" + tweet.getRetweets());
            Glide.with(context).load(tweet.getImage()).into(profile_pic);
            if(!tweet.getMedia_url().equals("")) {
                Glide.with(context).load(tweet.getMedia_url()).into(content);
                content.setVisibility(View.VISIBLE);
            }
        }
    }
}
