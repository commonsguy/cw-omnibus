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

package com.commonsware.android.constants;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

public class ConstantsFragment extends ListFragment implements
    DialogInterface.OnClickListener {
  private DatabaseHelper db=null;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setHasOptionsMenu(true);
    setRetainInstance(true);

    db=new DatabaseHelper(getActivity());
    new LoadCursorTask(getActivity().getApplicationContext()).execute();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    ((CursorAdapter)getListAdapter()).getCursor().close();
    db.close();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.add:
        add();
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  private void add() {
    LayoutInflater inflater=getActivity().getLayoutInflater();
    View addView=inflater.inflate(R.layout.add_edit, null);
    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

    builder.setTitle(R.string.add_title).setView(addView)
           .setPositiveButton(R.string.ok, this)
           .setNegativeButton(R.string.cancel, null).show();
  }

  public void onClick(DialogInterface di, int whichButton) {
    ContentValues values=new ContentValues(2);
    AlertDialog dlg=(AlertDialog)di;
    EditText title=(EditText)dlg.findViewById(R.id.title);
    EditText value=(EditText)dlg.findViewById(R.id.value);

    values.put(DatabaseHelper.TITLE, title.getText().toString());
    values.put(DatabaseHelper.VALUE, value.getText().toString());

    new InsertTask().execute(values);
  }

  private Cursor doQuery() {
    return(db.getReadableDatabase().rawQuery("SELECT _id, title, value "
                                                 + "FROM constants ORDER BY title",
                                             null));
  }

  private class LoadCursorTask extends AsyncTask<Void, Void, Void> {
    private Cursor constantsCursor=null;
    private Context ctxt=null;

    LoadCursorTask(Context ctxt) {
      this.ctxt=ctxt;
    }

    @Override
    protected Void doInBackground(Void... params) {
      DatabaseHelper.encrypt(ctxt);
      constantsCursor=doQuery();
      constantsCursor.getCount();

      return(null);
    }

    @TargetApi(11)
    @SuppressWarnings("deprecation")
    @Override
    public void onPostExecute(Void arg0) {
      SimpleCursorAdapter adapter;

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        adapter=
            new SimpleCursorAdapter(
                                    getActivity(),
                                    R.layout.row,
                                    constantsCursor,
                                    new String[] {
                                        DatabaseHelper.TITLE,
                                        DatabaseHelper.VALUE },
                                    new int[] { R.id.title, R.id.value },
                                    0);
      }
      else {
        adapter=
            new SimpleCursorAdapter(
                                    getActivity(),
                                    R.layout.row,
                                    constantsCursor,
                                    new String[] {
                                        DatabaseHelper.TITLE,
                                        DatabaseHelper.VALUE },
                                    new int[] { R.id.title, R.id.value });
      }

      setListAdapter(adapter);
    }
  }

  private class InsertTask extends AsyncTask<ContentValues, Void, Void> {
    private Cursor constantsCursor=null;

    @Override
    protected Void doInBackground(ContentValues... values) {
      db.getWritableDatabase().insert(DatabaseHelper.TABLE,
                                      DatabaseHelper.TITLE, values[0]);

      constantsCursor=doQuery();
      constantsCursor.getCount();

      return(null);
    }

    @Override
    public void onPostExecute(Void arg0) {
      ((CursorAdapter)getListAdapter()).changeCursor(constantsCursor);
    }
  }
}
