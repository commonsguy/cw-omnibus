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

package com.commonsware.android.shape;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockActivity;

public class MainActivity extends SherlockActivity implements TabListener
{
	private static final int TABS[] = { R.string.solid, R.string.gradient,
			R.string.border, R.string.rounded, R.string.ring, R.string.layered };
	private static final int DRAWABLES[] = { R.drawable.rectangle,
			R.drawable.gradient, R.drawable.border, R.drawable.rounded,
			R.drawable.ring, R.drawable.layered };
	private ImageView image = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		image = (ImageView) findViewById(R.id.image);

		ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		for (int i = 0; i < TABS.length; i++)
		{
			bar.addTab(bar.newTab().setText(getString(TABS[i]))
					.setTabListener(this));
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft)
	{
		image.setImageResource(DRAWABLES[tab.getPosition()]);
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
