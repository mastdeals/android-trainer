package com.glm.app;

import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.client.HttpClient;

import com.glm.bean.ConfigTrainer;
import com.glm.bean.VirtualRace;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;
import com.glm.utils.http.HttpClientHelper;
import com.glm.utils.vending.BillingService;
import com.glm.utils.vending.BillingService.RequestPurchase;
import com.glm.utils.vending.BillingService.RestoreTransactions;
import com.glm.utils.vending.Consts.PurchaseState;
import com.glm.utils.vending.Consts.ResponseCode;
import com.glm.utils.vending.PurchaseObserver;
import com.glm.utils.vending.ResponseHandler;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
public class StoreActivity extends Activity implements OnClickListener {
	private HttpClientHelper oHttpClient = new HttpClientHelper();
	private BillingService mBillingService;
	
	private TrainerPurchaseObserver mTrainerPurchaseObserver;
    private Handler mHandler;
	
    private String mSku;
    private String mPayloadContents = null;
    
	private LinearLayout obtnDonate;
	private LinearLayout obtnPolarCardio;
	private LinearLayout oInternal;
	private ImageButton oBtnBack;
	private ConfigTrainer oConfigTrainer;
	
	/**
     * A {@link PurchaseObserver} is used to get callbacks when Android Market sends
     * messages to this application so that we can update the UI.
     */
    private class TrainerPurchaseObserver extends PurchaseObserver {
        public TrainerPurchaseObserver(Handler handler) {
            super(StoreActivity.this, handler);
        }

        @Override
        public void onBillingSupported(boolean supported) {
                Log.i(this.getClass().getCanonicalName(), "supported: " + supported);
        }

        @Override
        public void onPurchaseStateChange(PurchaseState purchaseState, String itemId,
                int quantity, long purchaseTime, String developerPayload) {         
                Log.i(this.getClass().getCanonicalName(), "onPurchaseStateChange() itemId: " + itemId + " " + purchaseState);         
        }

        @Override
        public void onRequestPurchaseResponse(RequestPurchase request,
                ResponseCode responseCode) {
            Log.d(this.getClass().getCanonicalName(), request.mProductId + ": " + responseCode);
            if (responseCode == ResponseCode.RESULT_OK) {        
                Log.i(this.getClass().getCanonicalName(), "purchase was successfully sent to server");
                //logProductActivity(request.mProductId, "sending purchase request");
            } else if (responseCode == ResponseCode.RESULT_USER_CANCELED) {
                Log.i(this.getClass().getCanonicalName(), "user canceled purchase");
                //logProductActivity(request.mProductId, "dismissed purchase dialog");
            } else {
               
                Log.i(this.getClass().getCanonicalName(), "purchase failed");

                //logProductActivity(request.mProductId, "request purchase returned " + responseCode);
            }
        }

        @Override
        public void onRestoreTransactionsResponse(RestoreTransactions request,
                ResponseCode responseCode) {
            if (responseCode == ResponseCode.RESULT_OK) {
               
                Log.d(this.getClass().getCanonicalName(), "completed RestoreTransactions request");
                // Update the shared preferences so that we don't perform
                // a RestoreTransactions again.
                SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                //edit.putBoolean(DB_INITIALIZED, true);
                edit.commit();
            } else {
                Log.d(this.getClass().getCanonicalName(), "RestoreTransactions error: " + responseCode);
            }
        }
    }
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        setContentView(R.layout.new_trainer_store);
        oInternal = (LinearLayout) findViewById(R.id.internal);
        oBtnBack  = (ImageButton) findViewById(R.id.btn_back);
        oBtnBack.setOnClickListener(this);
        
        obtnDonate = (LinearLayout) findViewById(R.id.btnDonate);
        obtnDonate.setOnClickListener(this);
        
        obtnPolarCardio = (LinearLayout) findViewById(R.id.btnPolarCardio);
        obtnPolarCardio.setOnClickListener(this);
        
        
        mHandler = new Handler();
        mTrainerPurchaseObserver = new TrainerPurchaseObserver(mHandler);
        mBillingService = new BillingService();
        mBillingService.setContext(this);

        oConfigTrainer = ExerciseUtils.loadConfiguration(this,true);	

        // Check if billing is supported.
        ResponseHandler.register(mTrainerPurchaseObserver);
        if (!mBillingService.checkBillingSupported()) {
            
        }
                
    }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		ArrayList<VirtualRace> aVirtualRace = oHttpClient.getVirtualRace(oConfigTrainer, Locale.getDefault().getCountry());		
		int iIndex=aVirtualRace.size();
		for(int i=0;i<iIndex;i++){
			VirtualRace oVirtualRace = aVirtualRace.get(i);
			populareRow(oVirtualRace);
		}
		
	}
	/**inserisco i pulsanti per le gare virtuali*/
    private void populareRow(VirtualRace oVirtualRace) {
		LinearLayout oLinearVirtualRaceStore = new LinearLayout(this);
		oLinearVirtualRaceStore = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.new_virtual_race, null);
		
		int iChild=oLinearVirtualRaceStore.getChildCount();
    	for(int i=0;i<iChild;i++){
    		final String sMsKu=oVirtualRace.getsMsKu();
    		Log.v(this.getClass().getCanonicalName(),"oLinearVirtualRaceStore "+i+": "+ oLinearVirtualRaceStore.getChildAt(i).getClass().getCanonicalName());
    		RelativeLayout oInternal = ((RelativeLayout) oLinearVirtualRaceStore.getChildAt(i));
   	    	int jjChild=oInternal.getChildCount();
   	    	for(int jj=0;jj<jjChild;jj++){
   	    		Log.v(this.getClass().getCanonicalName(),"oInternal "+jj+": "+ oInternal.getChildAt(i).getClass().getCanonicalName());
   	    		if(jj==0){
   	    			//name
   	    			TextView oText = (TextView) oInternal.getChildAt(jj);
   	    			oText.setText(oVirtualRace.getsName());
   	    			oText = null;
   	    		}else if (jj==1){
   	    			//Price
   	    			TextView oText = (TextView) oInternal.getChildAt(jj);
   	    			oText.setText(String.valueOf(oVirtualRace.getfPrice()));
   	    			oText = null;
   	    		}else if (jj==2){
   	    			//Desc
   	    			TextView oText = (TextView) oInternal.getChildAt(jj);
   	    			oText.setText(oVirtualRace.getsDesc());
   	    			oText = null;
   	    		}else if (jj==3){
   	    			//Desc1
   	    			TextView oText = (TextView) oInternal.getChildAt(jj);
   	    			oText.setText(oVirtualRace.getsDesc1());
   	    			oText = null;
   	    		}
   	    	}
   	    	oInternal.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.v(this.getClass().getCanonicalName(),"mSku for billing:"+sMsKu);
					mBillingService.requestPurchase(sMsKu, mPayloadContents);
				}
			});
    		
    	}
		oInternal.addView(oLinearVirtualRaceStore);
		oLinearVirtualRaceStore=null;
	}
	@Override
    public void onClick(View v) {
		if (v.getId() == R.id.btnDonate) {
			mSku="donate2.0";
			//mSku="android.test.purchase";
			mBillingService.requestPurchase(mSku, mPayloadContents);
			//mBillingService
			
		}if (v.getId() == R.id.btnPolarCardio) {
			if(oConfigTrainer!=null){
				if(oConfigTrainer.isbCardioPolarBuyed()){
					Toast.makeText(getBaseContext(), 
		                    getString(R.string.purchased), 
		                    Toast.LENGTH_SHORT).show();
				}else{
					mSku="polarcardio";
					//mSku="android.test.purchase";
					mBillingService.requestPurchase(mSku, mPayloadContents);
				}
			}
			
		}if(v.getId() == R.id.btn_back){
			onBackPressed();
		}
    }
    @Override
    public void onBackPressed() {
    	ActivityHelper.startOriginalActivityAndFinish(this);
    }
}
