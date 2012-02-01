package com.mezcode;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class GeoListProvider extends AppWidgetProvider {
	private static final String TAG = "GeoWidgetProvider";
	public static final String CLICK = "Click";
    public static final String URL_TAG = "Url";
    public static final String REFRESH = "Refresh";

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
    	//Log.d(TAG, "wiki provider receive " + action);
        if (action.equals(CLICK)) {
            String location = intent.getStringExtra(URL_TAG);
            //Display a toast for the user to read while the page loads
            Toast.makeText(context, "Loading Wikipedia", Toast.LENGTH_SHORT).show();
            //Debugging toast Toast.makeText(context, "Loading Wikipedia" + location, Toast.LENGTH_SHORT).show();
            final Intent tnt = new Intent("android.intent.action.VIEW", Uri.parse(location));
            //tnt.putExtra(URL_TAG, location);
        	//final Intent tnt = new Intent(context.getApplicationContext(), WikipediaActivity.class);
        	//tnt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            tnt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            tnt.putExtra(URL_TAG, location);
            context.startActivity(tnt);
        }
        super.onReceive(context, intent);
    	//Log.d(TAG, "geo widget provider onReceive");

    } 

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter
    	//Log.d(TAG, "geo widget provider update");
        for (int i = 0; i < appWidgetIds.length; ++i) {
            // Here we setup the intent which points to the WikiWidgetService which will
            // provide the views for this collection.
            Intent intent = new Intent(context, WikiWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_list);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.list_view, intent);
            rv.setEmptyView(R.id.list_view, R.id.empty_view);

            final Intent toastIntent = new Intent(context, GeoListProvider.class);
            // Set the action to run when the user touches a particular view 
            toastIntent.setAction(CLICK);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            toastIntent.setData(Uri.parse(toastIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.list_view, toastPendingIntent);
            
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
            // Here we setup the a pending intent template. Individual items of a collection
            // cannot setup their own pending intents, instead, the collection as a whole can
            // setup a pending intent template, and the individual items can set a fillInIntent
            // to create unique before on an item to item basis.

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
