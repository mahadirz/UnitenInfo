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
import java.util.List;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import my.madet.adapter.ExamResultListAdapter;
import my.madet.function.DatabaseHandler;
import my.madet.function.HttpParser;
import android.os.AsyncTask;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Bundle;
import my.madet.function.FunctionLibrary;


public class ResultFragment extends Fragment {

	// View
	private View rootView;

	// progress dialog
	private ProgressDialog progressDialog;
	// database handler
	private DatabaseHandler dbHandler;
	private QuerryAsyncTask _initTask;

	private HashMap<String, String[]> ResultContent;
	private String[] ArrayPointer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_result, container, false);
		
		//init db handler
		dbHandler = new DatabaseHandler(getActivity());
		
		//init progress dialog
        progressDialog = new ProgressDialog(rootView.getContext());
        
        BuildData(); //build save to ResultContent & ArrayPointer

		/*
		HashMap<String, String[]> test = new HashMap<String, String[]>();
		test.put("Semester 1 2013/2014", new String[] { "GPA: 3.98",
				"CGPA: 3.88", "medal_award_gold" });
		test.put("Semester 2 2014/2015", new String[] { "GPA: 3.99",
				"CGPA: 3.96", "medal_award_silver" });
		String[] test2 = new String[] { "Semester 1 2013/2014",
				"Semester 2 2014/2015" };
		*/

		ExamResultListAdapter lvadapter = new ExamResultListAdapter(
				getActivity(), ResultContent, ArrayPointer);
		ListView mylist = (ListView) rootView.findViewById(R.id.lvResult);
		mylist.setAdapter(lvadapter);

		Button btn_resultButton = (Button) rootView
				.findViewById(R.id.btn_result_refresh);
		btn_resultButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// execute async
				_initTask = new QuerryAsyncTask();
				_initTask.execute(v.getContext());

			}
		});
		
		mylist.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
							
				//get subject list
				ArrayList<HashMap<String, String>> subjectList = dbHandler.getSubjectList(ArrayPointer[arg2]);
				//build row
				//string builder
				StringBuilder rows = new StringBuilder();
				for(int i=0; i<subjectList.size(); i++){
					rows.append("<TR CLASS=\"LINE"+((i%2)+1)+"\"><TD>"+(i+1)+".</TD><TD>");
					rows.append(subjectList.get(i).get(DatabaseHandler.RESULT_SUBJECT_CODE));
					rows.append("</TD><TD>");
					rows.append(subjectList.get(i).get(DatabaseHandler.RESULT_SUBJECT_DESCRIPTIONS));
					rows.append("</TD><TD>");
					rows.append(subjectList.get(i).get(DatabaseHandler.RESULT_SUBJECT_SECTION));
					rows.append("</TD><TD ALIGN=\"RIGHT\">");
					rows.append(subjectList.get(i).get(DatabaseHandler.RESULT_SUBJECT_CREDITS));
					rows.append("</TD><TD>");
					rows.append(subjectList.get(i).get(DatabaseHandler.RESULT_SUBJECT_GRADE));
					rows.append("<TD ALIGN=\"RIGHT\">");
					rows.append(subjectList.get(i).get(DatabaseHandler.RESULT_SUBJECT_POINTS));
					rows.append("</TD></TR>");					
				}
				
				
				
				Log.i("Rows string",rows.toString());
				
				FunctionLibrary functionlib = new FunctionLibrary();				
				String htmlTemplateString = functionlib.LoadData("templateresult.html",rootView.getContext());
				
				//replace template
				htmlTemplateString = htmlTemplateString.replaceFirst("%REPLACE_ROW%",rows.toString() );
				
				AlertDialog.Builder alert = new AlertDialog.Builder(rootView.getContext()); 
				alert.setTitle("Result");

				WebView wv = new WebView(rootView.getContext());
				//wv.loadUrl("http:\\www.google.com");
				wv.loadData(htmlTemplateString, "text/html; charset=UTF-8", null);
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
		
		//check if table is empty
		//v2.2 fix force close
        if(dbHandler.getRowCount(DatabaseHandler.TABLE_RESULT) <= 0){
        	//table is empty
        	//so init
			//execute async
			_initTask = new QuerryAsyncTask();
			_initTask.execute(rootView.getContext());
        }
		
		new LoadAdMob().execute();

		return rootView;
	}

	public void BuildData() {
		HashMap<String, String[]> exresult = dbHandler.getExamResult();

		HashMap<String, String[]> output = new HashMap<String, String[]>();

		// storing the pointer
		List<String> list = new ArrayList<String>();

		// parse exam result to add image
		// aKey is the semester name
		for (String aKey : exresult.keySet()) {
			String aValue[] = exresult.get(aKey);

			// recreating the gpa and cgpa
			String[] newStr = new String[3];
			newStr[0] = "GPA: " + aValue[0];
			newStr[1] = "CGPA: " + aValue[1];

			if (Float.parseFloat(aValue[0]) >= 3.5) {
				newStr[2] = "medal_award_gold";
			} else if (Float.parseFloat(aValue[0]) >= 3.0) {
				newStr[2] = "medal_award_silver";
			} else {
				newStr[2] = "medal_award_bronze";
			}

			// add to list
			list.add(aKey);

			output.put(aKey, newStr);
		}

		String[] arrList = list.toArray(new String[list.size()]);
		ArrayPointer = arrList;
		ResultContent = output;
	}

	// asyn call
	public class QuerryAsyncTask extends
			AsyncTask<Context, Integer, HashMap<String, String[]>> {
		
		private ArrayList<HashMap<String, String>> resultSubjectList;

		@Override
		protected void onPreExecute() {
			progressDialog.setCancelable(true);
			progressDialog.setMessage("Please wait..");
			progressDialog.setTitle("Refreshing the data");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.show();
		}

		@Override
		protected HashMap<String, String[]> doInBackground(Context... params) {
			// retrieve password n id
			HashMap<String, String> bioHashMap = new HashMap<String, String>();
			bioHashMap = dbHandler.getBiodata();

			HttpParser httpParser = new HttpParser(
					bioHashMap.get(DatabaseHandler.BIODATA_KEY_ID),
					bioHashMap.get(DatabaseHandler.BIODATA_KEY_PASSWORD));
			HashMap<String, String[]> output = httpParser.ExamResult();
			
			resultSubjectList = new ArrayList<HashMap<String, String>>();
			resultSubjectList = httpParser.getResultSubjectList();
			
			return output;
		}

		// @Override
		// dah selesai execute
		protected void onPostExecute(HashMap<String, String[]> result) {
			progressDialog.hide();

			if (result != null) {

				// reset the table first
				dbHandler.resetTables(DatabaseHandler.TABLE_RESULT);

				// save to databases
				dbHandler.addExamResult(result);

				// refresh the listview
				// get the listview
				BuildData();

				if (ResultContent != null) {
					 ExamResultListAdapter lvadapter = new
					 ExamResultListAdapter(getActivity(),
					 ResultContent,ArrayPointer);
					 ListView mylist = (ListView)
					 rootView.findViewById(R.id.lvResult);
					 mylist.setAdapter(lvadapter);
					 
					 lvadapter.notifyDataSetChanged();
				}

			} else {
				Toast.makeText(getActivity(), "Refresh failed",
						Toast.LENGTH_SHORT).show();
			}
			
			if(resultSubjectList != null){
				dbHandler.resetTables(DatabaseHandler.TABLE_RESULT_LIST);
				dbHandler.addSubjectList(resultSubjectList);
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
