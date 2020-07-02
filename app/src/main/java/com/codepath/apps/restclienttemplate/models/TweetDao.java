package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TweetDao {
    @Query("select id, date, poster, poster_username, text, image, media_url, likes, retweets, favorited, retweeted, user_id  from Tweet order by date asc limit 20")
    List<Tweet> recent();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);
}
