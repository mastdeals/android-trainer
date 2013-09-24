package com.glm.layout;

import com.glm.view.StopwatchView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
/***
 * Classe che contiene tutti gli oggetti custom dei quali devo interagire
 * */
public class StopwatchLayout extends LinearLayout{
	/**Oggetto principale per disegnare/controllare lo stopwatch**/	
	private StopwatchView StopwatchView;
	
	public StopwatchLayout(Context context, AttributeSet attrs) {
		super(context, attrs);		
		/**Oggetto Custom StopWatch**/
		StopwatchView = new StopwatchView(context);
		addView(StopwatchView);
	}
	public StopwatchView getStopwatchView(){
		return StopwatchView;
	}
}
