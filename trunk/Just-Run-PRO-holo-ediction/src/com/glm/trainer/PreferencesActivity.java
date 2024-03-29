package com.glm.trainer;

import java.util.Locale;

import com.glm.app.fragment.PreferencesFragment;
import com.glm.app.fragment.UserFragment;
import com.glm.utils.Rate;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

public class PreferencesActivity extends FragmentActivity implements
ActionBar.TabListener {

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

	private PreferencesFragment oPreferences=null;
	private UserFragment 		oUser=null;
	private boolean isFirstLaunch = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);   
		Bundle extras = getIntent().getExtras();
		if(extras !=null){
			isFirstLaunch = extras.getBoolean("first_launch");
		}    
		setContentView(R.layout.activity_new_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		if(isFirstLaunch) mViewPager.setOffscreenPageLimit(1);
		else mViewPager.setOffscreenPageLimit(4);
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
					.setTabListener(this));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), NewMainActivity.class);
		startActivity(intent);
		finish();	
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
				if(isFirstLaunch){
					//USER
					oUser =new UserFragment();
					oUser.setContext(PreferencesActivity.this);
					return oUser;
				}else{
					//Notification PREFERENCES
					oPreferences =new PreferencesFragment();
					oPreferences.setContext(PreferencesActivity.this,position);
					return oPreferences;
				}
			case 1:
				//General PREFERENCES
				oPreferences =new PreferencesFragment();
				oPreferences.setContext(PreferencesActivity.this,position);
				return oPreferences;
			case 2:
				//Trainer PREFERENCES
				oPreferences =new PreferencesFragment();
				oPreferences.setContext(PreferencesActivity.this,position);
				return oPreferences;	
			case 3:
				//USER
				oUser =new UserFragment();
				oUser.setContext(PreferencesActivity.this);
				return oUser;
			}

			return null;
		}

		@Override
		public int getCount() {
			if(isFirstLaunch) return 1;
			else return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				if(isFirstLaunch) return getString(R.string.user).toUpperCase(l);
				else return getString(R.string.pre).toUpperCase(l);
			case 1:
				return getString(R.string.general_pref).toUpperCase(l);
			case 2:
				return getString(R.string.show_motivator).toUpperCase(l);
			case 3:
				return getString(R.string.user).toUpperCase(l);
			}
			return null;
		}
	}
}