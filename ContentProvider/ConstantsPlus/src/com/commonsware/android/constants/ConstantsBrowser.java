/***
  Copyright (c) 2008-2014 CommonsWare, LLC
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

package com.commonsware.android.constants;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

public class ConstantsBrowser extends ListActivity implements OnClickListener {
  private static final int ADD_ID=Menu.FIRST + 1;
  private static final String[] PROJECTION=new String[] {
      Provider.Constants._ID, Provider.Constants.TITLE,
      Provider.Constants.VALUE };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    new LoadCursorTask().execute();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(Menu.NONE, ADD_ID, Menu.NONE, "Add")
        .setIcon(R.drawable.add).setAlphabeticShortcut('a');

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case ADD_ID:
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

    new InsertTask().execute(values);
  }

  private void add() {
    View addView=getLayoutInflater().inflate(R.layout.add_edit, null);
    AlertDialog.Builder builder=new AlertDialog.Builder(this);

    builder.setTitle(R.string.add_title).setView(addView)
           .setPositiveButton(R.string.ok, this)
           .setNegativeButton(R.string.cancel, null).show();
  }

  private Cursor doQuery() {
    return(getContentResolver().query(Provider.Constants.CONTENT_URI,
                                      PROJECTION, null, null, null));
  }

  private class LoadCursorTask extends AsyncTask<Void, Void, Void> {
    private Cursor constantsCursor=null;

    @Override
    protected Void doInBackground(Void... params) {
      constantsCursor=doQuery();
      constantsCursor.getCount();

      return(null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
    @Override
    public void onPostExecute(Void arg0) {
      SimpleCursorAdapter adapter;

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        adapter=
            new SimpleCursorAdapter(
                                    ConstantsBrowser.this,
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
                                    ConstantsBrowser.this,
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
      getContentResolver().insert(Provider.Constants.CONTENT_URI, values[0]);

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