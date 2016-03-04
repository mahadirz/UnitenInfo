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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import my.madet.function.DatabaseHandler;
import my.madet.function.HttpParser;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ScorunFragment extends Fragment {

	private View rootView;
	private ProgressDialog progressDialog;
	private QuerryAsyncTask _initTask;
	private DatabaseHandler dbHandler;
	private SimpleAdapter adapter;
	private ListView listview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_scorun, container,
				false);
		
		//database handler
        dbHandler = new DatabaseHandler(rootView.getContext());
        
      //init progress dialog
        progressDialog = new ProgressDialog(rootView.getContext());
        
      //init task execute replaced to the bottom of oncreate
        
        Button refreshB = (Button) rootView.findViewById(R.id.btn_scorun_refresh);
        refreshB.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				_initTask = new QuerryAsyncTask();
				_initTask.execute(v.getContext());
				
			}
		});
        
        ArrayList<Map<String, String>> list2 = buildData(rootView.getContext());
        String[] from = { "title", "subtitle" };
        int[] to = { android.R.id.text1, android.R.id.text2 };

        listview = (ListView) rootView.findViewById(R.id.lv_scorun);
        adapter = new SimpleAdapter(rootView.getContext(), list2,android.R.layout.simple_list_item_2, from, to);
        listview.setAdapter(adapter);
        
      //check if table is empty
        if(dbHandler.getRowCount(DatabaseHandler.TABLE_SCORUN) <= 0){
        	//table is empty
        	//so init
			//execute async
			_initTask = new QuerryAsyncTask();
			_initTask.execute(rootView.getContext());
        }
        
        new LoadAdMob().execute();

		return rootView;
	}
	// asyn call
			public class QuerryAsyncTask extends AsyncTask<Context, Integer, String[]> {

				@Override
				protected void onPreExecute() {
					progressDialog.setCancelable(true);
					progressDialog.setMessage("Please wait..");
					progressDialog.setTitle("Refreshing the data");
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.show();
				}

				@Override
				protected String[] doInBackground(Context... params) {
					HashMap<String, String> bioHashMap = new HashMap<String,String>();
					bioHashMap = dbHandler.getBiodata();
					
					HttpParser httpParser = new HttpParser(bioHashMap.get(DatabaseHandler.BIODATA_KEY_ID),
							bioHashMap.get(DatabaseHandler.BIODATA_KEY_PASSWORD));
					String[] output = httpParser.GetScorun();
					return output;
				}

				// @Override
				// dah selesai execute
				protected void onPostExecute(String[] result) {
					progressDialog.hide();

					if (result != null) {
						
						//reset the table first
						dbHandler.resetTables(DatabaseHandler.TABLE_SCORUN);
						
						// save to databases
						dbHandler.addScorun(result);
						
						//refresh the listview
						ArrayList<Map<String, String>> list2 = buildData(rootView.getContext());
				        String[] from = { "title", "subtitle" };
				        int[] to = { android.R.id.text1, android.R.id.text2 };

				        adapter = new SimpleAdapter(rootView.getContext(), list2,android.R.layout.simple_list_item_2, from, to);
				        listview.setAdapter(adapter);
				        adapter.notifyDataSetChanged();
					}
					else {
						Toast.makeText(rootView.getContext(),"Refresh failed",Toast.LENGTH_SHORT).show();
					}
				}
			}
		
			/**
			 * 0 - Arts & Cultural 
			 * 1- Communication & Enterpreneurship 
			 * 2- Leadership &  Intellectual 
			 * 3- Spiritual & Civilization 
			 * 4- Sports & Recreational 
			 * 5 - total
			 * 
			 * @param score
			 */
			private ArrayList<Map<String, String>> buildData(Context c) {
	        DatabaseHandler dbh  = new DatabaseHandler(c);
	        String[] score = dbh.getScorun();
	        
	        int i = 0;
	        for(String s : score){
	        	if(s == null)
	        		score[i] = "0.00";
	        	i++;
	        }
	        
		    ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		    list.add(putData("Arts & Cultural", score[0]));
		    list.add(putData("Communication & Entrepreneurship", score[1]));
		    list.add(putData("Leadership & Intellectual", score[2]));
		    list.add(putData("Spiritual & Civilization", score[3]));
		    list.add(putData("Sports & Recreational", score[4]));
		    list.add(putData("Total Scorun", score[5]));
		    return list;
		  }

		  private HashMap<String, String> putData(String title, String subtitle) {
		    HashMap<String, String> item = new HashMap<String, String>();
		    item.put("title", title);
		    item.put("subtitle", subtitle);
		    return item;
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
