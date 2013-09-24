package com.glm.utils.tw;

import com.glm.app.TwitterAuthActivity;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;

public class TwitterHelper {
	private static Twitter twitter;
	private static RequestToken requestToken;
	private static SharedPreferences mSharedPreferences;

	private Context oContext;
	private Intent oIntent;
	private Activity oActivity;
	public TwitterHelper(Activity mActivity,Context context,Intent intent) {
		oActivity=mActivity;
		oContext = context;	
		oIntent  = intent;
		mSharedPreferences = oContext.getSharedPreferences(Const.PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	public boolean tryConnect() {
			
		
		
		/**
		 * Handle OAuth Callback
		 */
		Uri uri = oIntent.getData();
		if (uri != null && uri.toString().startsWith(Const.CALLBACK_URL)) {
			String verifier = uri.getQueryParameter(Const.IEXTRA_OAUTH_VERIFIER);
			try {
				AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
				Editor e = mSharedPreferences.edit();
				e.putString(Const.PREF_KEY_TOKEN, accessToken.getToken());
				e.putString(Const.PREF_KEY_SECRET, accessToken.getTokenSecret());
				e.commit();
				Log.v(this.getClass().getCanonicalName(),"tryConnect-> Twitter KEY:"+accessToken.getToken()+" SEGRET"+accessToken.getTokenSecret());
				return true;
			} catch (Exception e) {
				Log.e(this.getClass().getCanonicalName(), e.getMessage());
				return false;
			}
		}else{
			Log.v(this.getClass().getCanonicalName(),"tryConnect-> Twitter KEY no");
			SharedPreferences mSharedPreferences = oContext.getSharedPreferences(Const.PREFERENCE_NAME, Context.MODE_PRIVATE);
		    String oauthAccessToken = mSharedPreferences.getString(Const.PREF_KEY_TOKEN, "");
			String oAuthAccessTokenSecret = mSharedPreferences.getString(Const.PREF_KEY_SECRET, "");
				
			Log.v(this.getClass().getCanonicalName(),"oauthAccessToken: "+oauthAccessToken+" oAuthAccessTokenSecret:"+oAuthAccessTokenSecret);
			if(mSharedPreferences.getString(Const.PREF_KEY_TOKEN, "").length()==0){
				askOAuth();
			}
			//checkConnected();
			return false;
		}
		
	}

	/*private void checkConnected() {
		
		if (isConnected()) {
			String oauthAccessToken = mSharedPreferences.getString(Const.PREF_KEY_TOKEN, "");
			String oAuthAccessTokenSecret = mSharedPreferences.getString(Const.PREF_KEY_SECRET, "");

			ConfigurationBuilder confbuilder = new ConfigurationBuilder();
			Configuration conf = confbuilder
					.setOAuthConsumerKey(Const.CONSUMER_KEY)
					.setOAuthConsumerSecret(Const.CONSUMER_SECRET)
					.setOAuthAccessToken(oauthAccessToken)
					.setOAuthAccessTokenSecret(oAuthAccessTokenSecret)
					.build();
			//twitterStream = new TwitterStreamFactory(conf).getInstance();
			Log.v(this.getClass().getCanonicalName(),"checkConnected-> Twitter KEY: true");			
		}else{
			Log.v(this.getClass().getCanonicalName(),"checkConnected-> Twitter KEY: false");	
		}
	}*/
	/**
	 * Posta in Twitt
	 * */
	public boolean postTwitt(String sMessage){
		try {
			
			if(twitter!=null){
				try{					
					AccessToken a = new AccessToken(mSharedPreferences.getString(Const.PREF_KEY_TOKEN, ""), mSharedPreferences.getString(Const.PREF_KEY_SECRET, ""));
					twitter.setOAuthAccessToken(a);
					Status oStatus = twitter.updateStatus(sMessage);
					Log.i(this.getClass().getCanonicalName(),"Post Twitter OK");
					return oStatus.isRetweet();
				}catch(IllegalArgumentException e){
					Log.e(this.getClass().getCanonicalName(),"Post Twitter error: "+mSharedPreferences.getString(Const.PREF_KEY_TOKEN, "")+" "+mSharedPreferences.getString(Const.PREF_KEY_SECRET, ""));
					return false;
				}												
			}else{		
				String oauthAccessToken = mSharedPreferences.getString(Const.PREF_KEY_TOKEN, "");
				String oAuthAccessTokenSecret = mSharedPreferences.getString(Const.PREF_KEY_SECRET, "");

				ConfigurationBuilder confbuilder = new ConfigurationBuilder();
				Configuration configuration = confbuilder
						.setOAuthConsumerKey(Const.CONSUMER_KEY)
						.setOAuthConsumerSecret(Const.CONSUMER_SECRET)
						.setOAuthAccessToken(oauthAccessToken)
						.setOAuthAccessTokenSecret(oAuthAccessTokenSecret)
						.build();
				twitter = new TwitterFactory(configuration).getInstance();
				
				Status oStatus = twitter.updateStatus(sMessage);
				Log.i(this.getClass().getCanonicalName(),"Post Twitter OK");
				return oStatus.isRetweet();
			}
		} catch (TwitterException e) {			
			Log.e(this.getClass().getCanonicalName(),e.getMessage());			
		}catch (NullPointerException e) {
			Log.e(this.getClass().getCanonicalName(),e.getMessage());
		}
		return false;
	}
	public String getAuthenticationURL(){
		return requestToken.getAuthenticationURL();
	}
	/**
	 * check if the account is authorized
	 * @return
	 */
	private boolean isConnected() {
		return mSharedPreferences.getString(Const.PREF_KEY_TOKEN, null) != null;
	}

	private void askOAuth() {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(Const.CONSUMER_KEY);
		configurationBuilder.setOAuthConsumerSecret(Const.CONSUMER_SECRET);
		Configuration configuration = configurationBuilder.build();
		twitter = new TwitterFactory(configuration).getInstance();

		try {
			requestToken = twitter.getOAuthRequestToken(Const.CALLBACK_URL);
			
			//Intent dialogIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));			
			//oActivity.startActivity(dialogIntent);
			//oActivity.startActivityForResult(dialogIntent,0);
			Intent intent = new Intent();
			intent.setClass(oContext, TwitterAuthActivity.class);
			intent.putExtra("auth_twitter_url", requestToken.getAuthenticationURL());
			
			//oActivity.startActivity(intent);
			oActivity.startActivityForResult(intent, 0);
			Log.v(this.getClass().getCanonicalName(),"askOAuth-> After:"+requestToken.getAuthenticationURL());	
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove Token, Secret from preferences
	 */
	private void disconnectTwitter() {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.remove(Const.PREF_KEY_TOKEN);
		editor.remove(Const.PREF_KEY_SECRET);

		editor.commit();
	}


	public void connectToTwitter() {
		
			if (isConnected()) {
				disconnectTwitter();
				
			} else {
				askOAuth();
			}			
	}
}