/***
  Copyright (c) 2012 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package com.commonsware.android.actionmodemc;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class HCMultiChoiceModeListener implements
		AbsListView.MultiChoiceModeListener
{
	ActionModeDemo activity;
	ActionMode mode;
	ListView list;

	HCMultiChoiceModeListener(ActionModeDemo activity, ListView list)
	{
		this.activity = activity;
		this.list = list;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu)
	{
		MenuInflater inflater = activity.getMenuInflater();

		inflater.inflate(R.menu.context, menu);
		mode.setTitle(R.string.context_title);
		mode.setSubtitle("(1)");
		this.mode = mode;

		return (true);
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu)
	{
		return (false);
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item)
	{
		boolean result = activity.performActions(item);

		updateSubtitle(mode);

		return (result);
	}

	@Override
	public void onDestroyActionMode(ActionMode mode)
	{
		mode = null;
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position,
			long id, boolean checked)
	{
		updateSubtitle(mode);
	}

	private void updateSubtitle(ActionMode mode)
	{
		mode.setSubtitle("(" + list.getCheckedItemCount() + ")");
	}
}