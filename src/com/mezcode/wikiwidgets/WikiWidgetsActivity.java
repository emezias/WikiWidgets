package com.mezcode.wikiwidgets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class WikiWidgetsActivity extends Activity implements OnMenuItemClickListener {
	/*
	 * This is the start activity for the app
	 * It is a choice when the user wants to open a wikipedia page from the browser
	 * It displays a list of featured and photo pages in an activity bar, same as the widget content
	 * It will open a map view to display geo-tagged articles that are nearby
	 */
	private static final String TAG = "WikiWidgetsActivity";
	private WebView mWebView;
	private ItemFields[] mPhotoUrls = null;
	private ItemFields[] mFeatureUrls = null;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.widget_activity);
	    //cache the WebView instead of doing a lookup for each call
	    mWebView = (WebView) findViewById(R.id.my_webview);
	    if(mWebView != null) mWebView.getSettings().setJavaScriptEnabled(true);
	    
	    /*
	     * I want two navigation mode entries on the action bar so I have to use a custom, action view
	     * 
	     * ActionBar bar = getActionBar();
	    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);	    
	    ArrayAdapter featureAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, 
	    		returnItemList(false)
	    );
	    
	    bar.setListNavigationCallbacks(featureAdapter,
	            new OnNavigationListener() {
	          		@Override
	          		public boolean onNavigationItemSelected(int position, long itemId) {
	          			//Log.e("item position",String.valueOf(position));
	          			if(mFeatureUrls == null) mFeatureUrls = returnUrlList(false);
	          			mWebView.loadUrl(mFeatureUrls[position]);
	        		    mWebView.invalidate();
	          			return true;
	          			//do whatever want to do...
	          		}
	        }
	    );*/
	}

	@Override
	protected void onResume() {
		// Get the URL from the widget and load it
		super.onResume();
	    //final Bundle b = getIntent().getExtras();
	    String location = getIntent().getStringExtra(BaseStackProvider.URL_TAG);
	    if(location != null) {
	    	Log.d(TAG, "first try " + location);
	    } else {
	    	//location = "http://www.wikipedia.org/";
	    	location = "http://m.wikipedia.org/";
	    }
	    
	    if(mWebView != null) {
	    	Log.d(TAG, "loading now " + location);
	    	mWebView.loadUrl(location);
		    mWebView.invalidate();
	    } else {
	    	Log.d(TAG, "webview is null");
	    }
	    //Log.d(TAG, returnDateForUrl(true));
    	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.main_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	String returnDateForUrl(boolean photo) {
		final Time t = new Time(); t.setToNow();
		final StringBuilder sb = new StringBuilder("http://en.m.wikipedia.org/wiki/");
		if(photo) {
			sb.append("Template:POTD/").append(t.format("%Y-%m-%d"));
		} else {
			sb.append("Wikipedia:Today%27s_featured_article/").append(t.format("%B_")).append(t.monthDay).append("%2C_").append(t.format("%Y"));
		}
		return sb.toString();
	}
	
	String[] returnItemList(boolean photo) {
		final Time t = new Time(); t.setToNow();
		final String[] listVals = new String[10];
		final StringBuilder sb = new StringBuilder(60);
		int dex;
		if(photo) {
			for(dex = 0; dex < 10; dex++) {
				sb.append(getString(R.string.photoMenu)).append(t.format(" %m-%d"));
				listVals[dex] = sb.toString();
				t.monthDay--; t.normalize(true);
				sb.setLength(0);
			}
			
		} else {
			for(dex = 0; dex < 10; dex++) {
				sb.append(getString(R.string.featureMenu)).append(t.format(" %B ")).append(t.monthDay);
				listVals[dex] = sb.toString();
				t.monthDay--; t.normalize(true);
				sb.setLength(0);
			}
			
		}
		return listVals;
	}
	
	String[] returnUrlList(boolean photo) {
		final Time t = new Time(); t.setToNow();
		final String[] listVals = new String[10];
		final StringBuilder sb = new StringBuilder(60);
		int dex;
		if(photo) {
			for(dex = 0; dex < 10; dex++) {
				sb.append("http://en.m.wikipedia.org/wiki/Template:POTD/").append(t.format("%Y-%m-%d"));
				listVals[dex] = sb.toString();
				t.monthDay--; t.normalize(true);
				sb.setLength(0);
			}
			
		} else {
			for(dex = 0; dex < 10; dex++) {
				sb.append("http://en.m.wikipedia.org/wiki/Wikipedia:Today%27s_featured_article/").append(t.format("%B_")).append(t.monthDay).append("%2C_").append(t.format("%Y"));
				listVals[dex] = sb.toString();
				t.monthDay--; t.normalize(true);
				sb.setLength(0);
			}
			
		}
		return listVals;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Three menu items for each data type
		switch(item.getItemId()) {
		case R.id.menu_feature:
			//http://en.m.wikipedia.org/wiki/Wikipedia:Today%27s_featured_article/February_5%2C_2012
			mWebView.loadUrl(returnDateForUrl(false));
		    mWebView.invalidate();
			break;
		case R.id.menu_location:
			startActivity(new Intent(this, LocationActivity.class));
			break;
		case R.id.menu_photo:
			showPopup(item.getActionView());
			//http://en.m.wikipedia.org/wiki/Template:POTD/2012-02-06
			//mWebView.loadUrl(returnDateForUrl(true));
		    //mWebView.invalidate();			
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	public void showPopup(View v) {
	    PopupMenu popup = new PopupMenu(this, v);
	    Menu m = popup.getMenu();
	    if(mPhotoUrls == null) mPhotoUrls = returnMenuItems(true);
	    for(int i = 0; i < 10; i++) {
	    	m.add((mPhotoUrls[i]).itemTitle);
	    }
	    /*MenuInflater inflater = popup.getMenuInflater();
	    inflater.inflate(R.menu.actions, popup.getMenu());*/
	    popup.setOnMenuItemClickListener(this);
	    popup.show();
	}
	
	public void showFeatureList(View v) {
		final PopupMenu fpopup = new PopupMenu(this, v);
	    Menu fm = fpopup.getMenu();
	    if(mFeatureUrls == null) mFeatureUrls = returnMenuItems(false);
	    for(int i = 0; i < 10; i++) {
	    	fm.add((mFeatureUrls[i]).itemTitle);
	    }
	    /*MenuInflater inflater = popup.getMenuInflater();
	    inflater.inflate(R.menu.actions, popup.getMenu());*/
	    fpopup.setOnMenuItemClickListener(this);
	    fpopup.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// This function will find the right url to load 
		final String tmp = (String) item.getTitle();
		//Log.d(TAG, "item id is " + tmp);
		if(tmp.contains(getString(R.string.photoItem))) {
			for(int dex = 0; dex < 10; dex++) {
				if(tmp.equals(mPhotoUrls[dex].itemTitle)) {
					mWebView.loadUrl(mPhotoUrls[dex].itemUrl);
	    		    mWebView.invalidate();
	    		    return true;
				}
			}//end for loop
		} else {
			for(int dex = 0; dex < 10; dex++) {
				if(tmp.equals(mFeatureUrls[dex].itemTitle)) {
					mWebView.loadUrl(mFeatureUrls[dex].itemUrl);
	    		    mWebView.invalidate();
	    		    return true;
				}
			}
		}
		
		return false;
	}
	
	private class ItemFields {
		String itemUrl;
		String itemTitle;
	}
	
	ItemFields[] returnMenuItems(boolean photo) {
		final Time t = new Time(); t.setToNow();
		final ItemFields[] listVals = new ItemFields[10];
		final StringBuilder sb = new StringBuilder(60);
		int dex;
		if(photo) {
			for(dex = 0; dex < 10; dex++) {
				listVals[dex] = new ItemFields();
				sb.append("http://en.m.wikipedia.org/wiki/Template:POTD/").append(t.format("%Y-%m-%d"));
				(listVals[dex]).itemUrl = sb.toString();
				sb.setLength(0);
				sb.append(getString(R.string.photoItem)).append(t.format(" %m-%d"));
				(listVals[dex]).itemTitle = sb.toString();
				sb.setLength(0);
				t.monthDay--; t.normalize(true);
			}
			
		} else {
			for(dex = 0; dex < 10; dex++) {
				listVals[dex] = new ItemFields();
				sb.append("http://en.m.wikipedia.org/wiki/Wikipedia:Today%27s_featured_article/").append(t.format("%B_")).append(t.monthDay).append("%2C_").append(t.format("%Y"));
				listVals[dex].itemUrl = sb.toString();
				sb.setLength(0);
				sb.append(getString(R.string.featureItem)).append(t.format(" %m-%d"));
				(listVals[dex]).itemTitle = sb.toString();
				sb.setLength(0);
				t.monthDay--; t.normalize(true);
			}
			
		}
		return listVals;
	}


}