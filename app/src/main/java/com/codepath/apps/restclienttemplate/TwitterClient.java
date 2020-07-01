package com.codepath.apps.restclienttemplate;

import android.content.Context;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); // NOTE: WHAT IS THIS

	public static final String REST_URL = "https://api.twitter.com/1.1/";
	public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;
	public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET;

	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html"; // NOTE: UNCLEAR

	//NOTE: Why intent? How does this work?
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) { //WHERE IS THE TOKEN/KEY STORED??
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				null,  // OAuth2 scope, null for OAuth1
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	public void getTimeline(JsonHttpResponseHandler handler) {
		//Retrieve JSONArray from API to then convert into the List of Tweets
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

	public void tweet(String tweet_text, JsonHttpResponseHandler handler) {
		//Retrieve JSONArray from API to then convert into the List of Tweets
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", tweet_text);
		client.post(apiUrl, params, "", handler);
	}

	//Send a create/destroy request into the correct endpoint to like/unlike a tweet
	public void like(long id, JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("favorites/create.json?id=" + id);
		client.post(apiUrl, handler);
	}
	public void unlike(long id, JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("favorites/destroy.json?id=" + id);
		client.post(apiUrl, handler);
	}

	//The calling class specifies a handler, to let the user know retweeting was successful
	//Send a POST request to retweet/unretweet a tweet
	public void retweet(long id, JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/retweet/" + id + ".json");
		client.post(apiUrl, handler);
	}
	public void unretweet(long id, JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/unretweet/" + id + ".json");
		client.post(apiUrl, handler);
	}
}
