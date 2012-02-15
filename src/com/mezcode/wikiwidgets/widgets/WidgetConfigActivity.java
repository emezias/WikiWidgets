package com.mezcode.wikiwidgets.widgets;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Button;

import com.mezcode.wikiwidgets.R;

public class WidgetConfigActivity extends PreferenceActivity implements OnPreferenceChangeListener {

	private String[] tmpPrefArray;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        PreferenceManager.setDefaultValues(this,
                R.xml.prefs, false);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs);
        //TODO inflate a view with set and cancel buttons that will instantiate the widget or finish the activity without creating the widget
        Button button = new Button(this);
        button.setText("Finish?");
        setListFooter(button);
        // Add a button to the header list.
        /*if (hasHeaders()) {
            //Button button = new Button(this);
            button.setText("Finish?");
            setListFooter(button);
        }*/
    }
	/**
     * Populate the activity with the top-level headers.
     */
    /*@Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
        
    }*/
    
	/*@Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);      
    	addPreferencesFromResource(R.xml.prefs);
    	getListView().setCacheColorHint(00000000);
	}*/

	@Override
	public boolean onPreferenceChange(Preference p, Object newValue) {
		// This is where the changes to values takes place
		// these keys may set int values for 
		final SharedPreferences.Editor ed = p.getEditor();
    	final String key = p.getTitle().toString();
		if(key.equals(getString(R.string.colorPrefTitle))) {
			tmpPrefArray = getResources().getStringArray(R.array.colors);
			for(int i=0; i<tmpPrefArray.length; i++) {
				if(tmpPrefArray[i].equals(newValue)) {
					ed.putInt("bg1", bg1[i]);
					ed.putInt("bg2", bg2[i]);
					ed.commit();
					return true;
				}
			} //for loop returned without a match to the newValue 
			return false;
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
	
	
	/**
     * This fragment shows the preferences for the first header.
     */
    public static class MyPrefFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Make sure default values are applied.  In a real app, you would
            // want this in a shared function that is used to retrieve the
            // SharedPreferences wherever they are needed.
            PreferenceManager.setDefaultValues(getActivity(),
                    R.xml.prefs, false);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.prefs);
        }
    }
}
