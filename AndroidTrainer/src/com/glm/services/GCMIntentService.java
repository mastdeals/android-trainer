package com.glm.services;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import android.content.Context;
import android.content.Intent;


public class GCMIntentService extends GCMBaseIntentService{
	private static final String SENDER_ID = "558307532040";
	
	public GCMIntentService() {
        super(SENDER_ID);
    }
	
	@Override
	protected void onError(Context context, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMessage(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onRegistered(Context context, String redID) {
		// TODO Auto-generated method stub
		GCMRegistrar.setRegisteredOnServer(context, true);
	}

	@Override
	protected void onUnregistered(Context context, String arg1) {
		// TODO Auto-generated method stub
		
	}

}
