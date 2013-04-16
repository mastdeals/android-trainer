package com.glm.app;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import com.glm.bean.ConfigTrainer;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;
import com.glm.utils.tw.Const;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class TwitterAuthActivity extends Activity{
	/**Twitter Integration*/
	private Twitter oTwitter;
	private RequestToken mReqToken;
	/** Name to store the users access token */
    private static final String PREF_ACCESS_TOKEN = "accessToken";
	 /** Preferences to store a logged in users credentials */
    private SharedPreferences mPrefs;
    
	private ConfigTrainer oConfigTrainer;
	private String sURI;
	private WebView oWeb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.twitter_auth);
		
		oWeb = (WebView) findViewById(R.id.TwitterWeb);
	}
	//@Override
	protected void onResume() {
		super.onResume();
		//TwitterTask task = new TwitterTask();
		//task.execute(null);
		oConfigTrainer=ExerciseUtils.loadConfiguration(getApplicationContext(),true);
		mPrefs = getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE);
	    oTwitter = new TwitterFactory().getInstance();
		oTwitter.setOAuthConsumer(Const.CONSUMER_KEY, Const.CONSUMER_SECRET);
		if (mPrefs.contains(PREF_ACCESS_TOKEN)) {
            Log.i(this.getClass().getCanonicalName(), "Repeat User");
            loginAuthorisedUser();
	    } else {
	        Log.i(this.getClass().getCanonicalName(), "New User");
	        loginNewUser();
	    }	
	}
	
	private void loginNewUser() {
		try {
            Log.i(this.getClass().getCanonicalName(), "Request App Authentication");
            mReqToken = oTwitter.getOAuthRequestToken(Const.CALLBACK_URL);

            Log.i(this.getClass().getCanonicalName(), "Starting Webview to login to twitter");
          
            oWeb.loadUrl(mReqToken.getAuthenticationURL());
            oWeb.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url){
                    // do your handling codes here, which url is the requested url
                    // probably you need to open that url rather than redirect:
                    view.loadUrl(url);
                    Log.i(this.getClass().getCanonicalName(), "Webview url: "+url);
                    Uri uri = Uri.parse(url);
                    if (uri != null && uri.toString().startsWith(Const.CALLBACK_URL)) { // If the user has just logged in                                           	
                    	String oauthVerifier = uri.getQueryParameter("oauth_verifier");
                    	                    
                        authoriseNewUser(oauthVerifier);
                        
                        try{
                 			ActivityHelper.startOriginalActivityAndFinish(TwitterAuthActivity.this);			
                 		}catch (NullPointerException e) {
                 			//ActivityHelper.startOriginalActivityAndFinish(getParent());		
                 			Intent intentMain = ActivityHelper.createActivityIntent(TwitterAuthActivity.this,MainTrainerActivity.class);
                 			//startActivity(intent);
                 			ActivityHelper.startNewActivityAndFinish(TwitterAuthActivity.this, intentMain);	
                 			//Log.e(this.getClass().getCanonicalName(),"Error back");
                 		}
                    }
                    return false; // then it is not handled by default action
               }
            });
          
        } catch (TwitterException e) {
            try{
            	Toast.makeText(this, "Twitter Login error, try again later", Toast.LENGTH_LONG).show();
    			ActivityHelper.startOriginalActivityAndFinish(this);			
    		}catch (NullPointerException e1) {
    			//ActivityHelper.startOriginalActivityAndFinish(getParent());		
    			Intent intentMain = ActivityHelper.createActivityIntent(this,MainTrainerActivity.class);
    			//startActivity(intent);
    			ActivityHelper.startNewActivityAndFinish(this, intentMain);	
    			//Log.e(this.getClass().getCanonicalName(),"Error back");
    		}catch (RuntimeException e2) {
				Log.e(this.getClass().getCanonicalName(),"Runtime error twitter");
			}
        }
	}
	  /**
     * The user had previously given our app permission to use Twitter</br>
     * Therefore we retrieve these credentials and fill out the Twitter4j helper
     */
    private void loginAuthorisedUser() {
            String token = mPrefs.getString(Const.PREF_KEY_TOKEN, null);
            String secret = mPrefs.getString(Const.PREF_KEY_SECRET, null);

            // Create the twitter access token from the credentials we got previously
            AccessToken at = new AccessToken(token, secret);

            oTwitter.setOAuthAccessToken(at);

            Toast.makeText(this, "Welcome back Twitter", Toast.LENGTH_SHORT).show();

            
    }
    /**
     * Catch when Twitter redirects back to our {@link CALLBACK_URL}</br>
     * We use onNewIntent as in our manifest we have singleInstance="true" if we did not the
     * getOAuthAccessToken() call would fail
     */
    @Override
    protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);
            Log.i(this.getClass().getCanonicalName(), "New Intent Arrived");
            dealWithTwitterResponse(intent);
            
            try{
    			ActivityHelper.startOriginalActivityAndFinish(this);			
    		}catch (NullPointerException e) {
    			//ActivityHelper.startOriginalActivityAndFinish(getParent());		
    			Intent intentMain = ActivityHelper.createActivityIntent(this,MainTrainerActivity.class);
    			//startActivity(intent);
    			ActivityHelper.startNewActivityAndFinish(this, intentMain);	
    			//Log.e(this.getClass().getCanonicalName(),"Error back");
    		}
    }
    /**
     * Twitter has sent us back into our app</br>
     * Within the intent it set back we have a 'key' we can use to authenticate the user
     *
     * @param intent
     */
    private void dealWithTwitterResponse(Intent intent) {
            Uri uri = intent.getData();
            if (uri != null && uri.toString().startsWith(Const.CALLBACK_URL)) { // If the user has just logged in
                    String oauthVerifier = uri.getQueryParameter("oauth_verifier");

                    authoriseNewUser(oauthVerifier);
            }
    }
    /**
     * Create an access token for this new user</br>
     * Fill out the Twitter4j helper</br>
     * And save these credentials so we can log the user straight in next time
     *
     * @param oauthVerifier
     */
    private void authoriseNewUser(String oauthVerifier) {
            try {
                    AccessToken at = oTwitter.getOAuthAccessToken(mReqToken, oauthVerifier);
                    oTwitter.setOAuthAccessToken(at);

                    saveAccessToken(at);

                    // Set the content view back after we changed to a webview
                    setContentView(R.layout.new_user_details);
                   
            } catch (TwitterException e) {
                Log.e(this.getClass().getCanonicalName(),"Twitter auth error");
            } catch (IllegalStateException e) {
           	 Log.e(this.getClass().getCanonicalName(),"Twitter IllegalStateException error");
			 } catch (NullPointerException e) {
           	 Log.e(this.getClass().getCanonicalName(),"Twitter NullPointerException error");
			 }
    }
    private void saveAccessToken(AccessToken at) {
        String token = at.getToken();
        String secret = at.getTokenSecret();
        Editor editor = mPrefs.edit();
        editor.putString(Const.PREF_KEY_TOKEN, token);
        editor.putString(Const.PREF_KEY_SECRET, secret);
        editor.commit();
    }
    
    private class TwitterTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPostExecute(Boolean result) {
			if (mPrefs.contains(PREF_ACCESS_TOKEN)) {
	            Log.i(this.getClass().getCanonicalName(), "Repeat User");
	            loginAuthorisedUser();
		    } else {
		        Log.i(this.getClass().getCanonicalName(), "New User");
		        loginNewUser();
		    }
		}
			
		@Override
		protected Boolean doInBackground(Void... mDB) {
			oConfigTrainer=ExerciseUtils.loadConfiguration(getApplicationContext(),true);
			mPrefs = getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE);
		    oTwitter = new TwitterFactory().getInstance();
			oTwitter.setOAuthConsumer(Const.CONSUMER_KEY, Const.CONSUMER_SECRET);
				
			return true;
		}
	}
}
