package com.codepath.apps.restclienttemplate.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcel;

import java.util.List;

import okhttp3.Headers;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    List<Tweet> tweets;
    Context context;
    TwitterClient twClient;
    OnClickReply ocr;
    OnClickProfile ocp;

    // This will be used to handle moving to ProfileActivity when username is clicked
    public interface OnClickProfile {
        public void OnClick(Tweet tweet);
    }

    // This will be used to handle moving to ComposeActivity when a movie is clicked
    public interface OnClickReply {
        public void OnClick(Tweet tweet);
    }

    // TweetAdapter needs to have a TwitterClient defined to call its "like", "unlike", ect methods
    public TweetAdapter(Context context, List<Tweet> tweets, TwitterClient twClient, OnClickReply ocr, OnClickProfile ocp) {
        this.context = context;
        this.tweets = tweets;
        this.twClient = twClient;
        this.ocr = ocr;
        this.ocp = ocp;
    }

    // Define layout for each Recycled View
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

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView poster;
        TextView poster_username;
        TextView text;
        TextView likes;
        TextView retweets;
        ImageView profile_pic;
        ImageView content;
        ImageView like;
        ImageView retweet;
        TextView time;
        ImageView reply;

        // Save this Recycled View's child Views to later bind with different Tweets as we scroll
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
            this.time = itemView.findViewById(R.id.time);
            this.reply = itemView.findViewById(R.id.reply);
        }

        // Change this Recycled View's child Views as we scroll
        public void bind(final Tweet tweet) {
            like.setImageResource(tweet.isFavorited() ? R.drawable.like_1 : R.drawable.like_0);
            retweet.setImageResource(tweet.isRetweeted() ? R.drawable.retweet_1 : R.drawable.retweet_0);

            // Set initial "Like" and "Retweet" count
            likes.setText("" + tweet.getLikes());
            retweets.setText("" + tweet.getRetweets());
            time.setText("" + tweet.getDate());

            // Features of Tweet that don't change
            poster.setText(tweet.getPoster()); // User who posted the Tweet...
            poster_username.setText(tweet.getPoster_username()); // ...his username...
            Glide.with(context).load(tweet.getImage()).into(profile_pic); // ...his profile picture
            text.setText(tweet.getText()); // The body of the Tweet

            // Before checking if we should bind a different picture,
            // ensure there if nothing, in case the previous tweet had a picture but this one doesn't
            content.setImageResource(android.R.color.transparent);
            content.setVisibility(View.GONE);

            if (!tweet.getMedia_url().equals("")) { // If the Tweet includes a picture, include it
                Glide.with(context).load(tweet.getMedia_url()).into(content);
                content.setVisibility(View.VISIBLE); // The ImageView for Tweet content is initially GONE, which assumes there is no picture
            }

            // Set like and retweet buttons to "like" and "retweet" the appropriate Tweet
            like.setOnClickListener(new View.OnClickListener() { // "Like" pressed
                @Override
                public void onClick(View view) {
                    if (tweet.isFavorited()) { // If Tweet was already "Liked"...
                        twClient.unlike(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                tweet.setFavorited(false); // ...undo "Like"
                                like.setImageResource(R.drawable.like_0); // Change icon to unliked
                                tweet.setLikes(tweet.getLikes() - 1); // Reduce "Like" count
                                likes.setText("" + tweet.getLikes()); // Update "Like" count displaying View
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("Error", "Error Unliking tweet", throwable);
                            }
                        });
                    } else { // Case when "Disliked"
                        twClient.like(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                tweet.setFavorited(true);
                                like.setImageResource(R.drawable.like_1);
                                tweet.setLikes(tweet.getLikes() + 1);
                                likes.setText("" + tweet.getLikes()); // Update "Like" count displaying View
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("Error", "Error Liking tweet", throwable);
                            }
                        });
                    }
                }
            });
            retweet.setOnClickListener(new View.OnClickListener() {
                // Same as before but for retweets
                @Override
                public void onClick(View view) { // Same as Likes but for Retweets
                    if (tweet.isRetweeted()) {
                        twClient.unretweet(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Toast.makeText(context, "Retweet deleted!", Toast.LENGTH_SHORT).show();
                                retweet.setImageResource(R.drawable.retweet_0);
                                tweet.setRetweeted(false);
                                tweet.setRetweets(tweet.getRetweets() - 1);
                                retweets.setText("" + tweet.getRetweets());
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("Error", "Error Unretweeting", throwable);
                            }
                        });
                    } else {
                        twClient.retweet(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Toast.makeText(context, "Retweeted!", Toast.LENGTH_SHORT).show();
                                retweet.setImageResource(R.drawable.retweet_1);
                                tweet.setRetweeted(true);
                                tweet.setRetweets(tweet.getRetweets() + 1);
                                retweets.setText("" + tweet.getRetweets());
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("Error", "Error Retweeting", throwable);
                            }
                        });
                    }
                }
            });
            //The reply button sends user to Compose activity with the tweet that they wish to reply to
            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ocr.OnClick(tweet);
                }
            });

            poster_username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ocp.OnClick(tweet);
                }
            });

        }
    }
}
