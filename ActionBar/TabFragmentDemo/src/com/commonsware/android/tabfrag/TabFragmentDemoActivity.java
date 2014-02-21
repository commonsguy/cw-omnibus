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

package com.commonsware.android.tabfrag;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class TabFragmentDemoActivity extends SherlockFragmentActivity implements
		TabListener
{
	private static final String KEY_MODELS = "models";
	private static final String KEY_POSITION = "position";
	private CharSequence[] models = new CharSequence[10];

	@Override
	public void onCreate(Bundle state)
	{
		super.onCreate(state);

		if (state != null)
		{
			models = state.getCharSequenceArray(KEY_MODELS);
		}

		ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		for (int i = 0; i < 10; i++)
		{
			bar.addTab(bar.newTab().setText("Tab #" + String.valueOf(i + 1))
					.setTabListener(this).setTag(i));
		}

		if (state != null)
		{
			bar.setSelectedNavigationItem(state.getInt(KEY_POSITION));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle state)
	{
		state.putCharSequenceArray(KEY_MODELS, models);
		state.putInt(KEY_POSITION, getSupportActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft)
	{
		int i = ((Integer) tab.getTag()).intValue();

		ft.replace(android.R.id.content,
				EditorFragment.newInstance(i, models[i]));
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft)
	{
		int i = ((Integer) tab.getTag()).intValue();
		EditorFragment frag = (EditorFragment) getSupportFragmentManager()
				.findFragmentById(android.R.id.content);

		if (frag != null)
		{
			models[i] = frag.getText();
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft)
	{
		// unused
	}
}