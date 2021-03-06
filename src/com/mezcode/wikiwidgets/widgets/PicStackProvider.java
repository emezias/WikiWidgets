package com.mezcode.wikiwidgets.widgets;

import com.mezcode.wikiwidgets.R;
import com.mezcode.wikiwidgets.R.xml;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

public class PicStackProvider extends BaseStackProvider {
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
        mLayoutId = R.xml.pic_stack_info;
        //Log.d(TAG, "pic enabled");
        //This method is run once per widget instance, when the widget is first created
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Broadcast receiver, code will execute when a title of the item in the collection is touched
        super.onReceive(context, intent);
        mLayoutId = R.xml.pic_stack_info;

    }
     
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter
        mLayoutId = R.xml.pic_stack_info;
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
