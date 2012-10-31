package com.glm.app.graph;

import android.app.Activity;
import android.os.Bundle;
//import android.util.Log;
//import android.webkit.WebView;
//import android.webkit.WebSettings.ZoomDensity;

import com.glm.app.ActivityHelper;
import com.glm.chart.LineChart;
//import com.glm.trainer.R;
//import com.glm.utils.ExerciseUtils;
//import com.glm.utils.JsHandler;

public class WebGraphWeightActivity extends Activity {
	 public void onCreate(Bundle savedInstanceState) {
		 	LineChart oChart = new LineChart(getApplicationContext(),-1);
	        super.onCreate(savedInstanceState);
	        setContentView(oChart);        
	        /*WebView wv = (WebView) findViewById(R.id.wv1); 
	        JsHandler jshandler = new JsHandler (wv,ExerciseUtils.getWeightData(this),getApplicationContext());
	        try {	           
	            // Load the local file into the webview\
	        	wv.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
	            wv.getSettings().setBuiltInZoomControls(false);		           
	            wv.getSettings().setJavaScriptEnabled(true);
	            wv.addJavascriptInterface(jshandler, "jshandler");
	            //jshandler.loadGraph();
	            //wv.loadData(text, "text/html", "UTF-8");
	            //Log.v(this.getClass().getCanonicalName(), "LoadURL:"+"file:///android_asset/jflot/graphtrainer.html");
	            wv.loadUrl("file:///android_asset/jflot/graphtrainerweight.html");
	        } catch (Exception e) {
	            // Should never happen!
	            throw new RuntimeException(e);
	        }*/
	 }
	 @Override
	 public void onBackPressed() {
		 ActivityHelper.startOriginalActivityAndFinish(this);
		 this.finish();
	 }
}
