package com.glm.app;

import java.io.File;

import com.glm.trainer.R;
import com.glm.app.graph.WebGraphExerciseActivity;
import com.glm.bean.ConfigTrainer;
import com.glm.bean.ExerciseManipulate;
import com.glm.utils.ExerciseUtils;
import com.glm.utils.JsHandler;
import com.glm.utils.fb.FacebookConnector;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebSettings.ZoomDensity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ExerciseDetails extends Activity implements OnClickListener{
	/**pulsante torna alla lista esercizi*/
	private Button oBtn_SaveShare;
	
	/***oggetto condivisione FB*/
	private FacebookConnector oFB = null; 
	
	/**pulsante graph*/
	//private Button oBtn_Graph;
	
	private TextView oTxt_Time;
	
	private TextView oTxt_Distance;
	
	private TextView oTxt_Kalories;
	
	private TextView oTxt_AVGSpeed;
	
	private TextView oTxt_AVGPace;
	
	private TextView oTxt_MAXSpeed;
	
	private TextView oTxt_MAXPace;	
	
	private TextView oTxt_Step;
	
	private TextView oTxt_MaxBpm;
	
	private TextView oTxt_AvgBpm;
	
	private Button oBtnExportKML;
	
	private Button oBtnExportGPX;
	
	private Button oBtnExportTCX;
	
	private ImageButton oBtnShareFB;
	
	private Button oBtnGraph;	
	
	private Button oBtnGMap;
	
	private RelativeLayout oMaxBpm;
	
	private RelativeLayout oAvgBpm;
	
	private EditText oNote;
	
	private LinearLayout oMainLinearLayout;
		
	private LinearLayout oLLDectails;
	
	private ConfigTrainer oConfigTrainer;
	
	private WebView wv;
	
	private Animation a;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.exercise_details);
        
        a = AnimationUtils.loadAnimation(this, R.animator.fadein);
        a.reset();
        
        wv = (WebView) findViewById(R.id.wv1); 
        
        oBtn_SaveShare = (Button) findViewById(R.id.btnSaveShareNote);
       
        //oBtn_Graph= (Button) findViewById(R.id.btn_graph);
        oTxt_Time	  = (TextView) findViewById(R.id.textTime);
        oTxt_Distance = (TextView) findViewById(R.id.textDistance);
        oTxt_Kalories = (TextView) findViewById(R.id.textKalories);
        oTxt_AVGSpeed = (TextView) findViewById(R.id.textAVGSpeed);
        oTxt_AVGPace  = (TextView) findViewById(R.id.textPace);
        oTxt_MAXSpeed = (TextView) findViewById(R.id.textMAXSpeed);
        oTxt_MAXPace  = (TextView) findViewById(R.id.textMAXPace);
        oTxt_Step	  = (TextView) findViewById(R.id.textStep);
        oTxt_MaxBpm	  = (TextView) findViewById(R.id.textMaxBpm);
        oTxt_AvgBpm	  = (TextView) findViewById(R.id.textAvgBpm);
        
        oMaxBpm	  		= (RelativeLayout) findViewById(R.id.RLMaxBpm);
        oAvgBpm	  		= (RelativeLayout) findViewById(R.id.RLAvgBpm);
        
        oNote		  = (EditText) findViewById(R.id.txtNote);
        oBtnExportKML = (Button) findViewById(R.id.btnExportKML);
        oBtnExportGPX = (Button) findViewById(R.id.btnExportGPX);
        oBtnExportTCX = (Button) findViewById(R.id.btnExportTCX);
        oBtnShareFB   = (ImageButton) findViewById(R.id.btnShareFB);
        oBtnGraph     = (Button) findViewById(R.id.btnGraph);
        oBtnGMap	  = (Button) findViewById(R.id.btnGmap);
        
        oNote.setEnabled(false);
        
        JsHandler jshandler = new JsHandler (wv,ExerciseUtils.getWeightData(this),getApplicationContext());
        try {	           
            // Load the local file into the webview\
        	wv.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
            wv.getSettings().setBuiltInZoomControls(false);		           
            wv.getSettings().setJavaScriptEnabled(true);
            wv.addJavascriptInterface(jshandler, "jshandler");
            //jshandler.loadGraph();
            //wv.loadData(text, "text/html", "UTF-8");
            //Log.v(this.getClass().getCanonicalName(), "LoadURL:"+"file:///android_asset/jflot/graphtrainerexercisealt.html");
            wv.loadUrl("file:///android_asset/jflot/smallgraphtrainerexercisealt.html");
        } catch (Exception e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
        
        
        oMainLinearLayout = (LinearLayout) findViewById(R.id.mainlinearLayout);   
        oLLDectails 	  = (LinearLayout) findViewById(R.id.LLDett);  
        
        /**Associo i listener all'activity*/
        oConfigTrainer = ExerciseUtils.loadConfiguration(this);	
        oBtn_SaveShare.setText(R.string.edit_note);
              
        oBtn_SaveShare.setOnClickListener(this);
        oBtnExportKML.setOnClickListener(this);
        oBtnExportGPX.setOnClickListener(this);
        oBtnExportTCX.setOnClickListener(this);
        oBtnShareFB.setOnClickListener(this);
        oBtnGraph.setOnClickListener(this);
        oBtnGMap.setOnClickListener(this);
        
        oMainLinearLayout.clearAnimation();
        oMainLinearLayout.setAnimation(a);        
	}
	@Override
	protected void onResume() {
		super.onResume();
		oMainLinearLayout.clearAnimation();
		oMainLinearLayout.setAnimation(a);
		
		ExeriseTask task = new ExeriseTask();
		task.execute(null);
		
	}
	
	@Override
	public void onClick(View oObj) {
		if(oObj.getId()==R.id.btnSaveShareNote){			
			
			if(oNote.isEnabled()){				
				oBtn_SaveShare.setText(R.string.edit_note);
				ExerciseUtils.saveNote(getApplicationContext(), ExerciseManipulate.getiIDExercise(), oNote.getText().toString());
				ExerciseUtils.populateExerciseDetails(this, oConfigTrainer, ExerciseManipulate.getiIDExercise());			
			}else{
				oNote.requestFocus();
				oBtn_SaveShare.setText(R.string.save_note);			
			}
			oNote.setEnabled(!oNote.isEnabled());
		}else if(oObj.getId()==R.id.btnShareFB){
			//Manual Sharing
			manualShare();
		}else if(oObj.getId()==R.id.btnExportGPX){	
			if(ExerciseUtils.writeGPX(-1,getApplicationContext(),oConfigTrainer)){
				Toast.makeText(getBaseContext(), getString(R.string.exercise_export_ok)+" "+ExerciseUtils.sExportFile, Toast.LENGTH_SHORT)
				.show();
				//Send via mail
				final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
	        	emailIntent.setType("plain/text"); 
	        	//emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"laverdone@gmail.com"}); 
	        	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.message_subject)); 
	        	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.message_text)); 
	        	File fileIn = new File(ExerciseUtils.sExportFile);
	            Uri u = Uri.fromFile(fileIn);       
	            emailIntent.putExtra(Intent.EXTRA_STREAM, u);
	        	startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			}else{
				Toast.makeText(getBaseContext(), getString(R.string.exercise_export_ko), Toast.LENGTH_SHORT)
				.show();
			}				
		}else if(oObj.getId()==R.id.btnExportTCX){	
			if(ExerciseUtils.writeTCX(-1,getApplicationContext(),oConfigTrainer)){
				Toast.makeText(getBaseContext(), getString(R.string.exercise_export_ok)+" "+ExerciseUtils.sExportFile, Toast.LENGTH_SHORT)
				.show();
				//Send via mail
				final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
	        	emailIntent.setType("plain/text"); 
	        	//emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"laverdone@gmail.com"}); 
	        	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.message_subject)); 
	        	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.message_text)); 
	        	File fileIn = new File(ExerciseUtils.sExportFile);
	            Uri u = Uri.fromFile(fileIn);       
	            emailIntent.putExtra(Intent.EXTRA_STREAM, u);
	        	startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			}else{
				Toast.makeText(getBaseContext(), getString(R.string.exercise_export_ko), Toast.LENGTH_SHORT)
				.show();
			}				
		}else if(oObj.getId()==R.id.btnExportKML){	
			if(ExerciseUtils.writeKML(-1,getApplicationContext(),oConfigTrainer)){
				Toast.makeText(getBaseContext(), getString(R.string.exercise_export_ok)+" "+ExerciseUtils.sExportFile, Toast.LENGTH_SHORT)
				.show();
				//Send via mail
				final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
	        	emailIntent.setType("plain/text"); 
	        	//emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"laverdone@gmail.com"}); 
	        	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.message_subject)); 
	        	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.message_text)); 
	        	File fileIn = new File(ExerciseUtils.sExportFile);
	            Uri u = Uri.fromFile(fileIn);       
	            emailIntent.putExtra(Intent.EXTRA_STREAM, u);
	        	startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			}else{
				Toast.makeText(getBaseContext(), getString(R.string.exercise_export_ko), Toast.LENGTH_SHORT)
				.show();
			}
		}else if(oObj.getId()==R.id.btnGraph){
			//Intent intent = ActivityHelper.createActivityIntent(HistoryActivity.this,GraphExerciseActivity.class);
			Intent intent = ActivityHelper.createActivityIntent(ExerciseDetails.this,WebGraphExerciseActivity.class);
			intent.putExtra("graph", "0");		
			ActivityHelper.startNewActivityAndFinish(ExerciseDetails.this, intent);	
		
		}else if (oObj.getId()==R.id.btnGmap){
			Intent intent = ActivityHelper.createActivityIntent(ExerciseDetails.this,GMapActivity.class);
			intent.putExtra("exercise", ExerciseManipulate.getiIDExercise());
			ActivityHelper.startNewActivityAndFinish(ExerciseDetails.this, intent);	
		}		
	}	
	@Override
    public void onBackPressed() {
		ActivityHelper.startOriginalActivityAndFinish(this);
    }
	/**
	 * Metodo per la condivisione manuale dell'esercizio
	 * */
	public void manualShare(){
		try{			
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
		
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "https://market.android.com/details?id=com.glm.trainerlite \n\n"+getString(R.string.time)+": "+ExerciseManipulate.getsTotalTime()+
					" " + 
					getString(R.string.distance)+": "+ExerciseManipulate.getsTotalDistance() +" "+
					getString(R.string.pace)+": "+ExerciseManipulate.getsMinutePerDistance()+" "+getString(R.string.kalories)+": "+
					ExerciseManipulate.getsCurrentCalories()+" Kcal ");
			sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(Intent.createChooser(sharingIntent,"Share using"));
		}catch (Exception e) {
			Log.w(this.getClass().getCanonicalName(), "FB Interactive sharing error:"+e.getMessage());
			Toast.makeText(getBaseContext(), getString(R.string.share_ko), Toast.LENGTH_SHORT)
			.show();
		}
	}
	
	private class ExeriseTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			oConfigTrainer = ExerciseUtils.loadConfiguration(getApplicationContext());		
			//Chiamo il tackAsink
			ExerciseUtils.populateExerciseDetails(getApplicationContext(), oConfigTrainer, ExerciseManipulate.getiIDExercise());
		    //Log.v(this.getClass().getCanonicalName(),"IDExercide: " +ExerciseManipulate.getiIDExercise()+" - "+ExerciseManipulate.getsTotalDistance());
		        
		    
	        
	        return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {			
			oTxt_Time.setText(ExerciseManipulate.getsTotalTime());
		    oTxt_Distance.setText(ExerciseManipulate.getsTotalDistance());
		    oTxt_AVGSpeed.setText(ExerciseManipulate.getsAVGSpeed());
		    oTxt_AVGPace.setText(ExerciseManipulate.getsMinutePerDistance());
		    oTxt_MAXSpeed.setText(ExerciseManipulate.getsMAXSpeed());
		    oTxt_MAXPace.setText(ExerciseManipulate.getsMAXMinutePerDistance());
		    oTxt_Step.setText(ExerciseManipulate.getsStepCount());
		    oTxt_Kalories.setText(ExerciseManipulate.getsCurrentCalories());
		    if(oConfigTrainer!=null){
		    	if(oConfigTrainer.isbCardioPolarBuyed()){
		    		if(ExerciseManipulate.getiMAXBpm()==0){
		    			oTxt_MaxBpm.setText("n.d.");
		    		}else{
		    			Log.i(this.getClass().getCanonicalName(),"MaxBPM"+ExerciseManipulate.getiMAXBpm());
		    			oTxt_MaxBpm.setText(String.valueOf(ExerciseManipulate.getiMAXBpm()));	
		    		}
		    		if(ExerciseManipulate.getiAVGBpm()==0){
		    			oTxt_AvgBpm.setText("n.d.");
		    		}else{
		    			oTxt_AvgBpm.setText(String.valueOf(ExerciseManipulate.getiAVGBpm()));
		    		}	    	    	    		
		    	}else{
		    		oLLDectails.removeView(oMaxBpm);
		    		oLLDectails.removeView(oAvgBpm); 	    		
		    	}
		    }    
		    oNote.setText(ExerciseManipulate.getsNote());
		    oBtn_SaveShare.requestFocus();
		    
	        //Log.v(this.getClass().getCanonicalName(), "LoadURL:"+"file:///android_asset/jflot/graphtrainerexercisealt.html");
	        wv.loadUrl("file:///android_asset/jflot/smallgraphtrainerexercisealt.html");
		}
	}
}
