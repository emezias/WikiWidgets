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

public class FeedProvider extends AppWidgetProvider {
	/*
	 * This class is the base for the RSS Atom Feed widgets that are displayed in a stackview
	 * The collection content is either the photo of the day or the featured article of the day, pulled in from an rss feed for the last 20 days
	 * The provider and service classes are selected based on the feed selection
	 * The feed is identified by the appwidget info class that is hard-coded into the subclass' mLayoutId 
	 */
	private static final String TAG = "FeedProvider";
	public static final String CLICK = "Click";
    public static final String URL_TAG = "Url";
    //TODO public static final String REFRESH = "Refresh";
    
    int mLayoutId = R.xml.wiki_feature_widget_info;

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
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Broadcast receiver, code will execute when a title of the item in the collection is touched
    	final String action = intent.getAction();
    	//Log.d(TAG, "wiki provider receive " + action);
        if (action.equals(CLICK)) {
            String location = intent.getStringExtra(URL_TAG);
            
            //Display a toast for the user to read while the page loads
            Toast.makeText(context, context.getString(R.string.toast_msg), Toast.LENGTH_SHORT).show();
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
    	//Log.d(TAG, "picwidget provider onReceive");

    }
         
    private Intent switchOnId(Context ctx, int layoutId) {
    	switch(layoutId) {
    	case R.xml.wiki_feature_widget_info:
    		return new Intent(ctx, FeatureWidgetService.class);
    	case R.xml.wikipic_widget_info:
    		return new Intent(ctx, PicWidgetService.class);
    	}
    	Log.e(TAG, "error assigning layoutId, null intent");
    	return null;
    }

    private Intent switchIntentOnId(Context ctx, int layoutId) {
    	switch(layoutId) {
    	case R.xml.wiki_feature_widget_info:
    		Log.d(TAG, "pic provider class sent");
    		return new Intent(ctx, PicWidgetProvider.class);
    	case R.xml.wikipic_widget_info:
    		Log.d(TAG, "feature class sent");
    		return new Intent(ctx, FeatureWidgetProvider.class);
    	}
    	Log.e(TAG, "error assigning layoutId, null intent");
    	return null;
    }
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter
    	if(mLayoutId == 0) return;
        for (int i = 0; i < appWidgetIds.length; ++i) {
        	//Log.d(TAG, "pic provider update");
            // Here we setup the intent which points to the WikiWidgetService which will
            // provide the views for this collection.
            Intent intent = switchOnId(context, mLayoutId);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_stack);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);
            rv.setEmptyView(R.id.stack_view, R.id.empty_view);

            final Intent toastIntent = switchIntentOnId(context, mLayoutId); //new Intent(context, FeedProvider.class); //
            // Set the action to run when the user touches a particular view 
            toastIntent.setAction(CLICK);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            toastIntent.setData(Uri.parse(toastIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);
            /*final Intent betterIntent = new Intent(context, PicWidgetProvider.class);
            // Set the action to run when the user touches a particular view 
            final Intent tnt = new Intent("android.intent.action.VIEW");
            tnt.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            tnt.setData(Uri.parse(tnt.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, tnt,
                PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);*/
            
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
