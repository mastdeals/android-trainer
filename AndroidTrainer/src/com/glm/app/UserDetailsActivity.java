package com.glm.app;

import java.io.IOException;
import java.net.MalformedURLException;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;


import com.glm.app.UserDetailsActivity;
import com.glm.app.db.Database;
import com.glm.bean.ConfigTrainer;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;
import com.glm.utils.fb.FacebookConnector;
import com.glm.utils.tw.Const;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class UserDetailsActivity extends Activity implements OnClickListener{
	private Button obtn_Save;
	private Button obtn_Cancel;
	
	/**Pulsanti +/- Weight*/
	private TextView oTxtWeight;
	/**Pulsanti +/- Weight*/
	
	/**Pulsanti +/- Age*/
	private TextView oTxtAge;
	/**Pulsanti +/- Age*/
	
	private TextView oTxtHeight;
	
	private TextView olblHeight;
	private TextView olblWeight;
	
	private EditText oEdtNick;
	private EditText oEdtName;
	
	private RadioButton RBMale;
	private RadioButton RBFemale;
	
	private CheckBox oCkFB;
	private CheckBox oCkGooglePlus;
	private CheckBox oCkTwitter;
	
	/**FaceBook Integration*/
	private FacebookConnector oFB = null;
	/**Twitter Integration*/
	private Twitter oTwitter;
    
	private ConfigTrainer oConfigTrainer;
	
	private Animation a;
	private LinearLayout oMainLayout;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        oTwitter = new TwitterFactory().getInstance();
		oTwitter.setOAuthConsumer(Const.CONSUMER_KEY, Const.CONSUMER_SECRET);
        
        a = AnimationUtils.loadAnimation(this, R.animator.slide_right);
        a.reset();
        
        setContentView(R.layout.new_user_details);
        oMainLayout = (LinearLayout) findViewById(R.id.main_layout);
        //obtn_back   = (Button) findViewById(R.id.btn_back);        
        obtn_Save   = (Button) findViewById(R.id.btnSave);
        obtn_Cancel = (Button) findViewById(R.id.btnCancel);               
        
        oTxtWeight  = (TextView) findViewById(R.id.txtWeight);
        oTxtAge		= (TextView) findViewById(R.id.txtAge);
        oTxtHeight	= (TextView) findViewById(R.id.txtHeight);
        
        olblWeight  = (TextView) findViewById(R.id.lblWeight);
        olblHeight	= (TextView) findViewById(R.id.lblHeight);
        
        oEdtNick	= (EditText) findViewById(R.id.editNick);
        oEdtName	= (EditText) findViewById(R.id.editName);
        
        RBMale		= (RadioButton) findViewById(R.id.radioGenderM);
        RBFemale	= (RadioButton) findViewById(R.id.radioGenderF);
    	
        oCkFB		= (CheckBox) findViewById(R.id.ckFB);
    	oCkGooglePlus		= (CheckBox) findViewById(R.id.ckGoogle);
    	oCkTwitter	= (CheckBox) findViewById(R.id.ckTwitter);
    	
    	
        //obtn_back.setOnClickListener(this);
        obtn_Save.setOnClickListener(this);
        obtn_Cancel.setOnClickListener(this);
        oCkFB.setOnClickListener(this);
    	oCkGooglePlus.setOnClickListener(this);
    	oCkTwitter.setOnClickListener(this);
    	RBMale.setOnClickListener(this);
    	RBFemale.setOnClickListener(this);
    	
    	oConfigTrainer=ExerciseUtils.loadConfiguration(getApplicationContext());
    	if(oConfigTrainer!=null){
    		if(oConfigTrainer.getiUnits()==0){
        		olblWeight.setText(olblWeight.getText()+" (Kg)");
        		olblHeight.setText(olblHeight.getText()+" (cm)");
        	}else{
        		olblWeight.setText(olblWeight.getText()+" (Lbs)");
        		olblHeight.setText(olblHeight.getText()+" (in)");
        	}
    	}
    		
    	oTxtWeight.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				
				return false;
			}
		});
        oMainLayout.clearAnimation();
        oMainLayout.setAnimation(a);  
    }

	/**
	 * Popolo i widget con i valori che leggo nel DB
	 * 
	 * TODO Da sostituire!!!
	 * */
	private void getUserFromDB() {
		try{
			Database oDB = new Database(this);		   	
			oDB.open();  		   	
		   	Cursor oCursor = oDB.fetchAll("trainer_users", null,null);
		   	if(oCursor!=null){        		
		   		int iKey 	 = oCursor.getColumnIndex("id_users");   			
	   			int iNick 	 = oCursor.getColumnIndex("nick");
	   			int iName 	 = oCursor.getColumnIndex("name");
	   			int iGender  = oCursor.getColumnIndex("gender");
	   			int iAge  	 = oCursor.getColumnIndex("age");
	   			int iWeight  = oCursor.getColumnIndex("weight");
	   			int iFB 	 = oCursor.getColumnIndex("facebook");
	   			int iBuzz 	 = oCursor.getColumnIndex("buzz");
	   			int iTwitter = oCursor.getColumnIndex("twitter");   			   			   			
	   			int iHeight  = oCursor.getColumnIndex("height");
 	   			
	   			while(oCursor.moveToNext()){ 
	   				oTxtWeight.setText(oCursor.getString(iWeight));
	   				oTxtAge.setText(oCursor.getString(iAge));  
	   				oTxtHeight.setText(oCursor.getString(iHeight));  
	   				
	   				oEdtNick.setText(oCursor.getString(iNick));
	   				oEdtName.setText(oCursor.getString(iName));
	   				
	   				if(oCursor.getString(iGender).compareToIgnoreCase("M")==0){
	   					RBMale.setChecked(true);
	   				}else{
	   					RBFemale.setChecked(true);
	   				}
	   				
	   				if(oCursor.getInt(iFB)==1){
	   					oCkFB.setChecked(true);
	   			    	
	   				}
	   				if(oCursor.getInt(iBuzz)==1){
	   					oCkGooglePlus.setChecked(true);	   			    		
	   				}
	   				if(oCursor.getInt(iTwitter)==1){
	   					oCkTwitter.setChecked(true);
	   				}
	   			}
	   			
	   			////Log.v(this.getClass().getCanonicalName(), "Exercise Max: "+fMaxDistance);	
	   			////Log.v(this.getClass().getCanonicalName(), "Max Width: "+dm.widthPixels);	  			
	   			oCursor.close();
	   		   	oDB.close();
	   		   	oCursor=null;
	   		   	oDB=null;
		   	}
		}catch (RuntimeException e) {
			Log.e(this.getClass().getCanonicalName()," Error RUNTIME");
		}
	}

	@Override
	public void onClick(View oView) {
		if(oView.getId()==R.id.btnSave){						
			saveUser();
			try{
				ActivityHelper.startOriginalActivityAndFinish(this);			
			}catch (NullPointerException e) {
				//ActivityHelper.startOriginalActivityAndFinish(getParent());		
				Intent intent = ActivityHelper.createActivityIntent(this,TwitterAuthActivity.class);
				//startActivity(intent);
				ActivityHelper.startNewActivityAndFinish(this, intent);	
				Log.e(this.getClass().getCanonicalName(),"Error back");
			}
		}else if(oView.getId()==R.id.btnCancel){
			try{
				ActivityHelper.startOriginalActivityAndFinish(this);			
			}catch (NullPointerException e) {
				//ActivityHelper.startOriginalActivityAndFinish(getParent());		
				Intent intent = ActivityHelper.createActivityIntent(this,TwitterAuthActivity.class);
				//startActivity(intent);
				ActivityHelper.startNewActivityAndFinish(this, intent);	
				Log.e(this.getClass().getCanonicalName(),"Error back");
			}
		}else if((oView.getId()==R.id.ckGoogle)){
			Toast.makeText(getBaseContext(), 
                    getString(R.string.coming_soon), 
                    Toast.LENGTH_SHORT).show();
			oCkGooglePlus.setChecked(false);
			             
		}else if(oView.getId()==R.id.ckTwitter){
			
			if(oCkTwitter.isChecked()){
				saveUser();		
				//Implementare il salvataggio dei Check
				if(ExerciseUtils.shareTwitter(getApplicationContext(), oCkTwitter.isChecked())){
					//ActivityHelper.startOriginalActivityAndFinish(getParent());		
					Intent intent = ActivityHelper.createActivityIntent(this,TwitterAuthActivity.class);
					//startActivity(intent);
					ActivityHelper.startNewActivityAndFinish(this, intent);			 										
				}
			}else{				
				ExerciseUtils.shareTwitter(getApplicationContext(), oCkTwitter.isChecked());
				SharedPreferences oPrefs = getSharedPreferences("aTrainer",Context.MODE_PRIVATE);
				SharedPreferences.Editor editPrefs = oPrefs.edit();
				editPrefs.putBoolean("share_twitter", false); 
				editPrefs.commit();
				obtn_Save.setEnabled(true);
			}						
		}else if(oView.getId()==R.id.ckFB){
			
			if(oCkFB.isChecked()){
				saveUser();
				//Implementare il salvataggio dei Check
				if(ExerciseUtils.shareFaceBook(getApplicationContext(), oCkFB.isChecked())){			
			        oFB = new FacebookConnector(getApplicationContext(),UserDetailsActivity.this);	
					//Intent intentMain = ActivityHelper.createActivityIntent(UserDetailsActivity.this,FacebookConnect.class);
         			//startActivity(intent);
         			//ActivityHelper.startNewActivityAndFinish(UserDetailsActivity.this, intentMain);	
				}
			}else{			
				ExerciseUtils.shareFaceBook(getApplicationContext(), oCkFB.isChecked());
				SharedPreferences oPrefs = getSharedPreferences("aTrainer",Context.MODE_PRIVATE);
				SharedPreferences.Editor editPrefs = oPrefs.edit();
				editPrefs.putBoolean("share_fb", false); 
				editPrefs.commit();
				
				try {
					oFB = new FacebookConnector(getApplicationContext(),UserDetailsActivity.this);
					oFB.getFacebookClient().logout(getApplicationContext());
				} catch (MalformedURLException e) {
					 Log.e(this.getClass().getCanonicalName(), "MalformedURLException FB");
				} catch (IOException e) {
					 Log.e(this.getClass().getCanonicalName(), "IOException FB");
				}
				obtn_Save.setEnabled(true);
			}						
		}else{
			obtn_Save.setEnabled(true);
		}
	}
	private void saveUser() {
		if(RBMale.isChecked()){
			try{
				Integer.parseInt(oTxtAge.getText().toString());
			}catch (NumberFormatException e) {
				 Toast.makeText(getBaseContext(), 
		                    getString(R.string.age_error), 
		                    Toast.LENGTH_SHORT).show();
				 return;
			}
			try{
				Float.parseFloat(oTxtWeight.getText().toString());
			}catch (NumberFormatException e) {
				 Toast.makeText(getBaseContext(), 
		                    getString(R.string.weight_error), 
		                    Toast.LENGTH_SHORT).show();
				 return;
			}
			
			if(ExerciseUtils.saveUserData(getApplicationContext(), oEdtNick.getText().toString(), oEdtName.getText().toString(),
			        oTxtWeight.getText().toString(), oTxtAge.getText().toString(), oTxtHeight.getText().toString(), oCkFB.isChecked(), 
			    	oCkGooglePlus.isChecked(), oCkTwitter.isChecked(),"M")){
				  Toast.makeText(getBaseContext(), 
		                    getString(R.string.usersaved), 
		                    Toast.LENGTH_SHORT).show();
				  obtn_Save.setEnabled(false);
			}
			
		}else{
			try{
				Integer.parseInt(oTxtAge.getText().toString());
			}catch (NumberFormatException e) {
				 Toast.makeText(getBaseContext(), 
		                    getString(R.string.age_error), 
		                    Toast.LENGTH_SHORT).show();
				 return;
			}
			try{
				Integer.parseInt(oTxtWeight.getText().toString());
			}catch (NumberFormatException e) {
				 Toast.makeText(getBaseContext(), 
		                    getString(R.string.weight_error), 
		                    Toast.LENGTH_SHORT).show();
				 return;
			}
			if(ExerciseUtils.saveUserData(getApplicationContext(), oEdtNick.getText().toString(), oEdtName.getText().toString(),
			        oTxtWeight.getText().toString(), oTxtAge.getText().toString(), oTxtHeight.getText().toString(), oCkFB.isChecked(), 
			    	oCkGooglePlus.isChecked(), oCkTwitter.isChecked(),"F")){
				  Toast.makeText(getBaseContext(), 
		                    getString(R.string.usersaved), 
		                    Toast.LENGTH_SHORT).show();
				  obtn_Save.setEnabled(false);
			}
		}
	}

	@Override
	protected void onResume() {		
		super.onResume();
		obtn_Save.setEnabled(true);
		getUserFromDB();
	}
	 @Override
    public void onBackPressed() {
		try{
			ActivityHelper.startOriginalActivityAndFinish(this);			
		}catch (NullPointerException e) {
			//ActivityHelper.startOriginalActivityAndFinish(getParent());		
			Intent intent = ActivityHelper.createActivityIntent(this,MainTrainerActivity.class);
			//startActivity(intent);
			ActivityHelper.startNewActivityAndFinish(this, intent);	
			//Log.e(this.getClass().getCanonicalName(),"Error back");
		}
    }  
}
