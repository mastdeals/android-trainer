package com.glm.services;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.glm.services.IExerciseService;
import com.glm.trainer.MainTrainerActivity;
import com.glm.trainer.R;
import com.glm.app.ShareFromService;
import com.glm.bean.CardioDevice;
import com.glm.bean.ConfigTrainer;
import com.glm.bean.NewExercise;
import com.glm.utils.AccelerometerListener;
import com.glm.utils.AccelerometerUtils;
import com.glm.utils.ExerciseUtils;
import com.glm.utils.MediaTrainer;
import com.glm.utils.VoiceToSpeechTrainer;
import com.glm.utils.sensor.BlueToothHelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.backup.BackupManager;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.telephony.TelephonyManager;

/**
 * Avvio il nuovo Esercizio come servizio in background e quindi
 * lo user per popolare la GUI se visibile.
 * 
 * TODO
 * **/
public class ExerciseService extends Service implements LocationListener, AccelerometerListener {
    private static int iHeartRate=0;
    
	private MediaTrainer oMediaPlayer;
	private VoiceToSpeechTrainer oVoiceSpeechTrainer ;
	/**stringe del motivatore*/
	private String sDistance;
	private String sUnit;
	/**
	 * identifica il tipo di esercizio
	 * 0=run
	 * 1=bike
	 * */
	private int iTypeExercise;
	/**
	 * Media Player
	 * */
	
	private boolean bStopListener=false;
	private NotificationManager mNM;
	
	//Identifica i Km di goal se 0 il goal e solo tempo
	private int iGoalDistance=0;
	//Ientifica le ore di goal, se ore e minuti sono 0 il goal è solo distance
	private double dGoalHH=0;
	//Ientifica i minuti di goal, se ore e minuti sono 0 il goal è solo distance
	private double dGoalMM=0;
	/**Timer da ripetere per il Goal*/
	private Timer GoalTimer = null;
	//Ritardo di pronuncia goal
	private final int iDELAY_GOAL=20000;
	
	private boolean isServiceAlive=false;
    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private static int NOTIFICATION = R.string.app_name_pro;

    // this is a hack and need to be changed, here the offset is the length of the tag XML "</Document></kml", 
    // we minus this offset from the end of the file and write the next Placemark entry.  
    //private static final int KML_INSERT_OFFSET = 17;
    
    private static final int gpsMinTime = 5;
    private static final int gpsMinDistance = 0;
    /**Avvio ms del Timer per le coordinate e salvare il tutto in DB*/
    private static final int START_TIMER_DELAY = 0;
    /**Intervallo ms Periodico del Timer per le coordinate e salvare il tutto in DB*/
    private static final int PERIOD_TIMER_DELAY = 5000;
    /**Periodo di lettura accelerometro*/
    private static final int PERIOD_ACCELEROMETER_TIMER=1000;
    
    private static final int GEOCODER_MAX_RESULTS = 5;
    
    private LocationManager LocationManager = null;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private double pre_latitude = 0.0;
    private double pre_longitude = 0.0;
    private long altitude = 0;
    private long pre_altitude = 0;
    
    /**Conta passi*/
    private int iStep=0;
    /**Timer per il motivatore*/
    private Timer MotivatorTimer = null;
    /**Timer per la condivisione interattiva*/
    private Timer InteractiveTimer=null;
    
    /**Timer per la condivisione interattiva*/
    private Timer AutoPauseTimer=null;
    /**Avvio ms del Timer per per l'autopausa*/
    private static final int SHORT_PERIOD_AUTOPAUSE_TIMER_DELAY=60000;
    private static final int MEDIUM_PERIOD_AUTOPAUSE_TIMER_DELAY=180000;
    private static final int LONG_PERIOD_AUTOPAUSE_TIMER_DELAY=300000;
    
    private int iAutoPauseDelay=SHORT_PERIOD_AUTOPAUSE_TIMER_DELAY;
    
    /**Avvio ms del Timer per le coordinate e salvare il tutto in DB*/
    private static final int SHORT_START_MOTIVATOR_TIMER_DELAY = 60000;
    /**Intervallo ms Periodico del Timer per le coordinate e salvare il tutto in DB ms*/
    private static final int SHORT_PERIOD_MOTIVATOR_TIMER_DELAY = 60000;
    /**Avvio ms del Timer per le coordinate e salvare il tutto in DB*/
    private static final int MEDIUM_START_MOTIVATOR_TIMER_DELAY = 180000;
    /**Intervallo ms Periodico del Timer per le coordinate e salvare il tutto in DB ms*/
    private static final int MEDIUM_PERIOD_MOTIVATOR_TIMER_DELAY = 180000;
    /**Avvio ms del Timer per le coordinate e salvare il tutto in DB*/
    private static final int LONG_START_MOTIVATOR_TIMER_DELAY = 300000;
    /**Intervallo ms Periodico del Timer per le coordinate e salvare il tutto in DB ms*/
    private static final int LONG_PERIOD_MOTIVATOR_TIMER_DELAY = 300000;
    
    private int iDelayMotivator=0;
    private int iRepeatMotivator=0;
    
    
    
    /**Timer usato per la scrittura nel DB*/
    private Timer monitoringTimer = null;
    
    /**Thread principale per il controllo del tempo di corsa quando il servizio e' attivo**/
    private Thread oRunningThread;
    
    /**Tempo di corsa*/
    private static long diffTime;
    
    /**identifica se il servizio e' up and running*/
    private static boolean isRunning = false;
    /**identifica se il servizio e' in AutoPausa*/
    private static boolean isAutoPause = false;
    /**identifica se il servizio e' in Resume AutoPausa*/
    private static boolean isResumeAutoPause = false;
    /**indica se il servizio e' in pausa*/
    private static boolean isPause=false;
    ArrayList<Messenger> mClients = new ArrayList<Messenger>(); // Keeps track of all current registered clients.
    /**messaggi supportati*/
    public static final int PAUSEEXERCISE=1;
    public static final int RESUMEEXERCISE=2;
    public static final int STARTEXERCISE=3;
    public static final int STOPSERVICE=4;
    public static final int SAVEEXERCISE=5;
	
    
    //private final IBinder mBinder = new TrainerServiceBinder(); 
    
    /**Continene tutte le configurazioni del Trainer**/
    private static ConfigTrainer oConfigTrainer;
   

    private String sPid="";
    /**
     * Gestione delle chiamate in arrivo
     * */    
    TelephonyManager telephonyManager=null;
    /** Will notify us on changes to the PhoneState*/
	
	/**identifica se c' una chiamata*/
	private boolean isInCalling=false;
	
	/**AudioManger per la gestione del volume*/
	AudioManager oAudioManager = null;
	
	/**gestione dei backup sul cloud*/
	private BackupManager oBackupManager =null;
	
	/**Gestione Cardio*/
	private static BlueToothHelper oBTHelper;
	
	private Timer tCardioTimer;
	
	/**
	 * Gestione delle chiamate in arrivo
	 * */  
    @Override
    public void onCreate() {    	
        super.onCreate();
       
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sPid=sdf.format(new Date());
        
        Log.i("Start Service ExerciseSevice", "OK");
        //Carico la configurazione
        try{
        	oConfigTrainer=ExerciseUtils.loadConfiguration(getApplicationContext());
        }catch (NullPointerException e) {
			Log.e(this.getClass().getCanonicalName(),"Error load Config");
			return;
		}
        
        isServiceAlive=true;
                
        oVoiceSpeechTrainer = new VoiceToSpeechTrainer(getApplicationContext());
                
		oAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);          
		AccelerometerUtils.setContext(getApplicationContext());	
	
		//Cardio
		if(oConfigTrainer.isbUseCardio() && oConfigTrainer.isbCardioPolarBuyed()){
			oBTHelper = new BlueToothHelper();
		}
		
		startGPSFix();
		/*oBackupManager.requestRestore(new RestoreObserver() {
            public void restoreFinished(int error) {
                *//** Done with the restore!  Now draw the new state of our data *//*
                //Log.v(this.getClass().getCanonicalName(), "Restore finished, error = " + error);
            }
        });*/
    }
   
    
	@Override
    public void onStart(Intent intent, int startId) {
		startGPSFix();	 	
        super.onStart(intent, startId);       
    }
    
    /**
     * Esecuzione di avvio esercizio
     * 
     * **/
    public int onStartCommand(Intent intent, int flags, int startId) {
    	 //Carico la configurazione
         oConfigTrainer=ExerciseUtils.loadConfiguration(getApplicationContext());
         //Oggetto per la gestione delle notifiche
         mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE); 	
         //Creazione di un Thread separato che ogni secondo incrementa il tempo di corsa
         startGPSFix(); 	 	 
  		 showNotification(R.drawable.start_trainer, getText(R.string.start_exercise));
  	  
    	 //startLoggingService();
            //startMonitoringTimer();            
         return Service.START_STICKY;
    }
    
    /**
     * Avvio il FIX del GPS
     * */
    private void startGPSFix(){
    	LocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
 		LocationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, PERIOD_TIMER_DELAY, gpsMinTime, this);
    }
    
    @Override
    public void onLocationChanged(Location location) {                  
       
       String sAlt="";
       if(bStopListener) return;
       //Non catturo piu' le coordinate quando sono in pausa
       if(location==null) {
    	   startGPSFix();
    	   return;
       }
       latitude = location.getLatitude();
       longitude = location.getLongitude();
       altitude = Math.round(location.getAltitude());        
       
       NumberFormat oNFormat = NumberFormat.getNumberInstance();
	   oNFormat.setMaximumFractionDigits(2);
	   sAlt=oNFormat.format(altitude);
       if(NewExercise.getdStartLatitude()==0){
    	   //Catturo le coordinate di avvio
    	   NewExercise.setdStartLatitude(latitude);
           NewExercise.setdStartLongitude(longitude);
          
           NewExercise.setCurrentAltidute(sAlt);
       }
       NewExercise.setdCurrentLatitude(latitude);
       NewExercise.setdCurrentLongitude(longitude);
       // imposto l'altitudine
       NewExercise.setlCurrentAltidute(altitude);      
       
       NewExercise.setCurrentAltidute(sAlt);      		

       if(telephonyManager!=null){
			if(telephonyManager.getCallState()==TelephonyManager.CALL_STATE_RINGING){
				stopMediaDuringCall();
			}else if(telephonyManager.getCallState()==TelephonyManager.CALL_STATE_IDLE){
				resumeMediaDuringCall();
			}
	    }
       
       //Lettuta alla notification dei GPS
       if (longitude != 0.0 && latitude != 0.0){    						
			if(pre_latitude!=latitude && pre_longitude!=longitude){
				//Cancello l'auto pausa e 
				if(isAutoPause){
					removeAutoPauseTimer();
					resumeAutoPauseExercise(); 
					addAutoPauseTimer();
				}else{	
					//Metto l'autopausa solo se running
					if(isRunning){
						removeAutoPauseTimer();
						addAutoPauseTimer();
					}
					
				}
				
				if(oConfigTrainer.isbPlayMusic() && !isInCalling && !isAutoPause){   		
		    		if(oMediaPlayer!=null && !oMediaPlayer.isPlaying()){
		    			oMediaPlayer.play(false);        							
		    		}
				}											
					if(oMediaPlayer!=null){
						ExerciseUtils.saveCoordinates(getApplicationContext(), oConfigTrainer,pre_latitude,pre_longitude,
								latitude, longitude, altitude, 
								getLocationName(latitude,longitude), oMediaPlayer.getCurrentSong(), location.getSpeed(), location.getAccuracy(), location.getTime(),iHeartRate);
					}else{
						ExerciseUtils.saveCoordinates(getApplicationContext(), oConfigTrainer,pre_latitude,pre_longitude,
								latitude, longitude, altitude, 
								getLocationName(latitude,longitude), null, location.getSpeed(), location.getAccuracy(), location.getTime(),iHeartRate);
					}
						
					
					double dPartialDistance=0;
					dPartialDistance=ExerciseUtils.getPartialDistanceUnFormattated(pre_longitude,pre_latitude,longitude,latitude);
					
					NewExercise.setfCurrentDistance(dPartialDistance
							+NewExercise.getdCurrentDistance());
									
					//ExerciseUtils.saveCurrentDistance(dPartialDistance,getApplicationContext(),oConfigTrainer);

					pre_latitude=latitude;
					pre_longitude=longitude;
					pre_altitude=altitude;		
			}
		}
		if(isAutoPause && oMediaPlayer!=null) oMediaPlayer.pause();	
		//Log.i(this.getClass().getCanonicalName(),"run TimeTask");
		//sendMessageToUI(1229);
    }

    @Override
    public void onProviderDisabled(String provider) {
    	//Log.i("GPSLoggerService.onProviderDisabled().","onProviderDisabled");
    	NewExercise.setGPSStatus(false);
    	//Se disabilitano il GPS fermo il trainer
    	stopExerciseAsService();
    }

    @Override
    public void onProviderEnabled(String provider) {
    	//Log.i("GPSLoggerService.onProviderEnabled().","onProviderEnabled");
    	NewExercise.setGPSStatus(true);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    	//Log.i("GPSLoggerService.onStatusChanged().","onStatusChanged");
    }    
        
    private void startMotivatorTimer(){   	
    	try{
    		/**Avvio Scrittura dati nel DB*/
        	MotivatorTimer = new Timer();
        	MotivatorTimer.scheduleAtFixedRate(
        			new TimerTask()
        			{
        				@Override
        				public void run()
        				{
        					
        					
        					if(!isRunning) return;
        					/**imposto la distanza corrente*/
        					NewExercise.setfCurrentDistance(
        							ExerciseUtils.getTotalDistanceUnFormattated(getApplicationContext(), oConfigTrainer, 
        									ExerciseUtils.getsIDCurrentExercise(getApplicationContext()), null));
        					
        					final String sDistanceToSpeech=String.valueOf(
        							ExerciseUtils.getTotalDistanceFormattated(NewExercise.getdCurrentDistance(),oConfigTrainer,false));
        					if(!isInCalling){
        						if(oConfigTrainer.isbPlayMusic()){
            						//Abbasso il volume della musica 
            						if(oMediaPlayer!=null) oMediaPlayer.pause();
            						
            					}  
        						
    								String sTimeToSpeech=getApplicationContext().getString(R.string.time)+ExerciseUtils.getTimeHHMMForSpeech(NewExercise.getlCurrentTime(),getApplicationContext());	
    								String sKaloriesToSpeech=String.valueOf(NewExercise.getsCurrentCalories())+getApplicationContext().getString(R.string.kaloriesspeech);
    								String sMinutePerUnit=getApplicationContext().getString(R.string.speach_measure_min_km);
    								
    								if(oConfigTrainer.getiUnits()==1){			
    									sMinutePerUnit=getApplicationContext().getString(R.string.speach_measure_min_miles);
    								}
    								////Log.v(this.getClass().getCanonicalName(),"Kalories: "+sKaloriesToSpeech);
    								
    								NewExercise.setiInclication(ExerciseUtils.getInclination(getApplicationContext(), ExerciseUtils.getsIDCurrentExercise(getApplicationContext())));
    								
        							
        							//TTS Speech
        							Log.i(this.getClass().getCanonicalName(),"Trainer Type: TTS");
        							oVoiceSpeechTrainer.sayDistanza(getApplicationContext(), oConfigTrainer, 
        									sDistanceToSpeech, oMediaPlayer, sTimeToSpeech,sKaloriesToSpeech,
        									NewExercise.getPace()+sMinutePerUnit, NewExercise.getiInclication()+"%", iHeartRate); 
        							//showNotification(R.drawable.start_trainer, getText(R.string.start_exercise)+" StepCount: "+iStep);
        							
        							//Avvio il timer del goal solo in certe condizioni
        							
        							if(iGoalDistance!=0 ||
        									dGoalHH!=0 ||
        									dGoalMM!=0){
        								goalTimerStart();
        							}
        							
        					}    							
        				}
        			}, 
        			iDelayMotivator,
    		   	    iRepeatMotivator);	
    	}catch (IllegalArgumentException e) {
			Log.e(this.getClass().getCanonicalName(),"Error start Time");
			stopSelf();
		}
    	
    }
    /**
     * metodo richiamato dal motivator se corrro col Goal
     * */
    private void goalTimerStart() {
    	GoalTimer = new Timer();
    	
    	GoalTimer.schedule(new TimerTask()
    			{
    				@Override
    				public void run()
    				{
    					
    					
    					if(!isRunning) return;
    					//Log.v(this.getClass().getCanonicalName(), "GoalHH:"+dGoalHH+" GoalMM"+dGoalMM+" GoalDiance:"+iGoalDistance);
    					//Goal solo distanza
    					if((dGoalHH==0 && dGoalMM==0) && iGoalDistance!=0){
    						//Esco se ho superato l'obiettivo
							if(iGoalDistance<ExerciseUtils.getTotalDistanceUnFormattated(getApplicationContext(), oConfigTrainer, 
									ExerciseUtils.getsIDCurrentExercise(getApplicationContext()), null))
								return;
							
							double iDistanceToGoal;
    						/**imposto la distanza corrente*/
        					NewExercise.setfCurrentDistance(
        							ExerciseUtils.getTotalDistanceUnFormattated(getApplicationContext(), oConfigTrainer, 
        									ExerciseUtils.getsIDCurrentExercise(getApplicationContext()), null));
        					iDistanceToGoal=(iGoalDistance/1000)-NewExercise.getdCurrentDistance();
        					//TODO OBIETTIVO RAGGIUNTO
        					if(iDistanceToGoal<0) iDistanceToGoal=0;
        					final String sDistanceToSpeech=String.valueOf(
        							ExerciseUtils.getTotalDistanceFormattated(iDistanceToGoal,oConfigTrainer,false));
        					
        					if(!isInCalling){
        						if(oConfigTrainer.isbPlayMusic()){
            						//Abbasso il volume della musica 
            						oMediaPlayer.pause();
            						
            					}  
        						
    								Log.i(this.getClass().getCanonicalName(),"Trainer Type: TTS");
        							oVoiceSpeechTrainer.sayDistanzaToGoal(getApplicationContext(), oConfigTrainer, 
        									sDistanceToSpeech, oMediaPlayer); 
        							//showNotification(R.drawable.start_trainer, getText(R.string.start_exercise)+" StepCount: "+iStep);   							  							
        					} 
    					}else if((dGoalHH!=0 || dGoalMM!=0) && iGoalDistance==0){
    						//Goal solo Tempo
    						
    						//Esco se ho superato l'obiettivo
							if(dGoalHH<ExerciseUtils.getTimeHH(NewExercise.getlCurrentTime(),getApplicationContext())
									&&
							   dGoalMM<ExerciseUtils.getTimeMM(NewExercise.getlCurrentTime(),getApplicationContext()))
								return;
							
    						if(!isInCalling){
        						if(oConfigTrainer.isbPlayMusic()){
            						//Abbasso il volume della musica 
            						oMediaPlayer.pause();
            						
            					}  
        						
    								Log.i(this.getClass().getCanonicalName(),"Trainer Type: TTS");
        							oVoiceSpeechTrainer.sayTimeToGoal(getApplicationContext(), oConfigTrainer, 
        									dGoalHH,dGoalMM,
        									ExerciseUtils.getTimeHH(NewExercise.getlCurrentTime(),getApplicationContext()),
        									ExerciseUtils.getTimeMM(NewExercise.getlCurrentTime(),getApplicationContext()), oMediaPlayer); 
        							//showNotification(R.drawable.start_trainer, getText(R.string.start_exercise)+" StepCount: "+iStep);   							  							
        					}
    					}else{
    						if(!isInCalling){
    							//Esco se ho superato l'obiettivo
    							if(iGoalDistance<ExerciseUtils.getTotalDistanceUnFormattated(getApplicationContext(), oConfigTrainer, 
    									ExerciseUtils.getsIDCurrentExercise(getApplicationContext()), null))
    								return;
    							
    							if(oConfigTrainer.isbPlayMusic()){
            						//Abbasso il volume della musica 
            						oMediaPlayer.pause();
            						
            					} 
        						
	    						//Goal con entrambi Time e Distance
	    						double dVelocitaMediaGoal=0;
	    						//Calcolo la velocità media che devo mantenere in KM/H
	    						try{
	    							dVelocitaMediaGoal=(iGoalDistance/1000)/(dGoalHH+(dGoalMM/60));
	    						}catch (ArithmeticException e) {
									Log.e(this.getClass().getCanonicalName(),"Errore calcolo velocità media!");
									dVelocitaMediaGoal=0;
								}
	    						//oVoiceSpeechTrainer.say("Media Goal è "+dVelocitaMediaGoal+", Media corrente è "+ExerciseUtils.getVelocitaMedia(getApplicationContext()));
	    						
	    						oVoiceSpeechTrainer.sayDistanceAndTimeToGoal(getApplicationContext(), oConfigTrainer, 
    								dVelocitaMediaGoal,
    								ExerciseUtils.getVelocitaMedia(getApplicationContext()), oMediaPlayer);
    						}
    					}   					   							
    				}
    			}, iDELAY_GOAL);
		
	}


	private String getLocationName(double latitude, double longiture){
            String name = "";
//            Geocoder geocoder = new Geocoder(this);
//            
//            try {
//                    List<Address> address = geocoder.getFromLocation(latitude, longiture, ExerciseService.GEOCODER_MAX_RESULTS);
//                    
//                    name = address.get(0).toString();
//            } catch (IOException e) {
//                    e.printStackTrace();
//            }               
            
            return name;
    }        

    @Override
    public void onDestroy() {
        if(oConfigTrainer!=null){
        	if(oConfigTrainer.isbDisplayNotification()){
            	// Cancel the persistent notification.
                if(mNM!=null) mNM.cancel(NOTIFICATION);
            }
            if(oConfigTrainer.isbPlayMusic()){
            	if(oMediaPlayer!=null) oMediaPlayer.destroy();
            }
            if (AccelerometerUtils.isListening()) {
        		AccelerometerUtils.stopListening();
            }
        }      
		if(oRunningThread!=null) oRunningThread.interrupt();
        bStopListener=true;
    	isRunning = false;
    	isServiceAlive=false;
    	NewExercise.reset();
    	if(oMediaPlayer!=null && oMediaPlayer.isPlaying()) oMediaPlayer.stop();
    	if(LocationManager!=null) LocationManager.removeUpdates(this);
    	if(monitoringTimer!=null) monitoringTimer.cancel();
    	if(MotivatorTimer!=null) MotivatorTimer.cancel();
    	if(AutoPauseTimer!=null) AutoPauseTimer.cancel();
    	if(InteractiveTimer!=null) InteractiveTimer.cancel(); 
    	
    	latitude = 0.0;
        longitude = 0.0;
    	pre_latitude = 0.0;
        pre_longitude = 0.0;
        
        try {
			this.finalize();
		} catch (Throwable e) {
			Log.e(this.getClass().getCanonicalName(),"OnDestroy: "+e.getMessage());
		}
    }

    /**
     * Show a notification while this service is running.
     * 
     * @param String sMessageToShow
     */
    private void showNotification(int iIcon, CharSequence charSequence) {
    	//Controllo la configurazione dal DB
    	if(!oConfigTrainer.isbDisplayNotification()) return;
    	// In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.app_name_pro);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(iIcon, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification Controller
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainTrainerActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, charSequence,
                       text, contentIntent);

        // Send the notification.
        if(mNM!=null) mNM.notify(NOTIFICATION, notification);
    }             
	
	/**
     * Classe che si occupa di monitorare il tempo di corsa istantaneo ogni sec.
     * 
     * **/
    public static class ThRunningTime implements Runnable{
    	public void run() {      		
    		while(!Thread.currentThread().isInterrupted()){
    			
				try {
					diffTime = (System.currentTimeMillis () - NewExercise.getlStartTime()+NewExercise.getlPauseTime());
					//Log.i("difftime: ",String.valueOf(NewExercise.getlCurrentTime()));
					//Log.i("NewExercise.getlStartTime(): ",String.valueOf(NewExercise.getlStartTime()));
					NewExercise.setlCurrentTime(diffTime);
					NewExercise.setlCurrentWatchPoint(diffTime);	
					if(oConfigTrainer.isbUseCardio() 
							&& 
							oConfigTrainer.isbCardioPolarBuyed() 
							&& oBTHelper!=null){
						
						iHeartRate=oBTHelper.getHeartRate();
					}
					Thread.sleep(1000);					
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}   	   
    	}
    }
    
    /**
     * AUTOPAusa l'esercizio disattivando tutti servizi che usano GPS/Accelerometro e salvataggi al DB
     * per un nuovo esercizio
     * 
     * 
     * **/
    private void autopauseExercise(){
    	isAutoPause		= true;
    	bStopListener	= false;
    	isRunning 		= false;  
    	isServiceAlive 	= true;
    	if(oConfigTrainer.isbPlayMusic()){
    		//TODO Play music during exercise STOP MUSIC
    		//Log.v(this.getClass().getCanonicalName(), "Music AUTOPAUSE Pause");
    		if(oMediaPlayer!=null) oMediaPlayer.pause();
    	}
    	if(oConfigTrainer.isbMotivator()){
	   		//TODO Send Message motivato
	   		//Log.v(this.getClass().getCanonicalName(), "Motivator AUTOPAUSE Pause");
	   		//oVoiceSpeechTrainer.say(this.getString(R.string.pauseexercise));	
	   		if(MotivatorTimer!=null) MotivatorTimer.cancel();
	   	}
    	//Log.v(this.getClass().getCanonicalName(), "AUTO-Pause Exercise");
        //mClients.add(msg.replyTo);
        showNotification(R.drawable.pause_trainer, getText(R.string.pauseexercise));    	
    	NewExercise.setlPauseTime(NewExercise.getlCurrentWatchPoint());
    }
    /**
     * RiAvvia l'esercizio messo in autopausa riattivando tutti servizi che usano GPS/Accelerometro e salvataggi al DB
     * per un nuovo esercizio
     * 
     * @see IExerciseService.Stub
     * **/
    private void resumeAutoPauseExercise(){
    	isResumeAutoPause = true;
    	isAutoPause		= false;
    	bStopListener	= false;
    	isRunning 		= true;  
    	isServiceAlive 	= true;
    	//Log.v(this.getClass().getCanonicalName(), "Resume AUTOPAUSE Exercise");
    	if(oConfigTrainer.isbPlayMusic()){
    		//TODO Play music during exercise STOP MUSIC
    		//Log.v(this.getClass().getCanonicalName(), "Music AUTOPAUSE Resume");
    		if(oMediaPlayer!=null) oMediaPlayer.play(true);
    	}
    	if(oConfigTrainer.isbMotivator()){	   		
	   		//Log.v(this.getClass().getCanonicalName(), "Motivator AUTOPAUSE Resume");
	   		//oVoiceSpeechTrainer.say(this.getString(R.string.resumexercise));
	   		startMotivatorTimer();
	   	}
        //mClients.remove(msg.replyTo);
    	showNotification(R.drawable.start_trainer, getText(R.string.start_exercise));    	
    	NewExercise.setlStartTime(System.currentTimeMillis());
    }
    
    /**
     * PAusa l'esercizio disattivando tutti servizi che usano GPS/Accelerometro e salvataggi al DB
     * per un nuovo esercizio
     * 
     * @see IExerciseService.Stub
     * **/
    private void pauseExerciseAsService(){
    	isPause			  = true;
    	isResumeAutoPause = false;
    	bStopListener	= true;
    	isRunning 		= false;  
    	isServiceAlive 	= true;
    	if(oConfigTrainer.isbPlayMusic()){
    		//TODO Play music during exercise STOP MUSIC
    		//Log.v(this.getClass().getCanonicalName(), "Music Pause");
    		if(oMediaPlayer!=null) oMediaPlayer.pause();
    	}
    	if(oConfigTrainer.isbMotivator()){	   		
	   		//Log.v(this.getClass().getCanonicalName(), "Motivator Pause");
	   		//oVoiceSpeechTrainer.say(this.getString(R.string.pauseexercise));
	   		if(MotivatorTimer!=null) MotivatorTimer.cancel();
	   	}
    	//Log.v(this.getClass().getCanonicalName(), "Pause Exercise");
        //mClients.add(msg.replyTo);
        showNotification(R.drawable.pause_trainer, getText(R.string.pauseexercise));    	
    	NewExercise.setlPauseTime(NewExercise.getlCurrentWatchPoint());
    	oRunningThread.interrupt();
    	/**Arresto il listener del Conta Passi*/
    	if (AccelerometerUtils.isListening()) {
    		AccelerometerUtils.stopListening();
        }
    }
    
    /**
     * RiAvvia l'esercizio messo in pausa riattivando tutti servizi che usano GPS/Accelerometro e salvataggi al DB
     * per un nuovo esercizio
     * 
     * @see IExerciseService.Stub
     * **/
    private void resumeExerciseAsService(){
    	isPause			= false;
    	bStopListener	= false;
    	isRunning 		= true;  
    	isServiceAlive 	= true;
    	//Log.v(this.getClass().getCanonicalName(), "Resume Exercise");
    	
    	if(oConfigTrainer.isbPlayMusic()){   
    		//Log.v(this.getClass().getCanonicalName(), "Music Resume");
    		if(oMediaPlayer!=null) oMediaPlayer.play(true);
    	}
    	if(oConfigTrainer.isbMotivator()){
	   		//Log.v(this.getClass().getCanonicalName(), "Motivator Resume");
	   		//oVoiceSpeechTrainer.say(this.getString(R.string.resumexercise));
	   		startMotivatorTimer();
	   	}
        //mClients.remove(msg.replyTo);
    	showNotification(R.drawable.start_trainer, getText(R.string.start_exercise));    	
    	NewExercise.setlStartTime(System.currentTimeMillis());
 		oRunningThread = new Thread(new ThRunningTime());
 		oRunningThread.start();
    }
    /**
     * Avvia tutti i servizi che usano GPS/Accelerometro e salvataggi al DB
     * per un nuovo esercizio
     * 
     * @see IExerciseService.Stub
     * **/
    private void startExerciseAsService(int TypeExercise, int goalDistance, double goalHH, double goalMM){    	 	
    	isResumeAutoPause = false;
    	isAutoPause		= false;
    	isRunning 		= true;   
    	isServiceAlive 	= true;
    	bStopListener	= false;
    	iTypeExercise   = TypeExercise;    	
    	iStep			= 0;
    	latitude 		= 0.0;
        longitude 		= 0.0;
    	pre_latitude 	= 0.0;
        pre_longitude 	= 0.0;
    	//Imposto 
    	
    	//Carico la configurazione
        oConfigTrainer=ExerciseUtils.loadConfiguration(getApplicationContext());
    	//Forzo il reset dell'esercizio
        NewExercise.reset();
	   	if(oConfigTrainer.isbPlayMusic()){
	   		//AVVIO DEL PLAYER ESTERNO	
	   		oMediaPlayer = new MediaTrainer(getApplicationContext());
	   		oMediaPlayer.play(false);
	   		listenForIncomingCall();
	   	}
		if(oConfigTrainer.isbDisplayMap()){	
			if(oConfigTrainer.getiAutoPauseTime()==0){
	   			//Short 1 Min
				iAutoPauseDelay=ExerciseService.SHORT_PERIOD_AUTOPAUSE_TIMER_DELAY;
		   	    
	   		}else if(oConfigTrainer.getiAutoPauseTime()==1){
	   			//Medium 3 Min
	   			iAutoPauseDelay=ExerciseService.MEDIUM_PERIOD_AUTOPAUSE_TIMER_DELAY;
		   	    
	   		}else if(oConfigTrainer.getiAutoPauseTime()==2){
	   			//Long 5 Min
	   			iAutoPauseDelay=ExerciseService.LONG_PERIOD_AUTOPAUSE_TIMER_DELAY;
		   	    
	   		}
		}
	   	
	   	
	   	if(oConfigTrainer.isbMotivator()){	   		
	   		//Imposto la frequenza del motivatore
	   		if(oConfigTrainer.getiMotivatorTime()==0){
	   			//Short 1 Min
	   			iDelayMotivator=ExerciseService.SHORT_START_MOTIVATOR_TIMER_DELAY;
		   	    iRepeatMotivator=ExerciseService.SHORT_PERIOD_MOTIVATOR_TIMER_DELAY;
	   		}else if(oConfigTrainer.getiMotivatorTime()==1){
	   			//Medium 3 Min
	   			iDelayMotivator=ExerciseService.MEDIUM_START_MOTIVATOR_TIMER_DELAY;
		   	    iRepeatMotivator=ExerciseService.MEDIUM_PERIOD_MOTIVATOR_TIMER_DELAY;
	   		}else if(oConfigTrainer.getiMotivatorTime()==2){
	   			//Long 5 Min
	   			iDelayMotivator=ExerciseService.LONG_START_MOTIVATOR_TIMER_DELAY;
		   	    iRepeatMotivator=ExerciseService.LONG_PERIOD_MOTIVATOR_TIMER_DELAY;
	   		}
	   		
	   		
	   		
	   		//Log.v(this.getClass().getCanonicalName(), "Motivator On");
	   		sDistance=this.getString(R.string.currentdistance);
	        if(oConfigTrainer.getiUnits()==1){
	        	sUnit=this.getString(R.string.miles);
	        }else{
	        	sUnit=this.getString(R.string.kilometer);
	        }
	   		oVoiceSpeechTrainer.say(this.getString(R.string.startexercise));
	        //oVoicePlayerTrainer.sayStart(oConfigTrainer);
	   		startMotivatorTimer();	   		  			   		
	   	}
	   	/**Aggiungo il timer per l'interactive sharing*/
	    if(oConfigTrainer.isbInteractiveExercise()){
	      	/**Aggiungo il timer per l'interactive sharing*/
		    if(oConfigTrainer.isShareFB() || oConfigTrainer.isShareTwitter()){
		    	addInteractiveTimer(oConfigTrainer.isShareFB(),oConfigTrainer.isShareTwitter());
		    }
	    }
	   	  	
	    //AutoPause
		if(oConfigTrainer.isbAutoPause()) addAutoPauseTimer();
		
		//Abilito l'incoming call listener
   		if (AccelerometerUtils.isSupported() 
   				&& iTypeExercise!=1) {
   			AccelerometerUtils.startListening(this);
        }	 
   		startGPSFix();
	   	//Log.v(this.getClass().getCanonicalName(), "Start New Exercise");
	   	
	   	dGoalHH=goalHH;
	   	dGoalMM=goalMM;
	   	if(goalDistance==0){
	   		
	   	}else if(goalDistance==0){
	   		iGoalDistance=0;
	   	}else if(goalDistance==1){
	   		iGoalDistance=5000;
	   	}else if(goalDistance==2){
	   		iGoalDistance=10000;
	   	}else if(goalDistance==3){
	   		iGoalDistance=15000;
	   	}else if(goalDistance==4){
	   		iGoalDistance=21097;
	   	}else if(goalDistance==5){
	   		iGoalDistance=31000;
	   	}else if(goalDistance==6){
	   		iGoalDistance=42195;
	   	}
	      
	   	//Cardio
	   	if(oConfigTrainer.isbUseCardio() && oConfigTrainer.isbCardioPolarBuyed()){
			startCardio();
		}
	   	
	   	NewExercise.setlStartTime(System.currentTimeMillis());
		oRunningThread = new Thread(new ThRunningTime());
		oRunningThread.start();         				
		//startMonitoringTimer(); 
    }
    /**
     * Fermo tutti i servizi che usano GPS/Accelerometro e salvataggi al DB
     * 
     * @see IExerciseService.Stub
     * **/
    private void stopExerciseAsService(){
    	if(isRunning){
    		//saveExerciseAsService();
    		if(oConfigTrainer.isShareFB()){
				//Log.v(this.getClass().getCanonicalName(), "Condivido su FB");
				//facebookShareEnd();
			}
    	}
    	bStopListener	= true;
    	isRunning 		= false;  
    	isServiceAlive 	= true;
    	isAutoPause     = false;
    	if(oConfigTrainer.isbPlayMusic()){   		
    		if(oMediaPlayer!=null && oMediaPlayer.isPlaying()){
    			oMediaPlayer.destroy();   			    			
        		oMediaPlayer=null;
    		}    			
    	} 
    	if(oConfigTrainer.isbMotivator()){	   		
	   		//Log.v(this.getClass().getCanonicalName(), "Motivator Stop");
	   		//oVoiceSpeechTrainer.say(this.getString(R.string.stopexercise));
	   		if(MotivatorTimer!=null) MotivatorTimer.cancel();
	   	}
    	
    	//Cardio
	   	if(oConfigTrainer.isbUseCardio() && oConfigTrainer.isbCardioPolarBuyed()){
			stopCardio();
		}
    	
    	if(LocationManager!=null) LocationManager.removeUpdates(this);
    	if(monitoringTimer!=null) monitoringTimer.cancel();
    	if(MotivatorTimer!=null) MotivatorTimer.cancel();
    	if(AutoPauseTimer!=null) AutoPauseTimer.cancel();
    	if(InteractiveTimer!=null) InteractiveTimer.cancel(); 
    	onDestroy();
    }
    /**
     * Salvo l'esercizio corrente
     * 
     * @see IExerciseService.Stub
     * **/
    private void saveExerciseAsService(){
    	if(oConfigTrainer.isbPlayMusic()){
    		if(oMediaPlayer!=null) {
    			if(oMediaPlayer.isPlaying()){   				
        			oMediaPlayer.destroy();
            		oMediaPlayer=null;
    			}else{
	    			if(oConfigTrainer.isbPlayMusic()){
	    				//Tolgo il MediaPlayer ero in pausa ed ho salvato
	    				oMediaPlayer.destroy();
	            		oMediaPlayer=null;
	    			}
    			}
    		}    		
    	}
    	if(oConfigTrainer.isbMotivator()){
	   		//Log.v(this.getClass().getCanonicalName(), "Motivator On");
	   		//oVoiceSpeechTrainer.say(this.getString(R.string.saveexercise));
	   		//oVoicePlayerTrainer.saySave(oConfigTrainer);
	   		if(MotivatorTimer!=null) MotivatorTimer.cancel();
	   	}
    	//SAVE
    	bStopListener=true;
    	mNM.cancel(NOTIFICATION);
	    bStopListener=true;
	    isRunning = false;	    
	    isAutoPause=false;
	    ExerciseUtils.saveExercise(getApplicationContext(),oConfigTrainer,iStep);
	    NewExercise.reset();
	    //ExerciseUtils.addStep(getApplicationContext(), iStep);
	    if(LocationManager!=null) LocationManager.removeUpdates(this);
	    if(monitoringTimer!=null) monitoringTimer.cancel();
	    if(InteractiveTimer!=null) InteractiveTimer.cancel(); 
	    if(AutoPauseTimer!=null) AutoPauseTimer.cancel();
	     /**Arresto il listener del Conta Passi*/
	    if (AccelerometerUtils.isListening()) {
    		AccelerometerUtils.stopListening();
    		iStep=0;
        }
    	//Avvio il backup sul Cloud
	    oBackupManager.dataChanged();
	    
    }                     
    /**
     * Ritorna il Servizio
     * **/
    public class TrainerServiceBinder extends Binder 
    { 
            public ExerciseService getService() 
            { 
                    return ExerciseService.this; 
            } 
    } 
    
	 /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
    	Log.i(this.getClass().getCanonicalName(), "onBind->Services");    	
    	return mBinder;
        //return mMessenger.getBinder();
    }
    
    private final IExerciseService.Stub mBinder = new IExerciseService.Stub() {
    	public String getPid(){
            return sPid;
        }       
		@Override
		public boolean startExercise(int TypeExercise, int goalDistance, double goalHH, double goalMM) throws RemoteException {			
			Log.i("Services Message: ", "start exercise via AIDL");
			startExerciseAsService(TypeExercise, goalDistance, goalHH, goalMM);  
			return false;
		}
		@Override
		public boolean isRunning() throws RemoteException {			
			return isRunning;
		}
		@Override
		public boolean stopExercise() throws RemoteException {			
			Log.i("Services Message: ", "stop exercise via AIDL");
			stopExerciseAsService();
			return false;
		}
		@Override
		public boolean pauseExercise() throws RemoteException {
			
			pauseExerciseAsService();
			return false;
		}
		@Override
		public boolean resumeExercise() throws RemoteException {
			
			resumeExerciseAsService();
			return false;
		}
		@Override
		public boolean saveExercise() throws RemoteException {
			
			saveExerciseAsService();
			return false;
		}
		@Override
		public long getExerciseTime() throws RemoteException {
			
			return NewExercise.getlStartTime()+NewExercise.getlPauseTime();
		}
		@Override
		public boolean isServiceAlive() throws RemoteException {
			
			return isServiceAlive;
		}
		@Override
		public String getsCurrentDistance() throws RemoteException {
			
			return ExerciseUtils.getTotalDistanceFormattated(NewExercise.getdCurrentDistance(),oConfigTrainer,true);
		}
		@Override
		public String getsPace() throws RemoteException {
			String sMinutePerUnit=getApplicationContext().getString(R.string.min_km);
			
			if(oConfigTrainer.getiUnits()==1){			
				sMinutePerUnit=getApplicationContext().getString(R.string.min_miles);
			}
			return NewExercise.getPace()+" "+sMinutePerUnit;
		}
		@Override
		public String getsKaloriesBurn() throws RemoteException {
			
			NewExercise.setsCurrentCalories(ExerciseUtils.getKaloriesBurn(oConfigTrainer, NewExercise.getdCurrentDistance()));
			return String.valueOf(NewExercise.getsCurrentCalories());
		}
		@Override
		public boolean isRefumeFromAutoPause() throws RemoteException {
			return isResumeAutoPause;
		}
		@Override
		public void setRefumeFromAutoPause(boolean bResumeAutoPause)
				throws RemoteException {
			isResumeAutoPause=bResumeAutoPause;
		}
		@Override
		public boolean isAutoPause() throws RemoteException {			
			return isAutoPause;
		}
		@Override
		public String getsAltitude() throws RemoteException {
			// TODO Auto-generated method stub
			return NewExercise.getCurrentAltidute();
		}
		
		@Override
		public int getiTypeExercise() throws RemoteException {			
			return iTypeExercise;
		}
		@Override
		public String getsVm() throws RemoteException {
			String sMinutePerUnit=getApplicationContext().getString(R.string.km_hours);
			
			if(oConfigTrainer.getiUnits()==1){			
				sMinutePerUnit=getApplicationContext().getString(R.string.miles_hours);
			}
			return NewExercise.getsVm()+" "+sMinutePerUnit;
		}
		@Override
		public double getLatidute() throws RemoteException {
			
			return latitude;
		}
		@Override
		public double getLongitude() throws RemoteException {
			
			return longitude;
		}
		@Override
		public boolean isPause() throws RemoteException {
			
			return isPause;
		}
		@Override
		public String getsInclination() throws RemoteException {			
			return NewExercise.getiInclication()+"%";
		}
		@Override
		public void stopGPSFix() throws RemoteException {
			endGPS();
		}
		@Override
		public void setHeartRate(int iheartRate) throws RemoteException {
			iHeartRate=iheartRate;
		}
		@Override
		public void shutDown() throws RemoteException {
			stopSelf();
		}
		@Override
		public void skipTrack() throws RemoteException {
			if(oMediaPlayer!=null) oMediaPlayer.SkipTrack();
		}
		@Override
		public int getHeartRate() throws RemoteException {
			return iHeartRate;
		}
		@Override
		public boolean isCardioConnected() throws RemoteException {
			if(oBTHelper!=null){
				return oBTHelper.isbConnect();
			}else {
				return false;
			}
			
		}
    };        

    /**
     *  Metodi utilizzati per l'uso dell'accelerometro per il conteggio dei passi*
     *  Se x cambia da 0 ad 1  stato fatto un passo
     * 
     * **/
	@Override
	public void onAccelerationChanged(float x, float y, float z) {
		// TODO Auto-generated method stub
		if(iTypeExercise==0){
			if(y>10 ||
					x==6
					|| z==6){
				////Log.v(this.getClass().getCanonicalName(),"Acceleration: x="+x+" y="+y+" z="+z);
				iStep++;
				//Log.v(this.getClass().getCanonicalName(),"Step: "+iStep);
			}
		}else{
			if(y>6 ||
					x==6
					|| z==6){
				////Log.v(this.getClass().getCanonicalName(),"Acceleration: x="+x+" y="+y+" z="+z);
				iStep++;
				//Log.v(this.getClass().getCanonicalName(),"Step: "+iStep);
			}
		}
		
		
		//ExerciseUtils.addStep(getApplicationContext(), x);
	}

	/**
	 * Ferma il GPS
	 * 
	 * */
	protected void endGPS() {
		if(LocationManager!=null) LocationManager.removeUpdates(this);
		LocationManager=null;
	}


	@Override
	public void onShake(float force) {
		// TODO Auto-generated method stub
		//Log.v(this.getClass().getCanonicalName(),"onShake: gForce="+force);		
	}
	/**
	 * Ferma tutto l'audio durante una chiamata
	 * */
	private void stopMediaDuringCall(){
		if(!isRunning) return;
		if(oMediaPlayer!=null){
			if(!oMediaPlayer.isPlaying()) return;	
		}
		
		//Log.v(this.getClass().getCanonicalName(), "Stop Media For INCOMING CALL!");
		isInCalling=true;
		if(oConfigTrainer.isbPlayMusic()){
			oMediaPlayer.pause();
		}
		//Finisco la chiamata se abilitata
		if(oConfigTrainer.isbEndCallOnWorkout()){
			//TODO
		}
		
		
	}
	/**
	 * Riavvia l'audio dopo una chiamata
	 * */
	private void resumeMediaDuringCall(){
		if(!isRunning || !isInCalling) return;
		//Log.v(this.getClass().getCanonicalName(), "Resume Media CALL END!");
		isInCalling=false;
		if(oConfigTrainer.isbPlayMusic()){
			oMediaPlayer.play(true);
		}		
	}
	/**
	 * Gestione delle chiamate in Arrivo
	 * 
	 * 
	 * **/
	
	/**
	 * Metodo da avviare col trainer per catturare le chiamate e spegnere i volumi
	 * 
	 * */
	private void listenForIncomingCall(){
		telephonyManager=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
	}

	  
    /**
     * Metodi deprecati
     * 
     * **/
    
    /**METODI DI CONDIVISIONE**/
    
    /**METODI DI CONDIVISIONE FACEBOOK
     * @param isTwitterShareActive 
     * @param isFaceBookShareActive **/
	
	
	private void facebookShareInteractive(boolean isFaceBookShareActive, boolean isTwitterShareActive) {
		try{
			Intent dialogIntent = new Intent(getBaseContext(), ShareFromService.class);
			dialogIntent.putExtra("distance", ExerciseUtils.getTotalDistanceFormattated(NewExercise.getdCurrentDistance(),oConfigTrainer,true));
			String sMinutePerUnit="";
			if(oConfigTrainer.getiUnits()==1){			
				sMinutePerUnit=getApplicationContext().getString(R.string.miles_hours);
			}
			dialogIntent.putExtra("pace", NewExercise.getPace()+" "+sMinutePerUnit);
			dialogIntent.putExtra("time", ExerciseUtils.getTimeHHMM(NewExercise.getlStartTime()+NewExercise.getlPauseTime())+
					ExerciseUtils.getTimeSSdd(NewExercise.getlStartTime()+NewExercise.getlPauseTime())
					);
			dialogIntent.putExtra("isFaceBookShareActive", isFaceBookShareActive);
			dialogIntent.putExtra("isTwitterShareActive", isTwitterShareActive);
			dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplication().startActivity(dialogIntent);	
		}catch (ActivityNotFoundException e) {
			Log.e(this.getClass().getCanonicalName(),"Error Sharing interactive"+e.getMessage());
		}
	}
	
	/**
	 * Lancia il TimerTask per l'interactive exercise
	 * @param c 
	 * @param isFaceBookShareActive 
	 * */
	private void addInteractiveTimer(final boolean isFaceBookShareActive,final boolean isTwitterShareActive) {
		InteractiveTimer = new Timer();
		InteractiveTimer.scheduleAtFixedRate(
    			new TimerTask()
    			{
    				@Override
    				public void run()
    				{    					
    					facebookShareInteractive(isFaceBookShareActive,isTwitterShareActive);
    				}					
    			}, 
    			iDelayMotivator,
    			iRepeatMotivator);
	}
	
	/**
	 * Rimuove il timer per l'auto pausa
	 * */
	private void removeAutoPauseTimer(){
		if(AutoPauseTimer!=null) AutoPauseTimer.cancel();
	}
	/**
	 * Imposta il timer per l'auto pausa
	 * */
	private void addAutoPauseTimer(){
		AutoPauseTimer = new Timer();		
		AutoPauseTimer.schedule(
    			new TimerTask()
    			{
    				@Override
    				public void run()
    				{    					
    					isAutoPause=true;
    					//Log.v(this.getClass().getCanonicalName(), "RUN AUTOPAUSE!!!"); 
    					autopauseExercise();
    				}					
    			}, 
    			iAutoPauseDelay);
	}

	/**
	 * Avvia la vomunicazione col Cardio
	 * */
	private void startCardio(){
		
		Log.i(this.getClass().getCanonicalName(),"Start Cardio ");
		
		oBTHelper.searchPairedDevice();
		
		ArrayList<CardioDevice> aCardio = oBTHelper.getaCardioName();
		int l=aCardio.size();
		for(int i=0;i<l;i++){
			String sDeviceName=aCardio.get(i).getsDeviceName();	
			if(sDeviceName.startsWith("Polar")){
				Log.i(this.getClass().getCanonicalName(),"Cardio Polar to Connect "+aCardio.get(i).getsDeviceAddress());
				oBTHelper.setDevice(aCardio.get(i).getoDeviceBluetooth());
					
				Timer ConnectTimer = new Timer();		
				ConnectTimer.schedule(
		    			new TimerTask()
		    			{
		    				@Override
		    				public void run()
		    				{    				
		    					Log.v(this.getClass().getCanonicalName(),"Timer Start Connect Received: ");
		    					oBTHelper.connect();
		    				}					
		    			}, 
		    			5000);
				
				final BroadcastReceiver mReceiver = new BroadcastReceiver() {	// BroadcastReceiver for ACTION_FOUND
		            public void onReceive(Context context, Intent intent) {
		                String action = intent.getAction();		                		
		                Log.v(this.getClass().getCanonicalName(),"Action Received: "+action);
		                if( BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
		                	
		                	//oBTHelper.connect();
		                }
		            }
		        };
		        
		        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
		        getApplicationContext().registerReceiver( mReceiver, filter);
				//Toast.makeText(this.getApplicationContext(), aCardio.get(i).getsDeviceName(), Toast.LENGTH_SHORT).show();	
				Log.i(this.getClass().getCanonicalName(),"Connect To "+aCardio.get(i).getsDeviceName()+" Success!");
			}
		}
	}
	/**
	 * chiude la sessione bluetooch
	 * */
	private void stopCardio(){
		if(oBTHelper!=null){
			if(oBTHelper.isbConnect()){
				oBTHelper.disconect();
			}
		}
	}
}

 