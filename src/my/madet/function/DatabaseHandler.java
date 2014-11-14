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
 * Database Class handler 
 *
 * @author Mahadir Ahmad
 * @version 1.0
 * 
 */

package my.madet.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 2;

	// Database Name
	private static final String DATABASE_NAME = "studentinfo";

	// biodata
	public static final String TABLE_BIODATA = "biodata";
	// class notices
	public static final String TABLE_CLASS_NOTICES = "classnotice";
	// ledger balance
	public static final String TABLE_LEDGER_BALANCE = "ledgerbalance";
	// result
	public static final String TABLE_RESULT = "result";
	// scorun
	public static final String TABLE_SCORUN = "scorun";
	// timetable
	public static final String TABLE_TIMETABLE = "timetable";
	//result list subject
	public static final String TABLE_RESULT_LIST = "resultlist";

	// biodate table column names
	public static final String BIODATA_KEY_FULLNAME = "fullname";
	public static final String BIODATA_KEY_ID = "id";
	public static final String BIODATA_KEY_PASSWORD = "password";
	public static final String BIODATA_KEY_STATUS = "status";
	public static final String BIODATA_KEY_PROGRAM = "program";
	public static final String BIODATA_KEY_CAMPUS = "campus";
	public static final String BIODATA_KEY_ADVISOR = "advisor";
	public static final String BIODATA_KEY_PHONE = "phone";
	public static final String BIODATA_KEY_EMAIL = "email";

	// class notices table column names
	public static final String CLASS_NOTICES_ID = "id";
	public static final String CLASS_NOTICES_DATETIME = "datetime";
	public static final String CLASS_NOTICES_TITLE = "title";
	public static final String CLASS_NOTICES_MESSAGES = "messages";
	
	// result list
	public static final String RESULT_SUBJECT_ID = "id";
	public static final String RESULT_SUBJECT_SEMESTER_NAME = "semester";
	public static final String RESULT_SUBJECT_CODE = "subjectcode";
	public static final String RESULT_SUBJECT_DESCRIPTIONS = "descriptions";
	public static final String RESULT_SUBJECT_SECTION = "section";
	public static final String RESULT_SUBJECT_CREDITS = "credit";
	public static final String RESULT_SUBJECT_GRADE = "grade";
	public static final String RESULT_SUBJECT_POINTS = "points";

	// ledger balance table column names
	public static final String LEDGER_BALANCE_AMOUNT = "amount";
	public static final String LEDGER_BALANCE_STATUS = "status";

	// result table columns
	public static final String RESULT_SEMESTER_NAME = "semester";
	public static final String RESULT_GPA = "gpa";
	public static final String RESULT_CGPA = "cgpa";

	// scorun table columns
	public static final String SCORUN_ARTS_CULTURAL = "arts";
	public static final String SCORUN_COMMUNICATION_ENTERPRENEURSHIP = "communication";
	public static final String SCORUN_LEADERSHIP_INTELECTUAL = "leadership";
	public static final String SCORUN_SPRITIUAL_CIVILIZATION = "spritiual";
	public static final String SCORUN_SPORTS_RECREATIONAL = "sports";
	public static final String SCORUN_TOTAL = "total";

	// timetable table columns
	public static final String TIMETABLE_DAY = "day";
	public static final String TIMETABLE_SUBJECT = "subject";
	public static final String TIMETABLE_LOCATION = "location";
	public static final String TIMETABLE_STARTTIME = "start";
	public static final String TIMETABLE_ENDTIME = "end";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {

		String CREATE_BIODATA_TABLE = "CREATE TABLE " + TABLE_BIODATA + "("
				+ BIODATA_KEY_ID + " TEXT," + BIODATA_KEY_FULLNAME + " TEXT,"
				+ BIODATA_KEY_STATUS + " TEXT," + BIODATA_KEY_PROGRAM
				+ " TEXT," + BIODATA_KEY_CAMPUS + " TEXT,"
				+ BIODATA_KEY_ADVISOR + " TEXT," + BIODATA_KEY_PHONE + " TEXT,"
				+ BIODATA_KEY_EMAIL + " TEXT," + BIODATA_KEY_PASSWORD + " TEXT"
				+ ")";

		String CREATE_TABLE_LEDGER_BALANCE = "CREATE TABLE "
				+ TABLE_LEDGER_BALANCE + "(" + LEDGER_BALANCE_AMOUNT + " TEXT,"
				+ LEDGER_BALANCE_STATUS + " TEXT" + ")";

		String CREATE_TABLE_RESULT = "CREATE TABLE " + TABLE_RESULT + "("
				+ RESULT_SEMESTER_NAME + " TEXT," + RESULT_GPA + " TEXT,"
				+ RESULT_CGPA + " TEXT" + ")";

		String CREATE_SCORUN_TABLE = "CREATE TABLE " + TABLE_SCORUN + "("
				+ SCORUN_ARTS_CULTURAL + " TEXT,"
				+ SCORUN_COMMUNICATION_ENTERPRENEURSHIP + " TEXT,"
				+ SCORUN_LEADERSHIP_INTELECTUAL + " TEXT,"
				+ SCORUN_SPRITIUAL_CIVILIZATION + " TEXT,"
				+ SCORUN_SPORTS_RECREATIONAL + " TEXT," + SCORUN_TOTAL
				+ " TEXT" + ")";

		String CREATE_TIMETABLE_TABLE = "CREATE TABLE " + TABLE_TIMETABLE + "("
				+ TIMETABLE_DAY + " TEXT," + TIMETABLE_SUBJECT + " TEXT,"
				+ TIMETABLE_LOCATION + " TEXT," + TIMETABLE_STARTTIME
				+ " TEXT," + TIMETABLE_ENDTIME + " TEXT" + ")";

		db.execSQL(CREATE_BIODATA_TABLE);
		db.execSQL(CREATE_TABLE_LEDGER_BALANCE);
		db.execSQL(CREATE_TABLE_RESULT);
		db.execSQL(CREATE_SCORUN_TABLE);
		db.execSQL(CREATE_TIMETABLE_TABLE);
		
		//added on version 2.0
		String CREATE_CLASS_NOTICES = "CREATE TABLE " + TABLE_CLASS_NOTICES + "("
				+ CLASS_NOTICES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
    			+ CLASS_NOTICES_DATETIME + " TEXT,"
    			+ CLASS_NOTICES_TITLE  + " TEXT,"
                + CLASS_NOTICES_MESSAGES + " TEXT"
				+ ")";
    	String CREATE_RESULT_LIST = "CREATE TABLE " + TABLE_RESULT_LIST + "("
				+ RESULT_SUBJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ RESULT_SUBJECT_SEMESTER_NAME + " TEXT,"
    			+ RESULT_SUBJECT_CODE + " TEXT,"
    			+ RESULT_SUBJECT_DESCRIPTIONS  + " TEXT,"
                + RESULT_SUBJECT_SECTION + " TEXT,"
                + RESULT_SUBJECT_CREDITS + " TEXT,"
                + RESULT_SUBJECT_GRADE + " TEXT,"
                + RESULT_SUBJECT_POINTS + " TEXT"
				+ ")";
    	db.execSQL(CREATE_CLASS_NOTICES);
    	db.execSQL(CREATE_RESULT_LIST);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		Log.i("newVersion","newVersion "+newVersion);
		Log.i("oldVersion","oldVersion "+oldVersion);
		
		//upgrade by incrementing
		for (int i = oldVersion; i < newVersion; i++)
        {
            switch(i)
            {
                case 1:
                	//upgrade to version 2
                	String CREATE_CLASS_NOTICES = "CREATE TABLE " + TABLE_CLASS_NOTICES + "("
            				+ CLASS_NOTICES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
                			+ CLASS_NOTICES_DATETIME + " TEXT,"
                			+ CLASS_NOTICES_TITLE  + " TEXT,"
                            + CLASS_NOTICES_MESSAGES + " TEXT"
            				+ ")";
                	String CREATE_RESULT_LIST = "CREATE TABLE " + TABLE_RESULT_LIST + "("
            				+ RESULT_SUBJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
            				+ RESULT_SUBJECT_SEMESTER_NAME + " TEXT,"
                			+ RESULT_SUBJECT_CODE + " TEXT,"
                			+ RESULT_SUBJECT_DESCRIPTIONS  + " TEXT,"
                            + RESULT_SUBJECT_SECTION + " TEXT,"
                            + RESULT_SUBJECT_CREDITS + " TEXT,"
                            + RESULT_SUBJECT_GRADE + " TEXT,"
                            + RESULT_SUBJECT_POINTS + " TEXT"
            				+ ")";
                	//db.execSQL("DELETE FROM TABLE " + TABLE_RESULT);
                	db.execSQL(CREATE_CLASS_NOTICES);
                	db.execSQL(CREATE_RESULT_LIST);
                    break;
                    
                case 2:
                	//upgrade to v2.1
                	//class notices not refresh to new after new semester
                	Log.i("DB Upgrade","Upgraded DB for v2.2 ");
                	//add new version table
                	//semester
                	break;
            }
        }
	}
	
	/**
	 * 
	 * @param classNotices
	 */
	public void addClassNotices(ArrayList<HashMap<String, String>> classNotices){
		Log.i("addClassNotices","Entering ");
		if(classNotices != null){
			SQLiteDatabase db = this.getWritableDatabase();
			for(int i=0; i<classNotices.size(); i++){
				ContentValues values = new ContentValues();
				values.put(CLASS_NOTICES_DATETIME, classNotices.get(i).get("title1"));
				values.put(CLASS_NOTICES_TITLE, classNotices.get(i).get("title2")+" ("+classNotices.get(i).get("title3")+")");
				values.put(CLASS_NOTICES_MESSAGES, classNotices.get(i).get("body"));
				
				if (db.insert(TABLE_CLASS_NOTICES, null, values) > 0)
					Log.i("addClassNotices", "added class notice");
			}
			db.close();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<HashMap<String, String>> getClassNotices(){
		ArrayList<HashMap<String, String>> output = new ArrayList<HashMap<String,String>>();
		String selectQuery = "SELECT * FROM " + TABLE_CLASS_NOTICES + " ORDER BY "+CLASS_NOTICES_ID+" DESC" ;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst(); // move cursor to first row
		while (!cursor.isAfterLast()) {
		    if(cursor.getCount() > 0) {
		    	HashMap<String, String> result = new HashMap<String,String>();
				result.put(CLASS_NOTICES_DATETIME, cursor.getString(1));
				result.put(CLASS_NOTICES_TITLE, cursor.getString(2));
				result.put(CLASS_NOTICES_MESSAGES, cursor.getString(3));				
				output.add(result);
		    }
		    cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return output;
	}
	
	/**
	 * 
	 * @param subjectList
	 */
	public void addSubjectList(ArrayList<HashMap<String, String>> subjectList){
		Log.i("addSubjectList","Entering ");
		if(subjectList != null){
			SQLiteDatabase db = this.getWritableDatabase();
			for(int i=0; i<subjectList.size(); i++){
				ContentValues values = new ContentValues();
				values.put(RESULT_SUBJECT_SEMESTER_NAME, subjectList.get(i).get(RESULT_SUBJECT_SEMESTER_NAME));
				values.put(RESULT_SUBJECT_CODE, subjectList.get(i).get(RESULT_SUBJECT_CODE));
				values.put(RESULT_SUBJECT_DESCRIPTIONS, subjectList.get(i).get(RESULT_SUBJECT_DESCRIPTIONS));
				values.put(RESULT_SUBJECT_SECTION, subjectList.get(i).get(RESULT_SUBJECT_SECTION));
				values.put(RESULT_SUBJECT_CREDITS, subjectList.get(i).get(RESULT_SUBJECT_CREDITS));
				values.put(RESULT_SUBJECT_GRADE, subjectList.get(i).get(RESULT_SUBJECT_GRADE));
				values.put(RESULT_SUBJECT_POINTS, subjectList.get(i).get(RESULT_SUBJECT_POINTS));
				
				if (db.insert(TABLE_RESULT_LIST, null, values) > 0)
					Log.i("addSubjectList", "added subject list");
				
			}
			db.close();
		}
	}
	
	/**
	 * 
	 * @param semester
	 * @return
	 */
	public ArrayList<HashMap<String, String>> getSubjectList(String semester){
		ArrayList<HashMap<String, String>> output = new ArrayList<HashMap<String, String>>();
		String selectQuery = "SELECT * FROM " + TABLE_RESULT_LIST + " WHERE "+RESULT_SEMESTER_NAME+"='"+semester+"'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst(); // move cursor to first row
		while (!cursor.isAfterLast()) {
		    if(cursor.getCount() > 0) {
		    	HashMap<String, String> result = new HashMap<String,String>();
				result.put(RESULT_SUBJECT_SEMESTER_NAME, cursor.getString(1));
				result.put(RESULT_SUBJECT_CODE, cursor.getString(2));
				result.put(RESULT_SUBJECT_DESCRIPTIONS, cursor.getString(3));
				result.put(RESULT_SUBJECT_SECTION, cursor.getString(4));
				result.put(RESULT_SUBJECT_CREDITS, cursor.getString(5));
				result.put(RESULT_SUBJECT_GRADE, cursor.getString(6));
				result.put(RESULT_SUBJECT_POINTS, cursor.getString(7));
				
				output.add(result);
		    }
		    cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return output;
	}

	/**
	 * array 0: id array 1: name array n:
	 * status,program,campus,advisor,phone,email array 8: password
	 * 
	 * @param bio
	 */
	public void addBiodata(String[] bio) {
		Log.i("addBiodata", "entering method.. ");
		if (bio != null) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(BIODATA_KEY_ID, bio[0]);
			values.put(BIODATA_KEY_FULLNAME, bio[1]);
			values.put(BIODATA_KEY_STATUS, bio[2]);
			values.put(BIODATA_KEY_PROGRAM, bio[3]);
			values.put(BIODATA_KEY_CAMPUS, bio[4]);
			values.put(BIODATA_KEY_ADVISOR, bio[5]);
			values.put(BIODATA_KEY_PHONE, bio[6]);
			values.put(BIODATA_KEY_EMAIL, bio[7]);
			values.put(BIODATA_KEY_PASSWORD, bio[8]);

			if (db.insert(TABLE_BIODATA, null, values) > 0)
				Log.i("addBiodata", "added bio to table");
			db.close();

		}

	}

	/**
	 * 
	 * @param timetablestruct
	 */
	public void AddTimeTable(List<TimeTableStruct> timetablestruct) {
		/*
		 * TIMETABLE_DAY TIMETABLE_SUBJECT TIMETABLE_LOCATION
		 * TIMETABLE_STARTTIME TIMETABLE_ENDTIME
		 */

		Log.i("AddTimeTable", "entering method.. ");
		if (timetablestruct != null) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();

			for (TimeTableStruct a : timetablestruct) {
				// iterate the timetable in class structure
				values.put(TIMETABLE_DAY, a.getDay());
				values.put(TIMETABLE_SUBJECT, a.getSubject());
				values.put(TIMETABLE_LOCATION, a.getLocation());
				values.put(TIMETABLE_STARTTIME, a.getStartTime());
				values.put(TIMETABLE_ENDTIME, a.getEndTime());

				if (db.insert(TABLE_TIMETABLE, null, values) > 0)
					Log.i("AddTimeTable", "added timetable " + a.getDay());
			}

			db.close();
		}
	}

	public void addExamResult(HashMap<String, String[]> result) {
		Log.i("addExamResult", "entering method.. ");
		if (result != null) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();

			for (String aKey : result.keySet()) {
				String aValue[] = result.get(aKey);
				values.put(RESULT_SEMESTER_NAME, aKey);
				values.put(RESULT_GPA, aValue[0]);
				values.put(RESULT_CGPA, aValue[1]);

				if (db.insert(TABLE_RESULT, null, values) > 0)
					Log.i("addExamResult", "added semester " + aKey);
			}

			db.close();
		}
	}

	/**
	 * 0 - Arts & Cultural 
	 * 1- Communication & Enterpreneurship 
	 * 2- Leadership &  Intelectual 
	 * 3- Spiritual & Civilization 
	 * 4- Sports & Recreational 
	 * 5 - total
	 * 
	 * @param score
	 */
	public void addScorun(String[] score) {
		Log.i("addScorun", "entering method.. ");
		if (score != null) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put(SCORUN_ARTS_CULTURAL, score[0]);
			values.put(SCORUN_COMMUNICATION_ENTERPRENEURSHIP, score[1]);
			values.put(SCORUN_LEADERSHIP_INTELECTUAL, score[2]);
			values.put(SCORUN_SPRITIUAL_CIVILIZATION, score[3]);
			values.put(SCORUN_SPORTS_RECREATIONAL, score[4]);
			values.put(SCORUN_TOTAL, score[5]);

			if (db.insert(TABLE_SCORUN, null, values) > 0)
				Log.i("addScorun", "added scorun " + score[0]);

			db.close();
		}
	}
	
	public void addLedgerBalance(String total, boolean blocked ){
		Log.i("addLedgerBalance", "entering method.. ");
		if (total != null) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(LEDGER_BALANCE_AMOUNT, total);
			if(blocked)
				values.put(LEDGER_BALANCE_STATUS, "You are blocked");
			else
				values.put(LEDGER_BALANCE_STATUS, "You are not blocked");
			
			if (db.insert(TABLE_LEDGER_BALANCE(), null, values) > 0)
				Log.i("addLedgerBalance", "added Balance " + total);

			db.close();
		}
	}
	
	public String[] getLedgerBalance(){
		String[] ledger = new String[2];
		String selectQuery = "SELECT * FROM " + TABLE_LEDGER_BALANCE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst(); // move cursor to first row
		if (cursor.getCount() > 0) {
			for(int i=0; i<2; i++)
				ledger[i] = cursor.getString(i);
		}
		cursor.close();
		db.close();
		return ledger;
	}
	
	/**
	 * 
	 * @return array
	 */
	public String[] getScorun(){
		String[] scorun = new String[6];
		String selectQuery = "SELECT * FROM " + TABLE_SCORUN;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst(); // move cursor to first row
		if (cursor.getCount() > 0) {
			for(int i=0; i<=5; i++)
			  scorun[i] = cursor.getString(i);
		}
		cursor.close();
		db.close();
		return scorun;
	}

	public HashMap<String, String[]> getExamResult() {
		HashMap<String, String[]> result = new HashMap<String, String[]>();

		String selectQuery = "SELECT * FROM " + TABLE_RESULT;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst(); // move cursor to first row
		while (!cursor.isAfterLast()) {
			if (cursor.getCount() > 0) {
				String[] arrStrings = new String[2];
				arrStrings[0] = cursor.getString(1); // GPA
				arrStrings[1] = cursor.getString(2); // CGPA
				// add to hashmap
				result.put(cursor.getString(0), arrStrings); // Semester name ->
																// GPA,CGPA
			}
			// move to next row
			cursor.moveToNext();
		}

		cursor.close();
		db.close();
		return result;
	}

	/**
	 * 
	 * @return List<TimeTableStruct>
	 * 
	 */
	public List<TimeTableStruct> getTimeTable() {
		List<TimeTableStruct> timetablestruct = new ArrayList<TimeTableStruct>();
		// query table
		String selectQuery = "SELECT * FROM " + TABLE_TIMETABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst(); // move cursor to first row
		while (!cursor.isAfterLast()) {
			if (cursor.getCount() > 0) {
				// create timetablestruct
				TimeTableStruct tt = new TimeTableStruct();
				tt.insertDay(cursor.getString(0));
				tt.insertsubject(cursor.getString(1));
				tt.insertLocation(cursor.getString(2));
				tt.insertStartTime(cursor.getString(3));
				tt.insertEndTime(cursor.getString(4));

				// save timetablestruct to list
				timetablestruct.add(tt);
			}
			// move to next row
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return timetablestruct;
	}

	/**
	 * Get the biodata of the student
	 * 
	 * @return bio
	 */
	public HashMap<String, String> getBiodata() {
		HashMap<String, String> bio = new HashMap<String, String>();
		String selectQuery = "SELECT * FROM " + TABLE_BIODATA;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			bio.put(BIODATA_KEY_ID, cursor.getString(0));
			bio.put(BIODATA_KEY_FULLNAME, cursor.getString(1));
			bio.put(BIODATA_KEY_STATUS, cursor.getString(2));
			bio.put(BIODATA_KEY_PROGRAM, cursor.getString(3));
			bio.put(BIODATA_KEY_CAMPUS, cursor.getString(4));
			bio.put(BIODATA_KEY_ADVISOR, cursor.getString(5));
			bio.put(BIODATA_KEY_PHONE, cursor.getString(6));
			bio.put(BIODATA_KEY_EMAIL, cursor.getString(7));
			bio.put(BIODATA_KEY_PASSWORD, cursor.getString(8));
		}
		cursor.close();
		db.close();
		return bio;
	}

	/**
	 * Getting table row total return true if rows are there in table
	 * */
	public int getRowCount(String tableName) {
		String countQuery = "SELECT  * FROM " + tableName;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();

		// return row count
		return rowCount;
	}
	
	public void resetAutoIncrement(String tableName){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + tableName + "'");
	}

	/**
	 * Re create database Delete all tables and create them again
	 * */
	public void resetTables(String tableName) {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(tableName, null, null);
		db.close();
	}

	public String TABLE_BIODATA() {
		return TABLE_BIODATA;
	}

	public String TABLE_CLASS_NOTICES() {
		return TABLE_CLASS_NOTICES;
	}

	public String TABLE_LEDGER_BALANCE() {
		return TABLE_LEDGER_BALANCE;
	}

	public String TABLE_RESULT() {
		return TABLE_RESULT;
	}

	public String TABLE_SCORUN() {
		return TABLE_SCORUN;
	}

	public String TABLE_TIMETABLE() {
		return TABLE_TIMETABLE;
	}

}
