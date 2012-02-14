package com.mezcode.wikiwidgets;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mezcode.wikiwidgets.LocationActivity.WikiOverlay;

public class MyAdapter extends BaseAdapter {
	//Built to last...
	//This list adapter will work for the 3 list activities in the app
	
	private static final String TAG = "MyAdapter";
	PicItem[] listItems;
	WikiOverlay geoItems;
	boolean mFeature;
	
	public MyAdapter(Context ctxt, PicItem[] itemList, boolean feature) {
		//the boolean is to set different layouts if it is photo or feature
		super();
		listItems = itemList;
		mFeature = feature;
	}
	
	public MyAdapter(WikiOverlay overlayArrayList) {
		super();
		geoItems = overlayArrayList;
	}
	
	String getUrl(int position) {
		if(listItems == null) {
			return geoItems.getItem(position).getSnippet();
		} else return listItems[position].getWikipediaUrl();
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(listItems == null) convertView = handleGeo(position, convertView, parent);
		else convertView = getFeedView(listItems, mFeature, position, convertView, parent);		
		
		if(position % 2 == 0) {
			convertView.setBackgroundResource(R.drawable.widget_item_background);
		} else {
			convertView.setBackgroundResource(R.drawable.widget_item_background2);
		}
		return convertView;
	}

	@Override
	public int getCount() {
		if(geoItems != null) {
			return geoItems.size();
		} else if(listItems != null) {
			return listItems.length;
		} return 0;
	}

	@Override
	public Object getItem(int position) {
		if(geoItems != null) {
			return geoItems.getItem(position);
		} else if(listItems != null) {
			return listItems[position];
		} return null;
		
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private View handleGeo(int position, View geoView, ViewGroup parent) {
		
		if(geoView == null) {
				geoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.locationlist_item, parent, false);
				geoView.setTag((TextView) geoView.findViewById(R.id.list_txt));
				((TextView) geoView.getTag()).setText(position+1 + ": " +
						geoItems.getItem(position).getTitle());
		}
		((TextView) geoView.getTag()).setText(position+1 + ": " +
					geoItems.getItem(position).getTitle());
		return geoView;
	}
	
	public static View getFeedView(PicItem[] items, boolean feature, int position, View atomView, ViewGroup parent) {
		//can use this method in the view factories that serve as adapters to remote views
		if(atomView == null) {
			if(feature) {
				Log.d(TAG, "feature_item+");
				atomView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feature_item, parent, false);
			} else {
				atomView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
			}
			atomView.setTag(R.id.text1, atomView.findViewById(R.id.text1));
			atomView.setTag(R.id.text2, atomView.findViewById(R.id.text2));
			atomView.setTag(R.id.list_image, atomView.findViewById(R.id.list_image));
		}
		((TextView) atomView.getTag(R.id.text1)).setText(items[position].title);
		if(items[position].photo != null) {
			((ImageView)atomView.getTag(R.id.list_image)).setImageBitmap(items[position].photo);
		} else {
			((ImageView)atomView.getTag(R.id.list_image)).setImageResource(R.drawable.icon);
		}
		((TextView) atomView.getTag(R.id.text2)).setText(items[position].summary);
		return atomView;
	}
}