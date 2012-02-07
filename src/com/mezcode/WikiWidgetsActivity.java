package com.mezcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class WikiWidgetsActivity extends Activity {
	/*
	 * This activity supports an explicit intent from the home screen widget collections
	 * TODO - transition the widget to an implicit browser intent 
	 * or to use the WikipediaActivity class in an explicit intent
	 */
	private static final String TAG = "WikiWidgetActivity";
	private WebView mWebView;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.widget_activity);
	    //cache the WebView instead of doing a lookup for each call
	    mWebView = (WebView) findViewById(R.id.my_webview);
	    if(mWebView != null) mWebView.getSettings().setJavaScriptEnabled(true);
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
	    getMenuInflater().inflate(R.menu.nearme_menu, menu);
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
			//http://en.m.wikipedia.org/wiki/Template:POTD/2012-02-06
			mWebView.loadUrl(returnDateForUrl(true));
		    mWebView.invalidate();			
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	

}