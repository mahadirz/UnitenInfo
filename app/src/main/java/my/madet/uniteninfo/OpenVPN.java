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

//must reset upon logout
//myvpn_user_name - string
//devPayloadId  -string
//purchaseData purchaseDataSignature

package my.madet.uniteninfo;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import my.madet.function.DatabaseHandler;
import my.madet.function.HttpHandler;
import my.madet.function.InAppPurchase;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.vending.billing.IInAppBillingService;

import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OpenVPN extends Fragment {

	protected static final int BILLING_RESPONSE_RESULT_OK = 0;

	private SharedPreferences sharedPref;
	private IInAppBillingService mService;
	private View rootView;
	private Button subscribeButton;
	private Button manageSubscriptionButton;
	private ProgressDialog progressDialog;

	private DatabaseHandler dbHandler;
	private String studentEmail;

	private TextView linkAccountTextView;
	private EditText linkAccountUserNameEditText;
	private EditText linkAccountPasswordEditText;
	private Button linkAccountConnectButton;
	
	boolean isSubscribed = false;
	
	
	ServiceConnection mServiceConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
		
		

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IInAppBillingService.Stub.asInterface(service);

			try {
				Bundle ownedItems = mService.getPurchases(3, getActivity()
						.getPackageName(), "subs", null);
				int response = ownedItems.getInt("RESPONSE_CODE");
				// Log.i("getPurchases","response:"+response);
				if (response == 0) {
					// ArrayList<String> ownedSkus =
					// ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
					ArrayList<String> purchaseDataList = ownedItems
							.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
					// ArrayList<String> signatureList =
					// ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE");
					// String continuationToken =
					// ownedItems.getString("INAPP_CONTINUATION_TOKEN");

					for (int i = 0; i < purchaseDataList.size(); ++i) {
						String purchaseData = purchaseDataList.get(i);
						// String signature = signatureList.get(i);
						// String sku = ownedSkus.get(i);

						JSONObject jo = new JSONObject(purchaseData);
						int purchaseState = jo.getInt("purchaseState");

						if (purchaseState == InAppPurchase.PURCHASE_STATE_PURCHASED) {
							isSubscribed = true;
							Log.i("purchased", "purchaseState: purchased");
						}

						Log.i("purchased", "purchaseData: " + purchaseData);

					}
								
					onSubscribed();
					
				}
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
	};



	/**
	 * Constructor
	 */
	public OpenVPN() {
		
	}
	
	public void onSubscribed(){
		SharedPreferences.Editor editor = sharedPref.edit();
		Log.d("oncreate", "enter subscribed");
		try{
			if(isSubscribed){
				manageSubscriptionButton.setVisibility(View.VISIBLE);
				subscribeButton.setVisibility(View.GONE);
				// check if connected to myvpn
				if (sharedPref.getString("myvpn_user_name", "").equals("")) {

					linkAccountTextView.setVisibility(View.VISIBLE);
					linkAccountUserNameEditText.setVisibility(View.VISIBLE);
					linkAccountPasswordEditText.setVisibility(View.VISIBLE);
					linkAccountConnectButton.setVisibility(View.VISIBLE);

					linkAccountConnectButton.setOnClickListener(new LinkAccountButtonClicked(rootView
									.getContext()));

				}
			}
			else{
				editor.putString("purchaseData", "");
				editor.putString("dataSignature", "");
				editor.putString("myvpn_user_name", "");
				editor.commit();
				manageSubscriptionButton.setVisibility(View.GONE);
				subscribeButton.setVisibility(View.VISIBLE);
			}
			
		}
		catch(Exception e){
			Log.e("onSubscribed", "Exception: "+e.getStackTrace());
		}
			
	}

	/****** start of oncreate **/

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_openvpn, container, false);
		// init progress dialog
		progressDialog = new ProgressDialog(rootView.getContext());
		// init sharedpref
		sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

		dbHandler = new DatabaseHandler(rootView.getContext());
		studentEmail = dbHandler.getBiodata().get(
				DatabaseHandler.BIODATA_KEY_EMAIL);

		// show linked button
		linkAccountTextView = (TextView) rootView.findViewById(R.id.txtLabel2);
		linkAccountUserNameEditText = (EditText) rootView
				.findViewById(R.id.edittextUserName);
		linkAccountPasswordEditText = (EditText) rootView
				.findViewById(R.id.edittextPassword);
		linkAccountConnectButton = (Button) rootView
				.findViewById(R.id.buttonConnectToMyVpn);

		// subscribe and manage subcription button
		manageSubscriptionButton = (Button) rootView
				.findViewById(R.id.buttonManageSubs);
		subscribeButton = (Button) rootView.findViewById(R.id.buttonSubscribe);

		// generate developerPayload identifier
		if (sharedPref.getString("devPayloadId", "").equals("")) {
			// set the payload
			String data = studentEmail;
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString("devPayloadId", data);
			editor.commit();
		}
		

		Intent serviceIntent = new Intent(
				"com.android.vending.billing.InAppBillingService.BIND");
		serviceIntent.setPackage("com.android.vending");
		rootView.getContext().bindService(serviceIntent, mServiceConn,
				Context.BIND_AUTO_CREATE);

		subscribeButton.setOnClickListener(new SubscribeButtonClicked(rootView
				.getContext()));
		manageSubscriptionButton
				.setOnClickListener(new ManageSubscriptionButtonClicked(
						rootView.getContext()));

		
		
		

		return rootView;
	}

	/***** oncreate end ******/

	public class LinkAccountButtonClicked implements OnClickListener {

		Context context;

		public LinkAccountButtonClicked(Context context) {
			this.context = context;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// make http request
			new HttpHandler() {
				@Override
				public HttpUriRequest getHttpRequestMethod() {

					// orderId
					String orderId = "";
					JSONObject jo;
					try {
						jo = new JSONObject(sharedPref.getString(
								"purchaseData", ""));
						orderId = jo.getString("orderId");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// return new
					// HttpGet("http://hmkcode.com/examples/index.php");
					HttpPost httppost = new HttpPost(
							"https://apps.madet.my/myvpn/googlebilling/linkaccount.php");
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("username",
							linkAccountUserNameEditText.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("password",
							linkAccountPasswordEditText.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("orderId",
							orderId));
					try {
						httppost.setEntity(new UrlEncodedFormEntity(
								nameValuePairs));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return httppost;
				}

				@Override
				public void onResponse(String result) {
					Log.d("HttpHandler", "result: " + result);
					progressDialog.hide();
					try {
						JSONObject jObject = new JSONObject(result);

						if (jObject.getInt("code") == InAppPurchase.MYVPN_RESPONSE_LINK_OK
								|| jObject.getInt("code") == InAppPurchase.MYVPN_RESPONSE_LINK_ALREADY_LINKED) {
							// success linked
							SharedPreferences.Editor editor = sharedPref.edit();
							editor.putString("myvpn_user_name",
									jObject.getString("user_name"));
							editor.commit();
							Toast.makeText(getActivity(), "Connected!",
									Toast.LENGTH_LONG).show();
							linkAccountTextView.setVisibility(View.GONE);
							linkAccountUserNameEditText
									.setVisibility(View.GONE);
							linkAccountPasswordEditText
									.setVisibility(View.GONE);
							linkAccountConnectButton.setVisibility(View.GONE);
						} else if (jObject.getInt("code") == InAppPurchase.MYVPN_RESPONSE_LINK_ACCOUNT_NOT_VALIDATED) {
							Toast.makeText(
									getActivity(),
									"Please verify your MyVPN email address and try again",
									Toast.LENGTH_LONG).show();
						} else if (jObject.getInt("code") == InAppPurchase.MYVPN_RESPONSE_LINK_UNAUTHORIZED) {
							Toast.makeText(getActivity(),
									"Invalid Login username or password",
									Toast.LENGTH_LONG).show();
						} else if (jObject.getInt("code") == InAppPurchase.MYVPN_RESPONSE_LINK_ORDER_ID_ERROR) {
							Toast.makeText(getActivity(), "Invalid Order ID",
									Toast.LENGTH_LONG).show();
							//try to resend
							sendResponseDataToMyVPN(sharedPref.getString("purchaseData", ""),sharedPref.getString("dataSignature", ""));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void preRequest() {
					// TODO Auto-generated method stub
					progressDialog.setCancelable(true);
					progressDialog.setMessage("Please wait...");
					progressDialog.setTitle("Loading");
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.show();
				}

			}.execute();

		}

	}

	public class ManageSubscriptionButtonClicked implements OnClickListener {

		Context context;

		public ManageSubscriptionButtonClicked(Context context) {
			this.context = context;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id="
								+ rootView.getContext().getPackageName())));
			} catch (android.content.ActivityNotFoundException anfe) {
				startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("http://play.google.com/store/apps/details?id="
								+ context.getPackageName())));
			}

		}

	}

	public class SubscribeButtonClicked implements OnClickListener {

		Context context;

		public SubscribeButtonClicked(Context context) {
			this.context = context;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {

				byte[] data = sharedPref.getString("devPayloadId", "")
						.getBytes("UTF-8");
				String devPayLoad_base64 = Base64.encodeToString(data,
						Base64.DEFAULT);
				Log.d("devPayload", "payload: " + devPayLoad_base64);

				// myvpnsubsc20141122
				// android.test.purchased
				Bundle buyIntentBundle = mService.getBuyIntent(3, rootView.getContext().getPackageName(), "myvpnsubsc20141122","subs", devPayLoad_base64);

				if (buyIntentBundle.getInt("RESPONSE_CODE", 0) == BILLING_RESPONSE_RESULT_OK) {
					PendingIntent pendingIntent = buyIntentBundle
							.getParcelable("BUY_INTENT");
					getActivity().startIntentSenderForResult(
							pendingIntent.getIntentSender(), 1001,
							new Intent(), Integer.valueOf(0),
							Integer.valueOf(0), Integer.valueOf(0));
				}

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SendIntentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == 1001) {
			String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
			String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

			Log.d("onActivityResult", "purchaseData: " + purchaseData);
			Log.d("onActivityResult", "dataSignature: " + dataSignature);
			Log.d("onActivityResult", "resultCode: " + resultCode);

			if (purchaseData != null) {

				// save to preference
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("purchaseData", purchaseData);
				editor.putString("dataSignature", purchaseData);
				editor.commit();
				Log.d("onActivityResult",
						"New preference purchaseData and dataSignature set");


				Toast.makeText(rootView.getContext(),"Successfully subscribed!", Toast.LENGTH_SHORT).show();

				// inform myvpn service about this new purchase
				// call myvpn API async
				sendResponseDataToMyVPN(purchaseData,dataSignature);

		   }
		}
		else{
			Log.d("onActivityResult", "resultCode: " + resultCode);
		}
	}
	
	public void sendResponseDataToMyVPN(final String purchaseData, final String dataSignature){
		
		new HttpHandler() {
			@Override
			public HttpUriRequest getHttpRequestMethod() {
				HttpPost httppost = new HttpPost(
						"https://apps.madet.my/myvpn/googlebilling/");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair(
						"purchaseData", purchaseData));
				nameValuePairs.add(new BasicNameValuePair(
						"purchaseDataSignature", dataSignature));
				try {
					httppost.setEntity(new UrlEncodedFormEntity(
							nameValuePairs));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return httppost;
			}

			@Override
			public void onResponse(String result) {
				Log.d("HttpHandler", "result: " + result);
				try {
					JSONObject jObject = new JSONObject(result);

					if (jObject.getInt("code") == InAppPurchase.MYVPN_RESPONSE_BILLING_OK) {
						// success linked
						
						manageSubscriptionButton.setVisibility(View.VISIBLE);
						subscribeButton.setVisibility(View.GONE);
						
						SharedPreferences.Editor editor = sharedPref
								.edit();
						editor.putString("myvpn_user_name",
								jObject.getString("user_name"));
						editor.commit();

					} else if (jObject.getInt("code") == InAppPurchase.MYVPN_RESPONSE_BILLING_SIGNATURE_ERROR) {
						Toast.makeText(getActivity(),
								"Purchase Signature error!",
								Toast.LENGTH_LONG).show();
					} else if (jObject.getInt("code") == InAppPurchase.MYVPN_RESPONSE_BILLING_USER_NOT_FOUND) {
						Toast.makeText(getActivity(),"Please register MyVPN account and link the account",
								Toast.LENGTH_LONG).show();
						linkAccountTextView.setVisibility(View.VISIBLE);
						linkAccountUserNameEditText.setVisibility(View.VISIBLE);
						linkAccountPasswordEditText.setVisibility(View.VISIBLE);
						linkAccountConnectButton.setVisibility(View.VISIBLE);
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mService != null) {
			rootView.getContext().unbindService(mServiceConn);
		}
	}

	private String getAlphaNumeric(int len) {
		String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuffer sb = new StringBuffer(len);
		for (int i = 0; i < len; i++) {
			int ndx = (int) (Math.random() * ALPHA_NUM.length());
			sb.append(ALPHA_NUM.charAt(ndx));
		}
		return sb.toString();
	}

}
