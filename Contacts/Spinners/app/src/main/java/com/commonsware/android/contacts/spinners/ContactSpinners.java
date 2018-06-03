/***
 Copyright (c) 2008-2015 CommonsWare, LLC
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

package com.commonsware.android.contacts.spinners;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ContactSpinners extends AbstractPermissionActivity implements
  LoaderManager.LoaderCallbacks<Cursor>,
  AdapterView.OnItemSelectedListener {
  private static final String[] PERMS={Manifest.permission.READ_CONTACTS};
  private static final int LOADER_NAMES=0;
  private static final int LOADER_NAMES_NUMBERS=1;
  private static final String[] PROJECTION_NAMES=new String[]{
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.DISPLAY_NAME,
  };
  private static final String[] PROJECTION_NUMBERS=new String[]{
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.DISPLAY_NAME,
    ContactsContract.CommonDataKinds.Phone.NUMBER
  };
  private static final String[] PROJECTION_EMAILS=new String[]{
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.DISPLAY_NAME,
    ContactsContract.CommonDataKinds.Email.DATA
  };
  private static final String[] COLUMNS_NAMES=new String[]{
    ContactsContract.Contacts.DISPLAY_NAME
  };
  private static final String[] COLUMNS_NUMBERS=new String[]{
    ContactsContract.Contacts.DISPLAY_NAME,
    ContactsContract.CommonDataKinds.Phone.NUMBER
  };
  private static final String[] COLUMNS_EMAILS=new String[]{
    ContactsContract.Contacts.DISPLAY_NAME,
    ContactsContract.CommonDataKinds.Email.DATA
  };
  private RVCursorAdapter adapter;

  @Override
  protected String[] getDesiredPermissions() {
    return (PERMS);
  }

  @Override
  protected void onPermissionDenied() {
    Toast
      .makeText(this, R.string.msg_no_perm, Toast.LENGTH_LONG)
      .show();
    finish();
  }

  @Override
  public void onReady() {
    setContentView(R.layout.main);

    Spinner spin=findViewById(R.id.spinner);
    spin.setOnItemSelectedListener(this);

    ArrayAdapter<String> aa=new ArrayAdapter<String>(this,
      android.R.layout.simple_spinner_item,
      getResources().getStringArray(R.array.options));

    aa.setDropDownViewResource(
      android.R.layout.simple_spinner_dropdown_item);
    spin.setAdapter(aa);

    RecyclerView rv=findViewById(android.R.id.list);

    rv.setLayoutManager(new LinearLayoutManager(this));
    rv.addItemDecoration(new DividerItemDecoration(this,
      DividerItemDecoration.VERTICAL));
    adapter=new RVCursorAdapter(getLayoutInflater());
    rv.setAdapter(adapter);
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

    return new CursorLoader(this, uri, projection, null, null,
      ContactsContract.Contacts.DISPLAY_NAME);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
    String[] columns;

    switch (loader.getId()) {
      case LOADER_NAMES:
        columns=COLUMNS_NAMES;
        break;

      case LOADER_NAMES_NUMBERS:
        columns=COLUMNS_NUMBERS;
        break;

      default:
        columns=COLUMNS_EMAILS;
        break;
    }

    adapter.changeCursor(c, columns);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    adapter.clearCursor();
  }

  @Override
  public void onItemSelected(AdapterView<?> parent,
                             View v, int position, long id) {
    getSupportLoaderManager().initLoader(position, null, this);
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // ignore
  }

  private static class RVCursorAdapter extends RecyclerView.Adapter<RowHolder> {
    private Cursor cursor;
    private final LayoutInflater inflater;
    private String[] columns;

    private RVCursorAdapter(LayoutInflater inflater) {
      this.inflater=inflater;
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                        int viewType) {
      View row=
        inflater.inflate(android.R.layout.simple_list_item_2, parent, false);

      return new RowHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder,
                                 int position) {
      cursor.moveToPosition(position);
      holder.bind(cursor, columns);
    }

    @Override
    public int getItemCount() {
      return cursor==null ? 0 : cursor.getCount();
    }

    private void changeCursor(Cursor cursor, String[] columns) {
      if (this.cursor!=null) {
        this.cursor.close();
      }

      this.cursor=cursor;
      this.columns=columns;
      notifyDataSetChanged();
    }

    private void clearCursor() {
      cursor=null;
      notifyDataSetChanged();
    }
  }

  private static class RowHolder extends RecyclerView.ViewHolder {
    private final TextView text1;
    private final TextView text2;

    RowHolder(View itemView) {
      super(itemView);
      text1=itemView.findViewById(android.R.id.text1);
      text2=itemView.findViewById(android.R.id.text2);
    }

    public void bind(Cursor cursor, String[] columns) {
      int index=cursor.getColumnIndex(columns[0]);

      text1.setText(cursor.getString(index));

      if (columns.length==2) {
        index=cursor.getColumnIndex(columns[1]);
        text2.setText(cursor.getString(index));
      }
    }
  }
}