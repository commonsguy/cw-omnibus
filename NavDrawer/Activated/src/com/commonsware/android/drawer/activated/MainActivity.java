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

package com.commonsware.android.drawer.activated;

import android.app.Activity;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity implements OnItemClickListener,
		LoremFragment.Contract, OnBackStackChangedListener
{
	static private final String STATE_CHECKED = "com.commonsware.android.drawer.simple.STATE_CHECKED";
	private DrawerLayout drawerLayout = null;
	private ActionBarDrawerToggle toggle = null;
	private LoremFragment lorem = null;
	private ContentFragment content = null;
	private ListView drawer = null;
	private StuffFragment stuff = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (getFragmentManager().findFragmentById(R.id.content) == null)
		{
			showLorem();
		}

		getFragmentManager().addOnBackStackChangedListener(this);

		drawer = (ListView) findViewById(R.id.drawer);
		drawer.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		String[] rows = getResources().getStringArray(R.array.drawer_rows);

		drawer.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_row,
				rows));

		if (savedInstanceState == null)
		{
			drawer.setItemChecked(0, true); // starting here
		}

		drawer.setOnItemClickListener(this);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		toggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close);
		drawerLayout.setDrawerListener(toggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public void onSaveInstanceState(Bundle state)
	{
		super.onSaveInstanceState(state);

		state.putInt(STATE_CHECKED, drawer.getCheckedItemPosition());
	}

	@Override
	public void onRestoreInstanceState(Bundle state)
	{
		int position = state.getInt(STATE_CHECKED, -1);

		if (position > -1)
		{
			drawer.setItemChecked(position, true);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);

		toggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);

		toggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (toggle.onOptionsItemSelected(item))
		{
			return (true);
		}

		return (super.onOptionsItemSelected(item));
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View row, int position,
			long id)
	{
		if (position == 0)
		{
			showLorem();
		}
		else
		{
			showContent();
		}

		drawerLayout.closeDrawers();
	}

	@Override
	public void onBackStackChanged()
	{
		if (lorem != null && lorem.isVisible())
		{
			drawer.setItemChecked(0, true);
		}
		else
		if (content != null && content.isVisible())
		{
			drawer.setItemChecked(1, true);
		}
		
	}

	@Override
	public void wordClicked()
	{
		int toClear = drawer.getCheckedItemPosition();

		if (toClear >= 0)
		{
			drawer.setItemChecked(toClear, false);
		}

		if (stuff == null)
		{
			stuff = new StuffFragment();
		}

		getFragmentManager().beginTransaction().replace(R.id.content, stuff)
				.addToBackStack(null).commit();
	}

	private void showLorem()
	{
		if (lorem == null)
		{
			lorem = new LoremFragment();
		}

		if (!lorem.isVisible())
		{
			getFragmentManager().popBackStack();
			getFragmentManager().beginTransaction()
					.replace(R.id.content, lorem).commit();
		}
	}

	private void showContent()
	{
		if (content == null)
		{
			content = new ContentFragment();
		}

		if (!content.isVisible())
		{
			getFragmentManager().popBackStack();
			getFragmentManager().beginTransaction()
					.replace(R.id.content, content).commit();
		}
	}
}
