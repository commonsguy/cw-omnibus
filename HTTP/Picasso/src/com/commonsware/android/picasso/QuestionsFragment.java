/***
  Copyright (c) 2013 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.android.picasso;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class QuestionsFragment extends
		ContractListFragment<QuestionsFragment.Contract> implements
		Callback<SOQuestions>
{

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View result = super.onCreateView(inflater, container,
				savedInstanceState);

		RestAdapter restAdapter = new RestAdapter.Builder()
		.setServer( "https://api.stackexchange.com").build();
		
		StackOverflowInterface so = restAdapter
				.create(StackOverflowInterface.class);

		so.questions("android", this);

		return (result);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		getContract().showItem(
				((ItemsAdapter) getListAdapter()).getItem(position));
	}

	@Override
	public void failure(RetrofitError exception)
	{
		Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG)
				.show();
		
		Log.e(getClass().getSimpleName(),
				"Exception from Retrofit request to StackOverflow", exception);
	}

	@Override
	public void success(SOQuestions questions, Response response)
	{
		setListAdapter(new ItemsAdapter(questions.items));
	}

	class ItemsAdapter extends ArrayAdapter<Item>
	{
		int size;

		ItemsAdapter(List<Item> items)
		{
			super(getActivity(), R.layout.row, R.id.title, items);

			size = getActivity().getResources().getDimensionPixelSize(
					R.dimen.icon);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View row = super.getView(position, convertView, parent);
			Item item = getItem(position);
			ImageView icon = (ImageView) row.findViewById(R.id.icon);

			Picasso.with(getActivity()).load(item.owner.profileImage)
					.resize(size, size).centerCrop()
					.placeholder(R.drawable.owner_placeholder)
					.error(R.drawable.owner_error).into(icon);

			TextView title = (TextView) row.findViewById(R.id.title);

			title.setText(Html.fromHtml(getItem(position).title));

			return (row);
		}
	}

	interface Contract
	{
		void showItem(Item item);
	}
}