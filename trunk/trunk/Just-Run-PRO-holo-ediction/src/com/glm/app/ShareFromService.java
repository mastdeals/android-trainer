package com.glm.app;

import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import com.glm.bean.ConfigTrainer;
import com.glm.bean.ExerciseManipulate;
import com.glm.bean.NewExercise;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;
import com.glm.utils.fb.FacebookConnector;
import com.glm.utils.tw.Const;
import com.glm.utils.tw.TwitterHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;
/***
 * classe per la condivizione 
 * 
 * */
public class ShareFromService extends Activity {
	 /**Continene tutte le configurazioni del Trainer**/
    private ConfigTrainer oConfigTrainer;
    /***oggetto condivisione FB*/
	private FacebookConnector oFB = null;
	/***Oggetto Twitter per i post interattivi*/
	private TwitterHelper oTwitter;
	private String sDistance="0";
	private String sTime="";
	private String sPace="0";
	private String sSong="";
	private boolean isFaceBookShareActive=false;
	private boolean isTwitterShareActive=false;
	private static SharedPreferences mSharedPreferences;
	private Timer tTimerToFinish;
	private ProgressBar oPbar;
	public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        oPbar = new ProgressBar(getApplicationContext());
        
        //setContentView(oPbar);
        mSharedPreferences = getSharedPreferences(Const.PREFERENCE_NAME, Context.MODE_PRIVATE);
        
        if(extras !=null){
     	   sDistance 			 = extras.getString("distance");
     	   sTime 				 = extras.getString("time");
     	   sPace 				 = extras.getString("pace"); 
     	   isFaceBookShareActive = extras.getBoolean("isFaceBookShareActive");
     	   isTwitterShareActive	 = extras.getBoolean("isTwitterShareActive");
     	   sSong				 = extras.getString("song"); 
        }
        
        Toast.makeText(getBaseContext(), "SHARE", Toast.LENGTH_LONG)
		.show();       
        shareWorkout();							
        tTimerToFinish = new Timer();
        tTimerToFinish.schedule(
    			new TimerTask()
    			{
    				@Override
    				public void run(){		    							    					
    					finish();	    							   							
    				}
    			}, 
    			5000);
        
    }
	/**
	 * condivido su FaceBook o Twitter
	 * 
	 * */
	private void shareWorkout() {
		
		if(isFaceBookShareActive)
			facebookShare();
		
		if(isTwitterShareActive)
			twitterShare();
	}
	/**
	 * Condivisione interattiva via twitter
	 * */
	private void twitterShare() {
		String sMessage="";
		if(sSong!=""){
			sMessage=getString(R.string.distance)+": "+sDistance+" "+getString(R.string.pace)+": "+sPace+" "+getString(R.string.listening)+" "+sSong+"\n "+getString(R.string.app_name_pro);
		}else{
			sMessage=getString(R.string.distance)+": "+sDistance+" "+getString(R.string.pace)+": "+sPace+"\n "+getString(R.string.app_name_pro);
		}
		String oauthAccessToken = mSharedPreferences.getString(Const.PREF_KEY_TOKEN, "");
		String oAuthAccessTokenSecret = mSharedPreferences.getString(Const.PREF_KEY_SECRET, "");
		Log.v(this.getClass().getCanonicalName(),"auth twitter: "+oauthAccessToken+" "+oAuthAccessTokenSecret);
		oTwitter = new TwitterHelper(this,getApplicationContext(),this.getIntent());		
		//oTwitter.tryConnect(getApplicationContext(), getIntent());
		oTwitter.postTwitt(sMessage);
		
	}
	
	/*
	 * Condivide su FaceBook
	 * */
    private void facebookShare() {
		
    	oFB = new FacebookConnector(getApplicationContext(),ShareFromService.this);
        
        
		//String sPace="n.d";
		try{
			NumberFormat oNFormat = NumberFormat.getNumberInstance();
			oNFormat.setMaximumFractionDigits(2);
			//sPace = oNFormat.format(ExerciseManipulate.getWatchPoint().get(ExerciseManipulate.getWatchPoint().size()-1).getdPace());
			Bundle params = new Bundle();
			if(sSong!=""){
				params.putString("message", 
						getString(R.string.distance)+": "+sDistance+" "+getString(R.string.pace)+": "+sPace+" "+getString(R.string.listening)+" "+sSong);
			}else{
				params.putString("message", 
						getString(R.string.distance)+": "+sDistance+" "+getString(R.string.pace)+": "+sPace);	
			}
			
			params.putString("name", getString(R.string.app_name_pro));
			params.putString("description", getString(R.string.app_description));
			params.putString("picture", "https://fbcdn-sphotos-a.akamaihd.net/hphotos-ak-ash3/528739_265227286907418_241853235911490_524034_434724615_n.jpg");
			if(oFB!=null) oFB.postMessageOnWall(params);		
		}catch (Exception e) {
			Log.w(this.getClass().getCanonicalName(), "FB Interactive sharing error:"+e.getMessage());
		}
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Toast.makeText(getBaseContext(), "Activity Result", Toast.LENGTH_LONG)
		.show();
	}
	private void facebookShareEnd() {
		
		ExerciseUtils.populateExerciseDetails(this, oConfigTrainer, 0);
		NumberFormat oNFormat = NumberFormat.getNumberInstance();
		oNFormat.setMaximumFractionDigits(2);
		try{
			String sPace = oNFormat.format(ExerciseManipulate.getWatchPoint().get(ExerciseManipulate.getWatchPoint().size()-1).getdPace());
			Bundle params = new Bundle();
			
			params.putString("message", getString(R.string.time)+": "+sTime+" "+
					ExerciseUtils.getTimeSSdd(NewExercise.getlCurrentTime()) + 
					getString(R.string.distance)+": "+ExerciseManipulate.getsTotalDistance() +" "+
					getString(R.string.pace)+": "+sPace+" "+getString(R.string.kalories)+": "+
					ExerciseManipulate.getsCurrentCalories()+" Kcal");
			params.putString("name", getString(R.string.app_name_pro));
			params.putString("description", getString(R.string.app_description));
			params.putString("picture", "https://lh6.googleusercontent.com/-hud-Azph6-Q/Th7YxoTZGqI/AAAAAAAAGBs/a-wG1jNYeY8/s640/personal-trainer.png");
			if(oFB!=null) oFB.postMessageOnWall(params);
		}catch (Exception e) {
			Log.w(this.getClass().getCanonicalName(), "FB Interactive sharing error:"+e.getMessage());
		}			
	}
}
