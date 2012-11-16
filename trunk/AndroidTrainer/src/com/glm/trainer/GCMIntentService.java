package com.glm.trainer;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class GCMIntentService extends GCMBaseIntentService{
	private static final String SENDER_ID = "558307532040";
	
	public GCMIntentService() {
        super(SENDER_ID);
        Log.i(this.getClass().getCanonicalName(), "Device registered: SENDER_ID = " + SENDER_ID);
    }
	
	@Override
	protected void onError(Context context, String arg1) {
		// TODO Auto-generated method stub
		Log.i(this.getClass().getCanonicalName(), "Device onError: = " + arg1);
	}

	@Override
	protected void onMessage(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		Log.i(this.getClass().getCanonicalName(), "Device onMessage: = " + arg1);
	}

	@Override
	protected void onRegistered(Context context, String redID) {
		// TODO Auto-generated method stub
		GCMRegistrar.setRegisteredOnServer(context, true);
		Log.i(this.getClass().getCanonicalName(), "Device onRegistered: = " + redID);
	}

	@Override
	protected void onUnregistered(Context context, String arg1) {
		// TODO Auto-generated method stub
		
	}

}
