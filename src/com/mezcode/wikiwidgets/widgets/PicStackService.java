package com.mezcode.wikiwidgets.widgets;

import com.mezcode.wikiwidgets.R;
import com.mezcode.wikiwidgets.R.xml;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class PicStackService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        //return new WikiRemoteViewsFactory(this.getApplicationContext(), intent);
        return new AtomFeedViewFactory(this.getApplicationContext(), intent, R.xml.pic_stack_info);
    }

}
