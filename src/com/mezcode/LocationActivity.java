package com.mezcode;

import java.util.ArrayList;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class LocationActivity extends MapActivity {
	private static final String TAG = "LocationActivity";
	private MapView mapView;
	private ProgressDialog progressDialog;
	private static WikiOverlay mPinList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearme);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.getController().setZoom(15);

		((Button)findViewById(R.id.redo)).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				new UpdateGeos().execute();
				Log.d(TAG, "fetching location with Button click");
			}
		});
	}
		
	@Override
	protected void onResume() {
		super.onResume();
		if(mPinList == null) {
			new UpdateGeos().execute();
		} else {
			mapView.getOverlays().add(mPinList);
			mapView.invalidate();
		}
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void showDialog() {
		//display a dialog while the async task is running
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle(getString(R.string.dialogTitle));
		progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
		progressDialog.setMessage(getString(R.string.dialogMessage));
		progressDialog.show();
	}

	
	private class WikiOverlay extends ItemizedOverlay<OverlayItem> {
		
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
			Log.d("WikiItemizedOverlay", "Index " + index + " Overlay title " + mOverlays.get(index).getTitle());
	        Toast.makeText(LocationActivity.this, getString(R.string.load_msg), Toast.LENGTH_SHORT).show();
	        final Intent goBack = new Intent(LocationActivity.this, WikiWidgetsActivity.class);
	        goBack.putExtra(BaseStackProvider.URL_TAG, mOverlays.get(index).getSnippet());
	        startActivity(goBack);
			
			return true;
		}

	}
	
	public static class ArrayListFragment extends ListFragment {

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
           /* setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, Shakespeare.TITLES));*/
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
        }
    }


	private class UpdateGeos extends AsyncTask<Void, Void, WikiOverlay>{
		protected void onPreExecute() {
			showDialog();
			mapView.getOverlays().clear();
		}
		protected WikiOverlay doInBackground(Void... gps) {
			final Context ctx = LocationActivity.this.getApplicationContext();
			final double[] center = NetworkHelper.getGPS(ctx);
			final GeoItem[] geoList = NetworkHelper.geoDataFetch(ctx);
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
			
			return ole;
		}
		protected void onPostExecute(WikiOverlay result) {
			//result.enableMyLocation();
			mapView.getOverlays().add(result);
			mapView.invalidate();
			progressDialog.dismiss();
			mPinList = result;
			MapPoint.resetMapNumbers();
		}
	}
	
	private class AsyncReturnValues {
		//TODO initialize fragment
		WikiOverlay geos;
		String[] titles;
	}

}
