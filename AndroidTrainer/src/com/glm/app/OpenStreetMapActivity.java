package com.glm.app;

import java.util.Timer;
import java.util.TimerTask;
import com.glm.bean.ConfigTrainer;
import com.glm.bean.NewExercise;
import com.glm.services.ExerciseService;
import com.glm.services.IExerciseService;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;
import com.glm.utils.JsHandler;
import com.glm.utils.animation.ActivitySwitcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebSettings.ZoomDensity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class OpenStreetMapActivity  extends Activity implements OnClickListener{
	private ConfigTrainer oConfigTrainer;
	private WebView wv;
	private Button oBtnBack;
	private ImageButton btnSkipTrack;
	
	private Timer updateMap = new Timer();
	private JsHandler jshandler;
	
	private double dLong;
	private double dLat;
	
	/**Interfaccia di collegamento al servizio*/
	private IExerciseService mIService;
	boolean mIsBound=false;
	/**Oggetto connessione al servizio*/
	private TrainerServiceConnection mConnection = new TrainerServiceConnection();
	private RelativeLayout oMainLayout;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stopwatch_map);        
		/**Bind col servizio*/
        doBindService();   
        oMainLayout = (RelativeLayout) findViewById(R.id.MainLayout);
		wv 				= (WebView) findViewById(R.id.wv1); 
		oBtnBack 		= (Button) findViewById(R.id.btnBack); 
		btnSkipTrack 	= (ImageButton) findViewById(R.id.btnSkipTrack);
        
		oConfigTrainer = ExerciseUtils.loadConfiguration(this);	

		jshandler = new JsHandler (wv,ExerciseUtils.getWeightData(this),getApplicationContext());
		try {	           
			// Load the local file into the webview\
			wv.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
			wv.getSettings().setBuiltInZoomControls(false);		           
			wv.getSettings().setJavaScriptEnabled(true);
			wv.addJavascriptInterface(jshandler, "jshandler");
			//jshandler.loadGraph();
			//wv.loadData(text, "text/html", "UTF-8");
			//Log.v(this.getClass().getCanonicalName(), "LoadURL:"+"file:///android_asset/map/jsmap.html");
			wv.loadUrl("file:///android_asset/map/jsmap.html");
			updateMap();
		} catch (Exception e) {
			// Should never happen!
			throw new RuntimeException(e);
		}	  
		oBtnBack.setOnClickListener(this);
		btnSkipTrack.setOnClickListener(this);
	}
	@Override
	public void onBackPressed() {		
		updateMap.cancel();
		doUnbindService();
		ActivityHelper.startOriginalActivityAndFinish(this);
	}
	@Override
	protected void onResume() {
		
		// animateIn this activity
		ActivitySwitcher.animationIn(oMainLayout, getWindowManager());
		super.onResume();
		jshandler = new JsHandler (wv,ExerciseUtils.getWeightData(this),getApplicationContext());
		try {	           
			// Load the local file into the webview\
			wv.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
			wv.getSettings().setBuiltInZoomControls(false);		           
			wv.getSettings().setJavaScriptEnabled(true);
			wv.addJavascriptInterface(jshandler, "jshandler");
			//jshandler.loadGraph();
			//wv.loadData(text, "text/html", "UTF-8");
			//Log.v(this.getClass().getCanonicalName(), "LoadURL:"+"file:///android_asset/map/jsmap.html");
			wv.loadUrl("file:///android_asset/map/jsmap.html");
			updateMap();
		} catch (RuntimeException e) {
			Log.e(this.getClass().getCanonicalName(),"Error Runtime");
		}	
		
		if(oConfigTrainer.isbPlayMusic()){
	       	btnSkipTrack.setVisibility(View.VISIBLE);
	    }else{
	      	btnSkipTrack.setVisibility(View.INVISIBLE);
	    }
		
	}


	@Override
	public void onClick(View oObj) {
		if(oObj.getId()==R.id.btnBack){
			updateMap.cancel();
			doUnbindService();
			ActivityHelper.startOriginalActivityAndFinish(this);			
		}if(oObj.getId()==R.id.btnSkipTrack){
			if(oConfigTrainer.isbPlayMusic()){
				if(mIService!=null){
					try {
						if(mIService.isRunning()){
							mIService.skipTrack();
						}
					} catch (RemoteException e) {
						Log.e(this.getClass().getCanonicalName(), "Error get state of Service");
					}
						
				}
			}
		}
	}
	
	/**
	 * Aggiorna la mappa
	 * 
	 * */
	private void updateMap() {
	   //Log.v(this.getClass().getCanonicalName(),"updateMap");
		  
		 
		updateMap.scheduleAtFixedRate(
    			new TimerTask()
    			{
    				@Override
    				public void run()
    				{    				
    					//Log.v(this.getClass().getCanonicalName(),"updateMap->run");
    					if(mIService!=null){ 
    						//Log.v(this.getClass().getCanonicalName(),"updateMap->run->!null");
    						try {
								dLat=mIService.getLatidute();
								dLong=mIService.getLongitude();
								if(jshandler!=null){
									jshandler.updateMap(dLong,dLat);																	
								}
								
							} catch (RemoteException e) {
								Log.e(this.getClass().getCanonicalName(),"updateMap->run->Error getting new Location");
								e.printStackTrace();
							}
    						
    					}    					
    				}					
    			}, 
    			5000,
    			5000);
    	
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
	                	
	                	try {                	        	
	            			if(mIService.isRunning()){
	            				//Esercizio in running prelevo il tempo e faccio partire il timer
	            				
	            				NewExercise.setlStartTime(mIService.getExerciseTime());
	            				//Log.v(this.getClass().getCanonicalName(),"start time restored: "+NewExercise.getlStartTime());

       				           				
	            			}
	            			if(mIService.isAutoPause()){


    		  		   	 	} 	            		    	 
	            		} catch (Exception e) {
	            			// TODO Auto-generated catch block
	            			e.printStackTrace();
	            		}
	                	//Toast.makeText(StopwatchActivity.this, "SERVICES CONNECTED",
		    	        //        Toast.LENGTH_LONG).show();
	                	mIsBound=true;
	                }catch (Exception e) {
	                	Log.e(this.getClass().getCanonicalName(), "onServiceConnected->Remote Exception"+e.getMessage());
	                	e.printStackTrace();
					}
	                
	        } 
	        @Override 
	        public void onServiceDisconnected(ComponentName name) 
	        { 
	        	mIService = null; 
	               /* Toast.makeText(StopwatchActivity.this, "TrainerServiceConnection->onServiceDisconnected"+R.string.pause_exercise,	    	              Toast.LENGTH_LONG).show();*/
	        } 
	} 
	
	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because there is no reason to be able to let other
	    // applications replace our component.
		//Intent bindIntent = new Intent(Main.this, MessengerService.class); 
        //bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);                 
		getApplicationContext().bindService(new Intent(OpenStreetMapActivity.this, 
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
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessengerIncoming = new Messenger(new IncomingHandler());
	
	class IncomingHandler extends Handler {
	    @Override
	    public void handleMessage(Message msg) {
	       
	        Log.i("Received from service: " , String.valueOf(msg.arg1));
	              
	    }
	}	
}
