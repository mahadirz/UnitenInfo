package my.madet.uniteninfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PreferenceActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingPreference()).commit();
	}
	
	
}
