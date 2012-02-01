package com.mezcode;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

public class PicStackProvider extends BaseAtomFeedProvider {
	//private static final String TAG = "PicWidgetProvider";

    
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
        mLayoutId = R.xml.wikipic_widget_info;
        //Log.d(TAG, "pic enabled");
        //This method is run once per widget instance, when the widget is first created
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Broadcast receiver, code will execute when a title of the item in the collection is touched
        super.onReceive(context, intent);
        mLayoutId = R.xml.wikipic_widget_info;

    }
     
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter
        mLayoutId = R.xml.wikipic_widget_info;
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
