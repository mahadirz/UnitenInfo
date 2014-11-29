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
 * Somehow I wonder why I created this class, lol 
 *
 * @author Mahadir Ahmad
 * @version 1.0
 * 
 */
package my.madet.function;


import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class FunctionLibrary {
	
	private SharedPreferences sharedPref;
	private Context  context;
	private MyPreferences myPreferences;
	
	public FunctionLibrary(Activity a){
		//init shared preference
		sharedPref = a.getPreferences(Context.MODE_PRIVATE);
		context = a.getApplicationContext();
		myPreferences = new MyPreferences(a);
	}
	
	/**
	 * Function get Login status
	 * */
	public boolean isUserLoggedIn(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount(DatabaseHandler.TABLE_BIODATA);
		if(count > 0){
			// user logged in
			return true;
		}
		return false;
	}
	
	/**
	 * Function to logout user
	 * Reset Database
	 * */
	public boolean logoutUser(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables(DatabaseHandler.TABLE_BIODATA);
		db.resetTables(DatabaseHandler.TABLE_LEDGER_BALANCE);
		db.resetTables(DatabaseHandler.TABLE_RESULT);
		db.resetTables(DatabaseHandler.TABLE_SCORUN);
		db.resetTables(DatabaseHandler.TABLE_TIMETABLE);
		db.resetTables(DatabaseHandler.TABLE_CLASS_NOTICES);
		//reset class notices
		//reset drawer position to 0
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("drawerPosition", 0);
		editor.putString("purchaseData", "");
	    editor.putString("dataSignature", "");
	    editor.putString("devPayloadId", "");
	    editor.putString("myvpn_user_name", "");
	    editor.putString("purchaseData", "");
		editor.commit();
		
		myPreferences.setIntegerPreference(MyPreferences.DRAWER_INDEX_POSITION, 0);
		
		return true;
	}
	
	public String getSubjectName(String subjectCode){
		subjectCode = subjectCode.toUpperCase();
		Scanner scan;
		String s;
	    String returnString = null;
	    Long startLong = System.currentTimeMillis();
	    
	    if(!myPreferences.getStringPreference(subjectCode).equals("")){
	    	//get hit,no need to search, 0 miliseconds
	    	return myPreferences.getStringPreference(subjectCode);
	    }
	    
		try {
			scan = new Scanner(context.getAssets().open("subjectList.csv"));
			

		   while (scan.hasNextLine()) 
		   {
		    s = scan.nextLine();
		    String[] csv = s.split(",");
		    if(csv[1].matches("\""+subjectCode.toUpperCase()+"\"")){
		    	returnString = csv[2].substring(1, (csv[2].length()-1));
		    	//cache it to sharedPref for speed
		    	myPreferences.setStringPreference(subjectCode, returnString);
		    }
		   }
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   Log.d("getSubjectName","Total searching time: "+ ((System.currentTimeMillis())-startLong)+" miliseconds"); 
	   return returnString;
	}
	

	
	public String LoadData(String inFile) {
		String tContents = "";

        try {
        	InputStream stream = context.getAssets().open(inFile);
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
        } catch (IOException e) {
        // Handle exceptions here
        }

        return tContents;

 }

}
