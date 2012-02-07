package com.mezcode;

import android.graphics.Bitmap;
import android.text.Html;

public class PicItem {

	//private static final String TAG = "PicItem";
	String wikipediaUrl;
	String title;
	String summary;
	Bitmap photo;
	private StringBuilder sb = new StringBuilder();

	public String getWikipediaUrl() {
		return wikipediaUrl;
	}

	private int index;
	public void setWikipediaUrl(String wikipediaUrl) {
		//Log.d(TAG, "link item is " + wikipediaUrl);
		if(!wikipediaUrl.contains("http://")) {
			sb.append("http://").append(wikipediaUrl);
		} else {
			sb.append(wikipediaUrl);
			index = sb.indexOf("http");
			if(index > 0) {
				sb.delete(0, index);
			}
		}
		
		index = sb.indexOf(".m.");
		if(index == -1) {
			index = sb.indexOf(".");
			//Log.d(TAG, "dot index is " + index);
			sb.insert(index, ".m");
		}
		
		/*if(sb.indexOf("featured_article") > 0) {
			Log.d(TAG, "article url is " + sb.toString());
		}*/
		this.wikipediaUrl = sb.toString();
		sb.setLength(0);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		sb.append(title);
		sb.delete(0, sb.indexOf(": ") +1);
		this.title = sb.toString();
		sb.setLength(0);
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		//Log.d(TAG, summary);
		sb.append((Html.fromHtml(summary)).toString());
		while(!Character.isLetter(sb.charAt(0))) {
			sb.deleteCharAt(0);
		}
		this.summary = sb.toString();
		//Log.d(TAG, "summary:" + this.summary);
		sb.setLength(0);
		//setPhoto(summary, sum_ctx);
	}
	
	public Bitmap getPhoto() {
		return photo;
	}
	
	/*public void setPhoto(String sumString, Context ctx) {
		//Log.d(TAG, "setPhoto");
		if(sumString == null) {
			//if the feed does not contain any src then return null
			Log.i(TAG, "no image in this description");
			this.photo = null;
			return;
		}
		try {
			this.photo = (Bitmap) new NetworkTask().execute(ctx, NetworkTask.PHOTO, sumString).get();
		} catch (InterruptedException e) {
			Log.e(TAG, "async Task exception");
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (ExecutionException e) {
			Log.e(TAG, "async Task exception");
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}*/

	
	public void setPhoto(Bitmap pic) {
		this.photo = pic;
	}



}
