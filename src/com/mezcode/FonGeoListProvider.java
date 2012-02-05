package com.mezcode;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

public class FonGeoListProvider extends BaseListProvider {
	//private static final String TAG = "GeoListProvider";
	public static final String CLICK = "Click";
    public static final String URL_TAG = "Url";

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
        mListLayoutId = R.xml.fon_geo_list_info;
        //This method is run once per widget instance, when the widget is first created
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Broadcast receiver, code will execute when a title of the item in the collection is touched
        mListLayoutId = R.xml.fon_geo_list_info;
        super.onReceive(context, intent);
    	//Log.d(TAG, "FonGeoListProvider onReceive");

    } 

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter
    	//Log.d(TAG, "FonGeoListProvider update");
        mListLayoutId = R.xml.fon_geo_list_info;
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
