package com.glm.app;

import java.util.ArrayList;

import com.glm.trainer.R;


import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TrainerHistoryActivityGroup extends ActivityGroup{	
	 
	public static TrainerHistoryActivityGroup group; 
     // Need to keep track of the history if you want the back-button to work properly, don't use this if your activities requires a lot of memory.  
	 private ArrayList<View> history;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.history = new ArrayList<View>();  
		group = this; 
		//you van get the local activitymanager to start the new activity

		View view = getLocalActivityManager()
		.startActivity("HistoryActivity", new
				Intent(this, HistoryActivity.class)
		.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
		.getDecorView();
		replaceView(view);
	}
	public void replaceView(View v) {  
		// Adds the old one to history  
		history.add(v);  
		// Changes this Groups View to the new View.  
		setContentView(v);  
	}  

	public void back() {  
		if(history.size() > 0) {  
			history.remove(history.size()-1);  
			setContentView(history.get(history.size()-1));  
		}else {  
			finish();  
		}  
	}  

	@Override  
	public void onBackPressed() {  
		AlertDialog alertDialog;
    	alertDialog = new AlertDialog.Builder(this).create();
    	alertDialog.setTitle(this.getString(R.string.titleendapp));
    	alertDialog.setMessage(this.getString(R.string.messageendapp));
    	alertDialog.setButton(this.getString(R.string.yes), new android.content.DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}        				
    		});
    	
    	alertDialog.setButton2(this.getString(R.string.no), new android.content.DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {					
				
			}        		
			
    		});
    	alertDialog.show();
	}  
}
