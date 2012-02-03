package com.mezcode;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class GeoStackService extends RemoteViewsService {
	private static final String TAG = "GeoStackService";

	@Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
    	Log.d(TAG,"create GeoStackService and GeoViewFactory");
        return new GeoViewFactory(this.getApplicationContext(), intent, R.xml.geo_stack_info);
    }

}
