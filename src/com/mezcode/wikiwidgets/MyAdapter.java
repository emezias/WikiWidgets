package com.mezcode.wikiwidgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mezcode.wikiwidgets.LocationActivity.WikiOverlay;

public class MyAdapter extends BaseAdapter {
	//GeoItem[] listItems;
	WikiOverlay listItems;
	boolean photoLayout;
	
	public MyAdapter(Context ctxt, WikiOverlay overlayArrayList, boolean photo) {
		super();
		listItems = overlayArrayList;
		photoLayout = photo;
	}
	
	public MyAdapter(WikiOverlay overlayArrayList) {
		super();
		listItems = overlayArrayList;
	}
	
	String getUrl(int position) {
		return listItems.getItem(position).getSnippet();
		//return null;//listItems[position].getWikipediaUrl();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			/*if(photoLayout){
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pic_widget_item, parent, false);
			} else {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pic_widget_item2, parent, false);
			}*/
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
			convertView.setTag((TextView) convertView.findViewById(R.id.list_txt));
		}
		((TextView) convertView.getTag()).setText(position+1 + ": " +
				listItems.getItem(position).getTitle());
		/*
		if(position % 2 == 0) {
			convertView.setBackgroundResource(R.drawable.list_txt_bg);
		} else {
			convertView.setBackgroundResource(R.drawable.list_txt_bg2);
		}*/
		return convertView;
	}

	@Override
	public int getCount() {
		if(listItems != null) {
			return listItems.size();
		} else return 0;
	}

	@Override
	public Object getItem(int position) {
		if(listItems != null) {
			return listItems.getItem(position);
		} else return null;
		
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}