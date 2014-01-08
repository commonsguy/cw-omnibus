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

package com.commonsware.android.eu4you;

import info.juanmendez.android.utils.Trace;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class DetailsActivity extends SherlockFragmentActivity
{
	public static final String EXTRA_URL = "com.commonsware.android.eu4you.EXTRA_URL";
	private String url = null;
	private DetailsFragment details = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		details = (DetailsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.details);

		if (details == null)
		{
			details = new DetailsFragment();

			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, details).commit();
		}

		url = getIntent().getStringExtra(EXTRA_URL);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		Trace.warn( "selected url :" + url, this );
		details.loadUrl(url);
	}
}
