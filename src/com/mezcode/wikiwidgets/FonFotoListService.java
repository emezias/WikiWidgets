package com.mezcode.wikiwidgets;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class FonFotoListService extends RemoteViewsService {
	private static final String TAG = "FonFotoListService";
	
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "foto factory and service");
        return new AtomFeedViewFactory(this.getApplicationContext(), intent, R.xml.fon_foto_list_info);
    }

}
