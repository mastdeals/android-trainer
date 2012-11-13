package com.glm.app;

import com.glm.trainer.MainActivity;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;
import com.glm.utils.vending.BillingService;
import com.glm.utils.vending.PurchaseObserver;
import com.glm.utils.vending.ResponseHandler;
import com.glm.utils.vending.BillingService.RequestPurchase;
import com.glm.utils.vending.BillingService.RestoreTransactions;
import com.glm.utils.vending.Consts.PurchaseState;
import com.glm.utils.vending.Consts.ResponseCode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
public class ChangeLogActivity extends Activity implements OnClickListener {
	private ImageButton oShareButton;
	private Button oStartButton;
	private ImageButton oVoteButton;
	private WebView owvChangeLog;
	private String URI_MARKET;
	private String sPackageName="";
	private BillingService mBillingService;
	
	private TrainerPurchaseObserver mTrainerPurchaseObserver;
    private Handler mHandler;
	
    private String mSku;
    private String mPayloadContents = null;
    
	/**identifica se l'utente ha già usato il programma*/
	private boolean asExercise=false;
	
	/**
     * A {@link PurchaseObserver} is used to get callbacks when Android Market sends
     * messages to this application so that we can update the UI.
     */
    private class TrainerPurchaseObserver extends PurchaseObserver {
        public TrainerPurchaseObserver(Handler handler) {
            super(ChangeLogActivity.this, handler);
        }

        @Override
        public void onBillingSupported(boolean supported) {
                Log.i(this.getClass().getCanonicalName(), "supported: " + supported);
        }

        @Override
        public void onPurchaseStateChange(PurchaseState purchaseState, String itemId,
                int quantity, long purchaseTime, String developerPayload) {         
                Log.i(this.getClass().getCanonicalName(), "onPurchaseStateChange() itemId: " + itemId + " " + purchaseState);         
        }

        @Override
        public void onRequestPurchaseResponse(RequestPurchase request,
                ResponseCode responseCode) {
            Log.d(this.getClass().getCanonicalName(), request.mProductId + ": " + responseCode);
            if (responseCode == ResponseCode.RESULT_OK) {        
                Log.i(this.getClass().getCanonicalName(), "purchase was successfully sent to server");
                //logProductActivity(request.mProductId, "sending purchase request");
            } else if (responseCode == ResponseCode.RESULT_USER_CANCELED) {
                Log.i(this.getClass().getCanonicalName(), "user canceled purchase");
                //logProductActivity(request.mProductId, "dismissed purchase dialog");
            } else {
               
                Log.i(this.getClass().getCanonicalName(), "purchase failed");

                //logProductActivity(request.mProductId, "request purchase returned " + responseCode);
            }
        }

        @Override
        public void onRestoreTransactionsResponse(RestoreTransactions request,
                ResponseCode responseCode) {
            if (responseCode == ResponseCode.RESULT_OK) {
               
                Log.d(this.getClass().getCanonicalName(), "completed RestoreTransactions request");
                // Update the shared preferences so that we don't perform
                // a RestoreTransactions again.
                SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                //edit.putBoolean(DB_INITIALIZED, true);
                edit.commit();
            } else {
                Log.d(this.getClass().getCanonicalName(), "RestoreTransactions error: " + responseCode);
            }
        }
    }
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /***    
		NotificationManager notificationManager = (NotificationManager)getSystemService(this.NOTIFICATION_SERVICE);

		int icon = R.drawable.trainer_notification_icon;
		CharSequence text = "Notification Text";
		CharSequence contentTitle = "Notification Title";
		CharSequence contentText = "Sample notification text.";
		long when = System.currentTimeMillis();
				
		Notification notification = new Notification(icon,text,when);						
		notificationManager.notify(APP_NOTIFICATION_ID, notification);
		*/
        
        //MARKET URI
        try {
    		
    	    PackageInfo manager=getPackageManager().getPackageInfo(getPackageName(), 0);
    	    sPackageName=manager.packageName;
    	    URI_MARKET = "market://details?id="+sPackageName;
    	    Log.i(this.getClass().getCanonicalName(),"Pacchetto Versione"+URI_MARKET);
    	} catch (NameNotFoundException e) {
    	   Log.e(this.getClass().getCanonicalName(),"Pacchetto non trovato");
    	}
        setContentView(R.layout.changelog_full);
        
        oShareButton  = (ImageButton) findViewById(R.id.btnShare);
        oStartButton = (Button) findViewById(R.id.btnStart);
        oVoteButton   = (ImageButton) findViewById(R.id.btnVote);
        owvChangeLog  = (WebView) findViewById(R.id.wvChangeLog);
        
        oShareButton.setOnClickListener(this);
        oStartButton.setOnClickListener(this);
        oVoteButton.setOnClickListener(this);
        
        mHandler = new Handler();
        mTrainerPurchaseObserver = new TrainerPurchaseObserver(mHandler);
        mBillingService = new BillingService();
        mBillingService.setContext(this);
        
        if(ExerciseUtils.getsIDCurrentExercise(getApplicationContext())!=null){
        	try{
        		int iExercise = Integer.parseInt(ExerciseUtils.getsIDCurrentExercise(getApplicationContext()));
        		if(iExercise>0) asExercise=true;
        	}catch (NumberFormatException e) {
				Log.e(this.getClass().getCanonicalName(),"Error parse exercise");
			}
        }
        
        if(!asExercise &&
        		sPackageName.compareToIgnoreCase("com.glm.trainerlite")==0){
        		//Metto il Donate solo sulla LITE
        		oStartButton.setText(getString(R.string.donate));     
        }
        
        // Check if billing is supported.
        ResponseHandler.register(mTrainerPurchaseObserver);
        if (!mBillingService.checkBillingSupported()) {
            
        }
        
        loadChangeLog();   
    }
	
	@Override
	public void onResume() {
		super.onResume();
		if(!isFirstBoot()){
			ActivityHelper.startOriginalActivityAndFinish(this);
		}else{			
			loadChangeLog();
		}
		
	}
	/**
	 * Controllo se Ã¨ il primo boot
	 * */
	private boolean isFirstBoot() {
		
		return ExerciseUtils.isFirstBoot(getApplicationContext());
	}
	

	/**
	 * Finestra di changelog
	 * 
	 * */
	private void loadChangeLog() {
	    owvChangeLog.loadUrl("file:///android_asset/changelog/us_US.html");
	        
	}
    @Override
    public void onClick(View v) {
		if (v.getId() == R.id.btnStart) {			
			if(!asExercise &&
					sPackageName.compareToIgnoreCase("com.glm.trainerlite")==0){        	
		      	//Inserire la donazione 
				mSku="donate2.0";
				//mSku="android.test.purchase";
				mBillingService.requestPurchase(mSku, mPayloadContents);
				//mBillingService
		    }else{			
		    	if(!ExerciseUtils.isUserExist(getApplicationContext())){
		    		Intent intent = ActivityHelper.createActivityIntent(this,UserDetailsActivity.class);
		    		startActivity(intent);
		    	}else{
		    		Intent intent = ActivityHelper.createActivityIntent(this,MainTrainerActivity.class);
		    		startActivity(intent);
		    	}
		    }
		}else if (v.getId() == R.id.btnShare) {	
			try{			
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.glm.trainerlite");
				sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(sharingIntent,"Share using"));
			}catch (Exception e) {
				Log.w(this.getClass().getCanonicalName(), "FB Interactive sharing error:"+e.getMessage());
				Toast.makeText(getBaseContext(), getString(R.string.share_ko), Toast.LENGTH_SHORT)
				.show();
			}
		}else if(v.getId() == R.id.btnVote){
			Intent dialogIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URI_MARKET));			
			startActivity(dialogIntent);
		}
    }
}
