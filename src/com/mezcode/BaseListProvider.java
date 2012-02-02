package com.mezcode;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BaseListProvider extends AppWidgetProvider {
	private static final String TAG = "BaseListProvider";
	public static final String CLICK = "Click";
    public static final String URL_TAG = "Url";
    public static final String REFRESH = "Refresh";

    int mListLayoutId = R.xml.geo_list_info;

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
        //This method is run once per widget instance, when the widget is first created
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Broadcast receiver, code will execute when a title of the item in the collection is touched
    	final String action = intent.getAction();
    	//Log.d(TAG, "list provider receive " + action);
        if (action.equals(CLICK)) {
        	Log.d(TAG, intent.getStringExtra(URL_TAG));
            BaseStackProvider.clickWidgetShowPage(context, intent.getStringExtra(URL_TAG));
            //Display a toast and display the page
        }
        super.onReceive(context, intent);
    	//Log.d(TAG, "geo widget provider onReceive");

    } 

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter
    	Log.d(TAG, "geo widget provider update");
        for (int i = 0; i < appWidgetIds.length; ++i) {
            // Here we setup the intent which points to the WikiWidgetService which will
            // provide the views for this collection.
        	BaseStackProvider.provideRemoteViews(context, appWidgetIds[i], mListLayoutId, appWidgetManager, false);

            /*TODO, work out the getActivity type of pending intent as a template
             * Intent toastIntent = new Intent(context, WikiWidgetActivity.class);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            toastIntent.setAction(Intent.ACTION_VIEW);
            toastIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getActivity(context, 0, toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.list_view, toastPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);*/
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
