package com.mezcode;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class FonFeatureStackService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        //return new WikiRemoteViewsFactory(this.getApplicationContext(), intent);
        return new AtomFeedViewFactory(this.getApplicationContext(), intent, R.xml.fon_feature_widget_info);
    }

}
