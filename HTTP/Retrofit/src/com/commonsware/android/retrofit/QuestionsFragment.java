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

package com.commonsware.android.retrofit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class QuestionsFragment extends
		ContractListFragment<QuestionsFragment.Contract> implements
		Callback<SOQuestions>
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View result = super.onCreateView(inflater, container,
				savedInstanceState);

		setRetainInstance(true);

		RestAdapter restAdapter = new RestAdapter.Builder()
		.setServer( "https://api.stackexchange.com").build();
		
		StackOverflowInterface so = restAdapter.create(StackOverflowInterface.class);

		/**
		 * so pulls 
		 * https://api.stackexchange.com/2.1/questions?order=desc&sort=creation&site=stackoverflow
		 * tagged as javascript
		 */
		so.questions("javascript", this);

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
		setListAdapter(new ItemsAdapter( getActivity(), questions.items));
	}

	

	interface Contract
	{
		void showItem(Item item);
	}
}
