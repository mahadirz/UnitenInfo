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

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.Context;
import android.widget.Toast;
import my.madet.adapter.MyExpandableListAdapter;
import my.madet.function.DatabaseHandler;
import my.madet.function.HttpParser;
import my.madet.function.TimeTableStruct;


public class TimetableFragment extends Fragment {

	private MyExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	private List<String> listDataHeader;
	private HashMap<String, List<String>> listDataChild;
	
	//View
	private View rootView;
	
	//progress dialog
	private ProgressDialog progressDialog;
	//database handler
	private DatabaseHandler dbHandler;
	private QuerryAsyncTask _initTask;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		rootView = inflater.inflate(R.layout.fragment_timetable,
				container, false);
		
		new LoadAdMob().execute();
		
		//init database handler
        dbHandler = new DatabaseHandler(rootView.getContext());
        
        //init progress dialog
        progressDialog = new ProgressDialog(rootView.getContext());
        
        // get the listview
		expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);

		// preparing list data
		prepareListData();

		listAdapter = new MyExpandableListAdapter(rootView.getContext(),
				listDataHeader, listDataChild);

		// setting list adapter
		expListView.setAdapter(listAdapter);
		
		//get day of the week
		Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        Log.i("Day of week","int: "+dayOfWeek);
        
        //expand group
        if(dayOfWeek == 1) //sunday, start from 1
        	dayOfWeek = 8; //make it last
        dayOfWeek = dayOfWeek- 2;
        expListView.expandGroup(dayOfWeek);
		
		//button btn_timetable_refresh
		Button btn_refreshButton = (Button) rootView.findViewById(R.id.btn_timetable_refresh);
		
		//set onClick
		btn_refreshButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//execute async
				_initTask = new QuerryAsyncTask();
				_initTask.execute(v.getContext());
			}
		});
		
		//check if table is empty
		//v2.2 fix force close
        if(dbHandler.getRowCount(DatabaseHandler.TABLE_TIMETABLE) <= 0){
        	//table is empty
        	//so init
			//execute async
			_initTask = new QuerryAsyncTask();
			_initTask.execute(rootView.getContext());
        }

		return rootView;
	}
	
	// asyn call
	public class QuerryAsyncTask extends
			AsyncTask<Context, Integer, List<TimeTableStruct>> {

		@Override
		protected void onPreExecute() {
				progressDialog.setCancelable(true);
				progressDialog.setMessage("Please wait..");
				progressDialog.setTitle("Refreshing the data");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.show();
			}

			@Override
			protected List<TimeTableStruct> doInBackground(Context... params) {
				//retrieve password n id
				HashMap<String, String> bioHashMap = new HashMap<String,String>();
				bioHashMap = dbHandler.getBiodata();
				
				//Log.d("do in bg: debug","BIODATA_KEY_ID "+bioHashMap.get(DatabaseHandler.BIODATA_KEY_ID));
				//Log.d("do in bg: debug","BIODATA_KEY_PASSWORD "+bioHashMap.get(DatabaseHandler.BIODATA_KEY_PASSWORD));
				
				HttpParser httpParser = new HttpParser(bioHashMap.get(DatabaseHandler.BIODATA_KEY_ID),
						bioHashMap.get(DatabaseHandler.BIODATA_KEY_PASSWORD));
				List<TimeTableStruct> output = httpParser.Timetable();
				return output;
			}

			// @Override
			// dah selesai execute
			protected void onPostExecute(List<TimeTableStruct> result) {
				progressDialog.hide();

				if (result != null) {
					
					//reset the table first
					dbHandler.resetTables(DatabaseHandler.TABLE_TIMETABLE);
					
					// save to databases
					dbHandler.AddTimeTable(result);
					
					//refresh the listview
					// get the listview
					expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);

					// preparing list data
					prepareListData();

					listAdapter = new MyExpandableListAdapter(rootView.getContext(),
							listDataHeader, listDataChild);

					// setting list adapter
					expListView.setAdapter(listAdapter);
					
					
					//get day of the week
					Calendar c = Calendar.getInstance();
			        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			        Log.i("Day of week","int: "+dayOfWeek);
			        
			        //expand group
			        if(dayOfWeek == 1) //sunday, start from 1
			        	dayOfWeek = 8; //make it last
			        dayOfWeek = dayOfWeek- 2;
			        expListView.expandGroup(dayOfWeek);
	
				}
				else {
					Toast.makeText(getActivity(),"Refresh failed",Toast.LENGTH_SHORT).show();
				}
			}
		}

	/*
	 * Preparing the list data
	 */
	private void prepareListData() {
		
		//retrieve time table from databases;
		List<TimeTableStruct>timeTableStructs = dbHandler.getTimeTable();
		
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		// Adding child data
		listDataHeader.add("Monday");
		listDataHeader.add("Tuesday");
		listDataHeader.add("Wednesday");
		listDataHeader.add("Thursday");
		listDataHeader.add("Friday");
		listDataHeader.add("Saturday");
		listDataHeader.add("Sunday");
		
		List<String> Monday = new ArrayList<String>();
		List<String> Tuesday = new ArrayList<String>();
		List<String> Wednesday = new ArrayList<String>();
		List<String> Thursday = new ArrayList<String>();
		List<String> Friday = new ArrayList<String>();
		List<String> Saturday = new ArrayList<String>();
		List<String> Sunday = new ArrayList<String>();
		
		//assign subject, time n location
		for(TimeTableStruct a : timeTableStructs){
			if(a.getDay().compareToIgnoreCase("monday") == 0){
				Monday.add(a.getStartTime()+" - "+a.getEndTime()+ "\n"+a.getSubject()+ "\n"+a.getLocation());
			}
			else if(a.getDay().compareToIgnoreCase("tuesday") == 0){
				Tuesday.add(a.getStartTime()+" - "+a.getEndTime()+ "\n"+a.getSubject()+ "\n"+a.getLocation());
			}
			else if(a.getDay().compareToIgnoreCase("wednesday") == 0){
				Wednesday.add(a.getStartTime()+" - "+a.getEndTime()+ "\n"+a.getSubject()+ "\n"+a.getLocation());
			}
			else if(a.getDay().compareToIgnoreCase("thursday") == 0){
				Thursday.add(a.getStartTime()+" - "+a.getEndTime()+ "\n"+a.getSubject()+ "\n"+a.getLocation());
			}
			else if(a.getDay().compareToIgnoreCase("friday") == 0){
				Friday.add(a.getStartTime()+" - "+a.getEndTime()+ "\n"+a.getSubject()+ "\n"+a.getLocation());
			}
			else if(a.getDay().compareToIgnoreCase("saturday") == 0){
				Saturday.add(a.getStartTime()+" - "+a.getEndTime()+ "\n"+a.getSubject()+ "\n"+a.getLocation());
			}
			else if(a.getDay().compareToIgnoreCase("sunday") == 0){
				Sunday.add(a.getStartTime()+" - "+a.getEndTime()+ "\n"+a.getSubject()+ "\n"+a.getLocation());
			}
		}

		listDataChild.put(listDataHeader.get(0), Monday); // Header, Child data
		listDataChild.put(listDataHeader.get(1), Tuesday);
		listDataChild.put(listDataHeader.get(2), Wednesday);
		listDataChild.put(listDataHeader.get(3), Thursday);
		listDataChild.put(listDataHeader.get(4), Friday);
		listDataChild.put(listDataHeader.get(5), Saturday);
		listDataChild.put(listDataHeader.get(6), Sunday);
	}
	
	//asyn call for admob
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
