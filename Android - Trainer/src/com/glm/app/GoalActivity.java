package com.glm.app;


import com.glm.app.db.Database;
import com.glm.app.stopwatch.WorkOutActivity;

import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TimePicker;

public class GoalActivity extends Activity implements OnClickListener {
	private Button oRunButton;
	private Spinner oSpinner;
	private ArrayAdapter<CharSequence> adapter;
	private TimePicker oTimeGoal;
	private String sType="0";
	private CheckBox oCheckGoal;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /***    
		NotificationManager notificationManager = (NotificationManager)getSystemService(this.NOTIFICATION_SERVICE);

		int icon = R.drawable.trainer_notification_icon;
		CharSequence text = "Notification Text";
		CharSequence contentTitle = "Notification Title";
		CharSequence contentText = "Sample notification text.";
		long when = System.currentTimeMillis();
				
		Notification notification = new Notification(icon,text,when);						
		notificationManager.notify(APP_NOTIFICATION_ID, notification);
		*/
        setContentView(R.layout.goal_run);
        
        oRunButton = (Button) findViewById(R.id.btnRun);
        
        oSpinner = ((Spinner) findViewById(R.id.spDistance));       	    				
		adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.goal_distance_km, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        	       	    	        
		oSpinner.setAdapter(adapter);
        
		oCheckGoal = ((CheckBox) findViewById(R.id.checkGoal));
		
		oTimeGoal = ((TimePicker) findViewById(R.id.timeGoal));       	    				
		oTimeGoal.setIs24HourView(true);
		oTimeGoal.setCurrentHour(0);
		oTimeGoal.setCurrentMinute(0);
		
        oRunButton.setOnClickListener(this);
                        
        oCheckGoal.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String sSelected="0";	       	    						
					Database oDb = new Database(getApplicationContext());
					oDb.open();
					SQLiteDatabase oDataBase = oDb.getOpenedDatabase();
					if(!oCheckGoal.isChecked()) sSelected="1";
					oDataBase.execSQL("UPDATE trainer_pref SET value='"+sSelected+"' WHERE id_pref='18'");
					//Log.v(this.getClass().getCanonicalName(), "UPDATE trainer_pref SET value='"+sSelected+"' WHERE name='"+getApplicationContext().getString(R.string.target).replaceAll("'", "''")+"'");
					oDataBase.close();
					oDb.close();
					ExerciseUtils.updatePreference(getApplicationContext(),18,Integer.parseInt(sSelected));
					oDb=null;
					oDataBase=null;
				}
	        });
    }

    @Override
    public void onClick(View v) {
		if (v.getId() == R.id.btnRun) {
			Bundle extras = getIntent().getExtras();
			if(extras !=null){
				sType = extras.getString("type");
			}
			if(sType.compareToIgnoreCase("0")==0){
				//Run
				Intent intent = ActivityHelper.createActivityIntent(GoalActivity.this,WorkOutActivity.class);
				intent.putExtra("type", 0);
				intent.putExtra("goalDistance", oSpinner.getSelectedItemId());
				intent.putExtra("goalHH", oTimeGoal.getCurrentHour());
				intent.putExtra("goalMM", oTimeGoal.getCurrentMinute());
				ActivityHelper.startNewActivityAndFinish(GoalActivity.this, intent);
			}else if(sType.compareToIgnoreCase("1")==0){
				//Bike
				Intent intent = ActivityHelper.createActivityIntent(GoalActivity.this,WorkOutActivity.class);
				intent.putExtra("type", 1);
				intent.putExtra("goalDistance", oSpinner.getSelectedItemId());
				intent.putExtra("goalHH", oTimeGoal.getCurrentHour());
				intent.putExtra("goalMM", oTimeGoal.getCurrentMinute());
				ActivityHelper.startNewActivityAndFinish(GoalActivity.this, intent);
			}else if(sType.compareToIgnoreCase("100")==0){
				//Walk
				Intent intent = ActivityHelper.createActivityIntent(GoalActivity.this,WorkOutActivity.class);
				intent.putExtra("type", 100);
				intent.putExtra("goalDistance", oSpinner.getSelectedItemId());
				intent.putExtra("goalHH", oTimeGoal.getCurrentHour());
				intent.putExtra("goalMM", oTimeGoal.getCurrentMinute());
				ActivityHelper.startNewActivityAndFinish(GoalActivity.this, intent);
			}
				
		}
    }
    @Override
    public void onBackPressed() {
    	ActivityHelper.startOriginalActivityAndFinish(this);
    }
}
