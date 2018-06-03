/***
  Copyright (c) 2008-2014 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.constants;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

public class ConstantsFragment extends ListFragment implements OnClickListener {
  private static final String[] PROJECTION=new String[] {
      Provider.Constants._ID, Provider.Constants.TITLE,
      Provider.Constants.VALUE };
  private Cursor current;
  private AsyncTask task=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setHasOptionsMenu(true);
    setRetainInstance(true);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    SimpleCursorAdapter adapter=
        new SimpleCursorAdapter(getActivity(), R.layout.row,
            current, new String[] {
            DatabaseHelper.TITLE,
            DatabaseHelper.VALUE },
            new int[] { R.id.title, R.id.value },
            0);

    setListAdapter(adapter);

    if (current==null) {
      task=new LoadCursorTask(getActivity()).execute();
    }
  }

  @Override
  public void onDestroy() {
    if (task!=null) {
      task.cancel(false);
    }

    ((CursorAdapter)getListAdapter()).getCursor().close();

    super.onDestroy();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.add) {
      add();
      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {
    ContentValues values=new ContentValues(2);
    AlertDialog dlg=(AlertDialog)dialog;
    EditText title=(EditText)dlg.findViewById(R.id.title);
    EditText value=(EditText)dlg.findViewById(R.id.value);

    values.put(DatabaseHelper.TITLE, title.getText().toString());
    values.put(DatabaseHelper.VALUE, value.getText().toString());

    task=new InsertTask(getActivity()).execute(values);
  }

  private void add() {
    LayoutInflater inflater=getActivity().getLayoutInflater();
    View addView=inflater.inflate(R.layout.add_edit, null);
    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

    builder.setTitle(R.string.add_title).setView(addView)
        .setPositiveButton(R.string.ok, this)
        .setNegativeButton(R.string.cancel, null).show();
  }

  abstract private class BaseTask<T> extends AsyncTask<T, Void, Cursor> {
    final ContentResolver resolver;

    BaseTask(Context ctxt) {
      super();

      resolver=ctxt.getContentResolver();
    }

    @Override
    public void onPostExecute(Cursor result) {
      ((CursorAdapter)getListAdapter()).changeCursor(result);
      current=result;
      task=null;
    }

    protected Cursor doQuery() {
      Cursor result=resolver.query(Provider.Constants.CONTENT_URI,
          PROJECTION, null, null, null);

      result.getCount();

      return(result);
    }
  }

  private class LoadCursorTask extends BaseTask<Void> {
    LoadCursorTask(Context ctxt) {
      super(ctxt);
    }

    @Override
    protected Cursor doInBackground(Void... params) {
      return(doQuery());
    }
  }

  private class InsertTask extends BaseTask<ContentValues> {
    InsertTask(Context ctxt) {
      super(ctxt);
    }

    @Override
    protected Cursor doInBackground(ContentValues... values) {
      resolver.insert(Provider.Constants.CONTENT_URI, values[0]);

      return(doQuery());
    }
  }
}