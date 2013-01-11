package com.glm.app;

import com.glm.app.db.Database;
import com.glm.bean.ConfigTrainer;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TableRow;

public class PrefActivity extends Activity implements OnItemClickListener, OnClickListener {
	
	private Context oContext;
	private LinearLayout Oscrol;
	private Animation a;
	private ConfigTrainer oConfigTrainer;
	private ImageButton obtn_Back;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oContext=this;
        //Carico la configurazione
        oConfigTrainer=ExerciseUtils.loadConfiguration(getApplicationContext(),true);
        
        
        a = AnimationUtils.loadAnimation(this, R.animator.slide_right);
        a.reset();
        
        setContentView(R.layout.new_new_pref);                                         
        
        Oscrol = (LinearLayout) findViewById(R.id.objMainLayout);                       
        obtn_Back  	= (ImageButton) findViewById(R.id.btn_back);
    	
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);       
        
        LinearLayout oLinearUser = new LinearLayout(this);
		oLinearUser = (LinearLayout) LayoutInflater.from(oContext).inflate(R.layout.row_user, null);
		Oscrol.addView(oLinearUser);
		ImageButton oImgUserDett = (ImageButton) findViewById(R.id.btnUserDett);
		oImgUserDett.setOnClickListener(new OnClickListener() {
   	    					
			@Override
			public void onClick(View v) {
				//Log.v(this.getClass().getCanonicalName(),"go to user dett");
				
				Intent intent = ActivityHelper.createActivityIntent(PrefActivity.this,UserDetailsActivity.class);
				//startActivity(intent);
				ActivityHelper.startNewActivityAndFinish(PrefActivity.this, intent);		
			}
        });
		Database oDB = new Database(this);
		Cursor oCursor=null;
        try{
        	oDB.open();       
        	
        	oCursor = oDB.rawQuery("select * from trainer_pref where visible=1 order by order_id", null);
        	if(oCursor!=null){        		
        		int iKey = oCursor.getColumnIndex("id_pref");
                int iType = oCursor.getColumnIndex("type");
                int iName = oCursor.getColumnIndex("name");
                int iDesc = oCursor.getColumnIndex("desc");
                int iValue = oCursor.getColumnIndex("value");
        		while(oCursor.moveToNext()){    
        			if(oCursor.getInt(iKey)==4){
	        			//Inserisco il titolo della  sezione Allenatore        			
	        			oLinearUser = new LinearLayout(this);
	        			oLinearUser = (LinearLayout) LayoutInflater.from(oContext).inflate(R.layout.row_trainer, null);
	        			Oscrol.addView(oLinearUser);
        			}else if(oCursor.getInt(iKey)==11){
        				//Inserisco il titolo della sezione Opzioni Generali        			
	        			oLinearUser = new LinearLayout(this);
	        			oLinearUser = (LinearLayout) LayoutInflater.from(oContext).inflate(R.layout.row_general, null);
	        			Oscrol.addView(oLinearUser);
        			}
        			
        			if(oCursor.getString(iType).compareToIgnoreCase("B")==0){
        				LinearLayout oLinear = new LinearLayout(oContext);
        				
        				oLinear = (LinearLayout) LayoutInflater.from(oContext).inflate(R.layout.row_check, null);
        				boolean bCheck=false;
        				if(oCursor.getString(iValue).compareToIgnoreCase("1")==0) bCheck=true;
        				
       					populateRow(oCursor.getInt(iKey), oCursor.getString(iValue), oLinear,"B",bCheck,oCursor.getString(iName),oCursor.getString(iDesc),0);         					

//        				/* Create a new row to be added. */
//                    	TableRow tr = new TableRow(this);  
//                    	TrainerObject oTrainerObj = new TrainerObject(this,dm.widthPixels);
//                    	if(oCursor.getString(iValue).compareToIgnoreCase("1")==0) bCheck=true;
//                        tr.addView(oTrainerObj.RowCheck(oCursor.getInt(iKey),bCheck,oCursor.getString(iName),oCursor.getString(iDesc)));
                        
                        /* Add row to TableLayout. */
        				if(oCursor.getInt(iKey)==19) {
        					Log.v(this.getClass().getCanonicalName(),"buy poloa: "+oConfigTrainer.isbCardioPolarBuyed());
        					if(oConfigTrainer.isbCardioPolarBuyed()){
        						Oscrol.addView(oLinear);
        					}
        				}else if(oCursor.getInt(iKey)==13){
        					Log.v(this.getClass().getCanonicalName(),"Twitter: "+oConfigTrainer.isShareTwitter()+" FB: "+oConfigTrainer.isShareFB());
        					//Nasconto l'allenamento interattivo se non c'è una share abilitata      					
        			        if((oConfigTrainer.isShareFB()) || (oConfigTrainer.isShareTwitter())){
        			        	Oscrol.addView(oLinear);  
        			        }
        				}else{
        					Oscrol.addView(oLinear);
        				}
        			}else if(oCursor.getString(iType).compareToIgnoreCase("L")==0){
        				LinearLayout oLinear = new LinearLayout(oContext);
        				oLinear = (LinearLayout) LayoutInflater.from(oContext).inflate(R.layout.row_list, null);
        				boolean bCheck=false;
        				if(oCursor.getString(iValue).compareToIgnoreCase("1")==0) bCheck=true;
        				
        				if(oCursor.getString(iDesc).compareToIgnoreCase("motovatorvoice")==0){
                    		populateRow(oCursor.getInt(iKey), oCursor.getString(iValue), oLinear,"L",bCheck,oCursor.getString(iName),oCursor.getString(iDesc),R.array.motivators);
        				}else if (oCursor.getString(iDesc).compareToIgnoreCase("units")==0){
        					populateRow(oCursor.getInt(iKey), oCursor.getString(iValue), oLinear,"L",bCheck,oCursor.getString(iName),oCursor.getString(iDesc),R.array.units);
        				}else if (oCursor.getString(iDesc).compareToIgnoreCase("motovatortime")==0){        					
        					populateRow(oCursor.getInt(iKey), oCursor.getString(iValue), oLinear,"L",bCheck,oCursor.getString(iName),oCursor.getString(iDesc),R.array.motivator_time);
        				}else if(oCursor.getString(iDesc).compareToIgnoreCase("autopausetime")==0){
          					populateRow(oCursor.getInt(iKey), oCursor.getString(iValue), oLinear,"L",bCheck,oCursor.getString(iName),oCursor.getString(iDesc),R.array.autopausetime);         				         					
        				}
        				        				
//        				/* Create a new row to be added. */
//                    	TableRow tr = new TableRow(this);  
//                    	TrainerObject oTrainerObj = new TrainerObject(this,dm.widthPixels);
//                    	
//                        
                    	/* Add row to TableLayout. */
                        Oscrol.addView(oLinear);
        			}              			
        		}        		        		
        	}        		                                              
            //oLayoutPref.addView(oTable);
            //setContentView(oLayoutPref);
            oCursor.close();
            oDB.close();
            oCursor=null;
            oDB=null;
        }catch (Exception e) {        
        	Log.e("Error Insert RUNT-TIME", e.getMessage()); 
        	 if(oCursor!=null){
        		 if(!oCursor.isClosed())  oCursor.close();
        	 }
        	if(oDB!=null){
        		oDB.close();
        	}    
             oCursor=null;
             oDB=null;
        	//Toast.makeText(this, "Error Insert RUNT-TIME!"+e, Toast.LENGTH_LONG).show();
        	e.printStackTrace();
		}
        
        obtn_Back.setOnClickListener(this);
        Oscrol.clearAnimation();
        Oscrol.setAnimation(a);       
    }
	@Override
	protected void onResume() {		
		super.onResume();
		//Carico la configurazione
        oConfigTrainer=ExerciseUtils.loadConfiguration(getApplicationContext(),true);
		Oscrol.clearAnimation();
        Oscrol.setAnimation(a);
	}
	/**
	 * Popola tutte gli oggetti all'interno della riga e ne associa anche i listner
	 * @param iID
	 * @param sValue
	 * @param sDescr 
	 * @param sTitle 
	 * @param bCheck 
	 * @param iListValues 
	 * 
	 * @param LinearLayout oLinear layout che contiene tutti gli oggetti figli
	 * @param String sType tipo di riga da restire
	 * */
    private void populateRow(int iID, String sValue, LinearLayout oLinear, String sType, boolean bCheck, String sTitle, String sDescr, int iListValues) {
    	Log.i(this.getClass().getCanonicalName(),"populateRow: "+iID);
    	if(sType.compareToIgnoreCase("B")==0){
    		int iChild=oLinear.getChildCount();
    		for(int i=0;i<iChild;i++){
        		if(i==0){
    				//TableLayout
	        		//Log.i(this.getClass().getCanonicalName(),"TableLayout "+i+": "+oLinear.getChildAt(i).getClass().getCanonicalName());  	   	    
	       	    	TableLayout oTableInternal = ((TableLayout) oLinear.getChildAt(i));
	       	    	int jChild=oTableInternal.getChildCount();
	       	    	for(int j=0;j<jChild;j++){
	       	    		//TableRow
	       	    		//Log.i(this.getClass().getCanonicalName(),"TableRow "+j+": "+oTableInternal.getChildAt(j).getClass().getCanonicalName());
	       	    		TableRow oTableRow = ((TableRow) oTableInternal.getChildAt(j));
	       	    		int iObjChild=oTableRow.getChildCount();
	       	    		for(int iObj=0;iObj<iObjChild;iObj++){
	       	    			//Mixed Obj
	       	    			//Log.i(this.getClass().getCanonicalName(),"Mixed Obj "+iObj+": "+oTableRow.getChildAt(iObj).getClass().getCanonicalName());
	       	    			//Sezione che contiene il testo
	       	    			if(iObj==0){
	       	    				LinearLayout oInternalLayout = ((LinearLayout) oTableRow.getChildAt(iObj));
	       	    				int iObj1Child=oInternalLayout.getChildCount();
	       	    				for(int iObj1=0;iObj1<iObj1Child;iObj1++){
	       	    					//TextViev
	       	    					//Log.i(this.getClass().getCanonicalName(),"TextViev Obj1 "+iObj1+": "+oInternalLayout.getChildAt(iObj1).getClass().getCanonicalName());
	       	    					TextView oText = ((TextView) oInternalLayout.getChildAt(iObj1));
	       	    					if(iObj1==0) oText.setText(sTitle);
	       	    					else oText.setText(sDescr);
	       	    					oText=null;
	           	    			}
	       	    			}else if(iObj==1){
	       	    				//CheckBox
	       	    				final CheckBox oCheck = ((CheckBox) oTableRow.getChildAt(iObj));
	       	    				oCheck.setChecked(bCheck);
	       	    				oCheck.setId(iID);
	       	    				oCheck.setOnClickListener(new OnClickListener() {
	       	    					
	       	    					@Override
	       	    					public void onClick(View v) {
	       	    						String sSelected="1";	       	    						
	       	    						Database oDb = new Database(getApplicationContext());
	       	    						oDb.open();
	       	    						SQLiteDatabase oDataBase = oDb.getOpenedDatabase();
	       	    						if(!oCheck.isChecked()) sSelected="0";
	       	    						oDataBase.execSQL("UPDATE trainer_pref SET value='"+sSelected+"' WHERE id_pref="+v.getId());
	       	    						Log.i(this.getClass().getCanonicalName(), "UPDATE trainer_pref SET value='"+sSelected+"' WHERE id_pref="+v.getId());
	       	    						oDataBase.close();
	       	    						oDb.close();	       	    						
	       	    						oDb=null;
	       	    						oDataBase=null;
	       	    						ExerciseUtils.updatePreference(getApplicationContext(),v.getId(),Integer.parseInt(sSelected));
	       	    					}
	       	    		        });
	       	    			}	       	    			
	       	    		}
	       	    	}
        		}
        	}
    	}else if(sType.compareToIgnoreCase("L")==0){
    		int iChild=oLinear.getChildCount();
    		for(int i=0;i<iChild;i++){
        		if(i==0){
    				//TableLayout
	        		//Log.i(this.getClass().getCanonicalName(),"TableLayout "+i+": "+oLinear.getChildAt(i).getClass().getCanonicalName());  	   	    
	       	    	TableLayout oTableInternal = ((TableLayout) oLinear.getChildAt(i));
	       	    	int jChild=oTableInternal.getChildCount();
	       	    	for(int j=0;j<jChild;j++){
	       	    		//TableRow
	       	    		//Log.i(this.getClass().getCanonicalName(),"TableRow "+j+": "+oTableInternal.getChildAt(j).getClass().getCanonicalName());
	       	    		TableRow oTableRow = ((TableRow) oTableInternal.getChildAt(j));
	       	    		int iObjChild=oTableRow.getChildCount();
	       	    		for(int iObj=0;iObj<iObjChild;iObj++){
	       	    			//Mixed Obj
	       	    			//Log.i(this.getClass().getCanonicalName(),"Mixed Obj "+iObj+": "+oTableRow.getChildAt(iObj).getClass().getCanonicalName());
	       	    			//Sezione che contiene il testo
	       	    			if(iObj==0){
	       	    				LinearLayout oInternalLayout = ((LinearLayout) oTableRow.getChildAt(iObj));
	       	    				int iObj1Child=oInternalLayout.getChildCount();
	       	    				for(int iObj1=0;iObj1<iObj1Child;iObj1++){
	       	    					//TextViev
	       	    					//Log.i(this.getClass().getCanonicalName(),"TextViev Obj1 "+iObj1+": "+oInternalLayout.getChildAt(iObj1).getClass().getCanonicalName());
	       	    					TextView oText = ((TextView) oInternalLayout.getChildAt(iObj1));
	       	    					if(iObj1==0) oText.setText(sTitle);
	       	    					else oText.setText("");
	       	    					oText=null;
	           	    			}
	       	    			}else if(iObj==1){
	       	    				//Spinner
	       	    				final Spinner oSpinner = ((Spinner) oTableRow.getChildAt(iObj));
	       	    				oSpinner.setId(iID);	       	    				
	       	    				ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	       	    						oContext, iListValues, android.R.layout.simple_spinner_item);
	       	    				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        	       	    	        
	       	    				oSpinner.setAdapter(adapter);
	       	    				//Imposto il valore salvato nel DB
	       	    				oSpinner.setSelection(Integer.parseInt(sValue));
	       	    				oSpinner.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener(){									

									@Override
									public void onItemSelected(
											AdapterView<?> arg0, View v,
											int iPos, long arg3) {
										// TODO Auto-generated method stub
										Log.i(this.getClass().getCanonicalName(),"Selected Item: "+oSpinner.getSelectedItem().toString());
										Database oDb = new Database(getApplicationContext());
	       	    						oDb.open();
	       	    						SQLiteDatabase oDataBase = oDb.getOpenedDatabase();      	    						
	       	    						oDataBase.execSQL("UPDATE trainer_pref SET value='"+iPos+"' WHERE id_pref="+oSpinner.getId());
	       	    						Log.i(this.getClass().getCanonicalName(), "UPDATE trainer_pref SET value='"+iPos+"' WHERE id_pref="+oSpinner.getId());
	       	    						oDataBase.close();
	       	    						oDb.close();	       	    						
	       	    						oDb=null;
	       	    						oDataBase=null;
	       	    						try{
	       	    							ExerciseUtils.updatePreference(getApplicationContext(),oSpinner.getId(),iPos);
	       	    						}catch (NullPointerException e) {
											Log.e(this.getClass().getCanonicalName(),"null Point Exception");
										}
									}

									@Override
									public void onNothingSelected(
											AdapterView<?> arg0) {
										// TODO Auto-generated method stub
										
									}
								
	       	    					
	       	    				});
	       	    			}	       	    			
	       	    		}
	       	    	}
        		}
        	}
    	}
    	
		
	}

	@Override
    public void onClick(View v) {
		if (v.getId() == R.id.btn_back) {
			ActivityHelper.startOriginalActivityAndFinish(this);
		}
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
    @Override
    public void onBackPressed() {
    	ActivityHelper.startOriginalActivityAndFinish(this);
    }    
}