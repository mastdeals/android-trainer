package com.glm.trainer;

import java.util.Locale;

import com.glm.app.db.Database;
import com.glm.app.fragment.AboutFragment;
import com.glm.app.fragment.StoreFragment;
import com.glm.app.fragment.SummaryFragment;
import com.glm.app.fragment.WorkoutFragment;
import com.glm.bean.ConfigTrainer;
import com.glm.bean.User;
import com.glm.chart.BarChart;
import com.glm.utils.ExerciseUtils;
import com.glm.utils.Rate;
import com.glm.utils.TrainerServiceConnection;
import com.glm.utils.fb.FacebookConnector;
import com.glm.utils.http.HttpClientHelper;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class NewMainActivity extends FragmentActivity implements
		ActionBar.TabListener {
	public ConfigTrainer oConfigTrainer=null;
	public User mUser=null;
	private ProgressDialog oWaitForLoad=null;
	private String sGCMId="";
	private static final String SENDER_ID = "558307532040";
	
	private TelephonyManager oPhone;
	private boolean isLicence=false;
	/**Gestione della licenza*/
	private LicenseCheckerCallback mLicenseCheckerCallback;
    private LicenseChecker mChecker;
    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsFrtouMzttS7hakJi3uuswjeGiRvLTKtx37kcg2QIQyamJtFsKtEiLQzhcnR/2p5ng98Jg0BLSvzL0VbZSHyWSoR4/RMuYWYhMsvR6MNjry/ABqFaAlsiFCNNfVGEvKV0Kz8ILNMbZA2XlZBviC0fht9BSSynPWTuvT8uHcl0yJY3GmKgyOzodYuCVtqG9pRew+ZstVEeiYcqYbm8Gd0LJVpXkIN1AB9iPlxsfEQWHYzFhwUNB9UR2VcuTEiKyFKmbCPrYGfHkss+Kbjd/mucZx0sWQITUjKSdK9tmMOql/yDXEjuT+PzgUmr1bmnRJgzYzNkpvWbNiIFrgojYAunwIDAQAB";
    private static final byte[] SALT = new byte[] {
        -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95,
        -45, 77, -117, -36, -113, -11, 32, -64, 89
        };
	
    /***oggetto condivisione FB*/
   	private FacebookConnector oFB = null;
    /**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private WorkoutFragment oWorkout=null;
	private SummaryFragment oSummary=null;
	private StoreFragment oStore=null;
	private AboutFragment oAbout=null;
	public TrainerServiceConnection mConnection=null;
	private LocationManager LocationManager = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		if(mConnection==null) mConnection = new TrainerServiceConnection(this);
        if (false) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects() //or .detectAll() for all detectable problems
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        

		setContentView(R.layout.activity_new_main);

		oSummary=new SummaryFragment();
		oWorkout= new WorkoutFragment();
		oStore =new StoreFragment();
		oAbout = new AboutFragment();
		
        Rate.app_launched(this);
        super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//WorkTaskAsync oTask = new WorkTaskAsync();
		//oTask.execute();
		oWaitForLoad = ProgressDialog.show(NewMainActivity.this, getString(R.string.app_name_buy), getString(R.string.please_wait));
		new Thread(new WorkTaskThread()).start();
	}
	@Override
    public void onBackPressed() {
		AlertDialog alertDialog;
    	alertDialog = new AlertDialog.Builder(this).create();
    	alertDialog.setTitle(this.getString(R.string.titleendapp));
    	alertDialog.setMessage(this.getString(R.string.messageendapp));
    	alertDialog.setButton(this.getString(R.string.yes), new android.content.DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if(mConnection!=null && mConnection.mIService!=null){
					try {
						mConnection.mIService.stopGPSFix();
						mConnection.mIService.shutDown();
						mConnection.destroy();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						//Log.e(this.getClass().getCanonicalName(),e.getMessage());
					}
				}
				finish();
			}        				
    		});
    	
    	alertDialog.setButton2(this.getString(R.string.no), new android.content.DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {					
				
			}        		
			
    		});
    	alertDialog.show();
    }    
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			
			switch(position) {
			case 0:
				//WORKOUT
				oWorkout.setContext(NewMainActivity.this);
				return oWorkout;
			case 1:
				//SUMMARY
				oSummary.setContext(NewMainActivity.this);
				return oSummary;
			case 2:
				//STORE
				
				oStore.setContext(NewMainActivity.this);
				return oStore;
			case 3:
				//ABOUT
				
				oAbout.setContext(NewMainActivity.this);
				return oAbout;
			}
			
			return null;
		}
		
		@Override
		public int getCount() {
			// Show 3 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section4).toUpperCase(l);
			case 3:
				return getString(R.string.title_section5).toUpperCase(l);
			}
			return null;
		}
	}
	
	@Override
	protected void onDestroy() {
		if(mHandleMessageReceiver!=null){
			try{
				 unregisterReceiver(mHandleMessageReceiver);   
			}catch (IllegalArgumentException e) {
				 Log.e(this.getClass().getCanonicalName(), "Receiver not registered error");
			}
		}    
		if(mConnection!=null) mConnection.destroy();
		mConnection=null;
		super.onDestroy();
	}
	@Override
	protected void onPause() {
		if(mHandleMessageReceiver!=null){
			try{
				 unregisterReceiver(mHandleMessageReceiver);   
			}catch (IllegalArgumentException e) {
				 Log.e(this.getClass().getCanonicalName(), "Receiver not registered error");
			}
		}    
		if(mConnection!=null) mConnection.destroy();
		mConnection=null;
		super.onPause();
	}
	
	private final BroadcastReceiver mHandleMessageReceiver =
	         new BroadcastReceiver() {
		     @Override
		     public void onReceive(Context context, Intent intent) {
		         Log.v(this.getClass().getCanonicalName(),"onReceive");
		     }
	};
	
	/**Classe per il controllo della licenza*/
	private class TrainerLicenseCheckerCallback implements LicenseCheckerCallback {
		
		@Override    
		public void allow(int reason) {
	        if (isFinishing()) {
	                // Don't update UI if Activity is finishing.	            	
	            return;
	        }
	            Toast.makeText(NewMainActivity.this, getString(R.string.licenceok),
		                Toast.LENGTH_SHORT).show();
	            ExerciseUtils.setLicenceOK(getApplicationContext());  
	            Log.v(this.getClass().getCanonicalName(), "licence Allow code "+reason);
	            isLicence=true;
	        // Should allow user access.
	        //displayResult(getString(R.string.lic_ok));
	    }
		
		@Override
	    public void dontAllow(int reason) {
	        if (isFinishing()) {
	            // Don't update UI if Activity is finishing.
	        	mConnection.destroy();
	            return;
	        }
	        //Toast.makeText(MainTrainerActivity.this, getString(R.string.licenceko),
	        //        Toast.LENGTH_SHORT).show();
	        ExerciseUtils.setLicenceKO(getApplicationContext());  
	        //displayResult(getString(R.string.lic_error));	       
	        // Should not allow access. An app can handle as needed,
	        // typically by informing the user that the app is not licensed
	        // and then shutting down the app or limiting the user to a
	        // restricted set of features.
	        // In this example, we show a dialog that takes the user to Market.
	        showDialog(0);
	        
	        /**
	         * DA MODIFICARE A FALSE IN PRODUZIONE
	         * 
	         * **/
	        isLicence=false;
	        Log.e(this.getClass().getCanonicalName(), "licence not Allow error code "+reason);
	    }
		@Override
		public void applicationError(int errorCode) {
			//Log.e(this.getClass().getCanonicalName(), "applicationError on check licence error code "+errorCode);
			/**
	         * DA MODIFICARE A FALSE IN PRODUZIONE
	         * 
	         * **/
	        isLicence=false;
		}
	}
	/**
	 * Visualizza una alert per il GPS non abilitato
	 *
	 * @author Gianluca Masci aka (GLM)
	 * */
	public void ShowAlertNoGPS() {
		try{
			AlertDialog alertDialog;
	    	alertDialog = new AlertDialog.Builder(this).create();
	    	alertDialog.setTitle(this.getString(R.string.titlegps));
	    	alertDialog.setMessage(this.getString(R.string.messagegpsnoenabled));
	    	alertDialog.setButton(this.getString(R.string.yes), new android.content.DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
				    startActivity(myIntent);
				}        				
	    		});
	    	
	    	alertDialog.setButton2(this.getString(R.string.no), new android.content.DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {					
					
				}        		
				
	    		});
	    	alertDialog.show();
		}catch (Exception e) {
			Toast.makeText(this, "ERROR DIALOG:"+e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.e("MEEERR: ",e.getMessage());
		}
	}
	
	
	class WorkTaskThread implements Runnable{

		@Override
		public void run() {
			Looper.prepare();
		
			Database oDB=new Database(getApplicationContext());
		
			if(oDB!=null)  {
				oDB.init();
			}
			
			Log.v(this.getClass().getCanonicalName(),"doInBackground WorkTaskAsync...");
			oConfigTrainer=ExerciseUtils.loadConfiguration(getApplicationContext());
			mUser = ExerciseUtils.loadUserDectails(getApplicationContext());
			oSummary.oTable=ExerciseUtils.getDistanceForType(oConfigTrainer, getApplicationContext());
			

			oSummary.oChart = new BarChart(getApplicationContext(),0);			
			if(oConfigTrainer.getsGCMId().compareToIgnoreCase("")==0){
				//GCM GOOGLE
				GCMRegistrar.checkDevice(getApplicationContext());
			    GCMRegistrar.checkManifest(getApplicationContext());
			    sGCMId = GCMRegistrar.getRegistrationId(getApplicationContext());
			    registerReceiver(mHandleMessageReceiver,
			                new IntentFilter("com.glm.app.DISPLAY_MESSAGE"));	       
			       
			       if (sGCMId.equals("")) {
			         GCMRegistrar.register(getApplicationContext(), SENDER_ID);
			         Log.v(this.getClass().getCanonicalName(), "Not registered, register now: "+sGCMId);
			         ExerciseUtils.saveGCMId(getApplicationContext(),sGCMId);
			       } else {
			         Log.v(this.getClass().getCanonicalName(), "Already registered: "+sGCMId);	
			         ExerciseUtils.saveGCMId(getApplicationContext(),sGCMId);
			       }
			       
			       if(sGCMId!=null){
			    	   if(sGCMId.length()>0){
			    		   SharedPreferences oPrefs = getApplicationContext().getSharedPreferences("aTrainer",Context.MODE_MULTI_PROCESS);
			    		   SharedPreferences.Editor editPrefs = oPrefs.edit();
			    		   editPrefs.putString("GCMId", sGCMId); 
			    		   editPrefs.commit();
			    		   new Thread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								//Send Id to Android Trainer WEB Server via POST METHOD
							       HttpClientHelper oHttpHelper = new HttpClientHelper();
							       oHttpHelper.registerToAndroidTrainerServer(sGCMId,oConfigTrainer);   
							}
			    		   });
			    		   
			    	   }
			       }
			     //GCM GOOGLE
			}
			
			if(!oConfigTrainer.isbLicence()){
    			/**Check della licenza*/
    			oPhone = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
    						
    			// Construct the LicenseCheckerCallback. The library calls this when done.
    	        mLicenseCheckerCallback = new TrainerLicenseCheckerCallback();

    	        // Construct the LicenseChecker with a Policy.
    	        mChecker = new LicenseChecker(
    	            getApplicationContext(), new ServerManagedPolicy(getApplicationContext(),
    	                new AESObfuscator(SALT, getPackageName(), oPhone.getDeviceId())),
    	            BASE64_PUBLIC_KEY  // Your public licensing key.
    	            );
    	        try{
    	        	 mChecker.checkAccess(mLicenseCheckerCallback);
    	        }catch (Exception e) {
    				Log.e(this.getClass().getCanonicalName(),"Error Checking Licence");
    			}	       
    	        /**Check della licenza*/     
    		}else{
    			isLicence=true;
    		}
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					//	
					// Set up the action bar.
					final ActionBar actionBar = getActionBar();
					actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
					actionBar.setDisplayShowHomeEnabled(false);
					actionBar.setDisplayShowTitleEnabled(false);
			        // Create the adapter that will return a fragment for each of the three
					// primary sections of the app.
					mSectionsPagerAdapter = new SectionsPagerAdapter(
							getSupportFragmentManager());
					actionBar.removeAllTabs();
					
					// Set up the ViewPager with the sections adapter.
					mViewPager = (ViewPager) findViewById(R.id.pager);
					mViewPager.setAdapter(mSectionsPagerAdapter);
					mViewPager.setOffscreenPageLimit(4);
					// When swiping between different sections, select the corresponding
					// tab. We can also use ActionBar.Tab#select() to do this if we have
					// a reference to the Tab.
					mViewPager
							.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
								@Override
								public void onPageSelected(int position) {
									actionBar.setSelectedNavigationItem(position);
								}
							});
		
					// For each of the sections in the app, add a tab to the action bar.
					for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
						// Create a tab with text corresponding to the page title defined by
						// the adapter. Also specify this Activity object, which implements
						// the TabListener interface, as the callback (listener) for when
						// this tab is selected.
						actionBar.addTab(actionBar.newTab()
								.setText(mSectionsPagerAdapter.getPageTitle(i))
								.setTabListener(NewMainActivity.this));
					}
					if(oWaitForLoad!=null) oWaitForLoad.dismiss();
					LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
			    	if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {	        
				       ShowAlertNoGPS();
				    }
				}
				
			});
						
					
		}
		
	}
	
	
	class WorkTaskAsync extends AsyncTask{
		@Override
		protected Object doInBackground(Object... params) {
			Database oDB=new Database(getApplicationContext());
		
			if(oDB!=null)  {
				oDB.init();
			}
			
			Log.v(this.getClass().getCanonicalName(),"doInBackground WorkTaskAsync...");
			oConfigTrainer=ExerciseUtils.loadConfiguration(getApplicationContext());
			mUser = ExerciseUtils.loadUserDectails(getApplicationContext());
			oSummary.oTable=ExerciseUtils.getDistanceForType(oConfigTrainer, getApplicationContext());
			

			oSummary.oChart = new BarChart(getApplicationContext(),0);			
			if(oConfigTrainer.getsGCMId().compareToIgnoreCase("")==0){
				//GCM GOOGLE
				GCMRegistrar.checkDevice(getApplicationContext());
			    GCMRegistrar.checkManifest(getApplicationContext());
			    sGCMId = GCMRegistrar.getRegistrationId(getApplicationContext());
			    registerReceiver(mHandleMessageReceiver,
			                new IntentFilter("com.glm.app.DISPLAY_MESSAGE"));	       
			       
			       if (sGCMId.equals("")) {
			         GCMRegistrar.register(getApplicationContext(), SENDER_ID);
			         Log.v(this.getClass().getCanonicalName(), "Not registered, register now: "+sGCMId);
			         ExerciseUtils.saveGCMId(getApplicationContext(),sGCMId);
			       } else {
			         Log.v(this.getClass().getCanonicalName(), "Already registered: "+sGCMId);	
			         ExerciseUtils.saveGCMId(getApplicationContext(),sGCMId);
			       }
			       
			       if(sGCMId!=null){
			    	   if(sGCMId.length()>0){
			    		   SharedPreferences oPrefs = getApplicationContext().getSharedPreferences("aTrainer",Context.MODE_MULTI_PROCESS);
			    		   SharedPreferences.Editor editPrefs = oPrefs.edit();
			    		   editPrefs.putString("GCMId", sGCMId); 
			    		   editPrefs.commit();
			    		   new Thread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								//Send Id to Android Trainer WEB Server via POST METHOD
							       HttpClientHelper oHttpHelper = new HttpClientHelper();
							       oHttpHelper.registerToAndroidTrainerServer(sGCMId,oConfigTrainer);   
							}
			    		   });
			    		   
			    	   }
			       }
			     //GCM GOOGLE
			}
			
			if(!oConfigTrainer.isbLicence()){
    			/**Check della licenza*/
    			oPhone = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
    						
    			// Construct the LicenseCheckerCallback. The library calls this when done.
    	        mLicenseCheckerCallback = new TrainerLicenseCheckerCallback();

    	        // Construct the LicenseChecker with a Policy.
    	        mChecker = new LicenseChecker(
    	            getApplicationContext(), new ServerManagedPolicy(getApplicationContext(),
    	                new AESObfuscator(SALT, getPackageName(), oPhone.getDeviceId())),
    	            BASE64_PUBLIC_KEY  // Your public licensing key.
    	            );
    	        try{
    	        	 mChecker.checkAccess(mLicenseCheckerCallback);
    	        }catch (Exception e) {
    				Log.e(this.getClass().getCanonicalName(),"Error Checking Licence");
    			}	       
    	        /**Check della licenza*/     
    		}else{
    			isLicence=true;
    		}
		       
			//if(oConfigTrainer.isShareFB()){
			//	oFB = new FacebookConnector(getApplicationContext(),NewMainActivity.this);
				//Session.openActiveSession(MainTrainerActivity.this, true, mStatusCallBabk);	
			//}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			
			
			//	
			// Set up the action bar.
			final ActionBar actionBar = getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowTitleEnabled(false);
	        // Create the adapter that will return a fragment for each of the three
			// primary sections of the app.
			mSectionsPagerAdapter = new SectionsPagerAdapter(
					getSupportFragmentManager());
			actionBar.removeAllTabs();
			
			// Set up the ViewPager with the sections adapter.
			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(mSectionsPagerAdapter);
			mViewPager.setOffscreenPageLimit(4);
			// When swiping between different sections, select the corresponding
			// tab. We can also use ActionBar.Tab#select() to do this if we have
			// a reference to the Tab.
			mViewPager
					.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
						@Override
						public void onPageSelected(int position) {
							actionBar.setSelectedNavigationItem(position);
						}
					});

			// For each of the sections in the app, add a tab to the action bar.
			for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
				// Create a tab with text corresponding to the page title defined by
				// the adapter. Also specify this Activity object, which implements
				// the TabListener interface, as the callback (listener) for when
				// this tab is selected.
				actionBar.addTab(actionBar.newTab()
						.setText(mSectionsPagerAdapter.getPageTitle(i))
						.setTabListener(NewMainActivity.this));
			}
			if(oWaitForLoad!=null) oWaitForLoad.dismiss();
			LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
	    	if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {	        
		       ShowAlertNoGPS();
		    }
		}
		@Override
		protected void onPreExecute() {
			oWaitForLoad = ProgressDialog.show(NewMainActivity.this, getString(R.string.app_name_buy), getString(R.string.please_wait));
			super.onPreExecute();
		}
	}
	
	
	
}
