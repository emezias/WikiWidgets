package com.mezcode.wikiwidgets.widgets;

import com.mezcode.wikiwidgets.R;
import com.mezcode.wikiwidgets.R.xml;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

public class FeatureListProvider extends BaseListProvider {
	//private static final String TAG = "FeatureListProvider";
	//This class sets an instance variable used by the FeedProvider to determine the widget type
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        mListLayoutId = R.xml.feature_list_info;
        //This method is run once per widget instance, when the widget is first created
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Broadcast receiver, code will execute when a title of the item in the collection is touched
        super.onReceive(context, intent);
        mListLayoutId = R.xml.feature_list_info;
        //Log.d(TAG, "feature onReceive");
    }
     
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter
        mListLayoutId = R.xml.feature_list_info;
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
