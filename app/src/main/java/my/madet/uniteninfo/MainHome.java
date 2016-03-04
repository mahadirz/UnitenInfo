/*
 * The MIT License (MIT)
 * Copyright (c) 2014 Mahadir Ahmad
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 * 
 */

/**
 * 
 *
 * @author Mahadir Ahmad
 * @version 1.0
 * 
 */

package my.madet.uniteninfo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import my.madet.adapter.NavDrawerListAdapter;
import my.madet.function.DatabaseHandler;
import my.madet.function.FunctionLibrary;
import my.madet.function.MyPreferences;
import my.madet.model.NavDrawerItem;

public class MainHome extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	private MyPreferences myPreferences;

	/**
	 * The {@link Tracker} used to record screen views.
	 */
	private Tracker mTracker;
	

	// nav drawer title
	//private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home);

		// [START shared_tracker]
		// Obtain the shared Tracker instance.
		AnalyticsApplication application = (AnalyticsApplication) getApplication();
		mTracker = application.getDefaultTracker();
		// [END shared_tracker]
		
		//init database
		DatabaseHandler dbhandler = new DatabaseHandler(getApplicationContext());
		
		//get total class notices
		String totalClassNotices = Integer.toString(dbhandler.getRowCount(DatabaseHandler.TABLE_CLASS_NOTICES));

		//init shared preference
		myPreferences = new MyPreferences(this);

		//mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// biodata
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// class notices
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1), true, totalClassNotices));
		// ledger balance
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		//result
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		// scorun
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// timetable
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
		
		//html timetable
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
		
		//openvpn removed
		//navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
		
		//config
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(8, -1)));
		
		//about
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(9, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle("Menu");
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			int pos = myPreferences.getIntegerPreference(MyPreferences.DRAWER_INDEX_POSITION);
			displayView(pos);
		}
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			myPreferences.setIntegerPreference(MyPreferences.DRAWER_INDEX_POSITION, position);
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:{
			Toast.makeText(getApplicationContext(), "Logging out..", Toast.LENGTH_SHORT).show();
			//save sql to file
			FunctionLibrary flib = new FunctionLibrary(this);
			flib.logoutUser(getApplicationContext());
			Intent i = new Intent(getApplicationContext(),MainActivity.class);
			startActivity(i);
			finish(); //destroy current  intent
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
        String screenName = "";
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new HomeFragment();
            screenName = "Home";
			break;
		case 1:
			fragment = new ClassNoticesFragment();
            screenName = "Class Notice";
			break;
		case 2:
			fragment = new LedgerBalanceFragment(); //ledger
            screenName = "Ledger Balance";
			break;
		case 3:
			fragment = new ResultFragment();
            screenName = "Result";
			break;
		case 4:
			fragment = new ScorunFragment();
            screenName = "Scorun";
			break;
		case 5:
			fragment = new TimetableFragment();
            screenName = "Timetable";
			break;
		case 6:
			fragment = new TimetableFragmentWeb();
            screenName = "Timetable Web";
			break;
		case 7:
			fragment = new PreferenceFragment();
            screenName = "Preference";
			break;
		case 8:
			fragment = new AboutFragment();
            screenName = "About";
			break;

		default:
			break;
		}
		
		
		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
						
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			//setTitle(navMenuTitles[position]);
			setTitle("Uniten Student Info");
			mDrawerLayout.closeDrawer(mDrawerList);

            // [START screen_view_hit]
            mTracker.setScreenName("Fragment~" + screenName);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
            // [END screen_view_hit]

		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("onActivityResult", "onActivityResult in main Home "+requestCode);
		Log.d("onActivityResult", "onActivityResult in main Home "+data.getIntExtra("RESPONSE_CODE", 0));
		super.onActivityResult(requestCode, resultCode, data);

	    FragmentManager fragmentManager = getFragmentManager();
	    Fragment fragment = fragmentManager.findFragmentById(R.id.frame_container);       
	    if (fragment != null)
	    {
	        fragment.onActivityResult(requestCode, resultCode,data);
	    } 
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		FragmentManager fragmentManager = getFragmentManager();
	    Fragment fragment = fragmentManager.findFragmentById(R.id.frame_container);       
	    if (fragment != null)
	    {
	        fragment.onDestroy();
	    } 
	}

}
