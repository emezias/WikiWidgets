package com.mezcode.wikiwidgets.widgets;

import com.mezcode.wikiwidgets.R;
import com.mezcode.wikiwidgets.R.array;
import com.mezcode.wikiwidgets.R.drawable;
import com.mezcode.wikiwidgets.R.string;
import com.mezcode.wikiwidgets.R.xml;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class WidgetConfigActivity extends PreferenceActivity implements OnPreferenceChangeListener {

	private String[] tmpPrefArray;
	
	@Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);      
    	addPreferencesFromResource(R.xml.prefs);
    	getListView().setCacheColorHint(00000000);
	}

	@Override
	public boolean onPreferenceChange(Preference p, Object newValue) {
		// This is where the changes to values takes place
		// these keys may set int values for 
		final SharedPreferences.Editor ed = p.getEditor();
    	final String key = p.getTitle().toString();
		if(key.equals(getString(R.string.colorPrefTitle))) {
			tmpPrefArray = getResources().getStringArray(R.array.colors);
			return true;
		} else if(key.equals(R.string.stackPrefTitle)) {
			tmpPrefArray = getResources().getStringArray(R.array.widgetType);
			return true;
		} else if(key.equals(R.string.fontSizeTitle)) {
			return true;
			
		} else if(key.equals(R.string.contentPrefTitle)) {
			return true;
			
		}
		return false;
	}
	
	//set the background resource for the widgets (and activities)
	private static int[] bg1 = { R.drawable.widget_item_background, R.drawable.purple_bg, R.drawable.blue_bg, 
		R.drawable.green_bg, R.drawable.gold_bg, R.drawable.red_bg };
	private static int[] bg2 = { R.drawable.widget_item_background2, R.drawable.purple_bg2, R.drawable.blue_bg2, 
		R.drawable.green_bg, R.drawable.gold_bg2, R.drawable.red_bg2 };
}
