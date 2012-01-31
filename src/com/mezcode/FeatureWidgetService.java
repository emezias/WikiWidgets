package com.mezcode;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class FeatureWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        //return new WikiRemoteViewsFactory(this.getApplicationContext(), intent);
    	Log.d("FeatureWidgetService", "FeatureWidgetService");
        return new AtomWidgetViewFactory(this.getApplicationContext(), intent, false);
    }

}
