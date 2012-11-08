package com.glm.app;

import com.glm.trainer.MainActivity;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
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
	private Button oVoteButton;
	private WebView owvChangeLog;
	private String URI_MARKET;
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
    	    URI_MARKET = "market://details?id="+manager.packageName;
    	    Log.i(this.getClass().getCanonicalName(),"Pacchetto Versione"+URI_MARKET);
    	} catch (NameNotFoundException e) {
    	   Log.e(this.getClass().getCanonicalName(),"Pacchetto non trovato");
    	}
        setContentView(R.layout.changelog_full);
        
        oShareButton  = (ImageButton) findViewById(R.id.btnShare);
        oStartButton = (Button) findViewById(R.id.btnStart);
        oVoteButton   = (Button) findViewById(R.id.btnVote);
        owvChangeLog  = (WebView) findViewById(R.id.wvChangeLog);
        
        oShareButton.setOnClickListener(this);
        oStartButton.setOnClickListener(this);
        oVoteButton.setOnClickListener(this);
        
        loadChangeLog();   
    }
	
	@Override
	public void onResume() {
		super.onResume();
		if(!isFirstBoot()){
			ActivityHelper.startOriginalActivityAndFinish(this);
		}else{
			ExerciseUtils.removeFirstBoot(getApplicationContext());
			loadChangeLog();
		}
		
	}
	/**
	 * Controllo se è il primo boot
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
			Intent intent = ActivityHelper.createActivityIntent(this,MainTrainerActivity.class);
			startActivity(intent);
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
