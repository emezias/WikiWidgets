package com.mezcode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.util.Xml;

public class NetworkHelper {
	private static final String TAG = "NetworkHelper";
    
    static final int LOCATION = 1;
    static final int ATOM_FEED = 2;
    static final int PHOTO = 3;
    
    static final String POTD_STREAM = "http://toolserver.org/~skagedal/feeds/potd.xml";
    static final String FEATURED_FEED = "http://toolserver.org/~skagedal/feeds/fa.xml";
    
	static final  String DESCRIPTION = "description";
    static final  String LINK = "link";
    static final  String TITLE = "title";
    static final  String ITEM = "item";
        
    static ArrayList<PicItem> returnList(BufferedInputStream is, ArrayList<PicItem> widgetList) {   
    	//This method will parse the atom feed
    	try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, null);
			// auto-detect the encoding from the stream		
			int eventType = parser.getEventType();
			PicItem currentPic = null;
			boolean done = false;
			String name, summary;
			//Log.i(TAG, "into while loop " + eventType);
			while (eventType != XmlPullParser.END_DOCUMENT && !done) {
				name = null;
				switch (eventType){
					case XmlPullParser.START_DOCUMENT:
						//Log.i(TAG, "read a new feed ");
						break;
					case XmlPullParser.START_TAG:
						name = parser.getName();
						//if(name != null) Log.d(TAG, name);

						if (name.equalsIgnoreCase(ITEM)){
							//new item in the feed creates a new entry in the widget's list
							currentPic = new PicItem();
						} else if (currentPic != null) {
							//process other types
							if (name.equalsIgnoreCase(LINK)){
								currentPic.setWikipediaUrl(parser.nextText());		//safeNextText(parser));
							} else if (name.equalsIgnoreCase(DESCRIPTION)){
								summary = parser.nextText();
								currentPic.setSummary(summary);
								currentPic.photo = downloadBitmap(getPhotoURL(summary));
							} else if (name.equalsIgnoreCase(TITLE)){
								currentPic.setTitle(parser.nextText());
							} //else parser.nextText();
						}
						break;
					case XmlPullParser.END_TAG:
						name = parser.getName();
						if (name.equalsIgnoreCase("item") && currentPic != null){
							widgetList.add(currentPic);
						} else if (name.equalsIgnoreCase("channel")){
							done = true;
						}
						break;
				}
				eventType = parser.next();
				}
			} catch (Exception e) {
			// Parser errors caught here
				Log.e(TAG, "Parsing failed " + e.getLocalizedMessage());
				e.printStackTrace();
			}
			return widgetList;
    }
    
    
    static String getPhotoURL(String subjectString){
		//passing in description string from the atom feed
		//pulling out the bitmap to download for the pic of the day
		//Log.d(TAG, "getPhotoURL");
    	StringBuilder sb = new StringBuilder(100);
		sb.append(subjectString);
		//Log.d(TAG, "getPhotoURL " + sb.toString());
		int indya=0, indyb=0;
		indya = sb.indexOf("src=") + 5;
		indyb = sb.indexOf("width=", indya) -2;
		//Log.d(TAG, "indices are " + indya + " : " + indyb);
		if(indya > 4 && indyb < subjectString.length() && indyb > 0) {
			sb = new StringBuilder(sb.substring(indya, indyb));
			sb.insert(0, "http:");
		}
		if(indya == 4) {
			Log.w(TAG, "no src in description");
			return null;
		}
		
		return sb.toString();
	}
    
    static Bitmap downloadBitmap(String urlstring) {
    	HttpURLConnection urlConnection = null;
 	    InputStream mapStream;
 	   //Log.d(TAG, "download bitmap started");
 	    if(urlstring == null || urlstring.equals("")) return null;
 	   //Log.d(TAG, "bitmap url is " + urlstring);
        try {
        	final URL url = new URL(urlstring);
        	urlConnection = (HttpURLConnection) url.openConnection();
        	int rcode = urlConnection.getResponseCode();
			//Log.i(TAG, "server response code: " + rcode);
			if ((rcode >= 200) && (rcode < 300)) {
				mapStream = urlConnection.getInputStream();
				//new BufferedInputStream(urlConnection.getInputStream());
				BitmapFactory.Options options = new BitmapFactory.Options();
    	    	if(urlConnection.getContentLength() > 50000) {
    	    		//an absolute guess at the max
    	    		options.inSampleSize = 2;
    	    	}
    		    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                final Bitmap bitmap = BitmapFactory.decodeStream(mapStream, null, options);
                return bitmap;
			} else {
				Log.e(TAG, "server responded with error code: " +  rcode);
				return null;
			}
            
        } catch (Exception e) {
	            Log.e(TAG, "Error while retrieving bitmap from " + urlstring + " " + e.toString());
	        } finally {
	        	if(urlConnection != null) {
	        		urlConnection.disconnect();
	        	}
	      }  
        return null;
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

    void processPageFetch(double latitude, double longitude, ArrayList<WidgetItem> widgetList) {
    	final String lang;
    	if(WikiWidgetsApp.language == null) {
    		lang = "en";
    	} else {
    		lang = WikiWidgetsApp.language;
    	}
       final StringBuilder scratch = new StringBuilder(10);

    	scratch.append("http://ws.geonames.net/findNearbyWikipediaJSON?formatted=true&lat=").append(latitude).append("&lng=").append(longitude).append("&username=wikimedia&lang=").append(lang);
		//Log.d(TAG, scratch.toString());

    	networkTaskCode(scratch.toString(), widgetList);
        //clean up, clear stringbuilder
 	    scratch.setLength(0);
    }
    
    void networkTaskCode(String urlstring, ArrayList<WidgetItem> widgetSvcCollection) {

    	//given a URL, fetch the web page and then process the buffered input stream into a scrolling widget collection
    	HttpURLConnection urlConnection = null;
    	
     	try {
     		final URL url = new URL(urlstring);
 	    	urlConnection = (HttpURLConnection) url.openConnection();
 	    	int rcode = urlConnection.getResponseCode();
 	    	//Log.d(TAG, "server response code: " +  rcode);
			if ((rcode >= 200) && (rcode < 300)) {
				InputStream is = urlConnection.getInputStream(); 
				final ArrayList<GeoName> geoList = returnGeos(is);
				if(widgetSvcCollection == null) {
					widgetSvcCollection = new ArrayList<WidgetItem>();
				} else {
					widgetSvcCollection.clear();
				}
				for(GeoName gn: geoList) {
					widgetSvcCollection.add(new WidgetItem(gn.getTitle(), gn.getSummary(), gn.getWikipediaUrl()));
				}
					
			} else {
				Log.e(TAG, "server responded with error code: " +  rcode);
			}
     	    //returns the list or null
 	    } catch (Exception e) {
 	            Log.w(TAG, "Error while retrieving data from " + urlstring + e.toString());
 	        } finally {
 	        	if(urlConnection != null) {
 	        		urlConnection.disconnect();
 	        	}
 	        	
 	    }            
			
    }
    
    static ArrayList<PicItem> networkTaskCode(URL url, ArrayList<PicItem> widgetSvcCollection) {
    	//given a URL, fetch the web page and then process the buffered input stream into a scrolling widget collection
    	HttpURLConnection urlConnection = null;
    	
     	try {
 	    	//URL url = new URL(urlstring);
 	    	urlConnection = (HttpURLConnection) url.openConnection();
 	    	int rcode = urlConnection.getResponseCode();
 	    	//Log.d(TAG, "server response code: " +  rcode);
			if ((rcode >= 200) && (rcode < 300)) {
				BufferedInputStream is = new BufferedInputStream(urlConnection.getInputStream()); 
				if(widgetSvcCollection == null) {
					widgetSvcCollection = new ArrayList<PicItem>();
				} else {
					widgetSvcCollection.clear();
				}
				widgetSvcCollection = returnList(is, widgetSvcCollection);
				return widgetSvcCollection;
			} else {
				Log.e(TAG, "server responded with error code: " +  rcode);
				return null;
			}
     	    //returns the list or null
 	    } catch (Exception e) {
 	            Log.w(TAG, "Error while retrieving data from " + url.getPath() + e.toString());
 	        } finally {
 	        	if(urlConnection != null) {
 	        		urlConnection.disconnect();
 	        	}
 	        	
 	    } //end catch, something went wrong, return null
 	    return null;
			
    }
    
	//code borrowed from the WikimediaFoundation's Phone Gap project
	public static String convertStreamToString(InputStream is) {
		if(is == null) return null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;

		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	//code borrowed from the WikimediaFoundation's Phone Gap project.  The NearMe activity is cool!
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
			//Log.d(TAG, "lat and long found");
		}
		return gps;
	}
}
