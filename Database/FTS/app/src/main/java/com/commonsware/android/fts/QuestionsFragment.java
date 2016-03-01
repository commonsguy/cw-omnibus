/***
  Copyright (c) 2013-2014 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.fts;

import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import de.greenrobot.event.EventBus;

public class QuestionsFragment extends ListFragment
    implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
  private static final String STATE_QUERY="q";
  private SearchView sv=null;
  private String initialQuery=null;
  private String lastQuery=null;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);

    setHasOptionsMenu(true);

    if (state!=null) {
      initialQuery=state.getString(STATE_QUERY);
      lastQuery=initialQuery;
    }
  }

  @Override
  public void onViewCreated(View view, Bundle state) {
    super.onViewCreated(view, state);

    SimpleCursorAdapter adapter=
      new SimpleCursorAdapter(getActivity(),
                              R.layout.row,
                              null,
                              new String[] {
                                  DatabaseHelper.TITLE,
                                  DatabaseHelper.PROFILE_IMAGE
                              },
                              new int[] { R.id.title, R.id.icon },
                              0);

    adapter.setViewBinder(new QuestionBinder());
    setListAdapter(adapter);
  }

  @Override
  public void onResume() {
    super.onResume();

    EventBus.getDefault().registerSticky(this);
  }

  @Override
  public void onPause() {
    EventBus.getDefault().unregister(this);

    super.onPause();
  }

  @Override
  public void onSaveInstanceState(Bundle state) {
    super.onSaveInstanceState(state);

    if (!sv.isIconified()) {
      state.putString(STATE_QUERY, sv.getQuery().toString());
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);

    configureSearchView(menu);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    SimpleCursorAdapter adapter=(SimpleCursorAdapter)getListAdapter();
    Cursor row=(Cursor)adapter.getItem(position);
    int link=row.getColumnIndex(DatabaseHelper.LINK);

    EventBus.getDefault().post(new QuestionClickedEvent(row.getString(link)));
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    return(false);
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    doSearch(query);

    return(true);
  }

  @Override
  public boolean onMenuItemActionExpand(MenuItem item) {
    return(true);
  }

  @Override
  public boolean onMenuItemActionCollapse(MenuItem item) {
    clearSearch();

    return(true);
  }

  public void onEventMainThread(ModelLoadedEvent event) {
    ((SimpleCursorAdapter)getListAdapter()).changeCursor(event.model);

    if (sv!=null) {
      sv.setEnabled(true);
    }
  }

  private void configureSearchView(Menu menu) {
    MenuItem search=menu.findItem(R.id.search);

    search.setOnActionExpandListener(this);
    sv=(SearchView)search.getActionView();
    sv.setOnQueryTextListener(this);
    sv.setSubmitButtonEnabled(true);
    sv.setIconifiedByDefault(true);

    if (initialQuery != null) {
      sv.setIconified(false);
      search.expandActionView();
      sv.setQuery(initialQuery, true);
    }
  }

  private void doSearch(String match) {
    if (!match.equals(lastQuery)) {
      lastQuery=match;

      if (sv != null) {
        sv.setEnabled(false);
      }

      EventBus.getDefault().post(new SearchRequestedEvent(match));
    }
  }

  private void clearSearch() {
    if (lastQuery!=null) {
      lastQuery=null;

      sv.setEnabled(false);
      EventBus.getDefault().post(new SearchRequestedEvent(null));
    }
  }

  private class QuestionBinder implements SimpleCursorAdapter.ViewBinder {
    int size;

    QuestionBinder() {
      size=getActivity()
              .getResources()
              .getDimensionPixelSize(R.dimen.icon);
    }

    @Override
    public boolean setViewValue (View view, Cursor cursor, int columnIndex) {
      switch (view.getId()) {
        case R.id.title:
          ((TextView)view).setText(Html.fromHtml(cursor.getString(columnIndex)));

          return(true);

        case R.id.icon:
          Picasso.with(getActivity()).load(cursor.getString(columnIndex))
              .resize(size, size).centerCrop()
              .placeholder(R.drawable.owner_placeholder)
              .error(R.drawable.owner_error).into((ImageView)view);

          return(true);
      }

      return(false);
    }
  }
}
