package com.mezcode;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class GeoListViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<PicItem> mListWidgetItems;
    private static Context mListContext;
    private static final String TAG = "GeoListViewFactory";
    private int mListType;

    public GeoListViewFactory(Context context, Intent intent, int widgetId) {
        mListContext = context.getApplicationContext();        
        mListType = R.xml.geo_list_info;
    }

    public void onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
    	
    }    
    
    void createList( ) {
    	mListWidgetItems = NetworkHelper.geoDataFetch(mListContext, mListWidgetItems);
    	Log.d(TAG, "end createList, size is " + mListWidgetItems.size());
    }
    
    public void onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        mListWidgetItems.clear();
    }

    public int getCount() {
    	//Log.d(TAG, "getCount");
    	if(mListWidgetItems != null) {
    		return mListWidgetItems.size();
    	} else return 0;
    }

    public RemoteViews getViewAt(int position) {
        // position will always range from 0 to getCount() - 1.
        // Construct a remote views item based on our widget item xml file 
        // set the title and summary text based on the position.
    	/*final int itemId = (position % 2 == 0 ? R.layout.widget_listitem
                : R.layout.widget_listitem2);*/
    	final int itemId = (position % 2 == 0 ? R.layout.pic_widget_item
                : R.layout.pic_widget_item2);
        final RemoteViews rv = new RemoteViews("com.mezcode", itemId);
        rv.setTextViewText(R.id.widget_item, mListWidgetItems.get(position).title);
        Log.d(TAG, "rv title is " + mListWidgetItems.get(position).title);
        rv.setTextViewText(R.id.widget_summary, mListWidgetItems.get(position).summary);
        if(mListWidgetItems.get(position).photo != null) {
        	rv.setImageViewBitmap(R.id.widget_pic, mListWidgetItems.get(position).photo);
        }
        /*final PicItem displayItem = mListWidgetItems.get(position);
        rv.setTextViewText(R.id.widget_item, displayItem.title);
        Log.d(TAG, "rv title is " + displayItem.title);
        rv.setTextViewText(R.id.widget_summary, displayItem.summary);
        if(displayItem.photo != null) {
        	rv.setImageViewBitmap(R.id.widget_pic, displayItem.photo);
        }*/
        /*rv.setTextViewText(R.id.widget_url, mWidgetItems.get(position).url);
         * TODO Consider an active link in a Text view as part of the layout 
         * This could replace the pending intent or provide the functionality with 2.x releases
         * */
        // Next, we set a fill-intent which to fill-in the pending intent template
        // set on the collection view in WikiWidgetProvider.
        final Bundle extras = new Bundle();
        extras.putString(WikiWidgetProvider.URL_TAG, "http://" + mListWidgetItems.get(position).wikipediaUrl);
        Log.d(TAG, "set url as extra " + mListWidgetItems.get(position).wikipediaUrl);
        final Intent fillInIntent = new Intent(); 
        fillInIntent.putExtras(extras);
        //fillInIntent.putExtra(WikiWidgetProvider.URL_TAG, mWidgetItems.get(position).url);
        //Setting the extra on the intent rather than in a bundle did not seem to work
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
        rv.setOnClickFillInIntent(R.id.widget_pic, fillInIntent);
        // You can do heaving lifting in here, synchronously. For example, if you need to
        // process an image, fetch something from the network, etc., it is ok to do it here,
        // synchronously. A loading view will show up in lieu of the actual contents in the
        // interim.
        return rv;
    }

    public RemoteViews getLoadingView() {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // For use with a content provider?  Run after 
    	//Log.d(TAG, "onDataSetChanged");
    	createList();
    }
}
