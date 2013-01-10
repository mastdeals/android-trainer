package com.glm.app;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.glm.bean.ConfigTrainer;
import com.glm.bean.DistancePerExercise;
import com.glm.chart.BarChart;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;

public class SummaryActivity extends Activity implements OnClickListener{
	private ConfigTrainer oConfigTrainer;
	
	private ImageButton oBtnRun;
	private ImageButton oBtnWalk;
	private ImageButton oBtnBike;
	private ImageButton obtn_Back;
	
	private TextView oTxtTot;
	private TextView oTxtRun;
	private TextView oTxtWalk;
	private TextView oTxtBike;

	private TextView oTxtKalRun;
	private TextView oTxtKalWalk;
	private TextView oTxtKalBike;

	
	private LinearLayout oGraph;
	
	private ProgressBar oBarWaiting;
	
	/**
	 * 0=pace
	 * 1=alt
	 * 2=...
	 * */
	private int iTypeGraph=0;
	private Vector<DistancePerExercise> oTable;
	private String sUnit="Km";
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	    	
	        setContentView(R.layout.new_summary_history);        
	        oBtnRun 	= (ImageButton) findViewById(R.id.btn_history_run); 
	    	oBtnWalk 	= (ImageButton) findViewById(R.id.btn_history_walk); 
	    	oBtnBike	= (ImageButton) findViewById(R.id.btn_history_bike); 
	    	obtn_Back  	= (ImageButton) findViewById(R.id.btn_back);
	    	 
	    	oGraph			= (LinearLayout) findViewById(R.id.llGraph);
	        oBarWaiting		= (ProgressBar) findViewById(R.id.pBarWaiting);
	    	
	    	oTxtTot		= (TextView) findViewById(R.id.textDistance_tot); 
	    	oTxtRun		= (TextView) findViewById(R.id.textDistance_run); 
	    	oTxtWalk	= (TextView) findViewById(R.id.textDistance_walk); 
	    	oTxtBike	= (TextView) findViewById(R.id.textDistance_bike); 
	    	
	    	oTxtKalRun	= (TextView) findViewById(R.id.textKal_run); 
	    	oTxtKalWalk	= (TextView) findViewById(R.id.textKal_walk); 
	    	oTxtKalBike	= (TextView) findViewById(R.id.textKal_bike);
	        
	        oConfigTrainer = ExerciseUtils.loadConfiguration(this,true);	
	        /**controllo e salvo esercizi non salvati*/
	    	ExerciseUtils.checkIncompleteWorkout(getApplicationContext(), oConfigTrainer);
	        if(oConfigTrainer.getiUnits()!=0){	        	
	        	//Mi
	        	sUnit="Mi";
	        }
		    /*ExerciseUtils.populateExerciseDetails(this, oConfigTrainer, ExerciseManipulate.getiIDExercise());
		 
	        JsHandler jshandler = new JsHandler (wv,ExerciseUtils.getWeightData(this),getApplicationContext());
	        try {	           
	            // Load the local file into the webview\
	        	wv.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
	            wv.getSettings().setBuiltInZoomControls(false);		           
	            wv.getSettings().setJavaScriptEnabled(true);
	            wv.addJavascriptInterface(jshandler, "jshandler");
	            //jshandler.loadGraph();
	            //wv.loadData(text, "text/html", "UTF-8");
	            AssetManager assetManager = getAssets();
	            String[] aSTR = assetManager.list("jflot/trainersummary.html");
	            //Log.v(this.getClass().getCanonicalName(), "aSTR.length "+aSTR.length);	 	
	            for(int i=0;i<aSTR.length;i++){
	            	//Log.v(this.getClass().getCanonicalName(), "aSTR["+i+"] "+aSTR[i]);	 	           	
	            }
	         
	            //Log.v(this.getClass().getCanonicalName(), "LoadURL:"+"file:///android_asset/jflot/trainersummary.html");
	            wv.loadUrl("file:///android_asset/jflot/trainersummary.html");
	        } catch (Exception e) {
	            // Should never happen!
	            throw new RuntimeException(e);
	        }	 */  
	       
			oTable=ExerciseUtils.getDistanceForType(oConfigTrainer, getApplicationContext());
			int iRun=0,iWalk=0,iBike=0;
	        int iTableSize=oTable.size();
			for(int i=0;i<iTableSize;i++){
	        	
	        	if(oTable.get(i).getiTypeExercise()==0){
	        		//Run
	        		try{
	        			iRun = Integer.parseInt(oTable.get(i).getsDistance().replace(" ", ""));
	        		}catch (NumberFormatException e) {
						iRun=0;
					}
	        		oTxtRun.setText(oTable.get(i).getsDistance()+sUnit);
	        		oTxtKalRun.setText(oTable.get(i).getsCalories()+" "+getString(R.string.kalories));
	        	}else if(oTable.get(i).getiTypeExercise()==1){
	        		//Bike
	        		try{
	        			iBike = Integer.parseInt(oTable.get(i).getsDistance().replace(" ", ""));
	        		}catch (NumberFormatException e) {
	        			iBike=0;
					}
	        		oTxtBike.setText(oTable.get(i).getsDistance()+sUnit);
	        		oTxtKalBike.setText(oTable.get(i).getsCalories()+" "+getString(R.string.kalories));
	        	}else if(oTable.get(i).getiTypeExercise()==100){
	        		//walk
	        		try{
	        			iWalk = Integer.parseInt(oTable.get(i).getsDistance().replace(" ", ""));
	        		}catch (NumberFormatException e) {
	        			iWalk=0;
					}
	        		oTxtWalk.setText(oTable.get(i).getsDistance()+sUnit);
	        		oTxtKalWalk.setText(oTable.get(i).getsCalories()+" "+getString(R.string.kalories));
	        	}
	        }
	        oTxtTot.setText((iRun+iBike+iWalk)+sUnit);
	        oBtnRun.setOnClickListener(this);
	    	oBtnWalk.setOnClickListener(this);
	    	oBtnBike.setOnClickListener(this);
	    	obtn_Back.setOnClickListener(this);
	 }
	 @Override
	 public void onBackPressed() {
		 ActivityHelper.startOriginalActivityAndFinish(this);
	 }
	@Override
	protected void onResume() {
		super.onResume();
        //wv.loadUrl("file:///android_asset/jflot/trainersummary.html");  	  
		BarChart oChart = new BarChart(getApplicationContext(),0);			
        oGraph.removeAllViews();
        oGraph.addView(oChart);    
	}
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_history_run) {
			Intent intent = ActivityHelper.createActivityIntent(this,HistoryActivity.class);
			intent.putExtra("history", "0");		 
			//startActivity(intent);
			ActivityHelper.startNewActivityAndFinish(this, intent);		
		}else if (v.getId() == R.id.btn_history_walk) {
			Intent intent = ActivityHelper.createActivityIntent(this,HistoryActivity.class);
			//startActivity(intent);
			intent.putExtra("history", "100");
			ActivityHelper.startNewActivityAndFinish(this, intent);	
		}else if (v.getId() == R.id.btn_history_bike) {
			Intent intent = ActivityHelper.createActivityIntent(this,HistoryActivity.class);
			//startActivity(intent);
			intent.putExtra("history", "1");
			ActivityHelper.startNewActivityAndFinish(this, intent);	
		}else if (v.getId() == R.id.btn_back) {
			ActivityHelper.startOriginalActivityAndFinish(this);
		}
	}
}
