package com.commonsware.android.retrofit;

import java.util.List;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemsAdapter extends ArrayAdapter<Item>
{
	
	
	ItemsAdapter( Activity activity, List<Item> items)
	{
		super( activity, android.R.layout.simple_list_item_1, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = super.getView(position, convertView, parent);
		TextView title = (TextView) row.findViewById(android.R.id.text1);

		title.setText(Html.fromHtml(getItem(position).title));

		return (row);
	}
}
