package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Parcel
@Entity
public class Tweet {
    @ColumnInfo
    @PrimaryKey
    long id;
    @ColumnInfo
    String date;
    @ColumnInfo
    String poster;
    @ColumnInfo
    String poster_username;
    @ColumnInfo
    String text;
    @ColumnInfo
    String image;
    @ColumnInfo
    String media_url;
    @ColumnInfo
    int likes;
    @ColumnInfo
    int retweets;
    @ColumnInfo
    boolean favorited;
    @ColumnInfo
    boolean retweeted;
    @ColumnInfo
    int user_id;

    // Required empty constructor for Parcel
    public Tweet() {
    }

    public Tweet(long id, int user_id, String poster, String poster_username, String text, String image, String media_url, int likes, int retweets, boolean favorited, boolean retweeted, String date) {
        this.id = id;
        this.poster = poster;
        this.poster_username = poster_username;
        this.text = text;
        this.image = image;
        this.likes = likes;
        this.retweets = retweets;
        this.media_url = media_url;
        this.favorited = favorited;
        this.retweeted = retweeted;
        this.date = getRelativeTime(date);
        this.user_id = user_id;
    }

    //Method to convert Twitter API Tweet date to relative
    public String getRelativeTime(String json_response) {
        //Define the given format
        String format = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(format, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            //Get Unix Epoch and get relative from today, then call toString to get readable difference
            long dateMillis = sf.parse(json_response).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static Tweet fromJSONObject(JSONObject obj) throws JSONException {
        JSONObject user = obj.getJSONObject("user"); // JSONObject containing info about the User who posted the Tweet
        JSONArray arr = null;
        // If there is no picture, the API doesn't send back any field named "media", so we need to check if it is included
        if (obj.getJSONObject("entities").has("media"))
            arr = obj.getJSONObject("entities").getJSONArray("media");
        return new Tweet(obj.getLong("id"),
                user.getInt("id"),
                user.getString("name"),
                user.getString("screen_name"),
                obj.getString("text"),
                user.getString("profile_image_url_https"),

                // Check if we found a field named "media"
                arr != null ? arr.getJSONObject(0).getString("media_url_https") : "",

                obj.getInt("favorite_count"),
                obj.getInt("retweet_count"),
                obj.getBoolean("favorited"),
                obj.getBoolean("retweeted"),
                obj.getString("created_at"));
    }

    public int getUser_id() {
        return user_id;
    }

    // Getters and Setters
    public String getPoster() {
        return poster;
    }

    public String getText() {
        return text;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setRetweets(int retweets) {
        this.retweets = retweets;
    }

    public long getId() {
        return id;
    }

    public int getRetweets() {
        return retweets;
    }

    public String getMedia_url() {
        return media_url;
    }

    public String getImage() {
        return image;
    }

    public String getPoster_username() {
        return poster_username;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public String getDate() {
        return date;
    }
}
