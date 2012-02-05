package com.mezcode;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class PicListService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        //List of photo of the day articles served up here
        return new AtomFeedViewFactory(this.getApplicationContext(), intent, R.xml.pic_list_info);
    }

}
