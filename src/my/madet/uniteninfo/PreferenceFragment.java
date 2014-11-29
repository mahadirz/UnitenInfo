package my.madet.uniteninfo;

import java.util.HashMap;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import my.madet.function.DatabaseHandler;
import my.madet.function.MyPreferences;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PreferenceFragment extends Fragment {
	
	public static final int RESULT_SETTINGS = 1356;
	public static final int DIALOG_FRAGMENT_PASSWORD = 1357;
	private MyPreferences myPreferences;
	private Button buttonOpenPref;
	
	int mStackLevel = 0;
	DialogFragment dialogFrag;
	// View
    private View rootView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    if (savedInstanceState != null) {
	        mStackLevel = savedInstanceState.getInt("level");
	    }
	    
	    myPreferences = new MyPreferences(getActivity());    
	    
	    
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_preferences, container, false);
		buttonOpenPref = (Button) rootView.findViewById(R.id.buttonOpenPref);
		
		if(myPreferences.getBooleanPreference(MyPreferences.RESULT_PASSWORD_PROTECTED)){
			//hide open preference
			buttonOpenPref.setVisibility(View.GONE);
	    	showDialog(DIALOG_FRAGMENT_PASSWORD);
	    }
	    else{
	    	//preference fragment must have dedicated activity to hold it 
		    Intent i = new Intent(getActivity(), PreferenceActivity.class);
			startActivityForResult(i, RESULT_SETTINGS);
	    }
		
		buttonOpenPref.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity(), PreferenceActivity.class);
				startActivityForResult(i, RESULT_SETTINGS);
			}
		});
		
		new LoadAdMob().execute();
		
		return rootView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putInt("level", mStackLevel);
	}
	
	void showDialog(int type) {

	    mStackLevel++;

	    FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
	    Fragment prev = getActivity().getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    switch (type) {

	        case DIALOG_FRAGMENT_PASSWORD:

	            dialogFrag = PasswordDialogFragment.newInstance(123);
	            dialogFrag.setTargetFragment(this, DIALOG_FRAGMENT_PASSWORD);
	            dialogFrag.show(getFragmentManager().beginTransaction(), "dialog");
	            

	            break;
	    }
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == RESULT_SETTINGS){
			Log.d("onActivityResult","prefPasswordProtected: "+myPreferences.getBooleanPreference(MyPreferences.RESULT_PASSWORD_PROTECTED));
		}
		else if (requestCode == DIALOG_FRAGMENT_PASSWORD) {
            // After Ok code.
        	EditText editTextPassword = (EditText) dialogFrag.getDialog().findViewById(R.id.password);
        	String passwordEnteredString = editTextPassword.getText().toString();
        	HashMap<String, String> bioHashMap = new HashMap<String, String>();
        	DatabaseHandler dbHandler = new DatabaseHandler(getActivity());
			bioHashMap = dbHandler.getBiodata();
			if(bioHashMap.get(DatabaseHandler.BIODATA_KEY_PASSWORD).equalsIgnoreCase(passwordEnteredString)){
				//password match
				//show button
				buttonOpenPref.setVisibility(View.VISIBLE);
			}
			else {
				Toast.makeText(getActivity(), "Invalid Password!", Toast.LENGTH_LONG).show();
			}
        	
        } else if (resultCode == Activity.RESULT_CANCELED){
            // After Cancel code.
        	Log.d("onActivityResult", "Cancel");
        }
	}
	
	public class LoadAdMob extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		protected void onPostExecute(String d ) {
			
			//emulator B3EEABB8EE11C2BE770B684D95219ECB			
			// Look up the AdView as a resource and load a request.			
			Log.i("OnPostExecute","Entering the admob");
	    	
			AdView adView = (AdView)rootView.findViewById(R.id.adView);
			/*
			AdRequest adRequest = new AdRequest.Builder()
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // Emulator
			.addTestDevice("D1D581E30365F91FF788F9194B179171") // My Galaxy Nexus test phone
			.build();
			*/
			AdRequest adRequest = new AdRequest.Builder().build();
			adView.loadAd(adRequest);
						
		}
		
	}
	
	
}
