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

package com.commonsware.android.passwordbox;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;

public class RosterFragment extends SherlockListFragment implements
		LoaderManager.LoaderCallbacks<Cursor>
{
	private SQLiteCursorLoader loader = null;
	private DatabaseHelper db = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		setListAdapter(new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_list_item_1, null,
				new String[] { DatabaseHelper.TITLE },
				new int[] { android.R.id.text1 }, 0));

		db = new DatabaseHelper(getActivity());
		getLoaderManager().initLoader(0, null, this);

		Loader<Cursor> genericCastsSuck = getLoaderManager().getLoader(0);

		loader = (SQLiteCursorLoader) genericCastsSuck;

		setHasOptionsMenu(true);
	}
	
	private CursorAdapter getAdapter()
	{
		return ((CursorAdapter) getListAdapter());
	}
	
	private Cursor getCursor()
	{
		return getAdapter().getCursor();
	}

	@Override
	public void onDestroy()
	{
		Cursor c = getCursor();

		if (c != null)
		{
			c.close();
		}

		super.onDestroy();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.roster, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.add)
		{
			getActivityContract().showPassphrase();

			return (true);
		}

		return (super.onOptionsItemSelected(item));
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args)
	{
		loader = db.buildSelectAllLoader(getActivity());

		return (loader);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
	{
		getAdapter().changeCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
		getAdapter().changeCursor(null);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		Cursor c = ((CursorAdapter) getListAdapter()).getCursor();

		c.moveToPosition(position);

		getActivityContract().showPassphrase(
				c.getInt(DatabaseHelper.SELECT_ALL_ID),
				c.getString(DatabaseHelper.SELECT_ALL_TITLE),
				c.getString(DatabaseHelper.SELECT_ALL_PASSPHRASE));
	}

	void savePassphrase(int id, String title, String passphrase)
	{
		ContentValues values = new ContentValues(2);

		values.put(DatabaseHelper.TITLE, title);
		values.put(DatabaseHelper.PASSPHRASE, passphrase);

		if (id == -1)
		{
			loader.insert(DatabaseHelper.TABLE, DatabaseHelper.TITLE, values);
		}
		else
		{
			String[] args = { String.valueOf(id) };

			loader.update(DatabaseHelper.TABLE, values, DatabaseHelper.ID
					+ "=?", args);
		}
	}

	private MainActivity getActivityContract()
	{
		return ((MainActivity) getActivity());
	}
}