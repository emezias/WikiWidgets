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
        
    static ArrayList<PicItem> returnListFromRSS(BufferedInputStream is) {   
    	//This method will parse the atom feed from the url connection stream
    	//results go into an ArrayList that will transformed into an array that is caches in the view factory to populate remote view objects
    	final ArrayList<PicItem> widgetList = new ArrayList<PicItem>();
    	try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, null);
			// auto-detect the encoding from the stream		
			int eventType = parser.getEventType();
			PicItem currentPic = null;
			boolean done = false;
			String name, summary;
			
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
								currentPic.photo = downloadBitmap(getPOTDTemplatePhotoURL(summary));
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
    
    
    static String getPOTDTemplatePhotoURL(String subjectString){
		//passing in description string from the atom feed
		//pulling out the bitmap to download for the pic of the day
		//Log.d(TAG, "getPOTDTemplatePhotoURL");
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
    	//Log.d(TAG, "download bitmap");
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
                if(bitmap.getHeight() < 30) return null;
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

    private static ArrayList<PicItem> returnGeos(InputStream iStream) {
    	//Log.e(TAG,"convertStreamToString next");
    	/*final StringBuilder jsonStr = new StringBuilder(100);
    	jsonStr.append(iStream);*/
    	final String jsonStr = NetworkHelper.convertStreamToString(iStream);
    	//final ArrayList<WidgetItem> glist = new ArrayList<WidgetItem>();
    	ArrayList<PicItem> gList = new ArrayList<PicItem>();
		// getting data and if we don't we just get out of here!
		JSONArray geonames = null;
		try {
			JSONObject json = new JSONObject(jsonStr.toString());
			geonames = json.getJSONArray("geonames");
			//Log.d(TAG, "geonames created, size is " + geonames.length());
		} catch (JSONException e) {
			Log.e(TAG, "Json exception " + e.getMessage());
			e.printStackTrace();
			return null;
		} 
		PicItem add2List;
		for (int i = 0; i < geonames.length(); i++) {
			try {
				JSONObject geonameObj = geonames.getJSONObject(i);
				add2List = new PicItem();
				add2List.wikipediaUrl = geonameObj.getString("wikipediaUrl");
				add2List.setTitle(geonameObj.getString("title"));
				add2List.setSummary(geonameObj.getString("summary"));
				//Log.d(TAG, "wiki url is " + add2List.getWikipediaUrl());
				gList.add(add2List);
				/* glist.add(new PicItem( geonameObj.getString("wikipediaUrl"), geonameObj
								.getString("title"), geonameObj
								.getString("summary"), geonameObj));*/
			} catch(JSONException e) {
				// ignore exception and keep going!
				Log.e(TAG, "JSON exception");
				e.printStackTrace();
				return null;
			}
		}
		//Log.d(TAG, "glist size " + gList.size() + " no problem with return Geos");
		return gList;
    }
    
    static PicItem[] geoDataFetch(Context ctx) {
    	double[] gps = getGPS(ctx);
    	/*
    	 * fetch the location or not!
    	 * TODO, cache old data with a serialized pref, 
    	 * pop toast or show that gps is not available some way
    	 * TODO refresh button
    	 */
    	final String lang;
    	if(WikiWidgetsApp.language == null) {
    		lang = "en";
    	} else {
    		lang = WikiWidgetsApp.language;
    	}
       final StringBuilder scratch = new StringBuilder(10);
       scratch.append("http://ws.geonames.net/findNearbyWikipediaJSON?formatted=true&lat=").append(gps[0])
    		.append("&lng=").append(gps[1]).append("&username=wikimedia&lang=").append(lang);
       final ArrayList<PicItem> tmpStructure = parseGeonamesPage(scratch.toString());
       //Log.d(TAG, scratch.toString() + " list length = " + tmpStructure.size());
        //clean up, clear stringbuilder
 	    scratch.setLength(0);
    	gps = null;
    	if(tmpStructure != null) {
    		PicItem[] widgetList = new PicItem[tmpStructure.size()];
    		widgetList = tmpStructure.toArray(widgetList);
        	//Log.d(TAG, "array length = " + widgetList.length);
        	tmpStructure.clear();
     	    return widgetList;
    	} 
    	return new PicItem[0];
    	
    }
    
    static ArrayList<PicItem> parseGeonamesPage(String urlstring) {
    	//given a URL, fetch the web page and then process the buffered input stream into a scrolling widget collection
    	HttpURLConnection urlConnection = null;
    	//Log.d(TAG, "parse page");
    	ArrayList<PicItem> widgetSvcCollection = null;
     	try {
     		final URL url = new URL(urlstring);
 	    	urlConnection = (HttpURLConnection) url.openConnection();
 	    	int rcode = urlConnection.getResponseCode();
 	    	//Log.d(TAG, "server response code: " +  rcode);
			if ((rcode >= 200) && (rcode < 300)) {
				InputStream is = urlConnection.getInputStream(); 
			    widgetSvcCollection = returnGeos(is);
				//Log.d(TAG, "return geos got list, num of items is " + widgetSvcCollection.size());					
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
 	        	
 	    } //have an array list of nearby wiki articles
     	//now take the wikipedia url and pull a picture out of the article
		for(PicItem geoPage: widgetSvcCollection) {
			geoPage.setPhoto(fetchGeoPhoto("http://" + geoPage.wikipediaUrl));
		}
		//Log.d(TAG, "list size again " + widgetSvcCollection.size());
		return widgetSvcCollection;
			
    }
    
    static Bitmap fetchGeoPhoto(String urlstring) {
    	
    	StringBuilder sb = new StringBuilder(urlstring);
		int index = sb.indexOf(".m.");
/*		if(index == -1) {
			index = sb.indexOf(".");
			//Log.d(TAG, "dot index is " + index);
			sb.insert(index, ".m");
		}*/
		index = sb.indexOf(".");
		//Log.d(TAG, "dot index is " + index);
		sb.insert(index, ".m");
		urlstring = sb.toString();
		sb.setLength(0);
		//Log.d(TAG, "fetchGeoPhoto " + urlstring);
    	HttpURLConnection urlConnection = null;
    	boolean hasImage = false;
     	try {
     		final URL url = new URL(urlstring);
 	    	urlConnection = (HttpURLConnection) url.openConnection();
 	    	int rcode = urlConnection.getResponseCode();
 	    	//Log.d(TAG, "server response code: " +  rcode);
			if ((rcode >= 200) && (rcode < 300)) {
				sb.append(convertStreamToString(urlConnection.getInputStream()));
				int start = sb.indexOf("image");
				
				if(start > 0) {
					sb = new StringBuilder(sb.substring(start, sb.length()));
					start = sb.indexOf("upload.wikimedia.org");
					//Log.d(TAG, "start index " + start);
					int end = sb.indexOf(" width=");
					if(end > 0) {
						hasImage = true;
						//Log.d(TAG, "end index " + end);
						sb = new StringBuilder(sb.substring(start, end-8));
						sb.insert(0, "http://");
						//Log.d(TAG, sb.toString());
					} //end, final string index										
				} //end if, src tag present but no jpg
			} else {
				Log.e(TAG, "server responded with error code: " +  rcode);
			}
     	    //returns the list or null
 	    } catch (Exception e) {
 	            Log.w(TAG, "Error while retrieving data from " + urlstring + " " + e.toString());
 	        } finally {
 	        	if(urlConnection != null) {
 	        		urlConnection.disconnect();
 	        	}
 	        }   	
    	if(hasImage) {
    		//Log.d(TAG, "has image ");
    		return downloadBitmap(sb.toString());
    	} 
    	return null;
    }
    
    static PicItem[] fetchRssFeed(URL url) {
    	//given a URL, fetch the web page and then process the buffered input stream into a scrolling widget collection
    	HttpURLConnection urlConnection = null;
    	
     	try {
 	    	//URL url = new URL(urlstring);
 	    	urlConnection = (HttpURLConnection) url.openConnection();
 	    	int rcode = urlConnection.getResponseCode();
 	    	//Log.d(TAG, "server response code: " +  rcode);
			if ((rcode >= 200) && (rcode < 300)) {
				BufferedInputStream is = new BufferedInputStream(urlConnection.getInputStream()); 
				final ArrayList<PicItem> widgetSvcCollection = returnListFromRSS(is);
				PicItem[] list = new PicItem[widgetSvcCollection.size()];
				list = widgetSvcCollection.toArray(list);
				widgetSvcCollection.clear();
				return list;
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
		if(is == null) {
			Log.e(TAG, "null input stream when converting to string");
			return null;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		//Log.d(TAG, "read line by line");
		try {
			while ((line = reader.readLine()) != null) {
				//Log.d(TAG, line);
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Log.e(TAG,"error closing buffered reader");
				e.printStackTrace();
			}
		}
		//Log.d(TAG, "returning now, " + sb.toString())
;		return sb.toString();
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
