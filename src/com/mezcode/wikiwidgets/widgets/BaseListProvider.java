package com.mezcode.wikiwidgets.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.mezcode.wikiwidgets.R;

public class BaseListProvider extends AppWidgetProvider {
	private static final String TAG = "BaseListProvider";
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
        if (action.equals(BaseStackProvider.CLICK)) {
        	//Log.d(TAG, "base list provider receive " + intent.getStringExtra(BaseStackProvider.URL_TAG));
            BaseStackProvider.clickWidgetShowPage(context, intent.getStringExtra(BaseStackProvider.URL_TAG));
            //Display a toast and display the page
        } else if(action.equals(REFRESH)) {
        	final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            //final ComponentName cn = new ComponentName(context, BaseListProvider.class);
            final int id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if(id == AppWidgetManager.INVALID_APPWIDGET_ID) {
            	Log.e(TAG, "Error, invald widget id");
            } else {
            	mgr.notifyAppWidgetViewDataChanged(id, R.id.list_view);
                //Log.d(TAG, "sending notification of view data changed");
            }
            
        }
        super.onReceive(context, intent);
    	//Log.d(TAG, "geo widget provider onReceive");

    } 

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter
    	
        for (int i = 0; i < appWidgetIds.length; ++i) {
            // Here we setup the intent which points to the WikiWidgetService which will
            // provide the views for this collection.
        	// ||mListLayoutId == R.xml.fon_geo_list_info 
        	if(mListLayoutId == R.xml.geo_list_info) {
        		provideGeoRemoteViews(context, appWidgetIds[i], mListLayoutId, appWidgetManager);
        	} else {
        		BaseStackProvider.provideRemoteViews(context, appWidgetIds[i], mListLayoutId, appWidgetManager, false);
        	}
        	
            /* code to support refresh button in geo list layout 
 				TODO set refresh button in stack layout
             * Limit this to Geo?  The other data sources will automatically change daily.  
             */
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
    
    public static void provideGeoRemoteViews(Context ctx, int widgetID, int layoutID, AppWidgetManager mgr) {
        // Set the action to run when the user touches a particular view 
        final Intent toastIntent = BaseStackProvider.switchPendingIntentTemplateOnId(ctx, layoutID); 
    	toastIntent.setAction(BaseStackProvider.CLICK);
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        toastIntent.setData(Uri.parse(toastIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent toastPendingIntent = PendingIntent.getBroadcast(ctx, 0, toastIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);
        //pending intent template ready!  
        
        final Intent intent = BaseStackProvider.switchSvcClassOnId(ctx, layoutID);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        final RemoteViews rv = new RemoteViews("com.mezcode.wikiwidgets", R.layout.geo_list_layout);
    	rv.setRemoteAdapter(widgetID, R.id.list_view, intent);
    	rv.setEmptyView(R.id.list_view, R.id.empty_list_view);
    	rv.setPendingIntentTemplate(R.id.list_view, toastPendingIntent);
                
    	final Intent freshTNT = BaseStackProvider.switchPendingIntentTemplateOnId(ctx, layoutID);
    	freshTNT.setAction(REFRESH);
    	freshTNT.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
    	freshTNT.setData(Uri.parse(freshTNT.toUri(Intent.URI_INTENT_SCHEME)));
    	final PendingIntent freshPendingIntent = PendingIntent.getBroadcast(ctx, 0, freshTNT, PendingIntent.FLAG_UPDATE_CURRENT);
    	rv.setOnClickPendingIntent(R.id.fresh_btn, freshPendingIntent);

    	rv.setTextViewText(R.id.listTitle, ctx.getString(R.string.geoListTitle));
        //pending intent seems restricted 
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
    
}
