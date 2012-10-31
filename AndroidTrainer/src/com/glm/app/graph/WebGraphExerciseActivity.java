package com.glm.app.graph;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebSettings.ZoomDensity;
import android.widget.Button;
import android.widget.LinearLayout;

import com.glm.app.ActivityHelper;
import com.glm.bean.ConfigTrainer;
import com.glm.bean.ExerciseManipulate;
import com.glm.chart.LineChart;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;
import com.glm.utils.JsHandler;

public class WebGraphExerciseActivity extends Activity implements OnClickListener{
	private ConfigTrainer oConfigTrainer;
	private WebView wv;
	private Button oBtnALT;
	
	private Button oBtnPace;
	
	private Button oBtnBpm;
	
	private String sType="0";
	
	private LinearLayout oGraphLayout;
	
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.web_exercise_graph);        
	        wv = (WebView) findViewById(R.id.wv1); 
	        
	        oBtnALT		  = (Button) findViewById(R.id.btnALT);
	        oBtnPace	  = (Button) findViewById(R.id.btnPace);
	        oBtnBpm 	  = (Button) findViewById(R.id.btnBPM);
	        oGraphLayout  = (LinearLayout) findViewById(R.id.graphLayout);
	        
	        oConfigTrainer = ExerciseUtils.loadConfiguration(this);				
		    ExerciseUtils.populateExerciseDetails(this, oConfigTrainer, ExerciseManipulate.getiIDExercise());
		 
	        /*JsHandler jshandler = new JsHandler (wv,ExerciseUtils.getWeightData(this),getApplicationContext());
	        try {	           
	            // Load the local file into the webview\
	        	wv.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
	            wv.getSettings().setBuiltInZoomControls(false);		           
	            wv.getSettings().setJavaScriptEnabled(true);
	            wv.addJavascriptInterface(jshandler, "jshandler");
	            //jshandler.loadGraph();
	            //wv.loadData(text, "text/html", "UTF-8");
	            //Log.v(this.getClass().getCanonicalName(), "LoadURL:"+"file:///android_asset/jflot/graphtrainerexercise.html");
	            wv.loadUrl("file:///android_asset/jflot/graphtrainerexercise.html");
	        } catch (Exception e) {
	            // Should never happen!
	            throw new RuntimeException(e);
	        }*/
	        Bundle extras = getIntent().getExtras();
	        if(extras !=null)
	        {
	     	   sType = extras.getString("graph");
	     	   if(sType.compareToIgnoreCase("0")==0){
					//wv.loadUrl("file:///android_asset/jflot/graphtrainerexercisealt.html");
					LineChart oChart = new LineChart(getApplicationContext(),0);
					oGraphLayout.addView(oChart);
					
		        }else if (sType.compareToIgnoreCase("1")==0){
		            //wv.loadUrl("file:///android_asset/jflot/graphtrainerexercise.html");
		            LineChart oChart = new LineChart(getApplicationContext(),1);
					oGraphLayout.addView(oChart);
		        }else if (sType.compareToIgnoreCase("2")==0){
		        	//wv.loadUrl("file:///android_asset/jflot/graphtrainerexercisebpm.html");
		        	LineChart oChart = new LineChart(getApplicationContext(),2);
					oGraphLayout.addView(oChart);
		        }
	        }
	        if(oConfigTrainer.isbCardioPolarBuyed()){	    		  
	    		oBtnBpm.setVisibility(View.VISIBLE);
	    	}else{	    		
	    		oBtnBpm.setVisibility(View.INVISIBLE);
	    	}
	        oBtnALT.setOnClickListener(this);
	        oBtnPace.setOnClickListener(this);     
	        oBtnBpm.setOnClickListener(this);
	 }
	 @Override
	 public void onBackPressed() {
		 ActivityHelper.startOriginalActivityAndFinish(this);
		 this.finish();
	 }
	@Override
	public void onClick(View oObj) {
		if(oObj.getId()==R.id.btnALT){
			//wv.loadUrl("file:///android_asset/jflot/graphtrainerexercisealt.html");
			LineChart oChart = new LineChart(getApplicationContext(),0);			
			oGraphLayout.removeAllViews();
			oGraphLayout.addView(oChart);
		}else if (oObj.getId()==R.id.btnPace){
			//wv.loadUrl("file:///android_asset/jflot/graphtrainerexercise.html");			
        	LineChart oChart = new LineChart(getApplicationContext(),1);
        	oGraphLayout.removeAllViews();
			oGraphLayout.addView(oChart);
		}else if(oObj.getId()==R.id.btnBPM){
			//wv.loadUrl("file:///android_asset/jflot/graphtrainerexercisebpm.html");			
        	LineChart oChart = new LineChart(getApplicationContext(),2);
        	oGraphLayout.removeAllViews();
			oGraphLayout.addView(oChart);
		}
	}
}
