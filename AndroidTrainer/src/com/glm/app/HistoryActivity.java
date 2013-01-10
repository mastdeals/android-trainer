package com.glm.app;


import java.io.File;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.glm.app.db.Database;
import com.glm.bean.ConfigTrainer;
import com.glm.bean.Exercise;
import com.glm.bean.ExerciseManipulate;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;
import com.glm.utils.fb.FacebookConnector;


public class HistoryActivity extends Activity implements OnClickListener {
	private DisplayMetrics dm = new DisplayMetrics();
	private Animation a;
	private final int I_CORRECT_WIDTH=99;
	private final int I_STEP_PER_PAGE=10;
	/**usato per calcolare il passo della paginazione*/
	private int iInitialStep=0;
	/**usato per definire la paginazione*/
	private int iPageStep=10;
	private ImageButton oBtn_prev;
	private ImageButton oBtn_next;
	private TextView oTextTitle;
	private int iRow=0;
	private int iWritedRow=0;
	//Prelevo prima tutte le distanze
	private	float fDistance=0;
	private	float fMaxDistance=0;
    /**contiene tutti i bean Exercise con i dettagli*/
	Vector<Exercise> vExercise = new Vector<Exercise>();
	private Context oContext;
	private ConfigTrainer oConfigTrainer;
	private Vector<LinearLayout> vRow = new Vector<LinearLayout>();
	
	private LinearLayout oRowLayout;
	private String sType="0";
	
	private int iWidth = 0;
	private int iHeight = 0;
	private final int IBAR_DEF_WIDTH=480;
	private final int IBAR_DEF_HEIGHT=800;
	private final int IBAR_WIDTH=290;
	private final int IBAR_HEIGHT=60;
	private int iMax_Bar_Height=0;
	private int iMax_Bar_Width=0;
	/***oggetto condivisione FB*/
	//private FacebookConnector oFB = null; 
	
	public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       oContext=this;
       oConfigTrainer = ExerciseUtils.loadConfiguration(this,true);
       setContentView(R.layout.new_new_history);
       
       
       Display display = getWindowManager().getDefaultDisplay(); 
       iWidth = display.getWidth();
       iHeight = display.getHeight();
       
       iMax_Bar_Width=Math.round((iWidth*IBAR_WIDTH)/IBAR_DEF_WIDTH);
       iMax_Bar_Height=Math.round((iHeight*IBAR_HEIGHT)/IBAR_DEF_HEIGHT);
       
       //Log.v(this.getClass().getCanonicalName(), "Screen Size W: "+iWidth+" H: "+iHeight
    //		   +" MAX_W: "+iMax_Bar_Width+" MAX_H: "+iMax_Bar_Height);
      
       
       a = AnimationUtils.loadAnimation(this, R.animator.fadein);
       a.reset();
       
       getWindowManager().getDefaultDisplay().getMetrics(dm); 
       
       oBtn_next = (ImageButton) findViewById(R.id.btn_forward);
       oBtn_prev = (ImageButton) findViewById(R.id.btn_back);
       oTextTitle = (TextView) findViewById(R.id.text_title);
       
       
       oBtn_next.setOnClickListener(this);
       oBtn_prev.setOnClickListener(this);
       oTextTitle.setOnClickListener(this);
       
       Bundle extras = getIntent().getExtras();
       if(extras !=null){
    	   sType = extras.getString("history");
       }
       if(sType.compareToIgnoreCase("0")==0){
			//Runnnig			   	   	   	    		
            oTextTitle.setText(getString(R.string.history_run));
		}else if(sType.compareToIgnoreCase("1")==0){			
			oTextTitle.setText(getString(R.string.history_bike));
		}else if(sType.compareToIgnoreCase("100")==0){			
            oTextTitle.setText(getString(R.string.history_walk));
		}
       
         
       oBtn_prev.setVisibility(View.INVISIBLE);
       //popolo gli esercizi dal DB
       populateExerciseFromDB();
      
       //Popolo la lista esercizi
       //populateHistory();
	}
	/**
	 * Popola la Riga con le Info dei vari esercizi
	 * 
	 * TODO Limitare la lista agli ultimi 10 esercizi
	 * **/
    private void populateHistory() {
    	try{
    		oRowLayout = (LinearLayout) findViewById(R.id.objMainLayout);
	    	/**cancellazione oggetti*/
	    	oRowLayout.removeAllViews();
	    	//Controllo se lo step e' superiore del vettore di esercizi 
	    	if(iPageStep>vExercise.size()){
	    		iPageStep=vExercise.size();
	    		oBtn_next.setVisibility(View.INVISIBLE);
	    	}
	    		   	
			float fParWidth=0;
			iWritedRow=0;
			for(iRow=iInitialStep;iRow<iPageStep;iRow++){
				
				//fParWidth=(ExerciseUtils.getTotalDistanceUnFormattated(getApplicationContext(), oConfigTrainer, vExercise.get(iRow).getsIDExerise(),null)/fMaxDistance)*(dm.widthPixels-I_CORRECT_WIDTH);
				fParWidth=(vExercise.get(iRow).getfDistance()/fMaxDistance)*(dm.widthPixels-I_CORRECT_WIDTH);
				
				vExercise.get(iRow).setiBar(Math.round(fParWidth));
				////Log.v(this.getClass().getCanonicalName(),"vExercise.get(iRow).getiBar()= "+vExercise.get(iRow).getiBar());
				//Non scrivo gli esercizi con distanza 0
				if(vExercise.get(iRow).getiBar()>0){
					//Scrivo solo se la barra � maggiore di 0
					populateHistoryRow(oRowLayout,vExercise.get(iRow).getsIDExerise(),
		   					vExercise.get(iRow).getsTitle(),vExercise.get(iRow).getsNote(),vExercise.get(iRow).getsStart(),vExercise.get(iRow).getsEnd(),vExercise.get(iRow).getiBar()
		   					,vExercise.get(iRow).getsDistanceFormatted(),vExercise.get(iRow).getsAVGSpeed(),vExercise.get(iRow).getsTotalTime(),vExercise.get(iRow).getsTotalKalories(), vExercise.get(iRow).getiTypeExercise(), iRow);
				}
				
				//Numero di righe scritte
				iWritedRow++;  				
			}
			//Associo lo step alla riga popolata
			iInitialStep=iRow;
			oRowLayout.clearAnimation();
	    	oRowLayout.setAnimation(a);    
    	}catch (Exception e) {
			Log.e(this.getClass().getCanonicalName(),"Error in paging");
			e.printStackTrace();
			iPageStep=I_STEP_PER_PAGE;
			iInitialStep=0;
		}
    	
	}
    /**
     * Popola la riga con tutti i dati di sommario dell'esercizio
     * 
     * **/
	private void populateHistoryRow(LinearLayout oRowLayout, String sIDExercise, 
									String sTitle, String sNote, String sStart, String sEnd, int iBar,final String sDistanceFormatted,
									final String sAVGSpeed, final String sTotalTime, final String sTotalKalories, final int iTypeExercise, int nRow) {
		
		//Inserimento della riga dinamica
	    LinearLayout oLinear = new LinearLayout(this);
	    oLinear = (LinearLayout) getLayoutInflater().inflate(R.layout.new_new_row_history, null);
	    String sDistance="";

	    
	    
	    try{
	    	int iChild=oLinear.getChildCount();
	    	for(int i=0;i<iChild;i++){
	   	    	if(i>0) break;
	   	    	
	   	    	//Log.v(this.getClass().getCanonicalName(),"oLinear "+i+": "+ oLinear.getChildAt(i).getClass().getCanonicalName());
	   	    	LinearLayout oInternal = ((LinearLayout) oLinear.getChildAt(i));
	   	    	int jjChild=oInternal.getChildCount();
	   	    	for(int jj=0;jj<jjChild;jj++){
	   	    		if(jj==0){
	   	    			//Log.v(this.getClass().getCanonicalName(),"oInternal "+i+": "+ oInternal.getChildAt(jj).getClass().getCanonicalName());	   	 	   	    
	   	    			
	   	    			RelativeLayout oInternalRelative = ((RelativeLayout) oInternal.getChildAt(jj));
	   	    			int jChild=oInternalRelative.getChildCount();
	   	    			for(int j=0;j<jChild;j++){
	   	    				//Log.v(this.getClass().getCanonicalName(),"oInternalRelative "+i+": "+ oInternalRelative.getChildAt(j).getClass().getCanonicalName());
	   		   	 	   	 
	   	   	   	    		if(j==0){
	   	   	   	    			/*if(j==0) continue;
	   	   	   	    			//BarLinear
	   	   	   	    			*//**BAR LINEAR*//*
	   	   	   	    			LinearLayout oTopLinear = ((LinearLayout) oInternalRelative.getChildAt(j));   	 
	   	   	   	    			int iTopChild=oTopLinear.getChildCount();
	   	    					for(int iTopLinear=0;iTopLinear<iTopChild;iTopLinear++){
	   	    						//Log.v(this.getClass().getCanonicalName(),"oBarLinear "+iTopLinear+": "+ oTopLinear.getChildAt(j).getClass().getCanonicalName());	   	 		   	 	   	    	   	    							   	   	    						   	    					
	   	   	   	    				if(iTopLinear==0){
	   	   	   	    					Button obtnExportShare = (Button)oTopLinear.getChildAt(iTopLinear);
			   	   	   	    			obtnExportShare.setClickable(true);
		   	   	    					obtnExportShare.setId(Integer.parseInt(sIDExercise));
	   	   	   	    					//KML
	   	   	   	    					obtnExportShare.setOnClickListener(new OnClickListener() {
	   										@Override
	   										public void onClick(View oRow) {									
	   											if(ExerciseUtils.writeKML(oRow.getId(),getApplicationContext(),oConfigTrainer)){
	   												Toast.makeText(getBaseContext(), getString(R.string.exercise_export_ok)+" "+ExerciseUtils.sExportFile, Toast.LENGTH_SHORT)
	   												.show();
	   												//Send via mail
	   												final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
	   									        	emailIntent.setType("plain/text"); 
	   									        	//emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"laverdone@gmail.com"}); 
	   									        	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.message_subject)); 
	   									        	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.message_text)); 
	   									        	File fileIn = new File(ExerciseUtils.sExportFile);
	   									            Uri u = Uri.fromFile(fileIn);       
	   									            emailIntent.putExtra(Intent.EXTRA_STREAM, u);
	   									        	startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	   											}else{
	   												Toast.makeText(getBaseContext(), getString(R.string.exercise_export_ko), Toast.LENGTH_SHORT)
	   												.show();
	   											}	
	   										}   	   	   	    				
	   		   	   	   	    			});
	   	   	   	    					obtnExportShare=null;
	   	   	   	    				}else if(iTopLinear==1){
			   	   	   	    			Button obtnExportShare = (Button)oTopLinear.getChildAt(iTopLinear);
			   	   	   	    			obtnExportShare.setClickable(true);
		   	   	    					obtnExportShare.setId(Integer.parseInt(sIDExercise));
	   	   	   	    					//GPX
	   	   	   	    					obtnExportShare.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View oRow) {									
											if(ExerciseUtils.writeGPX(oRow.getId(),getApplicationContext(),oConfigTrainer)){
												Toast.makeText(getBaseContext(), getString(R.string.exercise_export_ok)+" "+ExerciseUtils.sExportFile, Toast.LENGTH_SHORT)
												.show();
												//Send via mail
												final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
									        	emailIntent.setType("plain/text"); 
									        	//emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"laverdone@gmail.com"}); 
									        	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.message_subject)); 
									        	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.message_text)); 
									        	File fileIn = new File(ExerciseUtils.sExportFile);
									            Uri u = Uri.fromFile(fileIn);       
									            emailIntent.putExtra(Intent.EXTRA_STREAM, u);
									        	startActivity(Intent.createChooser(emailIntent, "Send mail..."));
											}else{
												Toast.makeText(getBaseContext(), getString(R.string.exercise_export_ko), Toast.LENGTH_SHORT)
												.show();
											}	
										}   	   	   	    				
	   	   	   	    					});
	   	   	   	    					obtnExportShare=null;
	   	   	   	    				}else if (iTopLinear==2){
			   	   	   	    			Button obtnExportShare = (Button)oTopLinear.getChildAt(iTopLinear);
			   	   	   	    			obtnExportShare.setClickable(true);
		   	   	    					obtnExportShare.setId(Integer.parseInt(sIDExercise));
	   	   	   	    					//TCX
	   	   	   	    					obtnExportShare.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View oRow) {									
											if(ExerciseUtils.writeTCX(oRow.getId(),getApplicationContext(),oConfigTrainer)){
												Toast.makeText(getBaseContext(), getString(R.string.exercise_export_ok)+" "+ExerciseUtils.sExportFile, Toast.LENGTH_SHORT)
												.show();
												//Send via mail
												final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
									        	emailIntent.setType("plain/text"); 
									        	//emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"laverdone@gmail.com"}); 
									        	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.message_subject)); 
									        	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.message_text)); 
									        	File fileIn = new File(ExerciseUtils.sExportFile);
									            Uri u = Uri.fromFile(fileIn);       
									            emailIntent.putExtra(Intent.EXTRA_STREAM, u);
									        	startActivity(Intent.createChooser(emailIntent, "Send mail..."));
											}else{
												Toast.makeText(getBaseContext(), getString(R.string.exercise_export_ko), Toast.LENGTH_SHORT)
												.show();
											}
										}   	   	   	    				
	   	   	   	    					});
	   	   	   	    					obtnExportShare=null;
	   	   	   	    				}else if (iTopLinear==3){
			   	   	   	    			ImageButton obtnExportShare = (ImageButton)oTopLinear.getChildAt(iTopLinear);
			   	   	   	    			obtnExportShare.setClickable(true);
		   	   	    					obtnExportShare.setId(Integer.parseInt(sIDExercise));//FB
	   	   	   	    					obtnExportShare.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View oRow) {									
												manualShare(sDistanceFormatted,sTotalTime,sAVGSpeed,sTotalKalories);												
												//ExerciseUtils.manualShare(getApplication());
												//manualShareFB(oRow.getId());
												//final Intent emailIntent = new Intent(Intent.ACTION_SEND); 
									        	//emailIntent.setType("plain/text"); 
									        	//emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"laverdone@gmail.com"}); 
									        	//emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.message_subject)); 
									        	//emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.message_text)); 
									        	//startActivity(Intent.createChooser(emailIntent, "Sharing..."));
										}   	   	   	    				
	   	   	   	    					});
	   	   	   	    					obtnExportShare=null;
	   	   	   	    				}
	   	   	   	    				
	   	   	    				}
	   	    					oTopLinear=null;*/
	   	    					/**BAR LINEAR*/
	   	    				}if(j==0){	   	   	   	       
	   	   	   	    			//TopLinear
	   	   	   	    			/**TOP LINEAR*/
	   	   	   	    			RelativeLayout oTopLinear = ((RelativeLayout) oInternalRelative.getChildAt(j));   	   	    				
	   	    					int iTopChild=oTopLinear.getChildCount();
	   	   	   	    			for(int iTopLinear=0;iTopLinear<iTopChild;iTopLinear++){
	   	    						//Log.v(this.getClass().getCanonicalName(),"oTopLinear "+iTopLinear+": "+ oTopLinear.getChildAt(j).getClass().getCanonicalName());
	   	 		   	 	   	    	   	    						
	   	   	    					TextView oTextDett = (TextView)oTopLinear.getChildAt(iTopLinear);
	   	   	   	    				if(iTopLinear==0){
	   	   	   	    					//Pace
	   	   	   	    					oTextDett.setText(sStart);
	   	   	   	    				}
	   	   	   	    				oTextDett=null;
	   	   	    				}
	   	    					oTopLinear=null;
	   	    					/**TOP LINEAR*/
	   	    				}else if(j==1){   	    	
	   	    					//PBar
	   	    					/**MIDDLE LINEAR*/
	   	    					RelativeLayout oMiddleLinear = ((RelativeLayout) oInternalRelative.getChildAt(j)); 
	   	    					int iMiddChild=oMiddleLinear.getChildCount();
	   	   	    				for(int iMiddleLinear=0;iMiddleLinear<iMiddChild;iMiddleLinear++){
	   	   	    					//Log.v(this.getClass().getCanonicalName(),"oMiddleLinear "+iMiddleLinear+": "+ oMiddleLinear.getChildAt(iMiddleLinear).getClass().getCanonicalName());
	   	   	    					
	   	   	    					if(iMiddleLinear==0){
	   	   	    						ImageButton oButtonExerciseDetails = (ImageButton)oMiddleLinear.getChildAt(iMiddleLinear);
	   	   	    						//Bottone Dettagli
		   	   	    					oButtonExerciseDetails.setClickable(true);
				   	   	    			oButtonExerciseDetails.setId(Integer.parseInt(sIDExercise));
					   	   	    		if(iTypeExercise==1000
												||iTypeExercise==1001 ||
												iTypeExercise==10000){
					   	   	    			oButtonExerciseDetails.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View oRow) {									
												Toast.makeText(getBaseContext(), getApplicationContext().getString(R.string.not_avail_manual), Toast.LENGTH_SHORT)
												.show();	
											}   	   	   	    				
			   	   	   	    			});
										}else{
											oButtonExerciseDetails.setOnClickListener(new OnClickListener() {
												@Override
												public void onClick(View oRow) {									
													
													ExerciseManipulate.setiIDExercise(oRow.getId());
													Intent intent = ActivityHelper.createActivityIntent(HistoryActivity.this,ExerciseDetails.class);
													//startActivity(intent);
													ActivityHelper.startNewActivityAndFinish(HistoryActivity.this, intent);	
													
												}   	   	   	    				
				   	   	   	    			});
										}
	   	   	    					}else if(iMiddleLinear==2){
	   	   	    						//Type Exercise
		   	    	   	   	    		ImageView oImgType = (ImageView)oMiddleLinear.getChildAt(iMiddleLinear);
		   	 		   	   	   	    	if(sType.compareToIgnoreCase("0")==0){
		   	 	  	   	    				//Runnnig
		   	 	  	   	    				oImgType.setBackgroundResource(R.drawable.running);		   	   	   	    		
		   	 	   	   	   	            	oTextTitle.setText(getString(R.string.history_run));
		   	 	  	   	    			}else if(sType.compareToIgnoreCase("1")==0){
		   	 	  	   	    				oImgType.setBackgroundResource(R.drawable.biking);
		   	 	  	   	    				oTextTitle.setText(getString(R.string.history_bike));
		   	 	  	   	    			}else if(sType.compareToIgnoreCase("100")==0){
		   	 	  	   	    				oImgType.setBackgroundResource(R.drawable.walking);	   	   
		   	 	   	   	   	            	oTextTitle.setText(getString(R.string.history_walk));
		   	 	  	   	    			}
	   	   	    					}else if(iMiddleLinear==5){
	   	   	    						//Distance
	   	   	    						TextView oTextDett = (TextView)oMiddleLinear.getChildAt(iMiddleLinear);
	   	   	   	    					oTextDett.setText(sDistanceFormatted);
	   	   	   	    					oTextDett=null;
	   	   	    					}else if(iMiddleLinear==6){
	   	   	    						//Kal
	   	   	    						TextView oTextDett = (TextView)oMiddleLinear.getChildAt(iMiddleLinear);
	   	   	   	    					oTextDett.setText(sTotalKalories+" "+getString(R.string.kalories));
	   	   	   	    					oTextDett=null;
	   	   	    					}else if(iMiddleLinear==7){
	   	   	    						//Pace
	   	   	    						TextView oTextDett = (TextView)oMiddleLinear.getChildAt(iMiddleLinear);
	   	   	   	    					oTextDett.setText(sAVGSpeed);
	   	   	   	    					oTextDett=null;
	   	   	    					}else if(iMiddleLinear==8){
	   	   	    						//Pace
	   	   	    						TextView oTextDett = (TextView)oMiddleLinear.getChildAt(iMiddleLinear);
	   	   	   	    					oTextDett.setText(sTotalTime);
	   	   	   	    					oTextDett=null;
	   	   	    					}
	   	   	    					
		   	   	    				/*LinearLayout oMiddleTopLinear = ((LinearLayout) oMiddleLinear.getChildAt(iMiddleLinear));   	
		   	   	    				int iMiddTopChild=oMiddleTopLinear.getChildCount();
		   	   	    				for(int iMiddleTopLinear=0;iMiddleTopLinear<iMiddTopChild;iMiddleTopLinear++){
		   	   	    					//Log.v(this.getClass().getCanonicalName(),"oMiddleTopLinear "+iMiddleTopLinear+": "+ oMiddleTopLinear.getChildAt(iMiddleTopLinear).getClass().getCanonicalName());

		   	   	    					LinearLayout oBarFrameBG = ((LinearLayout) oMiddleTopLinear.getChildAt(iMiddleTopLinear)); 
		   	   	    					int iBarChild=oBarFrameBG.getChildCount();
			   	   	    				for(int iBarFrameBG=0;iBarFrameBG<iBarChild;iBarFrameBG++){	
			   	   	    					//Log.v(this.getClass().getCanonicalName(),"oBarFrameBG "+iBarFrameBG+": "+ oBarFrameBG.getChildAt(iBarFrameBG).getClass().getCanonicalName());

			   	   	    					//Distance Text
			   	   	    	    			////Log.v(this.getClass().getCanonicalName(),"IDExercise: "+sIDExercise+" - "+sDistanceFormatted);
			   	   	    	    			TextView oDistance = (TextView)oBarFrameBG.getChildAt(iBarFrameBG);
			   	   	    	    			if(iBar<80){
			   	   	    	    				//Imposto la larghezza della barra minima
			   	   	    	    				oDistance.setLayoutParams(new LinearLayout.LayoutParams(80, iMax_Bar_Height));
			   	   	    	    			}else if (iBar>iMax_Bar_Width){
			   	   	    	    				oDistance.setLayoutParams(new LinearLayout.LayoutParams(iMax_Bar_Width, iMax_Bar_Height));
			   	   	    	    			}else{
			   	   	    	    				oDistance.setLayoutParams(new LinearLayout.LayoutParams(iBar, iMax_Bar_Height));	
			   	   	    	    			}
			  	    	    				
			   	   	    	    			sDistance=sDistanceFormatted;   	    	    			
			   	   	    	    			oDistance.setText(sDistance);
			   	   	    	    			oDistance=null;
			   	   	    					
			   	   	    				}
		   	   	    				
		   	   	    				}*/
	   	   	    				}    					
	   	    					/**MIDDLE LINEAR*/
	   	    				}else if(j==3){
	   	    					/**BOTTOM LINEAR*/
	   	    					/*// Note Image
	   	    					//BottomLinear
	   	   	    				LinearLayout oBottomLinear = ((LinearLayout) oInternalRelative.getChildAt(j));   
	   	   	    				int iBottomChild=oBottomLinear.getChildCount();
	   	   	    				for(int iBottomLinear=0;iBottomLinear<iBottomChild;iBottomLinear++){
	   	   	    					//Log.v(this.getClass().getCanonicalName(),"oBottomLinear "+iBottomLinear+": "+ oBottomLinear.getChildAt(iBottomLinear).getClass().getCanonicalName());

	   	   	    					if(iBottomLinear==1){
	   	   	    						//Note
	   	    	   	   	    			TextView oNote = (TextView)oBottomLinear.getChildAt(iBottomLinear);
	   	    	   	   	    			if(sNote!=null){
	   	    	   	   	    				oNote.setText(sTitle.substring(0,10)+" - "+sNote);
	   	    	   	   	    			}else{
	   	    	   	   	    				oNote.setText(sTitle.substring(0,10));
	   	    	   	   	    			}
	   	    	   	   	    				
	   	    	   	   	    			oNote=null;
	   	   	    					}else if(iBottomLinear==0){
	   	   	    						//Type Exercise
		   	    	   	   	    		ImageView oImgType = (ImageView)oBottomLinear.getChildAt(iBottomLinear);
		   	 		   	   	   	    	if(sType.compareToIgnoreCase("0")==0){
		   	 	  	   	    				//Runnnig
		   	 	  	   	    				oImgType.setBackgroundResource(R.drawable.running);		   	   	   	    		
		   	 	   	   	   	            	oTextTitle.setText(getString(R.string.history_run));
		   	 	  	   	    			}else if(sType.compareToIgnoreCase("1")==0){
		   	 	  	   	    				oImgType.setBackgroundResource(R.drawable.biking);
		   	 	  	   	    				oTextTitle.setText(getString(R.string.history_bike));
		   	 	  	   	    			}else if(sType.compareToIgnoreCase("100")==0){
		   	 	  	   	    				oImgType.setBackgroundResource(R.drawable.walking);	   	   
		   	 	   	   	   	            	oTextTitle.setText(getString(R.string.history_walk));
		   	 	  	   	    			}
	   	   	    					}
	   	   	    				}  	  
	   	   	    			
		   	   	    			oBottomLinear.setClickable(true);
		   	   	    			oBottomLinear.setId(Integer.parseInt(sIDExercise));
			   	   	    		if(iTypeExercise==1000
										||iTypeExercise==1001 ||
										iTypeExercise==10000){
			   	   	    			oBottomLinear.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View oRow) {									
										Toast.makeText(getBaseContext(), getApplicationContext().getString(R.string.not_avail_manual), Toast.LENGTH_SHORT)
										.show();	
									}   	   	   	    				
	   	   	   	    			});
								}else{
									oBottomLinear.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View oRow) {									
											
											ExerciseManipulate.setiIDExercise(oRow.getId());
											Intent intent = ActivityHelper.createActivityIntent(HistoryActivity.this,ExerciseDetails.class);
											//startActivity(intent);
											ActivityHelper.startNewActivityAndFinish(HistoryActivity.this, intent);	
											
										}   	   	   	    				
		   	   	   	    			});
								}
		   	   	    			
			   	   	    		oBottomLinear.setOnLongClickListener(new OnLongClickListener() {									
									@Override
									public boolean onLongClick(View oRow) {
										deleteExercise(oRow.getId());
										return false;
									}
								});
	   	   	    				oBottomLinear=null;*/
	   	   	    			/**BOTTOM LINEAR*/  
	   	    				}else if(j==4){
	   	    					/*LinearLayout oBottomLinear = ((LinearLayout) oInternalRelative.getChildAt(j));   
	   	    					int iBottomChild=oBottomLinear.getChildCount();
	   	   	    				for(int iBottomLinear=0;iBottomLinear<iBottomChild;iBottomLinear++){
	   	   	    					if(iBottomLinear==0){
	   	   	    						//Type Exercise
		   	    	   	   	    		ImageView oImgType = (ImageView)oBottomLinear.getChildAt(iBottomLinear);
		   	    	   	   	    		oImgType.setClickable(true);
		   	    	   	   	    		oImgType.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View oRow) {									
												if(iTypeExercise==1000
														||iTypeExercise==1001 ||
														iTypeExercise==10000){
													Toast.makeText(getBaseContext(), getApplicationContext().getString(R.string.not_avail_manual), Toast.LENGTH_SHORT)
													.show();
												}else{
													ExerciseManipulate.setiIDExercise(oRow.getId());
													Intent intent = ActivityHelper.createActivityIntent(HistoryActivity.this,ExerciseDetails.class);
													//startActivity(intent);
													ActivityHelper.startNewActivityAndFinish(HistoryActivity.this, intent);	
												}
											}   	   	   	    				
			   	   	   	    			});
	   	   	    					}
	   	   	    				}  	 	   	   	    			
		   	   	    			oBottomLinear.setClickable(true);
		   	   	    			oBottomLinear.setId(Integer.parseInt(sIDExercise));
			   	   	    		if(iTypeExercise==1000
										||iTypeExercise==1001 ||
										iTypeExercise==10000){
			   	   	    			oBottomLinear.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View oRow) {									
										Toast.makeText(getBaseContext(), getApplicationContext().getString(R.string.not_avail_manual), Toast.LENGTH_SHORT)
										.show();	
									}   	   	   	    				
	   	   	   	    			});
								}else{
									oBottomLinear.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View oRow) {									
											
											ExerciseManipulate.setiIDExercise(oRow.getId());
											Intent intent = ActivityHelper.createActivityIntent(HistoryActivity.this,ExerciseDetails.class);
											//startActivity(intent);
											ActivityHelper.startNewActivityAndFinish(HistoryActivity.this, intent);	
											
										}   	   	   	    				
		   	   	   	    			});
								}*/
	   	    				}
	   	    					    				   	   	   	    		  	   	   	    		  	   	   	    		   	    				
	   	   	   	    	}
	   	    		}   	    		
	   	    	}   	    	   	    
	   	    }
	    	
	    	
	    	
	    }catch (ClassCastException e) {
	    	Log.e(this.getClass().getCanonicalName(), "Error creating UI "+e.getMessage());
	    	e.printStackTrace();
		}
	    
   	    
   	 
	    vRow.add(oLinear);

	   	oRowLayout.addView(oLinear);
   	    
   	   	   	    			/*//Graph
   	   	   	    			ImageView oImgDett = (ImageView)oInternalRelative.getChildAt(j);
	  	   	    			oImgDett.setClickable(true);
	  	   	    			oImgDett.setId(Integer.parseInt(sIDExercise));
	  	   	    			oImgDett.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View oRow) {									
									ExerciseManipulate.setiIDExercise(oRow.getId());
									//Intent intent = ActivityHelper.createActivityIntent(HistoryActivity.this,GraphExerciseActivity.class);
									Intent intent = ActivityHelper.createActivityIntent(HistoryActivity.this,WebGraphExerciseActivity.class);
									//startActivity(intent);
									ActivityHelper.startNewActivityAndFinish(HistoryActivity.this, intent);	
									
								}   	   	   	    				
	  	   	    			});*/
   	   	   	    		
   	   
	}
	protected void deleteExercise(final int id) {		
		AlertDialog alertDialog;
    	alertDialog = new AlertDialog.Builder(oContext).create();
    	alertDialog.setTitle(getString(R.string.titledeleteexercise));
    	alertDialog.setMessage(getString(R.string.messagedeleteexercise));
    	alertDialog.setButton(getString(R.string.yes), new android.content.DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if(ExerciseUtils.deleteExercise(oContext, oConfigTrainer, String.valueOf(id))){
					iPageStep=I_STEP_PER_PAGE;
					iInitialStep=0;
					populateExerciseFromDB();
					populateHistory();
				}
			}        				
    		});
    	
    	alertDialog.setButton2(getString(R.string.no), new android.content.DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {					
				
			}        		
			
    		});
    	alertDialog.show();
	}
	@Override
    public void onClick(View oObj) {
		if(oObj.getId()==R.id.btn_back){
			//Tasto Back			 	
			iPageStep-=iWritedRow;
			iInitialStep-=I_STEP_PER_PAGE+iWritedRow;
			if(iInitialStep==0) oBtn_prev.setVisibility(View.VISIBLE);
			if(iInitialStep==vExercise.size()){
	        	oBtn_next.setVisibility(View.INVISIBLE);
	        	oBtn_prev.setVisibility(View.VISIBLE);
	        }else if(iInitialStep<vExercise.size() &&
	        		iInitialStep!=0){
	        	oBtn_next.setVisibility(View.VISIBLE);
	        	oBtn_prev.setVisibility(View.VISIBLE);
	        }else if(iInitialStep<vExercise.size() &&
	        		iInitialStep==0){
	        	oBtn_next.setVisibility(View.VISIBLE);
	        	oBtn_prev.setVisibility(View.INVISIBLE);
	        }
			populateHistory();
			//Log.v(this.getClass().getCanonicalName(),"Back: from "+iInitialStep+" to "+iPageStep);
			
		}else if(oObj.getId()==R.id.btn_forward){
			//Tasto forward				
			iPageStep+=I_STEP_PER_PAGE;
			populateHistory();
			if(iInitialStep==vExercise.size()){
	        	oBtn_next.setVisibility(View.INVISIBLE);
	        	oBtn_prev.setVisibility(View.VISIBLE);
	        }else if(iInitialStep<vExercise.size() &&
	        		iInitialStep!=0){
	        	oBtn_next.setVisibility(View.VISIBLE);
	        	oBtn_prev.setVisibility(View.VISIBLE);
	        }else if(iInitialStep<vExercise.size() &&
	        		iInitialStep==0){
	        	oBtn_next.setVisibility(View.VISIBLE);
	        	oBtn_prev.setVisibility(View.INVISIBLE);
	        }
			//Log.v(this.getClass().getCanonicalName(),"Forward: from "+iInitialStep+" to "+iPageStep);		
		}else{
			try{
				if(vRow.get(oObj.getId()).getMeasuredHeight()==0){
					vRow.get(oObj.getId()).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					vRow.get(oObj.getId()).clearAnimation();
					vRow.get(oObj.getId()).setAnimation(a);
				}else{
					vRow.get(oObj.getId()).setLayoutParams(new LinearLayout.LayoutParams(0, 0));
					vRow.get(oObj.getId()).clearAnimation();
					vRow.get(oObj.getId()).setAnimation(a);
				}
			}catch (ArrayIndexOutOfBoundsException e) {
				Log.e(this.getClass().getCanonicalName(),"Errore prelevamento vettore");
				return;
			}
			
			//LinearLayout oLinear = (LinearLayout) findViewById(oObj.getId()*1000);
			
		}
    }
    @Override
    public void onBackPressed() {
    	ActivityHelper.startOriginalActivityAndFinish(this);
    }
    @Override
    protected void onResume() {    	
    	super.onResume();
    	iPageStep=I_STEP_PER_PAGE;
    	iInitialStep=0;
    	//popolo gli esercizi dal DB
        populateExerciseFromDB();
        if(iPageStep==vExercise.size()){
     	   //Disabilito i pulsanti back e prev
     	   oBtn_next.setVisibility(View.INVISIBLE);
           
        }
    	//Popolo la lista esercizi
        populateHistory();
    }
    /**
     * Preleva tutti gli esercizi dal DB e popola il vettore di esecizi
     * da usare per le interazioni con la GUI
     * 
     * **/
    private void populateExerciseFromDB(){
    	String sTypeIN="";
    	vExercise.clear();
    	Database oDB = new Database(this);
	   	oDB.open();       	
	   	if(sType.compareToIgnoreCase("0")==0){
	   		//RUN
	   		sTypeIN=sType+", 1000";
	   	}else if(sType.compareToIgnoreCase("1")==0){
	   		//BIKE
	   		sTypeIN=sType+", 1001";
	   	}else if(sType.compareToIgnoreCase("100")==0){
	   		//WALK
	   		sTypeIN=sType+", 10000";
	   	}
	   	Cursor oCursor = oDB.fetchAll("trainer_exercise", " end_date is not null and distance>0 and id_type_exercise in ("+sTypeIN+")","id_exercise DESC");
	   	if(oCursor!=null){        		
	   		int iKey = oCursor.getColumnIndex("id_exercise");
   			int iTitle = oCursor.getColumnIndex("creation_date");
   			int iNote = oCursor.getColumnIndex("note");
   			int iStart = oCursor.getColumnIndex("start_date");
   			int iEnd = oCursor.getColumnIndex("end_date");
   			int iKalories = oCursor.getColumnIndex("kalories");
   			int iTypeExercise = oCursor.getColumnIndex("id_type_exercise");
   			int iAVGSpeed = oCursor.getColumnIndex("avg_speed");
   			int iTotalTime = oCursor.getColumnIndex("total_time");
   			//Calorie, Tempo e passo medio
   			
   			while(oCursor.moveToNext()){ 
   				fDistance=ExerciseUtils.getTotalDistanceUnFormattated(getApplicationContext(), oConfigTrainer, oCursor.getString(iKey),null);
   				
   				if(fMaxDistance==0){
   					fMaxDistance=fDistance;
   				}
   				if(fDistance>fMaxDistance){
   					fMaxDistance=fDistance;  				
   				} 
   				//Uso il ExerciseManipulate per popolare i bean degli exercise
   				//ExerciseUtils.populateExerciseDetails(this, oConfigTrainer, oCursor.getInt(iKey));
   				
   				Exercise oExerxise = new Exercise();   				
   				oExerxise.setsIDExerise(oCursor.getString(iKey));
   				oExerxise.setsTitle(oCursor.getString(iTitle));
   				oExerxise.setsNote(oCursor.getString(iNote));
   				oExerxise.setsStart(oCursor.getString(iStart));  				
   				oExerxise.setsEnd(oCursor.getString(iEnd));
   				oExerxise.setiTypeExercise(oCursor.getInt(iTypeExercise));   				
   				oExerxise.setfDistance(fDistance);
   				oExerxise.setsDistanceFormatted(ExerciseUtils.getTotalDistanceFormattated(fDistance, oConfigTrainer,true));   				
   				oExerxise.setsTotalKalories(String.valueOf(oCursor.getInt(iKalories)));
   				oExerxise.setsTotalTime(oCursor.getString(iTotalTime));  
   				oExerxise.setsAVGSpeed(ExerciseUtils.getFormattedAVGSpeedMT(oCursor.getDouble(iAVGSpeed),oConfigTrainer));
   				vExercise.add(oExerxise); 				
   				//Log.v(this.getClass().getCanonicalName(), "fdistance: "+fDistance+" Show Exercise: "+oCursor.getString(iKey)+" - "+oCursor.getString(iTitle)+" - "+oCursor.getString(iNote) + " - "+oExerxise.getsDistanceFormatted());	
   				oExerxise=null;
   			}
   			
   			////Log.v(this.getClass().getCanonicalName(), "Exercise Max: "+fMaxDistance);	
   			////Log.v(this.getClass().getCanonicalName(), "Max Width: "+dm.widthPixels);	  			
   			oCursor.close();
   		   	oDB.close();
   		   	oCursor=null;
   		   	oDB=null;
	   	}
    }
    /**
	 * Metodo per la condivisione manuale dell'esercizio
	 * */
	public void manualShare(String sDistanceFormatted, String sTotalTime, String sAVGSpeed, String sTotalKalories){
		
		try{				
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.glm.trainer \n\n"+getString(R.string.time)+": "+sTotalTime+
						" " + 
						getString(R.string.distance)+": "+sDistanceFormatted +" "+
						getString(R.string.pace)+": "+sAVGSpeed+" "+getString(R.string.kalories)+": "+
						sTotalKalories+" Kcal ");
				sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(sharingIntent,"Share using"));
			
		}catch (Exception e) {
			Log.w(this.getClass().getCanonicalName(), "FB Interactive sharing error:"+e.getMessage());
			Toast.makeText(getBaseContext(), getString(R.string.share_ko), Toast.LENGTH_SHORT)
			.show();
		}
	}
}

