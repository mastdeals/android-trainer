package com.glm.utils.fb;


import java.io.ByteArrayOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.Facebook.DialogListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

public class FacebookConnector {
	private static Facebook oFacebook;
	private static AsyncFacebookRunner oAsyncRunner;
	private static Context oContext;	
	private static Activity oActivity;
	private static final String[] FACEBOOK_PERMISSION = {"publish_stream","read_stream"};
	private static final String sAppID="145289522214142";
	private static String sToken=null;
	
	public FacebookConnector(Context context, String Token){
		
		oContext=context;
		oFacebook = new Facebook(sAppID);
		
		
		oAsyncRunner = new AsyncFacebookRunner(oFacebook);
				
		if(FacebookConnector.sToken==null){
			oFacebook.setAccessToken(Token);
			Log.v(this.getClass().getCanonicalName(),"Facebook Token Ricevuto:"+Token);
		}else{
			oFacebook.setAccessToken(sToken);
			Log.v(this.getClass().getCanonicalName(),"Facebook Token Settato:"+sToken);
		}
		
		
        SessionStore.restore(oFacebook, oContext);
        SessionEvents.addAuthListener(new SampleAuthListener());
        SessionEvents.addLogoutListener(new SampleLogoutListener());     
	}
	
	public FacebookConnector(Context context, Activity activity){
		oActivity=activity;
		oContext=context;
		oFacebook = new Facebook(sAppID);
		
		
		oAsyncRunner = new AsyncFacebookRunner(oFacebook);
		if(FacebookConnector.sToken==null){
			
			oFacebook.authorize(oActivity, FACEBOOK_PERMISSION,
	                new LoginDialogListener());
		}else{
			oFacebook.setAccessToken(sToken);
		}
		
		
        SessionStore.restore(oFacebook, oContext);
        SessionEvents.addAuthListener(new SampleAuthListener());
        SessionEvents.addLogoutListener(new SampleLogoutListener());     
	}
	
	public FacebookConnector(String appId,
			Context context,
			String[] permissions) {
		oContext=context;
		oFacebook = new Facebook(appId);
		
		
		oAsyncRunner = new AsyncFacebookRunner(oFacebook);

        SessionStore.restore(oFacebook, oContext);
        SessionEvents.addAuthListener(new SampleAuthListener());
        SessionEvents.addLogoutListener(new SampleLogoutListener());        		
	}

	public void setTokenID(String Tokes){
		sToken=Tokes;
	}
	public String getTokenID(){
		return sToken;
	}
	public Facebook getFacebookClient(){
		return oFacebook;
	}
	
	public void postMessageOnWall(Bundle params) {
		if(sToken!=null) oFacebook.setAccessToken(sToken);	
		
		if (oFacebook.isSessionValid()) {			
			oAsyncRunner.request("me", new TrainerRequestListener());               
			oAsyncRunner.request("me/feed", params, "POST",
			        new TrainerUploadListnerFB(), null);
		} else {
			Log.d(this.getClass().getCanonicalName(), "Not Valid Session FB.");
		}
	}
	
	/**
	 * TODO da CONTROLLARE
	 * 
	 * Creo il grafico con JFreeChart e lo pubblico come post dopo il save
	 * */
	public void postPhotoOnWall(){
		byte[] data = null;

		Bitmap bi = BitmapFactory.decodeFile("");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		data = baos.toByteArray();

		Bundle params = new Bundle();
		params.putString("method", "photos.upload");
		params.putByteArray("picture", data);
		if(sToken!=null) oFacebook.setAccessToken(sToken);	
		
		if (oFacebook.isSessionValid()) {	
			oAsyncRunner.request(null, params, "POST", new TrainerUploadListnerFB(), null);
		}				
	}
	/********LISTENER*********/
	public class SampleAuthListener implements com.glm.utils.fb.SessionEvents.AuthListener {

        public void onAuthSucceed() {
        	Log.i(this.getClass().getCanonicalName(),"You have logged in! ");
        	FacebookConnector.sToken=oFacebook.getAccessToken();
        }

        public void onAuthFail(String error) {
        	Log.e(this.getClass().getCanonicalName(),"Login Failed: " + error);
        }
    }

    public class SampleLogoutListener implements com.glm.utils.fb.SessionEvents.LogoutListener {
        public void onLogoutBegin() {
        	Log.v(this.getClass().getCanonicalName(),"Logging out...");
        }

        public void onLogoutFinish() {
        	Log.i(this.getClass().getCanonicalName(),"You have logged out! ");            
        }
    }
    private class LogoutRequestListener extends com.glm.utils.fb.BaseRequestListener {
    	@Override
    	public void onComplete(String response, final Object state) {
    		Log.i(this.getClass().getCanonicalName(),"You have logged out! ");         
    		SessionEvents.onLogoutFinish();
        }

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			
		}
    }
    public class TrainerRequestListener extends com.glm.utils.fb.BaseRequestListener {

		@Override
		public void onComplete(String response, Object state) {
			Log.d(this.getClass().getCanonicalName(), "TrainerRequestListener->Response: " + response.toString());
		}
		
    }

    public class TrainerUploadListnerFB extends com.glm.utils.fb.BaseRequestListener {

        public void onComplete(final String response, final Object state) {
            try {
                // process the response here: (executed in background thread)
                Log.d(this.getClass().getCanonicalName(), "Response: " + response.toString());
                JSONObject json = Util.parseJson(response);
                final String src = json.getString("src");

                Log.v(this.getClass().getCanonicalName(),"Response Upload: "+src);
            } catch (JSONException e) {
                Log.w(this.getClass().getCanonicalName(), "JSON Error in response");
            } catch (FacebookError e) {
                Log.w(this.getClass().getCanonicalName(), "Facebook Error: " + e.getMessage());
            }
        }		
    }
    public class WallPostRequestListener extends com.glm.utils.fb.BaseRequestListener {

		@Override
		public void onComplete(String response, Object state) {
			Log.d(this.getClass().getCanonicalName(), "WallPostRequestListener->Response: " + response.toString());			
		}
    	
    }

    public class WallPostDeleteListener extends com.glm.utils.fb.BaseRequestListener {

		@Override
		public void onComplete(String response, Object state) {
			Log.d(this.getClass().getCanonicalName(), "WallPostDeleteListener->Response: " + response.toString());			
		}
    	
    }
    
    private final class LoginDialogListener implements DialogListener {
        public void onComplete(Bundle values) {
        	Log.v(this.getClass().getCanonicalName(),"Token: "+oFacebook.getAccessToken());
        	
            SessionEvents.onLoginSuccess();
        }

        public void onFacebookError(FacebookError error) {
            SessionEvents.onLoginError(error.getMessage());
        }
        
        public void onError(DialogError error) {
            SessionEvents.onLoginError(error.getMessage());
        }

        public void onCancel() {
            SessionEvents.onLoginError("Action Canceled");
        }
    }
}
