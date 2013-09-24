package com.glm.app.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.glm.bean.ConfigTrainer;
import com.glm.bean.ExerciseManipulate;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;
import com.glm.utils.fb.FacebookConnector;
import com.glm.utils.quickaction.ActionItem;
import com.glm.utils.quickaction.QuickAction;
import com.glm.utils.quickaction.QuickBar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
public class WorkoutDetailFragment extends Fragment {
	private ConfigTrainer oConfigTrainer;
	private View rootView;
	private Context mContext;
	private int mIDWorkout=0;

	/***oggetto condivisione FB*/
	private FacebookConnector oFB = null; 
	private TextView txtDate;
	
	private TextView oTxt_Time;
	
	private TextView oTxt_Distance;
	
	private TextView oTxt_Kalories;
	
	private TextView oTxt_AVGSpeed;
	
	private TextView oTxt_AVGPace;
	
	private TextView oTxt_MAXSpeed;
	
	private TextView oTxt_MAXPace;	
	
	private TextView oTxt_Step;
	
	private TextView oTxt_MaxBpm;
	
	private TextView oTxt_AvgBpm;
	
	private LinearLayout oLLDectails;
	
	private RelativeLayout oMaxBpm;
	
	private RelativeLayout oAvgBpm;
	
	private RelativeLayout oInfo;
	/**
	 * 
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";

	public WorkoutDetailFragment() {
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext=getActivity().getApplicationContext();
		rootView = inflater.inflate(R.layout.new_exercise_details,
				container, false);
		rootView.setDrawingCacheEnabled(true);
		txtDate 	  = (TextView) rootView.findViewById(R.id.txtDate);
		oTxt_Time	  = (TextView) rootView.findViewById(R.id.textTime);
        oTxt_Distance = (TextView) rootView.findViewById(R.id.textDistance);
        oTxt_Kalories = (TextView) rootView.findViewById(R.id.textKalories);
        oTxt_AVGSpeed = (TextView) rootView.findViewById(R.id.textAVGSpeed);
        oTxt_AVGPace  = (TextView) rootView.findViewById(R.id.textPace);
        oTxt_MAXSpeed = (TextView) rootView.findViewById(R.id.textMAXSpeed);
        oTxt_MAXPace  = (TextView) rootView.findViewById(R.id.textMAXPace);
        oTxt_Step	  = (TextView) rootView.findViewById(R.id.textStep);
        oTxt_MaxBpm	  = (TextView) rootView.findViewById(R.id.textMaxBpm);
        oTxt_AvgBpm	  = (TextView) rootView.findViewById(R.id.textAvgBpm);           
        oLLDectails 	= (LinearLayout) rootView.findViewById(R.id.LLDett);  
        oMaxBpm	  		= (RelativeLayout) rootView.findViewById(R.id.RLMaxBpm);
        oAvgBpm	  		= (RelativeLayout) rootView.findViewById(R.id.RLAvgBpm); 
        oInfo			= (RelativeLayout) rootView.findViewById(R.id.btnInfo);
        oInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showOptions(v);
			}
		});
        ExeriseTask oTask = new ExeriseTask();
        oTask.execute();
		return rootView;
	}

	protected void showOptions(View v) {
		//Manual Sharing
		//manualShare();
		final QuickBar oBar = new QuickBar(mContext,oConfigTrainer);
		
		oBar.getQuickAction().setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {          
        @Override
        public void onItemClick(QuickAction source, int pos, int actionId) {
                //here we can filter which action item was clicked with pos or actionId parameter
                ActionItem actionItem = source.getActionItem(pos);
                Log.v(this.getClass().getCanonicalName(),"Item Click Pos: "+actionItem.getActionId());
                Intent intent = new Intent();
                switch (actionItem.getActionId()) {
				
                case 1:
					manualShare();
					break;
				case 2:
					//Facebook						
					//deleteExercise(ExerciseManipulate.getiIDExercise());  																    
					oFB = new FacebookConnector(mContext,getActivity());
					Bundle params = new Bundle();
					
					params.putString("message", 
							getString(R.string.time)+": "+ExerciseManipulate.getsTotalTime()+"\n "+getString(R.string.distance)+": "+ExerciseManipulate.getsTotalDistance()+
							"\n "+getString(R.string.pace)+" "+ExerciseManipulate.getsMinutePerDistance()+"\n "+
								  getString(R.string.kalories)+" "+ExerciseManipulate.getsCurrentCalories()+" Kcal\n\n");
					params.putString("name", getString(R.string.app_name_buy));
					params.putString("description", getString(R.string.app_description));
					if(oFB!=null) oFB.postMessageOnWall(params);
					Toast.makeText(mContext, getString(R.string.share_ok), Toast.LENGTH_SHORT)
					.show();	
					break;
				case 3:
					if(ExerciseUtils.writeKML(-1,mContext,oConfigTrainer)){
						Toast.makeText(mContext, getString(R.string.exercise_export_ok)+" "+ExerciseUtils.sExportFile, Toast.LENGTH_SHORT)
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
						Toast.makeText(mContext, getString(R.string.exercise_export_ko), Toast.LENGTH_SHORT)
						.show();
					}					
					break;
										
				case 4:
					if(ExerciseUtils.writeGPX(-1,mContext,oConfigTrainer)){
						Toast.makeText(mContext, getString(R.string.exercise_export_ok)+" "+ExerciseUtils.sExportFile, Toast.LENGTH_SHORT)
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
						Toast.makeText(mContext, getString(R.string.exercise_export_ko), Toast.LENGTH_SHORT)
						.show();
					}				
					break;	
				case 5:
					if(ExerciseUtils.writeTCX(-1,mContext,oConfigTrainer)){
						Toast.makeText(mContext, getString(R.string.exercise_export_ok)+" "+ExerciseUtils.sExportFile, Toast.LENGTH_SHORT)
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
						Toast.makeText(mContext, getString(R.string.exercise_export_ko), Toast.LENGTH_SHORT)
						.show();
					}	
					break;
				default:
					break;
				}	                                
            }
        });			
		oBar.getQuickAction().show(v);
	}

	public void setContext(Context context,int idWorkout) {
		mContext=context;	
		mIDWorkout=idWorkout;
	}
	
	
	
	
	
	class ExeriseTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			oConfigTrainer = ExerciseUtils.loadConfiguration(mContext);		
			//Chiamo il tackAsink
			ExerciseUtils.populateExerciseDetails(mContext, oConfigTrainer, mIDWorkout);
		    //Log.v(this.getClass().getCanonicalName(),"IDExercide: " +ExerciseManipulate.getiIDExercise()+" - "+ExerciseManipulate.getsTotalDistance());
			    
		    
	        
	        return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {			
			SimpleDateFormat dfm = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			
				txtDate.setText(dfm.format(ExerciseManipulate.getdDateTimeStart()));
				oTxt_Time.setText(ExerciseManipulate.getsTotalTime());
			    oTxt_Distance.setText(ExerciseManipulate.getsTotalDistance());
			    oTxt_AVGSpeed.setText(ExerciseManipulate.getsAVGSpeed());
			    oTxt_AVGPace.setText(ExerciseManipulate.getsMinutePerDistance());
			    oTxt_MAXSpeed.setText(ExerciseManipulate.getsMAXSpeed());
			    oTxt_MAXPace.setText(ExerciseManipulate.getsMAXMinutePerDistance());
			    oTxt_Step.setText(ExerciseManipulate.getsStepCount());
			    oTxt_Kalories.setText(ExerciseManipulate.getsCurrentCalories());
			    if(oConfigTrainer!=null){
			    	if(oConfigTrainer.isbCardioPolarBuyed()){
			    		if(ExerciseManipulate.getiMAXBpm()==0){
			    			oTxt_MaxBpm.setText("n.d.");
			    		}else{
			    			Log.i(this.getClass().getCanonicalName(),"MaxBPM"+ExerciseManipulate.getiMAXBpm());
			    			oTxt_MaxBpm.setText(String.valueOf(ExerciseManipulate.getiMAXBpm()));	
			    		}
			    		if(ExerciseManipulate.getiAVGBpm()==0){
			    			oTxt_AvgBpm.setText("n.d.");
			    		}else{
			    			oTxt_AvgBpm.setText(String.valueOf(ExerciseManipulate.getiAVGBpm()));
			    		}	    	    	    		
			    	}else{
			    		oLLDectails.removeView(oMaxBpm);
			    		oLLDectails.removeView(oAvgBpm); 	    		
			    	}
			    }
		}
	}
	/**
	 * Metodo per la condivisione manuale dell'esercizio
	 * */
	public void manualShare(){
		try{			
			
			
			  
		    AsyncTask.execute(new Runnable() {
		    	String sPathImage = Environment.getExternalStorageDirectory().getAbsolutePath()+"/personal_trainer";
				   
				@Override
				public void run() {
					File dir = new File(sPathImage);
					File mFile=null;
		            if(!dir.exists()) {
		                dir.mkdirs();
		            }
		            
		            //Task async per salvare l'immagine.
		            Bitmap oBmp =  rootView.getDrawingCache();
		            try {
		                if(oBmp!=null){
		                    mFile = new File(sPathImage+"/"+mIDWorkout+".png");
		                    FileOutputStream out = new FileOutputStream(mFile);

		                    oBmp.compress(Bitmap.CompressFormat.PNG, 90, out);
		                    out.close();
		                    out=null;
		                    MimeTypeMap mime = MimeTypeMap.getSingleton();
				            String type = mime.getMimeTypeFromExtension("png");
						    Intent sharingIntent = new Intent("android.intent.action.SEND");
						    sharingIntent.setType(type);
						    sharingIntent.putExtra("android.intent.extra.STREAM",Uri.fromFile(mFile));
						    sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(Intent.createChooser(sharingIntent,"Share using"));
		                }else{
		                    Log.e(this.getClass().getCanonicalName(),"NULL Page Preview saving image");
		                }
		            } catch (IOException e) {
		                Log.e(this.getClass().getCanonicalName(),"Error saving image");
		            }
		           
				}
				
			    
			});
		    

			/*Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
		
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.glm.trainer \n\n"+getString(R.string.time)+": "+ExerciseManipulate.getsTotalTime()+
					" " + 
					getString(R.string.distance)+": "+ExerciseManipulate.getsTotalDistance() +" "+
					getString(R.string.speed_avg)+": "+ExerciseManipulate.getsAVGSpeed()+" "+getString(R.string.kalories)+": "+
					ExerciseManipulate.getsCurrentCalories()+" Kcal ");
			getActivity().get
			sharingIntent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
			
			sharingIntent.setType("image/*");
			sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(Intent.createChooser(sharingIntent,"Share using"));*/
		}catch (Exception e) {
			Log.w(this.getClass().getCanonicalName(), "FB Interactive sharing error:"+e.getMessage());
			Toast.makeText(mContext, getString(R.string.share_ko), Toast.LENGTH_SHORT)
			.show();
		}
	}
}