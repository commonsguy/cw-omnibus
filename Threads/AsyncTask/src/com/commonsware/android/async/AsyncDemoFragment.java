/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
	
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.android.async;

import java.util.ArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockListFragment;

public class AsyncDemoFragment extends SherlockListFragment
{
	private static final String[] items = { "lorem", "ipsum", "dolor", "sit",
			"amet", "consectetuer", "adipiscing", "elit", "morbi", "vel",
			"ligula", "vitae", "arcu", "aliquet", "mollis", "etiam", "vel",
			"erat", "placerat", "ante", "porttitor", "sodales", "pellentesque",
			"augue", "purus" };
	
	private ArrayList<String> model = null;
	private ArrayAdapter<String> adapter = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		setRetainInstance(true);

		if (model == null)
		{
			model = new ArrayList<String>();
			new AddStringTask().execute();
		}

		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, model);

		getListView().setScrollbarFadingEnabled(false);
		setListAdapter(adapter);
	}

	class AddStringTask extends AsyncTask<Void, String, Void>
	{
		@Override
		protected Void doInBackground(Void... unused)
		{
			for (String item : items)
			{
				publishProgress(item);
				SystemClock.sleep(400);
			}

			return (null);
		}

		
		/**
		 * called at UI Thread
		 */
		@Override
		protected void onProgressUpdate(String... item)
		{
			adapter.add(item[0]);
		}
		
		/**
		 * called at UI Thread
		 */
		@Override
		protected void onPostExecute(Void unused)
		{
			Toast.makeText(getActivity(), R.string.done, Toast.LENGTH_SHORT).show();
		}
	}
}
