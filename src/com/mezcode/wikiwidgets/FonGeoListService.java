package com.mezcode.wikiwidgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class FonGeoListService extends RemoteViewsService {
	//private static final String TAG = "FonGeoListService";

	@Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
    	//Log.d(TAG,"create FonGeoListService");
        return new GeoViewFactory(this.getApplicationContext(), intent, R.xml.fon_geo_list_info);
    }

}
