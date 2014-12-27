/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.tilemode;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockActivity;

public class MainActivity extends SherlockActivity implements TabListener
{
	private static final int[] TABS = { R.string._default, R.string.clamp,
			R.string.repeat, R.string.mirror };
	private static final int[] DRAWABLES = { R.drawable._default,
			R.drawable.clamp, R.drawable.repeat, R.drawable.mirror };
	private View widget = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		widget = findViewById(R.id.widget);

		ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		for (int i = 0; i < TABS.length; i++)
		{
			bar.addTab(bar.newTab().setText(getString(TABS[i]))
					.setTabListener(this));
		}
		
		ListView list = (ListView) findViewById(R.id.list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		adapter.add("default");
		adapter.add("clamp");
		adapter.add("repeat");
		adapter.add("mirror");
		list.setAdapter(adapter);
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft)
	{
		widget.setBackgroundResource(DRAWABLES[tab.getPosition()]);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft)
	{
		// no-op
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft)
	{
		// no-op
	}
}
