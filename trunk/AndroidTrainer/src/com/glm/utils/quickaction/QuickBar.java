package com.glm.utils.quickaction;

import com.glm.trainer.R;

import android.content.Context;
import android.widget.Toast;

public class QuickBar{
	private ActionItem mShareItem;	
	private ActionItem mGraphItem;
	private ActionItem mMapItem;
	private ActionItem mEditItem;	
	private ActionItem mEraseItem;
	private ActionItem mKMLItem;
	private ActionItem mGPXItem;
	private ActionItem mTCXItem;
	private Context mContext;
	final QuickAction quickAction;
	public QuickBar(Context context) {				
		mContext=context;
				
		mShareItem  = new ActionItem(1, mContext.getString(R.string.share_long), mContext.getResources().getDrawable(R.drawable.share));
		mGraphItem  = new ActionItem(2, mContext.getString(R.string.exercise_graph), mContext.getResources().getDrawable(R.drawable.graph));
	    mMapItem    = new ActionItem(3, mContext.getString(R.string.maps), mContext.getResources().getDrawable(R.drawable.map));
	    mEditItem   = new ActionItem(4, mContext.getString(R.string.edit_note), mContext.getResources().getDrawable(R.drawable.edit));
        mEraseItem  = new ActionItem(5, mContext.getString(R.string.erase), mContext.getResources().getDrawable(R.drawable.erase));
        mKMLItem    = new ActionItem(6, "KML", mContext.getResources().getDrawable(R.drawable.export));
        mGPXItem    = new ActionItem(7, "GPX", mContext.getResources().getDrawable(R.drawable.export));
        mTCXItem    = new ActionItem(8, "TCX", mContext.getResources().getDrawable(R.drawable.export));
        
        quickAction = new QuickAction(mContext, QuickAction.VERTICAL);

        //add action items into QuickAction
        quickAction.addActionItem(mGraphItem);
        quickAction.addActionItem(mMapItem);
        quickAction.addActionItem(mEditItem);
        quickAction.addActionItem(mShareItem);
        quickAction.addActionItem(mEraseItem);
        quickAction.addActionItem(mKMLItem);
        quickAction.addActionItem(mGPXItem);
        quickAction.addActionItem(mTCXItem);
        /*quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {          
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                //here we can filter which action item was clicked with pos or actionId parameter
                ActionItem actionItem = quickAction.getActionItem(pos);

                Toast.makeText(mContext, actionItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();                
            }
        });
        quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {          
            @Override
            public void onDismiss() {
                Toast.makeText(mContext, "Dismissed", Toast.LENGTH_SHORT).show();
            }
        });*/
       
	}

	public QuickAction getQuickAction(){
		return quickAction; 
	}
	
	
}
