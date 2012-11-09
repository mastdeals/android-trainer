package com.glm.utils.quickaction;

import com.glm.trainer.R;

import android.content.Context;
import android.widget.Toast;

public class QuickBar{
	private ActionItem nextItem;
	private ActionItem prevItem;
	private ActionItem searchItem;
	private ActionItem infoItem;
	private ActionItem eraseItem;
	private ActionItem okItem;
	private Context mContext;
	final QuickAction quickAction;
	public QuickBar(Context context) {				
		mContext=context;
				
		nextItem     = new ActionItem(1, mContext.getString(R.string.share_long), mContext.getResources().getDrawable(R.drawable.share));
	    prevItem     = new ActionItem(2, "Prev", mContext.getResources().getDrawable(R.drawable.menu_up_arrow));
	    searchItem   = new ActionItem(2, "Find", mContext.getResources().getDrawable(R.drawable.menu_search));
        infoItem     = new ActionItem(3, "Info", mContext.getResources().getDrawable(R.drawable.menu_info));
        eraseItem    = new ActionItem(4, "Clear", mContext.getResources().getDrawable(R.drawable.menu_eraser));
        okItem       = new ActionItem(5, "OK", mContext.getResources().getDrawable(R.drawable.menu_ok));
        prevItem.setSticky(true);
        nextItem.setSticky(true);
        quickAction = new QuickAction(mContext, QuickAction.VERTICAL);

        //add action items into QuickAction
        quickAction.addActionItem(nextItem);
        quickAction.addActionItem(prevItem);
        quickAction.addActionItem(searchItem);
        quickAction.addActionItem(infoItem);
        quickAction.addActionItem(eraseItem);
        quickAction.addActionItem(okItem);
        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {          
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
        });
       
	}

	public QuickAction getQuickAction(){
		return quickAction; 
	}
	
	
}
