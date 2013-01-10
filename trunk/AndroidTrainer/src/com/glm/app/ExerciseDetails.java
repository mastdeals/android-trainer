package com.glm.app;

import java.io.File;

import com.glm.trainer.R;
import com.glm.app.graph.WebGraphExerciseActivity;
import com.glm.bean.ConfigTrainer;
import com.glm.bean.ExerciseManipulate;
import com.glm.utils.ExerciseUtils;
import com.glm.utils.animation.ActivitySwitcher;
import com.glm.utils.fb.FacebookConnector;
import com.glm.utils.quickaction.ActionItem;
import com.glm.utils.quickaction.QuickAction;
import com.glm.utils.quickaction.QuickBar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ExerciseDetails extends Activity implements OnClickListener{
	
	private boolean isEditNote=false;
	/**pulsante torna alla lista esercizi*/
	private Button oBtn_SaveShare;
	
	/***oggetto condivisione FB*/
	//private FacebookConnector oFB = null; 
	
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
	
	/*private Button oBtnExportKML;
	
	private Button oBtnExportGPX;
	
	private Button oBtnExportTCX;*/
	
	private ImageButton oBtnShareFB;
	
		
	private RelativeLayout oMaxBpm;
	
	private RelativeLayout oAvgBpm;
	
	private RelativeLayout oInfo;
	
	private EditText oNote;
	
	private LinearLayout oMainLinearLayout;
		
	private LinearLayout oLLDectails;
	
	private LinearLayout oGraph;
	
	private ProgressBar oBarWaiting;
	
	private ConfigTrainer oConfigTrainer;
	
	private Animation a;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
		if(extras !=null){
			isEditNote = extras.getBoolean("note");
		}
		
		if(!isEditNote){
	        setContentView(R.layout.new_exercise_details);
	        
	        a = AnimationUtils.loadAnimation(this, R.animator.fadein);
	        a.reset();
	        
	       
	        
	        
	       
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
	        oInfo			= (RelativeLayout) findViewById(R.id.btnInfo);   
	        oGraph			= (LinearLayout) findViewById(R.id.llGraph);
	        oBarWaiting		= (ProgressBar) findViewById(R.id.pBarWaiting);
	        oLLDectails 	= (LinearLayout) findViewById(R.id.LLDett);  
	        oInfo.setOnClickListener(this);
		}else{   
			setContentView(R.layout.new_exercise_details_note);
	        oNote		  = (EditText) findViewById(R.id.txtNote);
	        oBtn_SaveShare = (Button) findViewById(R.id.btnSaveShareNote);
	       /* oBtnExportKML = (Button) findViewById(R.id.btnExportKML);
	        oBtnExportGPX = (Button) findViewById(R.id.btnExportGPX);
	        oBtnExportTCX = (Button) findViewById(R.id.btnExportTCX);
	      */  
	        oNote.setEnabled(true);
	        oBtn_SaveShare.setText(R.string.save_note);
	        oBtn_SaveShare.setOnClickListener(this);
		}  
       /* JsHandler jshandler = new JsHandler (wv,ExerciseUtils.getWeightData(this),getApplicationContext());
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
        }*/
        
        
        oMainLinearLayout = (LinearLayout) findViewById(R.id.mainlinearLayout);   
        
        /**Associo i listener all'activity*/
        oConfigTrainer = ExerciseUtils.loadConfiguration(this,true);	
               
       /* oBtn_SaveShare.setOnClickListener(this);
        oBtnExportKML.setOnClickListener(this);
        oBtnExportGPX.setOnClickListener(this);
        oBtnExportTCX.setOnClickListener(this);
        oBtnShareFB.setOnClickListener(this);*/
        
        oMainLinearLayout.clearAnimation();
        oMainLinearLayout.setAnimation(a);        
	}
	@Override
	protected void onResume() {
		
		
		if(isEditNote){
			ActivitySwitcher.animationIn(oMainLinearLayout, getWindowManager());
		}else{
			oMainLinearLayout.clearAnimation();
			oMainLinearLayout.setAnimation(a);
		}
		
		super.onResume();
		
		       
		ExeriseTask task = new ExeriseTask();
		task.execute(null);
		
	}
	@Override
	protected void onPause() {
		if(isEditNote){
			ActivitySwitcher.animationOut(oMainLinearLayout, getWindowManager());
		}
		super.onPause();
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
		}else if(oObj.getId()==R.id.btnInfo){
			//Manual Sharing
			//manualShare();
			final QuickBar oBar = new QuickBar(getApplicationContext());
			
			oBar.getQuickAction().setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {          
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
	                //here we can filter which action item was clicked with pos or actionId parameter
	                ActionItem actionItem = source.getActionItem(pos);
	                Log.v(this.getClass().getCanonicalName(),"Item Click Pos: "+actionItem.getActionId());
	                
	                switch (actionItem.getActionId()) {
					
	                case 1:
						manualShare();
						break;
					case 2:
						//Intent intent = ActivityHelper.createActivityIntent(HistoryActivity.this,GraphExerciseActivity.class);
						Intent intent = ActivityHelper.createActivityIntent(ExerciseDetails.this,WebGraphExerciseActivity.class);
						intent.putExtra("graph", "0");		
						ActivityHelper.startNewActivityAndFinish(ExerciseDetails.this, intent);						
						break;
					case 3:
						//Intent intent = ActivityHelper.createActivityIntent(HistoryActivity.this,GraphExerciseActivity.class);
						Intent intentMap = ActivityHelper.createActivityIntent(ExerciseDetails.this,GMapActivity.class);
						intentMap.putExtra("exercise", ExerciseManipulate.getiIDExercise());
						ActivityHelper.startNewActivityAndFinish(ExerciseDetails.this, intentMap);	
						break;
					case 4:	
						Intent intentNote = ActivityHelper.createActivityIntent(ExerciseDetails.this,ExerciseDetails.class);
						intentNote.putExtra("exercise", ExerciseManipulate.getiIDExercise());
						intentNote.putExtra("note", true);
						ActivityHelper.startNewActivityAndFinish(ExerciseDetails.this, intentNote);
						
						/*if(oNote.isEnabled()){				
							oBtn_SaveShare.setText(R.string.edit_note);
							ExerciseUtils.saveNote(getApplicationContext(), ExerciseManipulate.getiIDExercise(), oNote.getText().toString());
							ExerciseUtils.populateExerciseDetails(getApplicationContext(), oConfigTrainer, ExerciseManipulate.getiIDExercise());			
						}else{
							oNote.requestFocus();
							oBtn_SaveShare.setText(R.string.save_note);			
						}
						oNote.setEnabled(!oNote.isEnabled());*/
						break;
					case 5:
						//Erase						
						//deleteExercise(ExerciseManipulate.getiIDExercise());  
						break;
					case 6:
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
						break;
											
					case 7:
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
						break;	
					case 8:
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
						break;
					default:
						break;
					}	                                
	            }
	        });			
			oBar.getQuickAction().show(oObj);
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
		
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.glm.trainer \n\n"+getString(R.string.time)+": "+ExerciseManipulate.getsTotalTime()+
					" " + 
					getString(R.string.distance)+": "+ExerciseManipulate.getsTotalDistance() +" "+
					getString(R.string.speed_avg)+": "+ExerciseManipulate.getsAVGSpeed()+" "+getString(R.string.kalories)+": "+
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
			oConfigTrainer = ExerciseUtils.loadConfiguration(getApplicationContext(),true);		
			//Chiamo il tackAsink
			ExerciseUtils.populateExerciseDetails(getApplicationContext(), oConfigTrainer, ExerciseManipulate.getiIDExercise());
		    //Log.v(this.getClass().getCanonicalName(),"IDExercide: " +ExerciseManipulate.getiIDExercise()+" - "+ExerciseManipulate.getsTotalDistance());
			    
		    
	        
	        return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {			
			
			if(!isEditNote){			
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
			}else{
				oNote.setText(ExerciseManipulate.getsNote());
				oBtn_SaveShare.requestFocus();	
			}
	        
	        //Log.v(this.getClass().getCanonicalName(), "LoadURL:"+"file:///android_asset/jflot/graphtrainerexercisealt.html");
	        //wv.loadUrl("file:///android_asset/jflot/smallgraphtrainerexercisealt.html");
		}
	}
	protected void deleteExercise(final int id) {
		Log.v(this.getClass().getCanonicalName(), "Delete: "+id);
		AlertDialog alertDialog;
    	alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
    	alertDialog.setTitle(getString(R.string.titledeleteexercise));
    	alertDialog.setMessage(getString(R.string.messagedeleteexercise));
    	alertDialog.setButton(getString(R.string.yes), new android.content.DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if(ExerciseUtils.deleteExercise(getApplicationContext(), oConfigTrainer, String.valueOf(id))){
					onBackPressed();
				}
			}        				
    		});
    	
    	alertDialog.setButton2(getString(R.string.no), new android.content.DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {					
				
			}        		
			
    		});
    	
    	alertDialog.show();
	}
}
