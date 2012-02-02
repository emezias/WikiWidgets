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

public class BaseStackProvider extends AppWidgetProvider {
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
    
    int mLayoutId = R.xml.feature_stack_info;

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
    	//Log.d(TAG, "Base stack provider receive " + action);
        if (action.equals(CLICK)) {
            clickWidgetShowPage(context, intent.getStringExtra(URL_TAG));
        }
        super.onReceive(context, intent);
    	//Log.d(TAG, "picwidget provider onReceive");

    }
    
    public static void clickWidgetShowPage(Context ctx, String location) {
    	//This method executes the action for all the widgets to take when the title or picture is touched
        //Display a toast for the user to read while the page loads
        Toast.makeText(ctx, "Loading Wikipedia", Toast.LENGTH_SHORT).show();
        //Debugging toast Toast.makeText(context, "Loading Wikipedia" + location, Toast.LENGTH_SHORT).show();
        final Intent tnt = new Intent("android.intent.action.VIEW", Uri.parse(location));
        //tnt.putExtra(URL_TAG, location);
    	//final Intent tnt = new Intent(context.getApplicationContext(), WikipediaActivity.class);
    	//tnt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        tnt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        tnt.putExtra(URL_TAG, location);
        ctx.startActivity(tnt);
    }
         
    public static Intent switchSvcClassOnId(Context ctx, int layoutId) {
    	//This method is shared all the widgets through the 2 base classes
    	//It identifies the service class that will instantiate the remote views object and populate the collection
    	switch(layoutId) {
    	case R.xml.feature_stack_info:
    		return new Intent(ctx, FeatureStackService.class);
    	case R.xml.pic_stack_info:
    		return new Intent(ctx, PicStackService.class);
    	case R.xml.fon_feature_stack_info:
    		return new Intent(ctx, FonFeatureStackService.class);
    	case R.xml.geo_list_info:
    		return new Intent(ctx, GeoListService.class);	
    	}
    	Log.e(TAG, "error assigning layoutId, null intent");
    	return null;
    }

    public static Intent switchPendingIntentTemplateOnId(Context ctx, int layoutId) {
    	//This method is shared by the 2 base classes
    	//It identifies the provider class that will go into a pending intent template and execute on receive 
    	switch(layoutId) {
    	case R.xml.feature_stack_info:
    		//Log.d(TAG, "feature provider class sent");
    		return new Intent(ctx, FeatureStackProvider.class);
    	case R.xml.pic_stack_info:
    		//Log.d(TAG, "pic class sent");
    		return new Intent(ctx, PicStackProvider.class);
    	case R.xml.fon_feature_stack_info:
    		return new Intent(ctx, FonFeatureStackProvider.class);
    	case R.xml.geo_list_info:
    		return new Intent(ctx, GeoListProvider.class);	
    	}
    	Log.e(TAG, "error assigning layoutId, null intent");
    	return null;
    }
    
    public static void provideRemoteViews(Context ctx, int widgetID, int layoutID, AppWidgetManager mgr, boolean stack) {
        final Intent intent = switchSvcClassOnId(ctx, layoutID);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        final RemoteViews rv;
        if(stack) {
        	rv = new RemoteViews("com.mezcode", R.layout.widget_stack);
        	rv.setRemoteAdapter(widgetID, R.id.stack_view, intent);
        	rv.setEmptyView(R.id.stack_view, R.id.empty_view);
        } else {
        	rv = new RemoteViews("com.mezcode", R.layout.widget_list);
        	rv.setRemoteAdapter(widgetID, R.id.list_view, intent);
        	rv.setEmptyView(R.id.list_view, R.id.empty_list_view);
        }        

        final Intent toastIntent = switchPendingIntentTemplateOnId(ctx, layoutID); //new Intent(context, FeedProvider.class); //
        // Set the action to run when the user touches a particular view 
        toastIntent.setAction(CLICK);
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        toastIntent.setData(Uri.parse(toastIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent toastPendingIntent = PendingIntent.getBroadcast(ctx, 0, toastIntent,
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
        
        mgr.updateAppWidget(widgetID, rv);
        
    }
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter
    	if(mLayoutId == 0) return;
        for (int i = 0; i < appWidgetIds.length; ++i) {
        	//Log.d(TAG, "base stack provider update");
        	provideRemoteViews(context, appWidgetIds[i], mLayoutId, appWidgetManager, true);

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
