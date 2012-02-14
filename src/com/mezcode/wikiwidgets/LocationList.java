package com.mezcode.wikiwidgets;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.maps.OverlayItem;
import com.mezcode.wikiwidgets.widgets.BaseStackProvider;

public class LocationList extends ListFragment {
	//Keeping it simple
	//Fragment to display a list of the geo tagged pages on the map view

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
        final Context ctx = getActivity().getApplicationContext();
        Toast.makeText(ctx, getString(R.string.load_msg), Toast.LENGTH_SHORT).show();
        final Intent goBack = new Intent(ctx, WikiWidgetsActivity.class);
        OverlayItem tmp = (OverlayItem) getListAdapter().getItem(position);
        goBack.putExtra(BaseStackProvider.URL_TAG, tmp.getSnippet());
        startActivity(goBack);

    }
    
}