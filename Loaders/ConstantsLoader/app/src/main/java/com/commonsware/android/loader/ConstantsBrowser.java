/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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
   
package com.commonsware.android.loader;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ConstantsBrowser extends RecyclerViewActivity
  implements LoaderManager.LoaderCallbacks<Cursor> {
  private static final int ADD_ID = Menu.FIRST+1;
  private static final int DELETE_ID = Menu.FIRST+3;
  private static final String[] PROJECTION = new String[] {
      Provider.Constants._ID, Provider.Constants.TITLE,
      Provider.Constants.VALUE};
  private SimpleCursorAdapter adapter=null;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    adapter=new SimpleCursorAdapter(this,
                          R.layout.row, null,
                          new String[] {Provider.Constants.TITLE,
                                          Provider.Constants.VALUE},
                          new int[] {R.id.title, R.id.value});
    
    setListAdapter(adapter);
    registerForContextMenu(getListView());
    getSupportLoaderManager().initLoader(0, null, this);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(Menu.NONE, ADD_ID, Menu.NONE, "Add")
        .setIcon(R.drawable.add)
        .setAlphabeticShortcut('a');

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

  public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
    return(new CursorLoader(this, Provider.Constants.CONTENT_URI,
                            PROJECTION, null, null, null));
  }

  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    adapter.swapCursor(cursor);
  }

  public void onLoaderReset(Loader<Cursor> loader) {
    adapter.swapCursor(null);
  }
  
  private void add() {
    LayoutInflater inflater=LayoutInflater.from(this);
    View addView=inflater.inflate(R.layout.add_edit, null);
    final DialogWrapper wrapper=new DialogWrapper(addView);
    
    new AlertDialog.Builder(this)
      .setTitle(R.string.add_title)
      .setView(addView)
      .setPositiveButton(R.string.ok,
                          new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog,
                              int whichButton) {
          processAdd(wrapper);
        }
      })
      .setNegativeButton(R.string.cancel,
                          new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog,
                              int whichButton) {
          // ignore, just dismiss
        }
      })
      .show();
  }

  private void processAdd(DialogWrapper wrapper) {
    ContentValues values=new ContentValues(2);
    
    values.put(Provider.Constants.TITLE, wrapper.getTitle());
    values.put(Provider.Constants.VALUE, wrapper.getValue());
    
    getContentResolver().insert(Provider.Constants.CONTENT_URI,
                                  values);
  }

  class DialogWrapper {
    EditText titleField=null;
    EditText valueField;
    View base;
    
    DialogWrapper(View base) {
      this.base=base;
      valueField=base.findViewById(R.id.value);
    }
    
    String getTitle() {
      return(getTitleField().getText().toString());
    }
    
    float getValue() {
      return(Float.valueOf(getValueField().getText().toString()));
    }
    
    private EditText getTitleField() {
      if (titleField==null) {
        titleField=base.findViewById(R.id.title);
      }
      
      return(titleField);
    }
    
    private EditText getValueField() {
      if (valueField==null) {
        valueField=base.findViewById(R.id.value);
      }
      
      return(valueField);
    }
  }
}