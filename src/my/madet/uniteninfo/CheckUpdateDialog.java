package my.madet.uniteninfo;

import my.madet.function.HttpHandler;
import my.madet.function.MyPreferences;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;

public class CheckUpdateDialog {
	
	private Context ourContext;
	private MyPreferences myPreferences;
	
	public CheckUpdateDialog(Context c){
		ourContext = c;
		myPreferences = new MyPreferences(c);
	}
	
	public void checkUpdate(){
		long unixTime = System.currentTimeMillis() / 1000L;
		Long unixTimePlus24Hours = myPreferences.getLongPreference(MyPreferences.LAST_UPDATE_CHECKED)+86400L;
		//just check once per day
		if(!myPreferences.getBooleanPreference(MyPreferences.UPDATE_ENABLED) ||  (unixTime < unixTimePlus24Hours)){
			return;
		}
		
		//update last check
		myPreferences.setLongPreference(MyPreferences.LAST_UPDATE_CHECKED, unixTime);
		
		new HttpHandler() {
			@Override
			public HttpUriRequest getHttpRequestMethod() {

				return new HttpGet("https://raw.githubusercontent.com/mahadirz/UnitenInfo/master/version.txt");
			}

			@Override
			public void onResponse(String result) {
				Log.d("HttpHandler", "result: " + result);
				int versionCode = 0;
			    try {
			    	versionCode = ourContext.getPackageManager().getPackageInfo(ourContext.getPackageName(), 0).versionCode;
			    } catch (NameNotFoundException e) {
			    	versionCode = 1000;
			    }
				try {
					JSONObject jObject = new JSONObject(result);
					
					//check if have new version
					if (jObject.getInt("versionCode") > versionCode) {
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ourContext);
				 
							// set title
							alertDialogBuilder.setTitle("New Update");
				 
							// set dialog message
							alertDialogBuilder
								.setMessage("Version "+jObject.getString("versionName")+" is available for update!")
								.setCancelable(false)
								.setPositiveButton("Update",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {
										ourContext.startActivity(new Intent(
												Intent.ACTION_VIEW,
												Uri.parse("http://play.google.com/store/apps/details?id="
														+ ourContext.getPackageName())));
									}
								  })
								.setNegativeButton("Dismiss",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {
										// if this button is clicked, just close
										// the dialog box and do nothing
										dialog.cancel();
									}
								})
							
							.setNeutralButton("Don't show Again",
						            new DialogInterface.OnClickListener() {
						                public void onClick(DialogInterface dialog, int id) {

						                   myPreferences.setBooleanPreference(MyPreferences.UPDATE_ENABLED, false);

						                }
						            });
				 
								// create alert dialog
								AlertDialog alertDialog = alertDialogBuilder.create();
				 
								// show it
								alertDialog.show();
					}
						
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void preRequest() {
				// TODO Auto-generated method stub
			}

		}.execute();
	}
}
