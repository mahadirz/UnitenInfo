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

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.io.File;

import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.*;

import my.madet.function.*;

public class TimetableFragmentWeb extends Fragment{
	
	//View
	private View rootView;
	//web view
	private static WebView web;
	
	//progress dialog
	private ProgressDialog progressDialog;
	private ProgressBar progressBar;
	//database handler
	private DatabaseHandler dbHandler;
	private QuerryAsyncTask _initTask;
	
	//file cache
	private FileCache fileCache;
	
	private HashMap<String, String> bioHashMap;
			

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		rootView = inflater.inflate(R.layout.fragment_timetableweb,container, false);
		
		//perform auto update checking
        CheckUpdateDialog checkUpdateDialog = new CheckUpdateDialog(getActivity());
        checkUpdateDialog.checkUpdate();
		
		//init database handler
        dbHandler = new DatabaseHandler(rootView.getContext());
		
		//get id
		bioHashMap = new HashMap<String,String>();
		bioHashMap = dbHandler.getBiodata();
		
		
		fileCache = new FileCache(rootView.getContext(),bioHashMap.get(DatabaseHandler.BIODATA_KEY_ID));
		//init progress dialog
        progressDialog = new ProgressDialog(rootView.getContext());
        

	    if(fileCache.isHtmlTimeTableExist()==false){
	    	_initTask = new QuerryAsyncTask();
	    	_initTask.execute(getActivity());
	    }
		
	    //load admob
        new LoadAdMob().execute();
	    
	    //load webview
	    new LoadWebViewAsync().execute();
	    
	    Button refreshButton = (Button) rootView.findViewById(R.id.btn_timetable_refresh);
	    refreshButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				_initTask = new QuerryAsyncTask();
		    	_initTask.execute(getActivity());
			}
		});
	    
        return rootView;

	}
		
	public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            view.loadUrl(url);
            return true;

        }
        @Override
        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl) {
            // TODO Auto-generated method stub
             Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            //super.onReceivedError(view, errorCode, description, failingUrl);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

            progressBar.setVisibility(View.GONE);          
            
        }
    }
	
	//async call for webview
	public class LoadWebViewAsync extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		protected void onPostExecute(String d ) {
			
			web = (WebView) rootView.findViewById(R.id.webtimetable);
	        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
	        web.setWebViewClient(new myWebClient());
	        //web.invokeZoomPicker();
	        web.getSettings().setBuiltInZoomControls(true);
	        web.loadUrl(fileCache.htmlTimeTablePath());
	        
		}
		
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
			AdRequest adRequest = new AdRequest.Builder()
			//.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // Emulator
			//.addTestDevice("D1D581E30365F91FF788F9194B179171") // My Galaxy Nexus test phone
			.build();
			adView.loadAd(adRequest);
						
		}
		
	}
	
	
	// asyn call
		public class QuerryAsyncTask extends AsyncTask<Context, Void, String> {
			
			private String htmlTimetableString;
			private String htmlStyleCssString;

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
					//retrieve password n id
					Log.d("do in bg: debug","BIODATA_KEY_ID "+bioHashMap.get(DatabaseHandler.BIODATA_KEY_ID));
					Log.d("do in bg: debug","BIODATA_KEY_PASSWORD "+bioHashMap.get(DatabaseHandler.BIODATA_KEY_PASSWORD));
					
					HttpParser httpParser = new HttpParser(bioHashMap.get(DatabaseHandler.BIODATA_KEY_ID),
							bioHashMap.get(DatabaseHandler.BIODATA_KEY_PASSWORD));
					
					htmlTimetableString = httpParser.fetchHtmlTimeTable();
					htmlStyleCssString = httpParser.fetchHtmlStyleCss();			
					
					return null;
				}

				@Override
				protected void onPostExecute(String d ) {
					Log.i("onPostExecute","Entered the onPostExecute");
					progressDialog.hide();
					
					Display display = getActivity().getWindowManager().getDefaultDisplay();
				    DisplayMetrics metrics = new DisplayMetrics();
				    display.getMetrics(metrics);

				    //Log.i("TimetableFragment", "density :" +  metrics.density);

				    // density interms of dpi
				    //Log.i("TimetableFragment", "D density :" +  metrics.densityDpi);

				    // horizontal pixel resolution
				    //Log.i("TimetableFragment", "width pix :" +  metrics.widthPixels);

				     // actual horizontal dpi
				    //Log.i("TimetableFragment", "xdpi :" +  metrics.xdpi);

				    // actual vertical dpi
				    //Log.i("TimetableFragment", "ydpi :" +  metrics.ydpi);
				    
				    htmlTimetableString = htmlTimetableString.replaceAll("<HEAD>", "<HEAD><meta name=\"viewport\" content=\"target-densitydpi="+metrics.ydpi+"\" \\/>");

					if(htmlTimetableString != null){
						fileCache.saveHtmlTimeTable(htmlTimetableString);
						fileCache.saveHtmlStyleCss(htmlStyleCssString);
						Log.i("html path",fileCache.htmlTimeTablePath());
						//load webview
					    new LoadWebViewAsync().execute();
					}
					else{
						Log.e("TimetableFragmentWeb","Error in onPostExecute ");
					}
				}
			}
	
	
}
