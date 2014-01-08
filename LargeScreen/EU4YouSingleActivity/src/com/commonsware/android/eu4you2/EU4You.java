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

package com.commonsware.android.eu4you2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class EU4You extends SherlockFragmentActivity implements
		CountriesFragment.Contract
{
	private static final String TAG_COUNTRIES = "countries";
	private static final String TAG_DETAILS = "details";
	private CountriesFragment countries = null;
	private DetailsFragment details = null;

	private boolean hasDetailContainer()
	{
		return findViewById(R.id.details) != null ? true : false;
	}
	
	private boolean inContainer( Fragment view, int containerID )
	{
		return view.getId() == containerID;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		countries = (CountriesFragment) getSupportFragmentManager()
				.findFragmentByTag(TAG_COUNTRIES);
		details = (DetailsFragment) getSupportFragmentManager()
				.findFragmentByTag(TAG_DETAILS);

		FragmentManager fm = getSupportFragmentManager();
		if (countries == null)
		{
			countries = new CountriesFragment();
			fm.beginTransaction()
					.add(R.id.mainfrag, countries, TAG_COUNTRIES).commit();
		}

		if (details == null)
		{
			details = new DetailsFragment();

			if (hasDetailContainer())
			{
				fm.beginTransaction()
						.add(R.id.details, details, TAG_DETAILS).commit();
			}
		}
		else
		{
			/**
			 * if details in mainfrag, remove from that container
			 * otherwise, details needs to be removed from details container
			 */
			if (inContainer(details, R.id.mainfrag) && hasDetailContainer() )
			{
				fm.beginTransaction().remove(details).add(R.id.details, details, TAG_DETAILS).commit();
			}
			else
		    if (!hasDetailContainer())
			{
		    	fm.beginTransaction().remove(details).commit();
			}
		}
	}

	@Override
	public void onCountrySelected(Country c)
	{
		String url = getString(c.url);

		details.loadUrl(url);

		//if details is not already within details, then 
		//we are in single pane and need to replace the main container
		if (!inContainer(details, R.id.details) && !hasDetailContainer() )
		{
			FragmentManager fm = getSupportFragmentManager();
			fm.beginTransaction()
					.replace(R.id.mainfrag, details, TAG_DETAILS)
					.addToBackStack(null).commit();
		}
	}

	@Override
	public boolean isPersistentSelection()
	{
		return (details.isVisible());
	}
}
