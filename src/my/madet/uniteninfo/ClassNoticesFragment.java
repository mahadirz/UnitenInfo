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
 * Class Notices 
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
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import my.madet.function.DatabaseHandler;
import my.madet.function.HttpParser;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ClassNoticesFragment extends Fragment {
	
	// progress dialog
	private ProgressDialog progressDialog;
	// database handler
	private DatabaseHandler dbHandler;
	private QuerryAsyncTask _initTask;
	private View rootView;
	private ListView listview;
	private SimpleAdapter adapter;
	
	private ArrayList<HashMap<String, String>> classNoticeArrayList;
	
	public ClassNoticesFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.fragment_class_notices, container, false);
        //init database
        dbHandler = new DatabaseHandler(rootView.getContext());        
        //init progress dialog
        progressDialog = new ProgressDialog(rootView.getContext());
        
        //init list view
        listview = (ListView) rootView.findViewById(R.id.lv_classnotices);
        
        //check if table is empty
        if(dbHandler.getRowCount(DatabaseHandler.TABLE_CLASS_NOTICES) <= 0){
        	_initTask = new QuerryAsyncTask();
            _initTask.execute(rootView.getContext());
        }
        
        
        Button refreshButton = (Button) rootView.findViewById(R.id.btn_classnotice);
        refreshButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				_initTask = new QuerryAsyncTask();
	            _initTask.execute(rootView.getContext());
			}
		});
        
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        //add mapping
        classNoticeArrayList = dbHandler.getClassNotices();
        for(int i=0; i< classNoticeArrayList.size(); i++){
        	HashMap<String, String> item = new HashMap<String, String>();
        	item.put("title", classNoticeArrayList.get(i).get(DatabaseHandler.CLASS_NOTICES_TITLE));
        	item.put("subtitle", classNoticeArrayList.get(i).get(DatabaseHandler.CLASS_NOTICES_DATETIME));
        	list.add(item);
        }
		String[] from = { "title", "subtitle" };
		int[] to = { android.R.id.text1, android.R.id.text2 };

		adapter = new SimpleAdapter(rootView.getContext(), list,android.R.layout.simple_list_item_2, from, to);
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alert = new AlertDialog.Builder(rootView.getContext()); 
				alert.setTitle(classNoticeArrayList.get(arg2).get(DatabaseHandler.CLASS_NOTICES_TITLE));

				WebView wv = new WebView(rootView.getContext());
				//wv.loadUrl("http:\\www.google.com");
				wv.loadData(classNoticeArrayList.get(arg2).get(DatabaseHandler.CLASS_NOTICES_MESSAGES), "text/html; charset=UTF-8", null);
				wv.setWebViewClient(new WebViewClient() {
				    @Override
				    public boolean shouldOverrideUrlLoading(WebView view, String url) {
				        view.loadUrl(url);

				        return true;
				    }
				});

				alert.setView(wv);
				alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int id) {
				        dialog.dismiss();
				    }
				});
				alert.show();
				
			}
		});
        
        //load advertisement
        new LoadAdMob().execute();
        return rootView;
    }
	
	
	public class QuerryAsyncTask extends AsyncTask<Context, Integer, ArrayList<HashMap<String, String>>> {

		@Override
		protected void onPreExecute() {
			progressDialog.setCancelable(true);
			progressDialog.setMessage("Please wait..");
			progressDialog.setTitle("Refreshing the data");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.show();
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(Context... params) {
			HashMap<String, String> bioHashMap = new HashMap<String,String>();
			bioHashMap = dbHandler.getBiodata();
			
			HttpParser httpParser = new HttpParser(bioHashMap.get(DatabaseHandler.BIODATA_KEY_ID),
					bioHashMap.get(DatabaseHandler.BIODATA_KEY_PASSWORD));
			ArrayList<HashMap<String, String>> output = httpParser.getClassNotices();
			return output;
		}

		// @Override
		// dah selesai execute
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			progressDialog.hide();

			if (result != null) {
				
				//check if has latest notices
				if(dbHandler.getRowCount(DatabaseHandler.TABLE_CLASS_NOTICES)<result.size()){
					
					//reset the table first
					dbHandler.resetTables(DatabaseHandler.TABLE_CLASS_NOTICES);
					
					//reset auto increment
					dbHandler.resetAutoIncrement(DatabaseHandler.TABLE_CLASS_NOTICES);
					
					// save to databases
					dbHandler.addClassNotices(result);
					
					//update the view
					ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
			        //add mapping
			        classNoticeArrayList = dbHandler.getClassNotices();
			        for(int i=0; i< classNoticeArrayList.size(); i++){
			        	HashMap<String, String> item = new HashMap<String, String>();
			        	item.put("title", classNoticeArrayList.get(i).get(DatabaseHandler.CLASS_NOTICES_TITLE));
			        	item.put("subtitle", classNoticeArrayList.get(i).get(DatabaseHandler.CLASS_NOTICES_DATETIME));
			        	list.add(item);
			        }
					String[] from = { "title", "subtitle" };
					int[] to = { android.R.id.text1, android.R.id.text2 };

					adapter = new SimpleAdapter(rootView.getContext(), list,android.R.layout.simple_list_item_2, from, to);
					listview.setAdapter(adapter);
				}

			}
			else {
				Toast.makeText(rootView.getContext(),"Refresh failed",Toast.LENGTH_SHORT).show();
			}
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
			//adView.setAdSize(AdSize.SMART_BANNER);
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
