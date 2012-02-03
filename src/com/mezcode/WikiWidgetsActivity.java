package com.mezcode;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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
    	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.nearme_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}

}