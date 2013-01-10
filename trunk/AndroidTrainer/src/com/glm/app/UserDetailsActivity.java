package com.glm.app;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;


import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.glm.app.UserDetailsActivity;
import com.glm.app.db.Database;
import com.glm.bean.ConfigTrainer;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;
import com.glm.utils.fb.FacebookConnector;
import com.glm.utils.tw.Const;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class UserDetailsActivity extends Activity implements OnClickListener{
	private Button obtn_Save;
	private Button obtn_Cancel;
	private ImageButton obtn_Back;
	
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
	//private CheckBox oCkGooglePlus;
	private CheckBox oCkTwitter;
	
	/**FaceBook Integration*/
	private FacebookConnector oFB = null;
	/**Twitter Integration*/
	private Twitter oTwitter;
    
	private ConfigTrainer oConfigTrainer;
	
	private Animation a;
	private LinearLayout oMainLayout;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
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
        obtn_Back   = (ImageButton) findViewById(R.id.btn_back);
        
        oTxtWeight  = (TextView) findViewById(R.id.txtWeight);
        oTxtAge		= (TextView) findViewById(R.id.txtAge);
        oTxtHeight	= (TextView) findViewById(R.id.txtHeight);
        
        olblWeight  = (TextView) findViewById(R.id.lblWeight);
        olblHeight	= (TextView) findViewById(R.id.lblHeight);
        
        oEdtNick	= (EditText) findViewById(R.id.editNick);
        oEdtName	= (EditText) findViewById(R.id.editName);
        
        RBMale		= (RadioButton) findViewById(R.id.radioGenderM);
        RBFemale	= (RadioButton) findViewById(R.id.radioGenderF);
    	
        oCkFB				= (CheckBox) findViewById(R.id.ckFB);       
    	//oCkGooglePlus		= (CheckBox) findViewById(R.id.ckGoogle);
    	oCkTwitter			= (CheckBox) findViewById(R.id.ckTwitter);
    	
    	
        //obtn_back.setOnClickListener(this);
        obtn_Save.setOnClickListener(this);
        obtn_Cancel.setOnClickListener(this);
        obtn_Back.setOnClickListener(this);
        oCkFB.setOnClickListener(this);
    	//oCkGooglePlus.setOnClickListener(this);
    	oCkTwitter.setOnClickListener(this);
    	RBMale.setOnClickListener(this);
    	RBFemale.setOnClickListener(this);
    	
    	oConfigTrainer=ExerciseUtils.loadConfiguration(getApplicationContext(),true);
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
    	
    	oTxtWeight.setText(String.valueOf(oConfigTrainer.getiWeight()));
    	oTxtHeight.setText(String.valueOf(oConfigTrainer.getiHeight()));
		oTxtAge.setText(String.valueOf(oConfigTrainer.getiAge()));  
		
		oEdtNick.setText(oConfigTrainer.getsNick());
		oEdtName.setText(oConfigTrainer.getsName());
			
		if(oConfigTrainer.getsGender().compareToIgnoreCase("M")==0){
			RBMale.setChecked(true);
		}else{
			RBFemale.setChecked(true);
		}
			
		if(oConfigTrainer.isShareFB()){
			oCkFB.setChecked(true);
			Log.v(this.getClass().getCanonicalName(), "oCkFB.setChecked(true)");
		}
			
		if(oConfigTrainer.isShareTwitter()){
			oCkTwitter.setChecked(true);				
		}
        oMainLayout.clearAnimation();
        oMainLayout.setAnimation(a);  
        
        /*oFB = new FacebookConnector(getApplicationContext(),UserDetailsActivity.this);	
        Bundle params = new Bundle();
		
		params.putString("message", "test");
		params.putString("name", getString(R.string.app_name_pro));
		params.putString("description", getString(R.string.app_description));
		params.putString("picture", "https://lh6.googleusercontent.com/-hud-Azph6-Q/Th7YxoTZGqI/AAAAAAAAGBs/a-wG1jNYeY8/s640/personal-trainer.png");
		oFB.postMessageOnWall(params);*/
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
		}else if(oView.getId()==R.id.btn_back){
			try{
				ActivityHelper.startOriginalActivityAndFinish(this);			
			}catch (NullPointerException e) {
				//ActivityHelper.startOriginalActivityAndFinish(getParent());		
				Intent intent = ActivityHelper.createActivityIntent(this,MainTrainerActivity.class);
				//startActivity(intent);
				ActivityHelper.startNewActivityAndFinish(this, intent);	
				//Log.e(this.getClass().getCanonicalName(),"Error back");
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
		}/*else if((oView.getId()==R.id.ckGoogle)){
			Toast.makeText(getBaseContext(), 
                    getString(R.string.coming_soon), 
                    Toast.LENGTH_SHORT).show();
			oCkGooglePlus.setChecked(false);
			             
		}*/else if(oView.getId()==R.id.ckTwitter){
			
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
				saveUser();
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
				try {
					saveUser();
					oFB = new FacebookConnector(getApplicationContext(),UserDetailsActivity.this);
					oFB.logout();
					ExerciseUtils.shareFaceBook(getApplicationContext(), oCkFB.isChecked());
					SharedPreferences oPrefs = getSharedPreferences("aTrainer",Context.MODE_PRIVATE);
					SharedPreferences.Editor editPrefs = oPrefs.edit();
					editPrefs.putBoolean("share_fb", false); 
					editPrefs.commit();
				} catch (Exception e) {
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
			Log.v(this.getClass().getCanonicalName(),"oCkTwitter.isChecked():"+oCkTwitter.isChecked()+" - oCkFB.isChecked():"+oCkFB.isChecked());
			if(ExerciseUtils.saveUserData(getApplicationContext(), oEdtNick.getText().toString(), oEdtName.getText().toString(),
			        oTxtWeight.getText().toString(), oTxtAge.getText().toString(), oTxtHeight.getText().toString(), oCkFB.isChecked(), 
			    	false, oCkTwitter.isChecked(),"M")){
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
			Log.v(this.getClass().getCanonicalName(),"oCkTwitter.isChecked():"+oCkTwitter.isChecked()+" - oCkFB.isChecked():"+oCkFB.isChecked());
			if(ExerciseUtils.saveUserData(getApplicationContext(), oEdtNick.getText().toString(), oEdtName.getText().toString(),
			        oTxtWeight.getText().toString(), oTxtAge.getText().toString(), oTxtHeight.getText().toString(), oCkFB.isChecked(), 
			    	false, oCkTwitter.isChecked(),"F")){
				  Toast.makeText(getBaseContext(), 
		                    getString(R.string.usersaved), 
		                    Toast.LENGTH_SHORT).show();
				  obtn_Save.setEnabled(false);
			}
		}
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (exception instanceof FacebookOperationCanceledException ||
                exception instanceof FacebookAuthorizationException) {
                new AlertDialog.Builder(UserDetailsActivity.this)
                    .setTitle(R.string.cancel_user)
                    .setMessage(R.string.no)
                    .setPositiveButton(R.string.yes, null)
                    .show();
          
        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
           Log.v(this.getClass().getCanonicalName(),"Pendinng");
        }
        
    }
	
	@Override
	protected void onResume() {		
		super.onResume();
		obtn_Save.setEnabled(true);
		//getUserFromDB();
		oConfigTrainer=ExerciseUtils.loadConfiguration(getApplicationContext(),true);
		oTxtWeight.setText(String.valueOf(oConfigTrainer.getiWeight()));
    	oTxtHeight.setText(String.valueOf(oConfigTrainer.getiHeight()));
		oTxtAge.setText(String.valueOf(oConfigTrainer.getiAge()));  
		
		oEdtNick.setText(oConfigTrainer.getsNick());
		oEdtName.setText(oConfigTrainer.getsName());
			
		if(oConfigTrainer.getsGender().compareToIgnoreCase("M")==0){
			RBMale.setChecked(true);
		}else{
			RBFemale.setChecked(true);
		}
			
		if(oConfigTrainer.isShareFB()){
			oCkFB.setChecked(true);
			Log.v(this.getClass().getCanonicalName(), "oCkFB.setChecked(true)");
		}
			
		if(oConfigTrainer.isShareTwitter()){
			oCkTwitter.setChecked(true);				
		}
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
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    Session.getActiveSession()
	        .onActivityResult(this, requestCode, resultCode, data);
	} 
}
