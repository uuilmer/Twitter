package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Tweet {
    long id;
    String poster;
    String poster_username;
    String text;
    String image;
    String media_url;
    int likes;
    int retweets;
    boolean favorited;
    boolean retweeted;

    public Tweet(long id, String poster, String poster_username, String text, String image, String media_url, int likes, int retweets, boolean favorited, boolean retweeted){
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
    }
    public static Tweet fromJSONObject(JSONObject obj) throws JSONException {
        System.out.println(obj.toString());
        JSONObject user = obj.getJSONObject("user");
        JSONArray arr = null;
        if(obj.getJSONObject("entities").has("media"))
            arr = obj.getJSONObject("entities").getJSONArray("media");
        return new Tweet(obj.getLong("id"),
                user.getString("name"),
                user.getString("screen_name"),
                obj.getString("text"),
                user.getString("profile_image_url_https"),
                arr != null ? arr.getJSONObject(0).getString("media_url_https") : "",
                obj.getInt("favorite_count"),
                obj.getInt("retweet_count"),
                obj.getBoolean("favorited"),
                obj.getBoolean("retweeted"));
    }

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
}
