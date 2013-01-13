package com.glm.utils.fb;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.facebook.Request;

import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.glm.trainer.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;



public class FacebookConnector {
	private static Facebook oFacebook;
	//private static AsyncFacebookRunner oAsyncRunner;
	private static Context oContext;	
	private static Activity oActivity;
	private static List<String> FACEBOOK_PERMISSION = new ArrayList<String>();
	private static final String sAppID="145289522214142";
	private static String sToken=null;
	private boolean pendingPublishReauthorization = false;
	private static Session activeSession;
	private static GraphUser mUser;
	
	
	private StatusCallback mStatusCallBabk = new StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {			
			if(session.isOpened()){
				Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
					
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if(user!=null){
							mUser=user;
							Log.v(this.getClass().getCanonicalName(),"FacebookConnector->user Facebook "+user.getFirstName());
							
						}
						if(response.getError()!=null){
							Log.e(this.getClass().getCanonicalName(),"FacebookConnector->user Facebook null "
											+response.getError().getErrorCode()+" - "
											+response.getError().getErrorMessage());
						}
					}
				});
			}
			
			if(state==SessionState.CREATED){
					
			}else if(state==SessionState.CREATED_TOKEN_LOADED){
				sToken=session.getAccessToken();
			}else if(state==SessionState.OPENED){
				Log.v(this.getClass().getCanonicalName(),"FacebookConnector->Session Open To Facebook");
				
			}else if(state==SessionState.OPENING){
				Log.v(this.getClass().getCanonicalName(),"FacebookConnector->Session Opening To Facebook");
				
			}else if(state==SessionState.CLOSED){
				Log.v(this.getClass().getCanonicalName(),"FacebookConnector->Session Closed To Facebook");
			}
		}
	};
	public FacebookConnector(Context context, Activity activity){
		oActivity=activity;
		oContext=context;
		
		FACEBOOK_PERMISSION.add("publish_actions");
		
		activeSession = Session.getActiveSession();
		if (activeSession == null) {
			Log.v(this.getClass().getCanonicalName(),"Login To Facebook");
			
	        activeSession = new Session.Builder(oActivity).setApplicationId(sAppID).build();
	        
	        Session.setActiveSession(activeSession);	           
 	        Session.openActiveSession(oActivity, true, mStatusCallBabk);
 	        
	        /*activeSession.open(sToken, new StatusCallback() {
				
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					Log.v(this.getClass().getCanonicalName(),"Session Open");
				}
			});
	        Session.NewPermissionsRequest newPermissionsRequest = new Session
	                    .NewPermissionsRequest(oActivity, FACEBOOK_PERMISSION);
	            activeSession.requestNewPublishPermissions(newPermissionsRequest);*/
	      Log.v(this.getClass().getCanonicalName(),"FacebookConnector->Session null To Facebook");
	    }else{
	    	 if(activeSession.getState().isClosed()) {
	    		 Log.v(this.getClass().getCanonicalName(),"FacebookConnector->Session Close To Facebook");
	    	 }else{
	    		 Session.openActiveSession(oActivity, true, mStatusCallBabk);
	    	 }
	    }
        SessionEvents.addAuthListener(new FacebookAuthListener());
        SessionEvents.addLogoutListener(new FacebookLogoutListener());     
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
		String sMessage="";
		
		activeSession = Session.getActiveSession();
		if(activeSession!=null){
			Log.v(this.getClass().getCanonicalName(),"FacebookConnector->postMessageOnWall ");
			if(activeSession.isClosed()){
				Session.openActiveSession(oActivity, true, mStatusCallBabk);
	 	        Session.setActiveSession(activeSession);
			}
			
			List<String> permissions = activeSession.getPermissions();
	        if (!isSubsetOf(FACEBOOK_PERMISSION, permissions)) {
	            pendingPublishReauthorization = true;
	            Session.NewPermissionsRequest newPermissionsRequest = new Session
	                    .NewPermissionsRequest(oActivity, FACEBOOK_PERMISSION);
	            activeSession.requestNewPublishPermissions(newPermissionsRequest);
	            return;
	        }
	        sMessage=params.getString("message")+" "+params.getString("name")+" "+params.getString("description"); 
	        Request request = Request
                    .newStatusUpdateRequest(Session.getActiveSession(), sMessage, new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            //TODO
                        	Log.v(this.getClass().getCanonicalName(),"Share workout:" + response.getError());
                        }
                    });
            request.executeAsync();
            
		}
	}
	/**
	 * Controlla se ho autorizzato tutto
	 * */
	
	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
	    for (String string : subset) {
	        if (!superset.contains(string)) {
	            return false;
	        }
	    }
	    return true;
	}
	/**
	 * 
	 * Creo il grafico con JFreeChart e lo pubblico come post dopo il save
	 * */
	public void postPhotoOnWall(){
		Bitmap image = BitmapFactory.decodeResource(oContext.getResources(), R.drawable.icon_buy);
        Request request1 = Request.newUploadPhotoRequest(Session.getActiveSession(), image, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
            	Log.v(this.getClass().getCanonicalName(),"Share Photo workout:" + response.getError());
            }
        });
        request1.executeAsync();
	}
    public void logout(){
    	Log.v(this.getClass().getCanonicalName(), "Logout to Facebook ");	
    	activeSession.close();
    	activeSession.closeAndClearTokenInformation();
    }
    public class FacebookAuthListener implements com.glm.utils.fb.SessionEvents.AuthListener {

        public void onAuthSucceed() {
        	Log.i(this.getClass().getCanonicalName(),"You have logged in! ");
        	FacebookConnector.sToken=activeSession.getAccessToken();
        }

        public void onAuthFail(String error) {
        	Log.e(this.getClass().getCanonicalName(),"Login Failed: " + error);
        }
    }

    public class FacebookLogoutListener implements com.glm.utils.fb.SessionEvents.LogoutListener {
        public void onLogoutBegin() {
        	activeSession.close();
        	Log.v(this.getClass().getCanonicalName(),"Logging out...");
        }

        public void onLogoutFinish() {
        	Log.i(this.getClass().getCanonicalName(),"You have logged out! ");            
        }
    }
}
