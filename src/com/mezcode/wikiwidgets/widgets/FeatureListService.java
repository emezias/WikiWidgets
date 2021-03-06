package com.mezcode.wikiwidgets.widgets;

import com.mezcode.wikiwidgets.R;
import com.mezcode.wikiwidgets.R.xml;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class FeatureListService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        //return a service to dish out a list of tablet size widgets displaying featured Wikipedia articles
        return new AtomFeedViewFactory(this.getApplicationContext(), intent, R.xml.feature_list_info);
    }

}
