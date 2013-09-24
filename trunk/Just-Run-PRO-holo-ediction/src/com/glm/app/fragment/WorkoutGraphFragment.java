package com.glm.app.fragment;

import java.io.File;

import com.glm.app.db.Database;
import com.glm.bean.ConfigTrainer;
import com.glm.bean.ExerciseManipulate;
import com.glm.chart.LineChart;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;



/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
public class WorkoutGraphFragment extends Fragment {
	private ConfigTrainer oConfigTrainer;
	private View rootView;
	private Context mContext;
	private int mIDWorkout=0;
	private LineChart oChartALT;
	private LineChart oChartPACE;
	private LineChart oChartHEART;
	private RelativeLayout oGraphContainerALT;
	private RelativeLayout oGraphContainerPACE;
	private RelativeLayout oGraphContainerHeart;
	private ProgressBar oWaitForGraphALT;
	private ProgressBar oWaitForGraphPACE;
	private ProgressBar oWaitForGraphHeart;
	/**
	 * 
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";

	public WorkoutGraphFragment() {
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext=getActivity().getApplicationContext();
		if(rootView==null){
			rootView = inflater.inflate(R.layout.web_exercise_graph,
					container, false);
			oGraphContainerALT = (RelativeLayout) rootView.findViewById(R.id.graphLayoutALT);
			oGraphContainerPACE = (RelativeLayout) rootView.findViewById(R.id.graphLayoutPACE);
			oGraphContainerHeart = (RelativeLayout) rootView.findViewById(R.id.graphLayoutHEART);
			
			oWaitForGraphALT = (ProgressBar) rootView.findViewById(R.id.waitForGraphALT);
			oWaitForGraphALT.setVisibility(View.VISIBLE);
			
			oWaitForGraphPACE = (ProgressBar) rootView.findViewById(R.id.waitForGraphPACE);
			oWaitForGraphPACE.setVisibility(View.VISIBLE);
			
			oWaitForGraphHeart = (ProgressBar) rootView.findViewById(R.id.waitForGraphHEART);
			oWaitForGraphHeart.setVisibility(View.VISIBLE);
		}
		GraphTask oTask = new GraphTask();
        oTask.execute();
        
		return rootView;
	}

	public void setContext(Context context,int idWorkout) {
		mContext=context;	
		mIDWorkout=idWorkout;
	}
	
	class GraphTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			oConfigTrainer = ExerciseUtils.loadConfiguration(mContext);		
			//Chiamo il tackAsink
			ExerciseUtils.populateExerciseDetails(mContext, oConfigTrainer, mIDWorkout);
		    Log.v(this.getClass().getCanonicalName(),"IDExercide: " +mIDWorkout);
			oChartALT = new LineChart(mContext,0);
			oChartPACE = new LineChart(mContext,1);
			if(oConfigTrainer.isbCardioPolarBuyed()){
				oChartHEART = new LineChart(mContext,2);	
			}
			
		    
	        
	        return null;
		}
		
		@Override
		protected void onPreExecute() {
			if(oWaitForGraphALT!=null) oWaitForGraphALT.setVisibility(View.VISIBLE);
			if(oWaitForGraphPACE!=null) oWaitForGraphPACE.setVisibility(View.VISIBLE);
			if(oWaitForGraphHeart!=null) oWaitForGraphHeart.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}
		@Override
		protected void onPostExecute(Void result) {		
			if(oWaitForGraphALT!=null) oWaitForGraphALT.setVisibility(View.GONE);
			if(oWaitForGraphPACE!=null) oWaitForGraphPACE.setVisibility(View.GONE);
			if(oWaitForGraphHeart!=null) oWaitForGraphHeart.setVisibility(View.GONE);
			
			if(oGraphContainerALT!=null){
				oGraphContainerALT.removeAllViews();
				oGraphContainerALT.addView(oChartALT);
			}
			if(oGraphContainerPACE!=null){
				oGraphContainerPACE.removeAllViews();
				oGraphContainerPACE.addView(oChartPACE);
			}
			if(oConfigTrainer.isbCardioPolarBuyed() && 
					oGraphContainerHeart!=null){
				oGraphContainerHeart.removeAllViews();
				oGraphContainerHeart.addView(oChartHEART);
			}else{
				oGraphContainerHeart.setVisibility(View.GONE);
			}
		}
	}
}