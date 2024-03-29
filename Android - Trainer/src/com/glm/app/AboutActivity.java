package com.glm.app;


import com.glm.app.db.Database;
import com.glm.trainer.R;
import com.glm.utils.sensor.BlueToothHelper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
public class AboutActivity extends Activity implements OnClickListener {
	private TextView txtVersion;
	
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
        setContentView(R.layout.info);
        
        Button oMailButton = (Button) findViewById(R.id.btn_mail_segnalazione);
        
        txtVersion = (TextView) findViewById(R.id.txtVersion);
        Database oDBDatabase = new Database(getApplicationContext());
        
        txtVersion.setText(oDBDatabase.getDataBaseVersionAndLicence(null));
        oMailButton.setOnClickListener(this);
        //TEST BLUETOOCH	      		
   		   //Log.i("oBTHelper","Cont: "+oBTHelper.getaDeviceAddress().size());
   		   //Log.i("oBTHelper","Cont: "+oBTHelper.getaDeviceAddress().get(0));
   		   //Abilitale la connessione al cardio.
   		//oBTHelper.connect(device)   
               
    }

    @Override
    public void onClick(View v) {
		if (v.getId() == R.id.btn_mail_segnalazione) {		
			//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.giano-solutions.com")));
		    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		    emailIntent .setType("message/rfc822") ;
		    emailIntent .putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"laverdone@gmail.com"});
		    emailIntent .putExtra(android.content.Intent.EXTRA_SUBJECT, "Issue Android - Trainer for Android");
		    emailIntent .putExtra(android.content.Intent.EXTRA_TEXT, "");
		    //startActivityForResult(emailIntent,1);
		    startActivity(Intent.createChooser(emailIntent, "Seleziona l'applicazione email."));
		}
    }
    @Override
    public void onBackPressed() {
    	ActivityHelper.startOriginalActivityAndFinish(this);
    }
}
