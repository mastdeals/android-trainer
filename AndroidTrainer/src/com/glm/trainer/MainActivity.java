package com.glm.trainer;


import com.glm.app.ActivityHelper;
import com.glm.app.ChangeLogActivity;

import com.glm.app.MainTrainerActivity;

import com.glm.app.UserDetailsActivity;
import com.glm.app.db.Database;

import com.glm.app.stopwatch.WorkOutActivity;
import com.glm.bean.ConfigTrainer;

import com.glm.services.ExerciseService;
import com.glm.services.IExerciseService;
import com.glm.trainer.MainActivity;

import com.glm.utils.ExerciseUtils;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class MainActivity  extends Activity{
	/**Oggetto connessione al servizio*/
	private TrainerServiceConnection mConnection = new TrainerServiceConnection();
	private ConfigTrainer oConfigTrainer;
	private IExerciseService mIService;

    /** Preferences to store a logged in users credentials */
    private SharedPreferences mPrefs;

 	private String sVersionPackage="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.main);	     
	       
	       DBTask task = new DBTask();
		   task.execute(new Database(this));
	}
	
	public void onResume(Bundle savedInstanceState) {
		setContentView(R.layout.main);		
	}
	@Override
	protected void onPause() {
		//doUnbindService();
		super.onPause();
	}
	
	boolean mIsBound;
	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because there is no reason to be able to let other
	    // applications replace our component.
		//Intent bindIntent = new Intent(Main.this, MessengerService.class); 
        //bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);                 
		mIsBound = getApplicationContext().bindService(new Intent(MainActivity.this, 
	            ExerciseService.class), mConnection, Context.BIND_AUTO_CREATE);
		
		Log.i(this.getClass().getCanonicalName(), "Binding from Services");
	}
	void doUnbindService() {
	    if (mIsBound) {
	        // If we have received the service, and hence registered with
	       
	        Log.i(this.getClass().getCanonicalName(), "UnBinding from Services");

	        // Detach our existing connection.
	        getApplicationContext().unbindService(mConnection);
	        mIsBound = false;	       
	    }
	}
	/**
	 * Classe Connection che stabilisce il bind col servizio
	 * 
	 * @author gianluca masci aka (GLM)
	 * 
	 * **/
	public class TrainerServiceConnection implements ServiceConnection 
	{ 
		
	        protected ExerciseService oTrainerService = null; 
	        @Override 
	        public void onServiceConnected(ComponentName name, IBinder service) 
	        { 
	                try{
	                	mIService= IExerciseService.Stub.asInterface(service);
	                	
	            		
            			if(mIService.isServiceAlive() && 
            					mIService.isRunning()){
            				//Toast.makeText(MainTrainerActivity.this, "First type: "+mIService.getiTypeExercise(),
        	    	        //        Toast.LENGTH_LONG).show();
            				Intent intent=null;
            				if(mIService.getiTypeExercise()==0){
            					//RUN
                				intent = ActivityHelper.createActivityIntent(MainActivity.this,WorkOutActivity.class);	
            				}else if (mIService.getiTypeExercise()==100){
            					//WALK
                				intent = ActivityHelper.createActivityIntent(MainActivity.this,WorkOutActivity.class);
            				}else if (mIService.getiTypeExercise()==1){
            					//BIKE 
                				intent = ActivityHelper.createActivityIntent(MainActivity.this,WorkOutActivity.class);
            				}     				
            				//startActivity(intent);
            				ActivityHelper.startNewActivityAndFinish(MainActivity.this, intent);
            			}else if(mIService.isServiceAlive() && 
            						!mIService.isRunning()){  
            				//Toast.makeText(MainTrainerActivity.this, "Second type: "+mIService.getiTypeExercise(),
        	    	        //        Toast.LENGTH_LONG).show();
            				if(mIService.isAutoPause()){
            					Intent intent=null; 
            					if(mIService.getiTypeExercise()==0){
                					//RUN            						
                    				intent = ActivityHelper.createActivityIntent(MainActivity.this,WorkOutActivity.class);	
                    				intent.putExtra("type", 0);
                				}else if (mIService.getiTypeExercise()==100){
                					//WALK
                    				intent = ActivityHelper.createActivityIntent(MainActivity.this,WorkOutActivity.class);
                    				intent.putExtra("type", 100);
                				}else if (mIService.getiTypeExercise()==1){
                					//BIKE 
                    				intent = ActivityHelper.createActivityIntent(MainActivity.this,WorkOutActivity.class);
                    				intent.putExtra("type", 1);
                				}
            					ActivityHelper.startNewActivityAndFinish(MainActivity.this, intent);
            				}else if (mIService.isPause()){
            					Intent intent=null; 
            					if(mIService.getiTypeExercise()==0){
                					//RUN
                    				intent = ActivityHelper.createActivityIntent(MainActivity.this,WorkOutActivity.class);	
                    				intent.putExtra("type", 0);
                				}else if (mIService.getiTypeExercise()==100){
                					//WALK
                    				intent = ActivityHelper.createActivityIntent(MainActivity.this,WorkOutActivity.class);
                    				intent.putExtra("type", 100);
                				}else if (mIService.getiTypeExercise()==1){
                					//BIKE 
                    				intent = ActivityHelper.createActivityIntent(MainActivity.this,WorkOutActivity.class);
                    				intent.putExtra("type", 1);
                				}
            					intent.putExtra("Status", "service_under_user_pause");
            					ActivityHelper.startNewActivityAndFinish(MainActivity.this, intent);
            				}else{
            					doUnbindService();
            					if(!ExerciseUtils.isUserExist(getApplicationContext())){
                    				Intent intent = ActivityHelper.createActivityIntent(MainActivity.this,UserDetailsActivity.class);            				
                    				ActivityHelper.startNewActivityAndFinish(MainActivity.this, intent);	
            			    	}else{
            			    		Intent intent=ActivityHelper.createActivityIntent(MainActivity.this,MainTrainerActivity.class);            				
            			    		ActivityHelper.startNewActivityAndFinish(MainActivity.this, intent);
            			    	}
            				}
            				   				
            			}
            			
	                }catch (Exception e) {
	                	Log.e(this.getClass().getCanonicalName(), "onServiceConnected->Remote Exception"+e.getMessage());
	                	e.printStackTrace();
					}	                
	        } 
	        @Override 
	        public void onServiceDisconnected(ComponentName name) 
	        { 
	        	
	               /* Toast.makeText(StopwatchActivity.this, "TrainerServiceConnection->onServiceDisconnected"+R.string.pause_exercise,
	    	                Toast.LENGTH_LONG).show();*/
	        } 
	}     

	
	private class DBTask extends AsyncTask<Database, Void, Boolean> {
		@Override
		protected void onPostExecute(Boolean result) {					       
		       oConfigTrainer=ExerciseUtils.loadConfiguration(getApplicationContext());		       		        							       
				try{					   				    		       			     	
			    	try {
			    		
			    	    PackageInfo manager=getPackageManager().getPackageInfo(getPackageName(), 0);
			    	    sVersionPackage = manager.versionName;
			    	    Log.i(this.getClass().getCanonicalName(),"Pacchetto Versione"+sVersionPackage);
			    	} catch (NameNotFoundException e) {
			    	   Log.e(this.getClass().getCanonicalName(),"Pacchetto non trovato");
			    	}
			    	
				    /*if(ExerciseUtils.isFirstBoot(getApplicationContext(),oConfigTrainer, sVersionPackage)){
				    	Intent intent = ActivityHelper.createActivityIntent(MainActivity.this,ChangeLogActivity.class);
						//startActivity(intent);
				    	ActivityHelper.startNewActivityAndFinish(MainActivity.this, intent);	
				    }*/
				    
			    	doBindService();			    	
				}catch (Exception e) {
					Log.e(this.getClass().getCanonicalName(), "check user: "+e.getMessage());			
				}		
		}
			
		@Override
		protected Boolean doInBackground(Database... mDB) {
			Database oDB=null;
			for (Database DB : mDB) {
				oDB= DB;
			}   
			if(oDB!=null)  {
				oDB.init();
				
			}
			return true;
		}
	}
}

