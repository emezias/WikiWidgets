package com.mezcode.wikiwidgets;

import java.io.File;

import android.app.Application;
import android.content.Context;

public class WikiWidgetsApp extends Application {

	public static String language = "en";


	@Override
	public void onCreate() {
		// This will insure that the http cache is enabled to make communication with the web more efficient
		super.onCreate();
		enableHttpResponseCache(this);
	}


	private void enableHttpResponseCache(Context ctx) {
	    try {
	        long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
	        File httpCacheDir = new File(ctx.getCacheDir(), "http");
	        Class.forName("android.net.http.HttpResponseCache")
	            .getMethod("install", File.class, long.class)
	            .invoke(null, httpCacheDir, httpCacheSize);
	    	} catch (Exception httpResponseCacheNotAvailable) { }
		}


}
