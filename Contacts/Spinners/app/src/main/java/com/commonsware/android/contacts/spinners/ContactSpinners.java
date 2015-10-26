/***
  Copyright (c) 2008-2015 CommonsWare, LLC
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

package com.commonsware.android.contacts.spinners;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class ContactSpinners extends ListActivity implements
    LoaderManager.LoaderCallbacks<Cursor>,
    AdapterView.OnItemSelectedListener {
  private static final int LOADER_NAMES=0;
  private static final int LOADER_NAMES_NUMBERS=1;
  private static final String[] PROJECTION_NAMES=new String[] {
      ContactsContract.Contacts._ID,
      ContactsContract.Contacts.DISPLAY_NAME,
  };
  private static final String[] PROJECTION_NUMBERS=new String[] {
      ContactsContract.Contacts._ID,
      ContactsContract.Contacts.DISPLAY_NAME,
      ContactsContract.CommonDataKinds.Phone.NUMBER
  };
  private static final String[] PROJECTION_EMAILS=new String[] {
      ContactsContract.Contacts._ID,
      ContactsContract.Contacts.DISPLAY_NAME,
      ContactsContract.CommonDataKinds.Email.DATA
  };
  private static final String[] COLUMNS_NAMES=new String[] {
    ContactsContract.Contacts.DISPLAY_NAME
  };
  private static final String[] COLUMNS_NUMBERS=new String[] {
      ContactsContract.Contacts.DISPLAY_NAME,
      ContactsContract.CommonDataKinds.Phone.NUMBER
  };
  private static final String[] COLUMNS_EMAILS=new String[] {
      ContactsContract.Contacts.DISPLAY_NAME,
      ContactsContract.CommonDataKinds.Email.DATA
  };
  private static final int[] VIEWS_ONE=new int[] {
      android.R.id.text1
  };
  private static final int[] VIEWS_TWO=new int[] {
      android.R.id.text1,
      android.R.id.text2
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    Spinner spin=(Spinner)findViewById(R.id.spinner);
    spin.setOnItemSelectedListener(this);
    
    ArrayAdapter<String> aa=new ArrayAdapter<String>(this,
                              android.R.layout.simple_spinner_item,
                              getResources().getStringArray(R.array.options));
    
    aa.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);
    spin.setAdapter(aa);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
    String[] projection;
    Uri uri;

    switch (loaderId) {
      case LOADER_NAMES:
        projection=PROJECTION_NAMES;
        uri=ContactsContract.Contacts.CONTENT_URI;
        break;

      case LOADER_NAMES_NUMBERS:
        projection=PROJECTION_NUMBERS;
        uri=ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        break;

      default:
        projection=PROJECTION_EMAILS;
        uri=ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        break;
    }

    return(new CursorLoader(this, uri, projection, null, null,
                              ContactsContract.Contacts.DISPLAY_NAME));
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
    String[] columns;
    int layoutId;
    int[] views;

    switch(loader.getId()) {
      case LOADER_NAMES:
        columns=COLUMNS_NAMES;
        layoutId=android.R.layout.simple_list_item_1;
        views=VIEWS_ONE;
        break;

      case LOADER_NAMES_NUMBERS:
        columns=COLUMNS_NUMBERS;
        layoutId=android.R.layout.simple_list_item_2;
        views=VIEWS_TWO;
        break;

      default:
        columns=COLUMNS_EMAILS;
        layoutId=android.R.layout.simple_list_item_2;
        views=VIEWS_TWO;
        break;
    }

    setListAdapter(new SimpleCursorAdapter(this, layoutId, c,
                                            columns, views, 0));
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    setListAdapter(null);
  }

  @Override
  public void onItemSelected(AdapterView<?> parent,
                                View v, int position, long id) {
    getLoaderManager().initLoader(position, null, this);
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // ignore
  }
}