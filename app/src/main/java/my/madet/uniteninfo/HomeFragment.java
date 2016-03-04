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

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import my.madet.function.DatabaseHandler;
import my.madet.function.HttpParser;

public class HomeFragment extends Fragment {

	private View rootView;
	private ProgressDialog progressDialog;
	private QuerryAsyncTask _initTask;
	private DatabaseHandler dbHandler;
	private SimpleAdapter adapter;
	private ListView listview;
	

	public HomeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_home, container, false);
		
		//perform auto update checking
        CheckUpdateDialog checkUpdateDialog = new CheckUpdateDialog(getActivity());
        checkUpdateDialog.checkUpdate();

		listview = (ListView) rootView.findViewById(R.id.lv_biodata);

		// database handler
		dbHandler = new DatabaseHandler(rootView.getContext());
		
		//init progress dialog
		progressDialog = new ProgressDialog(rootView.getContext());
		

		Button refreshB = (Button) rootView
				.findViewById(R.id.btn_biodata_refresh);
		refreshB.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Toast.makeText(rootView.getContext(),
				// "Button Clicked",Toast.LENGTH_SHORT).show();
				_initTask = new QuerryAsyncTask();
				_initTask.execute(v.getContext());

			}
		});

		ArrayList<Map<String, String>> list2 = buildData(rootView.getContext());
		String[] from = { "title", "subtitle" };
		int[] to = { android.R.id.text1, android.R.id.text2 };

		adapter = new SimpleAdapter(rootView.getContext(), list2,
				android.R.layout.simple_list_item_2, from, to);
		listview.setAdapter(adapter);
		
		//dbHandler.createSubjectList();
		//FunctionLibrary functionLibrary = new FunctionLibrary(getActivity());
		//functionLibrary.readCSV("subjectList.csv");
		//dbHandler.insertSubjectList(functionLibrary.readCSV("subjectList.csv"));

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
			HashMap<String, String> bioHashMap = new HashMap<String, String>();
			bioHashMap = dbHandler.getBiodata();

			HttpParser httpParser = new HttpParser(
					bioHashMap.get(DatabaseHandler.BIODATA_KEY_ID),
					bioHashMap.get(DatabaseHandler.BIODATA_KEY_PASSWORD));
			String[] output = httpParser.bioData();
			return output;
		}

		// @Override
		// dah selesai execute
		protected void onPostExecute(String[] result) {
			progressDialog.hide();

			if (result != null) {

				// reset the table first
				dbHandler.resetTables(DatabaseHandler.TABLE_BIODATA);

				// save to databases
				dbHandler.addBiodata(result);

				// refresh the listview
				ArrayList<Map<String, String>> list2 = buildData(rootView
						.getContext());
				String[] from = { "title", "subtitle" };
				int[] to = { android.R.id.text1, android.R.id.text2 };

				adapter = new SimpleAdapter(rootView.getContext(), list2,
						android.R.layout.simple_list_item_2, from, to);
				listview.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			} else {
				Toast.makeText(rootView.getContext(), "Refresh failed",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private ArrayList<Map<String, String>> buildData(Context c) {
		HashMap<String, String> bio = new HashMap<String, String>();
		DatabaseHandler dbh = new DatabaseHandler(c);
		bio = dbh.getBiodata();

		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		list.add(putData("Student ID", bio.get(DatabaseHandler.BIODATA_KEY_ID)));
		list.add(putData("Full Name",
				bio.get(DatabaseHandler.BIODATA_KEY_FULLNAME)));
		list.add(putData("Student Status",
				bio.get(DatabaseHandler.BIODATA_KEY_STATUS)));
		list.add(putData("Program",
				bio.get(DatabaseHandler.BIODATA_KEY_PROGRAM)));
		list.add(putData("Campus", bio.get(DatabaseHandler.BIODATA_KEY_CAMPUS)));
		list.add(putData("Advisor",
				bio.get(DatabaseHandler.BIODATA_KEY_ADVISOR)));
		list.add(putData("Phone Number",
				bio.get(DatabaseHandler.BIODATA_KEY_PHONE)));
		list.add(putData("E-Mail Address",
				bio.get(DatabaseHandler.BIODATA_KEY_EMAIL)));
		return list;
	}

	private HashMap<String, String> putData(String title, String subtitle) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("title", title);
		item.put("subtitle", subtitle);
		return item;
	}

}
