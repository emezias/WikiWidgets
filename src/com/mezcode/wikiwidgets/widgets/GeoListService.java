package com.mezcode.wikiwidgets.widgets;

import com.mezcode.wikiwidgets.R;
import com.mezcode.wikiwidgets.R.xml;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class GeoListService extends RemoteViewsService {
	//private static final String TAG = "GeoListService";

	@Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
    	//Log.d(TAG,"create GeoListFactory");
        return new GeoViewFactory(this.getApplicationContext(), intent, R.xml.geo_list_info);
    }

}
