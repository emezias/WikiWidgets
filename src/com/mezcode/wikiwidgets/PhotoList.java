package com.mezcode.wikiwidgets;


import java.net.MalformedURLException;
import java.net.URL;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher.ViewFactory;

public class PhotoList extends ListActivity implements ViewFactory {
	// TODO save data to display something if the feed is unavailable
	private static final String TAG = "PhotoList";
	private static int mYearDay = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.list_activity_layout);
	    ((TextSwitcher) findViewById(R.id.headerText)).setFactory(this);
	}
	
	@Override
	protected void onResume() {
		// This method will load the list with the latest data 
		// check the date and skip the async task, if appropriate
		super.onResume();
		if(getListAdapter() == null || mYearDay != NetworkHelper.fetchYearDay()) { 
			new FetchPhotos().execute( );
		} 

		((TextSwitcher) findViewById(R.id.headerText)).setText(getString(R.string.photoListTitle));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//code is shared among all 3 list activities
		//NetworkHelper will open the page for the item in the list in the main activity class
		NetworkHelper.loadPageFromList(this, (String) ((MyAdapter)getListAdapter()).getUrl(position));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//TODO photo menus
	    getMenuInflater().inflate(R.menu.main_menu, menu);
	    MenuItem tmp = menu.findItem(R.id.menu_photo);
	    if(tmp != null) {
	    	menu.removeItem(tmp.getItemId());
	    }
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//all activities share the same menu logic
		WikiWidgetsActivity.handleActions(this, item.getItemId());
		return super.onContextItemSelected(item);

	}
		
	/******Async Task class to fetch data from the network off the main thread***********/
	private class FetchPhotos extends AsyncTask<Void, Void, PicItem[]>{
		//the progress dialog skips the activity callbacks
		private ProgressDialog pd;
		protected void onPreExecute() {
			pd = LocationActivity.getProgress(PhotoList.this);
			pd.show();
		}
		
	protected PicItem[] doInBackground(Void... params) {
		//the array is cached in the NetworkHelper class so there will be only one network call per "session"
		PicItem[] photoList = null;
		try {
			photoList = NetworkHelper.fetchRssFeed(new URL(NetworkHelper.POTD_STREAM));
		} catch (MalformedURLException e) {
			Log.e(TAG, "createList URL exception " + e.getMessage());
			e.printStackTrace();
		}
		//Network Helper has parsed the RSS feed and created an array of objects to display
		return photoList;
		}
	
		protected void onPostExecute(PicItem[] glist) {
			if(glist != null) {
				//Log.d(TAG, "set adapter here");
				PhotoList.this.setListAdapter(new MyAdapter(PhotoList.this, glist, false));
				mYearDay = NetworkHelper.fetchYearDay();
				glist = null;
			}
			pd.dismiss();
		}
	}
	/**************************/
	//style the switcher font with a dark serif text
	@Override
	public View makeView() {
		return FeatureList.getSwitcherTV(getApplicationContext());
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);
		/*if(id != 3) {
			return MainActivity.createGeoDialog(new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomeDialog)), 
					(ViewGroup) findViewById(R.id.layout_root), getApplicationContext());
		}*/
		return FeatureList.dateDialog(this, true);
	}
	
	public void genUrlAndLoad(View v) {
		showDialog(3); //could be any int...
	}


}
