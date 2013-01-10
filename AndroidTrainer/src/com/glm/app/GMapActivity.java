package com.glm.app;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.google.android.maps.MapActivity;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import com.glm.bean.ConfigTrainer;
import com.glm.bean.ExerciseManipulate;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;
import com.glm.utils.TrackOverlay;

public class GMapActivity extends MapActivity implements OnClickListener{
	private ConfigTrainer oConfigTrainer;
	private ImageButton oBtnBack;
	private MapView oMapView;
	
	private List<Overlay> mapOverlays;
	
	private MapController oMapController;
	private GeoPoint TrackPoints [];
	private TrackOverlay oTrack;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.new_exercise_gmap);
	   	    
	   
	    oMapView = (MapView) findViewById(R.id.gmap);
	    oBtnBack = (ImageButton) findViewById(R.id.btn_back); 
	    oBtnBack.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		try{
			Bundle extras = getIntent().getExtras();
			 
			oMapView.setBuiltInZoomControls(true); 
		    oMapView.setEnabled(true);
		    
		    mapOverlays = oMapView.getOverlays();        
		   
		    oConfigTrainer = ExerciseUtils.loadConfiguration(this,true);				
		   
		    if(extras !=null){
		    	ExerciseUtils.populateExerciseDetails(this, oConfigTrainer,  extras.getInt("exercise"));	    	
	        }
		    int maxZoom = oMapView.getMaxZoomLevel();
	        int initZoom = maxZoom-6;
	        oMapController = oMapView.getController();
	        oMapController.setZoom(initZoom);
	        
	        oTrack = new TrackOverlay(getApplicationContext(),TrackPoints,ExerciseManipulate.getWatchPoint(),ExerciseManipulate.getsTotalTime(),ExerciseManipulate.getsTotalDistance());
	        
		    mapOverlays.add(oTrack);    
		    
		    oMapController.setCenter(oTrack.getCenter());
		    oMapView.invalidate();
		}catch (NullPointerException e) {
			Log.e(this.getClass().getCanonicalName(), "Unable to start map");
			
		}
		
	}
	
	@Override
    public void onBackPressed() {
		ActivityHelper.startOriginalActivityAndFinish(this);
    }
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View oObj) {
		if(oObj.getId()==R.id.btn_back){			
			ActivityHelper.startOriginalActivityAndFinish(this);
		}
	}

}
