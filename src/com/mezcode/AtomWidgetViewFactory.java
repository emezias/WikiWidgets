package com.mezcode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class AtomWidgetViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<PicItem> mListWidgetItems;
    private boolean photoFeed;
    private static String mPkg;
    //private static Context mContext;
    private static final String TAG = "AtomWidgetViewFactory";

   /* public AtomWidgetViewFactory(Context context, Intent intent) {
        //mContext = context.getApplicationContext();   
        mPkg = context.getPackageName();
        Log.d(TAG, "package string set " + mPkg);
    }*/
    
    public AtomWidgetViewFactory(Context context, Intent intent, boolean pics) {
    	mPkg = context.getPackageName();
        photoFeed = pics;
    }

    public void onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
    	Log.d(TAG, "atom onCreate");
    	Log.d(TAG, "onCreate boolean value is " + photoFeed);
    	
    }
    
        
    void createList( ) {
    	//use an RSS feed to build the widget collection 
    	URL webpage = null;
    	try {
    		if(photoFeed) {
    			webpage = new URL(NetworkHelper.POTD_STREAM);
    		} else {
    			webpage = new URL(NetworkHelper.FEATURED_FEED);
    		}
			
			mListWidgetItems = NetworkHelper.networkTaskCode(webpage, mListWidgetItems);
		} catch (MalformedURLException e) {
			Log.e(TAG, "createList exception " + e.getMessage());
			e.printStackTrace();
		}
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
    
    private int fetchId(int position) {
    	if(photoFeed) {
    		Log.d(TAG, "photoFeed true");
    		return (position % 2 == 0 ? R.layout.pic_widget_item
                    : R.layout.pic_widget_item2);
    	} else {
    		Log.d(TAG, "photoFeed false");
    		return (position % 2 == 0 ? R.layout.feature_widget_item
                    : R.layout.feature_widget_item2);
    	}
    }

    public RemoteViews getViewAt(int position) {
        // position will always range from 0 to getCount() - 1.
    	//Log.d(TAG, "getViewAt " + position);
        // Construct a remote views item based on our widget item xml file 
        // set the title and summary text based on the position.
    	final int itemId = fetchId(position);
        final RemoteViews rv = new RemoteViews(mPkg, itemId);
        rv.setTextViewText(R.id.widget_item, mListWidgetItems.get(position).title);
        rv.setTextViewText(R.id.widget_summary, mListWidgetItems.get(position).summary);
        /*************/
        
        if(mListWidgetItems.get(position).photo != null) {
        	rv.setImageViewBitmap(R.id.widget_pic, mListWidgetItems.get(position).photo);
        } else {
        	rv.setImageViewResource(R.id.widget_pic, R.drawable.icon);
        }
        /*************/
        // Next, we set a fill-intent which to fill-in the pending intent template
        // set on the collection view in WikiWidgetProvider.
        final Bundle extras = new Bundle();
        extras.putString(WikiWidgetProvider.URL_TAG, mListWidgetItems.get(position).wikipediaUrl);
        //Log.d(TAG, "set url as extra " + mListWidgetItems.get(position).wikipediaUrl);
        final Intent fillInIntent = new Intent(); 
        fillInIntent.putExtras(extras);
        //fillInIntent.putExtra(WikiWidgetProvider.URL_TAG, mWidgetItems.get(position).url);
        //Setting the extra on the intent rather than in a bundle did not seem to work
        //        fillInIntent.setData(Uri.parse(mListWidgetItems.get(position).wikipediaUrl));
        //not able to use the "set data" call and go directly to an action view, popping toast back in the provider instead
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
        rv.setOnClickFillInIntent(R.id.widget_pic, fillInIntent);
        //TODO template should be set on both fields in provider?

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
