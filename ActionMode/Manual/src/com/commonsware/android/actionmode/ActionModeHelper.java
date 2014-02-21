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

package com.commonsware.android.actionmode;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ActionModeHelper implements ActionMode.Callback,
		AdapterView.OnItemLongClickListener
{
	ActionModeDemo activity;
	ActionMode activeMode;
	ListView list;

	ActionModeHelper(final ActionModeDemo activity, ListView list)
	{
		this.activity = activity;
		this.list = list;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> view, View row, int position,
			long id)
	{
		list.clearChoices();
		list.setItemChecked(position, true);

		if (activeMode == null)
		{
			activeMode = activity.startActionMode(this);
		}

		return (true);
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu)
	{
		MenuInflater inflater = activity.getSupportMenuInflater();

		inflater.inflate(R.menu.context, menu);
		mode.setTitle(R.string.context_title);

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
		boolean result = activity.performAction(item.getItemId(),
				list.getCheckedItemPosition());

		if (item.getItemId() == R.id.remove)
		{
			activeMode.finish();
		}

		return (result);
	}

	/**
	 * However, for reasons that are not yet clear,clear
	 * Choices()does not update the UI when called from onDestroyActionMode()unless you also callrequestLayout().
	 */
	@Override
	public void onDestroyActionMode(ActionMode mode)
	{
		activeMode = null;
		list.clearChoices();
		list.requestLayout();
	}
}