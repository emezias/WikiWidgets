package com.mezcode.wikiwidgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class FonGeoStackService extends RemoteViewsService {
	//private static final String TAG = "FonGeoStackService";

	@Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
    	//Log.d(TAG,"create FonGeoStackService and GeoViewFactory");
        return new GeoViewFactory(this.getApplicationContext(), intent, R.xml.fon_geo_stack_info);
    }

}
