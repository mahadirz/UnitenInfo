package my.madet.function;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferences {
	
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
	
	public final static String RESULT_PASSWORD_PROTECTED = "prefPasswordProtected"; //bool
	public final static String DRAWER_INDEX_POSITION = "drawerPosition"; //int
	public final static String UPDATE_ENABLED = "prefCheckUpdate"; //bool
	public final static String LAST_UPDATE_CHECKED = "lastupdatechecked"; //DEPRECATED
	public final static String NEW_UPDATE_AVAILABLE = "newupdateavailable"; //TODO

	public MyPreferences(Context c){
		sharedPref = c.getSharedPreferences("my.madet.uniteninfo_preferences",Context.MODE_PRIVATE);
	}
	
	public boolean getBooleanPreference(String key){
		return sharedPref.getBoolean(key, false);
	}
	
	public void setBooleanPreference(String key,boolean value){
		editor = sharedPref.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public String getStringPreference(String key){
		return sharedPref.getString(key, "");
	}
	
	public void setStringPreference(String key,String value){
		editor = sharedPref.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public int getIntegerPreference(String key){
		return sharedPref.getInt(key, 0);
	}
	
	public long getLongPreference(String key){
		return sharedPref.getLong(key, 0);
	}
	
	public void setIntegerPreference(String key,int value){
		editor = sharedPref.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public void setLongPreference(String key,Long value){
		editor = sharedPref.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
}
