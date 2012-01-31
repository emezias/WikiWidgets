package com.mezcode;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class GeoWidgetViewFactory /*extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WikiRemoteViewsFactory(this.getApplicationContext(), intent);
    }

}

class WikiRemoteViewsFactory */implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<WidgetItem> mListWidgetItems;
    private static Context mContext;
    private static final String TAG = "GeoWidgetViewFactory";
    private static final StringBuilder mScratch = new StringBuilder(10);

    public GeoWidgetViewFactory(Context context, Intent intent) {
        mContext = context.getApplicationContext();        
    }

    public void onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
    	
    }
    
    private ArrayList<GeoName> returnGeos(InputStream iStream) {
    	final String jsonStr = NetworkHelper.convertStreamToString(iStream);
    	final ArrayList<GeoName> glist = new ArrayList<GeoName>();
		// getting data and if we don't we just get out of here!
		JSONArray geonames = null;
		try {
			JSONObject json = new JSONObject(jsonStr);
			geonames = json.getJSONArray("geonames");
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} 

		for (int i = 0; i < geonames.length(); i++) {
			try {
				JSONObject geonameObj = geonames.getJSONObject(i);
				glist.add(new GeoName(
						geonameObj.getString("wikipediaUrl"), geonameObj
								.getString("title"), geonameObj
								.getString("summary"), geonameObj
								.getDouble("lat"), geonameObj
								.getDouble("lng")));
			} catch(JSONException e) {
				// ignore exception and keep going!
				Log.e(TAG, "JSON exception");
				e.printStackTrace();
			}
		}
		return glist;
    }
    
    void networkTaskCode(double latitude, double longitude) {
    	final String lang;
    	if(WikiWidgetsApp.language == null) {
    		lang = "en";
    	} else {
    		lang = WikiWidgetsApp.language;
    	}
    	
    	mScratch.append("http://ws.geonames.net/findNearbyWikipediaJSON?formatted=true&lat=").append(latitude).append("&lng=").append(longitude).append("&username=wikimedia&lang=").append(lang);
		Log.d(TAG, mScratch.toString());

    	//given a URL, fetch the web page and then process the buffered input stream
    	HttpURLConnection urlConnection = null;
    	ArrayList<GeoName> geoList = new ArrayList<GeoName>();
     	try {
 	    	URL url = new URL(mScratch.toString());
 	    	urlConnection = (HttpURLConnection) url.openConnection();
 	    	int rcode = urlConnection.getResponseCode();
 	    	Log.d(TAG, "server response code: " +  rcode);
			if ((rcode >= 200) && (rcode < 300)) {
				InputStream is = urlConnection.getInputStream(); 
				geoList = returnGeos(is);
				if(mListWidgetItems == null) {
					mListWidgetItems = new ArrayList<WidgetItem>();
				} else {
					mListWidgetItems.clear();
				}
				for(GeoName gn: geoList) {
					mListWidgetItems.add(new WidgetItem(gn.getTitle(), gn.getSummary(), gn.getWikipediaUrl()));
		    		//mWidgetItems.add(new WidgetItem(gn.getTitle()));
				}
				WikiWidgetsApp.geonames = geoList;
				//could be trouble, watch for concurrent access TODO
				} else {
				Log.e(TAG, "server responded with error code: " +  rcode);
				}
     	    //returns the buffered stream or null
 	    } catch (Exception e) {
 	            Log.w(TAG, "Error while retrieving data from " + mScratch.toString() + " " + e.toString());
 	        } finally {
 	        	if(urlConnection != null) {
 	        		urlConnection.disconnect();
 	        	}
 	        	
 	    }            
        //clean up, clear stringbuilder
 	    mScratch.setLength(0);
			
    }
    
    void createList( ) {
    	//a network task in the rest json class will get the geo name objects
    	if(WikiWidgetsApp.geonames == null) {
    		final double[] gps = getGPS(mContext);
        	//need to populate the data structure that will be used by the widget
    		/*((WikipediaApp)mListContext).geonames = 
    			RestJsonClient.getWikipediaNearbyLocations(mListContext, gps[0], gps[1], ((WikipediaApp)mListContext).language);
*/    	
    	networkTaskCode(gps[0], gps[1]);	
    	}
    	//TODO, tighten this up and use a single data structure for the app and the widget
    	//now iterate through the list from the rest json class and build the widget collection
    	//TODO extract images to download and display
    	Log.d(TAG, "for loop in createList");

    	Log.d(TAG, "end for loop size is " + mListWidgetItems.size());
    }
    
    //this function is straight from the NearMe Activity, could make it public and static to share across the two classes
    private static double[] getGPS(Context ctx) {
		final LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		final List<String> providers = lm.getProviders(true);
		/*
		 * Loop over the array backwards, and if you get an accurate location,
		 * then break out the loop
		 */
		Location l = null;

		for (int i = providers.size() - 1; i >= 0; i--) {
			l = lm.getLastKnownLocation(providers.get(i));
			if (l != null)
				break;
		}
		providers.clear();
		double[] gps = new double[2];
		if (l != null) {
			gps[0] = l.getLatitude();
			gps[1] = l.getLongitude();
			Log.d(TAG, "lat and long found");
		}
		return gps;
	}

    public void onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        mListWidgetItems.clear();
    }

    public int getCount() {
    	Log.d(TAG, "getCount");
    	if(WikiWidgetsApp.geonames != null) {
    		return WikiWidgetsApp.geonames.size();
    	} else return 0;
    }

    public RemoteViews getViewAt(int position) {
        // position will always range from 0 to getCount() - 1.
    	Log.d(TAG, "getViewAt " + position);
        // Construct a remote views item based on our widget item xml file 
        // set the title and summary text based on the position.
    	final int itemId = (position % 2 == 0 ? R.layout.widget_listitem
                : R.layout.widget_listitem2);
        final RemoteViews rv = new RemoteViews(mContext.getPackageName(), itemId);
        rv.setTextViewText(R.id.widget_item, mListWidgetItems.get(position).text);
        rv.setTextViewText(R.id.widget_summary, mListWidgetItems.get(position).summary);
        /*rv.setTextViewText(R.id.widget_url, mWidgetItems.get(position).url);
         * TODO Consider an active link in a Text view as part of the layout 
         * This could replace the pending intent or provide the functionality with 2.x releases
         * */
        // Next, we set a fill-intent which to fill-in the pending intent template
        // set on the collection view in WikiWidgetProvider.
        final Bundle extras = new Bundle();
        extras.putString(WikiWidgetProvider.URL_TAG, "http://" + mListWidgetItems.get(position).url);
        //Log.d(TAG, "set url as extra " + mWidgetItems.get(position).url);
        final Intent fillInIntent = new Intent(); 
        fillInIntent.putExtras(extras);
        //fillInIntent.putExtra(WikiWidgetProvider.URL_TAG, mWidgetItems.get(position).url);
        //Setting the extra on the intent rather than in a bundle did not seem to work
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

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
    	Log.d(TAG, "onDataSetChanged");
    	createList();
    }
}
