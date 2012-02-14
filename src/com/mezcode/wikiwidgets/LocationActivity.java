package com.mezcode.wikiwidgets;

import java.util.ArrayList;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.mezcode.wikiwidgets.widgets.BaseStackProvider;

public class LocationActivity extends MapActivity {
	private static final String TAG = "LocationActivity";
	private MapView mapView;
	private static WikiOverlay mPinList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearme);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.getController().setZoom(15);

		/*((Button)findViewById(R.id.redo)).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				new UpdateGeos().execute();
				Log.d(TAG, "fetching location with Button click");
			}
		});*/
	}
		
	@Override
	protected void onResume() {
		super.onResume();
		if(mPinList == null) {
			new UpdateGeos().execute();
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
	
	private class UpdateGeos extends AsyncTask<Void, Void, WikiOverlay>{
		ProgressDialog pd;
		protected void onPreExecute() {
			pd = getProgress(LocationActivity.this);
			mapView.getOverlays().clear();
		}
		protected WikiOverlay doInBackground(Void... gps) {
			final Context ctx = LocationActivity.this.getApplicationContext();
			final double[] center = NetworkHelper.getGPS(ctx);
			GeoItem[] geoList = NetworkHelper.geoDataFetch(ctx);
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
