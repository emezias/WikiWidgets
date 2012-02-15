package com.mezcode.wikiwidgets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.mezcode.wikiwidgets.widgets.BaseStackProvider;

public class LocationActivity extends MapActivity {
	//Activity shows a list of wikipedia pages in a fragment on the left
	//the MapView on the right displays the pages as points on a map
	//Activity also has 2 buttons to set the location (geocode a string or pull GPS coordinates)
	private static final String TAG = "LocationActivity";
	private static final int GEOCODE = 1;
	private static final int MY_LOCATION = 2;
	private MapView mapView;
	private static WikiOverlay mPinList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearme);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.getController().setZoom(15);
		
	}
		
	@Override
	protected void onResume() {
		//display the last location set by the user or fetch the current location and list
		super.onResume();
		if(mPinList == null) {
			new UpdateGeos().execute(MY_LOCATION);
		} else {
			mapView.getOverlays().add(mPinList);
			mapView.invalidate();
			((ListFragment)getFragmentManager().findFragmentById(R.id.geo_list_detail))
				.setListAdapter(new MyAdapter(mPinList));

		}
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//TODO feature menu
	    getMenuInflater().inflate(R.menu.main_menu, menu);
	    MenuItem tmp = menu.findItem(R.id.menu_location);
	    if(tmp != null) {
	    	menu.removeItem(tmp.getItemId());
	    }
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		WikiWidgetsActivity.handleActions(this, item.getItemId());
		return super.onContextItemSelected(item);
	}
	
	public static void clearList() {
		mPinList = null;
	}
	
	public void geoButton(View v) {
		//TODO
		Log.d(TAG, "button press");
	}
	
	public static ProgressDialog getProgress(Context ctx) {
		//display a dialog while the async task is running
		final ProgressDialog progressDialog = new ProgressDialog(ctx, R.style.CustomeDialog);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle(ctx.getString(R.string.dialogTitle));
		progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
		progressDialog.setMessage(ctx.getString(R.string.dialogMessage));
		progressDialog.show();
		return progressDialog;
	}
	
	public static AlertDialog createGeoDialog(AlertDialog.Builder builder, ViewGroup rootNode, final Context ctx) {
		final View layout = LayoutInflater.from(ctx).inflate(R.layout.geo_search_layout, 
				rootNode);
		builder.setTitle(R.string.geo_search_title)
		.setView(layout)
		.setCancelable(true)
		.setIcon(R.drawable.geo_button)
		.setPositiveButton(ctx.getString(R.string.geo_pos), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//MyActivity.this.finish();
					
				String locationString = ((EditText) layout.findViewById(R.id.locationText)).getText().toString();
				/*final char[] chars = tmp.toCharArray();
				for(int i = 0; i < chars.length; i++) {
					if(Character.isWhitespace(chars[i])) {
						chars[i] = '+';
					}
				}
				tmp = new String(chars);*/
				/*Log.d(TAG, "tmp is " + tmp);
				final String locationString = "http://maps.googleapis.com/maps/api/geocode/json?address=" +
				TextUtils.htmlEncode(tmp) + "&sensor=false";
						//((EditText) layout.findViewById(R.id.locationText)).getText().toString();
				
				Log.d(TAG, "location from dialog is " + locationString);*/
				//extras.putString(LocationList.TAG, locationString);
				((LocationActivity) ctx).new UpdateGeos().execute(GEOCODE, locationString);
				
			}
		})
		.setNegativeButton(ctx.getString(R.string.geo_neg), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		return builder.create();
	}

	public void geo_button(View v) {
		switch(v.getId()) {
		case R.id.geo_dialog_btn:
			createGeoDialog(new AlertDialog.Builder(this), (ViewGroup) findViewById(R.id.layout_root), this).show();
			break;
		case R.id.redo:
			new UpdateGeos().execute(MY_LOCATION);
			break;
		}
	}
	
	public class WikiOverlay extends ItemizedOverlay<OverlayItem> {
		
		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

		public WikiOverlay(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
		}
				
		public void addOverlay(OverlayItem overlay) {
			mOverlays.add(overlay);
			populate();
		}
		
		@Override
		protected OverlayItem createItem(int i) {
			return mOverlays.get(i);
		}
		
		@Override
		public int size() {
			return mOverlays.size();
		}
		
		@Override
		protected boolean onTap(int index) {
			
			// Weirdest thing ever! index is always 0 and mOverlays size is 1
			//Log.d("WikiItemizedOverlay", "Index " + index + " Overlay title " + mOverlays.get(index).getTitle());
	        Toast.makeText(LocationActivity.this, getString(R.string.load_msg), Toast.LENGTH_SHORT).show();
	        final Intent goBack = new Intent(LocationActivity.this, WikiWidgetsActivity.class);
	        goBack.putExtra(BaseStackProvider.URL_TAG, mOverlays.get(index).getSnippet());
	        startActivity(goBack);
			
			return true;
		}

	}
	
	public class UpdateGeos extends AsyncTask<Object, Void, WikiOverlay>{
		ProgressDialog pd;
		protected void onPreExecute() {
			pd = getProgress(LocationActivity.this);
			mapView.getOverlays().clear();
		}
		protected WikiOverlay doInBackground(Object... params) {
			final Context ctx = LocationActivity.this.getApplicationContext();
			GeoItem[] geoList = null; 
			double[] center = new double[2];
			switch((Integer)params[0]) {
			case GEOCODE:
				//user pushed set location button, geo coding to get lat and long from the dialog
				final String location = (String) params[1];
				final List<Address> fa;
				try {
					fa = (new Geocoder(ctx)).getFromLocationName(location, 1);
					if(fa.isEmpty()) {
						Log.e(TAG, "getFromLocationName, nothingFound");
						//TODO pop toast
						center = NetworkHelper.getGPS(ctx);
						geoList = NetworkHelper.geoDataFetch(ctx);
					} else {
						final Address addr = fa.get(0);
						//mYearDay = MainActivity.fetchYearDay();
						/*center[0] = addr.getLatitude();
						center[1] = addr.getLongitude();*/
						geoList = NetworkHelper.geoDataFetch(ctx, center[0] = addr.getLatitude(),
								center[1] = addr.getLongitude());
						} 
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case MY_LOCATION:
				//default, or, user wants to get a newer GPS reading
				center = NetworkHelper.getGPS(ctx);
				geoList = NetworkHelper.geoDataFetch(ctx);
				break;
			}
					
			final WikiOverlay ole = new WikiOverlay(new MapPoint());
			
			((MapController) mapView.getController()).setCenter(
				new GeoPoint((int)(center[0] * Math.pow(10, 6)), (int)(center[1] * Math.pow(10, 6))));
			OverlayItem point;
			for(GeoItem item: geoList) {
				point = new OverlayItem(
						new GeoPoint((int)(item.latitude * Math.pow(10, 6)), (int)(item.longitude * Math.pow(10, 6))),
						item.getTitle(), 
						item.getWikipediaUrl()
						);
				point.setMarker(new MapPoint());
				ole.addOverlay(point);
			}
			//mAdapter = new MyAdapter(ctx, geoList, false);
			geoList = null;
			return ole;
		}
		protected void onPostExecute(WikiOverlay result) {
			//result.enableMyLocation();
			mapView.getOverlays().add(result);
			mapView.invalidate();
			pd.dismiss();
			mPinList = result;
			MapPoint.resetMapNumbers();
			((ListFragment)getFragmentManager().findFragmentById(R.id.geo_list_detail)).setListAdapter(new MyAdapter(result));
					//LocationActivity.this.findViewById(R.id.mapview)).setListAdapter(mAdapter);
		}
	}
	
}
