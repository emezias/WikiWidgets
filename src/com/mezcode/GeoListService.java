package com.mezcode;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class GeoListService extends RemoteViewsService {
	private static final String TAG = "GeoListService";
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
    	Log.d(TAG,"create GeoListFactory");
        return new GeoListViewFactory(this.getApplicationContext(), intent, R.xml.geo_list_info);
    }

}
