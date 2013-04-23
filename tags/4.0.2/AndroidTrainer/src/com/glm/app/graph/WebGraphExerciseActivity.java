package com.glm.app.graph;

import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.glm.app.ActivityHelper;
import com.glm.bean.ConfigTrainer;
import com.glm.bean.ExerciseManipulate;
import com.glm.chart.LineChart;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;
import com.glm.utils.GraphAdapter;

public class WebGraphExerciseActivity extends Activity{
	private ConfigTrainer oConfigTrainer;	
	
	private String sType="0";	
	
	private ViewFlow viewFlow;
	private GraphAdapter mGraphadapter;
	private CircleFlowIndicator indic;
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.flow_exercise_graph);        
	        
	        oConfigTrainer = ExerciseUtils.loadConfiguration(this,true);	
	        ExerciseUtils.populateExerciseDetails(this, oConfigTrainer, ExerciseManipulate.getiIDExercise());	        	       	      
	        
	        viewFlow 	  = (ViewFlow) findViewById(R.id.viewflow);
	        indic 		  = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
	        
	        mGraphadapter = new GraphAdapter(getApplicationContext());
	          
		    	    
		    viewFlow.setAdapter(mGraphadapter);
		    viewFlow.setFlowIndicator(indic);	 
		    
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
/*	        Bundle extras = getIntent().getExtras();
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
	        oBtnBpm.setOnClickListener(this);*/
	 }
	/* If your min SDK version is < 8 you need to trigger the onConfigurationChanged in ViewFlow manually, like this */	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//viewFlow.onConfigurationChanged(newConfig);
	}
	 @Override
	 public void onBackPressed() {
		 ActivityHelper.startOriginalActivityAndFinish(this);
		 this.finish();
	 }
	/*@Override
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
	}*/
}
