package com.mezcode.wikiwidgets;


import java.net.MalformedURLException;
import java.net.URL;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class FeatureList extends ListActivity implements ViewFactory {
	private static final String TAG = "FeatureList";
	private static int mYearDay = -1;
			
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.list_activity_layout);
	    ((TextSwitcher) findViewById(R.id.headerText)).setFactory(this);
	} //duplicated in PhotoList class
	
	@Override
	protected void onResume() {
		// check data in the NetworkHelper and call the task only if the cache is old
		super.onResume();
		if(getListAdapter() == null || mYearDay != NetworkHelper.fetchYearDay()) {
			new FetchFeatures().execute( );
		} 
		
		((TextSwitcher) findViewById(R.id.headerText)).setText(getString(R.string.featureListTitle));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//All of the list objects have a URL and call the same NetworkHelper code on a list click
		NetworkHelper.loadPageFromList(this, (String) ((MyAdapter)getListAdapter()).getUrl(position));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//TODO feature menu
	    getMenuInflater().inflate(R.menu.main_menu, menu);
	    MenuItem tmp = menu.findItem(R.id.menu_feature);
	    if(tmp != null) {
	    	menu.removeItem(tmp.getItemId());
	    }
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		WikiWidgetsActivity.handleActions(this, item.getItemId());
		return super.onContextItemSelected(item);
	}
		
/******Async Task class to fetch data from the network off the main thread***********/
	private class FetchFeatures extends AsyncTask<Void, Void, PicItem[]>{
		private ProgressDialog pd;
		protected void onPreExecute() {
			pd = LocationActivity.getProgress(FeatureList.this);
			pd.show();
		}
		
	protected PicItem[] doInBackground(Void... params) {
		PicItem[] photoList = null;
		try {
			photoList = NetworkHelper.fetchRssFeed(new URL(NetworkHelper.FEATURED_FEED));
			
		} catch (MalformedURLException e) {
			Log.e(TAG, "createList exception " + e.getMessage());
			e.printStackTrace();
		}
			
		return photoList;
		}
	
	protected void onPostExecute(PicItem[] glist) {
		//DEBUG empty list
		//glist = new WikiObject[0];
		if(glist != null) {
			FeatureList.this.setListAdapter(new MyAdapter(FeatureList.this, glist, true));
			mYearDay = NetworkHelper.fetchYearDay();
			glist = null;
		}
		pd.dismiss();
		}
	}
/****** Data is cached in the adapter object  ***********/


	//duplicate code in PhotoList, different string passed to the string builder
	@Override
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);
		/*
		 * TODO geo picker dialog
		 * if(id != 3) {
			return MainActivity.createGeoDialog(new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomeDialog)), 
						(ViewGroup) findViewById(R.id.layout_root), getApplicationContext());
		}*/
		return dateDialog(this, false);
	    
	}
	
	public static Dialog dateDialog(final Context ctx, final boolean photo) {
		//Code shared with the Photo list to initialize the date picker and listener
		final Time t = new Time(); t.setToNow();
	    return new DatePickerDialog(ctx,  R.style.CustomeDialog,
	    		new DatePickerDialog.OnDateSetListener() {
	    			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	    				final StringBuilder sb = new StringBuilder(50);
	    				t.year = year;
	    				t.month = monthOfYear;
	    				t.monthDay = dayOfMonth;
	    				if(photo) {
		    				sb.append("http://en.m.wikipedia.org/wiki/Template:POTD/")
		    					.append(t.format("%Y-%m-%d"));	    					
	    				} else {
							sb.append("http://en.m.wikipedia.org/wiki/Wikipedia:Today%27s_featured_article/")
								.append(t.format("%B_")).append(t.monthDay).append("%2C_").append(t.format("%Y"));	    					
	    				}
	    				NetworkHelper.loadPageFromList(ctx, sb.toString());
	    				sb.setLength(0);
	    				//updateDisplay();
	    			}
        }, t.year, t.month, t.monthDay);
	}
	
	public void genUrlAndLoad(View v) {
		showDialog(3); //could be any int...
	}
	
	public static TextView getSwitcherTV(Context appCtx) {
		final TextView t = new TextView(appCtx);
		t.setTextAppearance(appCtx, R.style.SwitcherFont);
		t.setGravity(Gravity.CENTER);
		return t;
	} //code is called in PhotoList
	
	@Override
	public View makeView() {
		return getSwitcherTV(getApplicationContext());
	} //code is duplicated in PhotoList
}
