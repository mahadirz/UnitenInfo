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

import my.madet.function.DatabaseHandler;
import my.madet.function.FunctionLibrary;
import my.madet.function.HttpParser;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends Activity {

	// class var
	private EditText unitenid, passwEditText;
	private ProgressDialog progressDialog;
	private Button loginButton;
	private QuerryAsyncTask _initTask;
	private FunctionLibrary flib;

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final String TAG = "MainActivity";
	private BroadcastReceiver mRegistrationBroadcastReceiver;

    /**
     * The {@link Tracker} used to record screen views.
     */
    private Tracker mTracker;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();

		}

        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        // [END shared_tracker]

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
        if (sentToken) {
            Log.i(TAG, "Token retrieved and sent to server! You can now use gcmsender to send downstream messages to this app");
        } else {
            Log.i(TAG, "An error occurred while either fetching the InstanceID token,\n" +
                    "        sending the fetched token to the server or subscribing to the PubSub topic. Please try\n" +
                    "        running the sample again.");
        }

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

		// goto main home if already logged in
		flib = new FunctionLibrary(this);
		if (flib.isUserLoggedIn(getApplicationContext())) {
			Intent i = new Intent(getApplicationContext(), MainHome.class);
			startActivity(i);
			finish(); // destroy current login intent
		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);

		unitenid = (EditText) findViewById(R.id.EditTextIdentifier);
		passwEditText = (EditText) findViewById(R.id.EditTextPass);

        // [START screen_view_hit]
        Log.i(TAG, "Setting screen name:Login");
        mTracker.setScreenName("Login Screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]

		loginButton = (Button) findViewById(R.id.btnLogin);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				// spinner dialog
				progressDialog = new ProgressDialog(view.getContext());

				_initTask = new QuerryAsyncTask();
				_initTask.execute(MainActivity.this);

				// Toast.makeText(MainActivity.this, "Button Clicked",
				// Toast.LENGTH_SHORT).show();
			}

		});

		return true;
	}

	// asyn call
	public class QuerryAsyncTask extends AsyncTask<Context, Integer, String[]> {

		@Override
		protected void onPreExecute() {
			progressDialog.setCancelable(true);
			progressDialog.setMessage("Logging you in...");
			progressDialog.setTitle("Loading");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.show();
			loginButton.setEnabled(false);
		}

		@Override
		protected String[] doInBackground(Context... params) {
			HttpParser httpParser = new HttpParser(unitenid.getText()
					.toString(), passwEditText.getText().toString());
			String[] output = httpParser.bioData();
			return output;
		}

		// @Override
		// dah selesai execute
		protected void onPostExecute(String[] result) {
			progressDialog.hide();
			loginButton.setEnabled(true);

			try{
			if (result != null) {
				if(result[0].compareToIgnoreCase(unitenid.getText().toString()) ==0){
					//compare id insert by user and from uniten info
					//if same
					// save to databases
					DatabaseHandler dHandler = new DatabaseHandler(
							getApplicationContext());
					dHandler.addBiodata(result);
				}
				
			}
			}
			catch (Exception e){
				Log.e("onPostExecute","Ex: "+e.toString());
			}

			// check if data already in databases
			if (flib.isUserLoggedIn(getApplicationContext())) {
				Intent i = new Intent(getApplicationContext(), MainHome.class);
				startActivity(i);
				finish(); // destroy current login intent
			} else {
				Toast.makeText(MainActivity.this,
						"Network errors or invalid credentials",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
				new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
	}

	@Override
	protected void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
		super.onPause();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}
	


}
