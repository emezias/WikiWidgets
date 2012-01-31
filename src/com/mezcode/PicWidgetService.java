package com.mezcode;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class PicWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        //return new WikiRemoteViewsFactory(this.getApplicationContext(), intent);
    	Log.d("PicWidgetService", "PicWidgetService");
        return new AtomWidgetViewFactory(this.getApplicationContext(), intent, true);
    }

}
