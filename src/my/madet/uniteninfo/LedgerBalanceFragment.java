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


import java.util.HashMap;

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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LedgerBalanceFragment extends Fragment {

	private View rootView;
	private ProgressDialog progressDialog;
	private QuerryAsyncTask _initTask;
	private DatabaseHandler dbHandler;
	private TextView txtlledgerbalance;
	private ImageView tickorexclaim;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_ledgerbalance, container,
				false);

		// database handler
		dbHandler = new DatabaseHandler(rootView.getContext());
		
		//init progress dialog
        progressDialog = new ProgressDialog(rootView.getContext());
        
        
        
        //check if table is empty
        if(dbHandler.getRowCount(DatabaseHandler.TABLE_LEDGER_BALANCE) <= 0){
        	//table is empty
        	//so init
			//execute async
			_initTask = new QuerryAsyncTask();
			_initTask.execute(rootView.getContext());
        }
        else{
        	//try to fix force close
        	//11-15 18:21:01.623: E/AndroidRuntime(5526): Caused by: java.lang.IllegalStateException: Cannot perform this operation because the connection pool has been closed.
        	UpdateUi();
        }
        

		Button refreshB = (Button) rootView
				.findViewById(R.id.btn_ledger_refresh);
		refreshB.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				_initTask = new QuerryAsyncTask();
				_initTask.execute(v.getContext());

			}
		});

		return rootView;
	}
	
	public void UpdateUi(){
		txtlledgerbalance = (TextView) rootView.findViewById(R.id.txtlledgerbalance);
		tickorexclaim = (ImageView) rootView.findViewById(R.id.tickorexclaim);
		
		try{
		String[] ledger = dbHandler.getLedgerBalance(); //get balance from db
		if(ledger != null){
			txtlledgerbalance.setText("RM "+ledger[0]);
			if(ledger[1].compareToIgnoreCase("You are not blocked") == 0)
				tickorexclaim.setImageResource(R.drawable.tick);
			else{
				tickorexclaim.setImageResource(R.drawable.exclamation);
				
				alertbox("Blocked Status", "You might not be able to view certain information from Student Info");
			}
		}
		}
		catch(Exception e){
			Log.e("UpdateUi"," "+e);
			txtlledgerbalance.setText("Error");
			tickorexclaim.setImageResource(R.drawable.exclamation);
		}	
	}
	
	protected void alertbox(String title, String mymessage)
	   {
	   new AlertDialog.Builder(getActivity())
	      .setMessage(mymessage)
	      .setTitle(title)
	      .setCancelable(true)
	      .setNeutralButton(android.R.string.cancel,
	         new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton){}
	         })
	      .show();
	   }

	// asyn call
	public class QuerryAsyncTask extends AsyncTask<Context, Integer, String> {

		private boolean blocked = true;

		@Override
		protected void onPreExecute() {
			progressDialog.setCancelable(true);
			progressDialog.setMessage("Please wait..");
			progressDialog.setTitle("Refreshing the data");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.show();
			
		}

		@Override
		protected String doInBackground(Context... params) {
			HashMap<String, String> bioHashMap = new HashMap<String, String>();
			bioHashMap = dbHandler.getBiodata();

			HttpParser httpParser = new HttpParser(
					bioHashMap.get(DatabaseHandler.BIODATA_KEY_ID),
					bioHashMap.get(DatabaseHandler.BIODATA_KEY_PASSWORD));
			String output = httpParser.LedgerBalance();
			blocked = !(httpParser.StatusNotBlocked());
			return output;
		}

		// @Override
		// dah selesai execute
		protected void onPostExecute(String result) {
			progressDialog.hide();

			if (result != null) {

				// reset the table first
				dbHandler.resetTables(DatabaseHandler.TABLE_LEDGER_BALANCE);

				// save to databases
				try{
					dbHandler.addLedgerBalance(result, blocked);
				}
				catch(Exception e){
					Log.e("Catch in OnPostExecute", "Error addledger "+e.toString());
				}				

				// refresh the balance
				UpdateUi();

			} else {
				Toast.makeText(rootView.getContext(), "Refresh failed",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
