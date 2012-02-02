package com.mezcode;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class FeatureStackService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        //return a service to dish out a tablet size widgets displaying featured Wikipedia articles
        return new AtomFeedViewFactory(this.getApplicationContext(), intent, R.xml.feature_stack_info);
    }

}
