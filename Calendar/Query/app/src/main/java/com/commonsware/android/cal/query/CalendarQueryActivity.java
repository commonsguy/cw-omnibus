/***
  Copyright (c) 2011-2012 CommonsWare, LLC
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

package com.commonsware.android.cal.query;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarQueryActivity extends AbstractPermissionActivity implements
    LoaderManager.LoaderCallbacks<Cursor> {
  private static final String[] PERMS={Manifest.permission.READ_CALENDAR};
  private static final String[] PROJECTION=
      new String[] { CalendarContract.Events.TITLE,
          CalendarContract.Events.DTSTART,
          CalendarContract.Events.DTEND };
  private RVCursorAdapter adapter;

  @Override
  protected String[] getDesiredPermissions() {
    return(PERMS);
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
    adapter=new RVCursorAdapter(getLayoutInflater());

    RecyclerView rv=getRecyclerView();

    setLayoutManager(new LinearLayoutManager(this));
    rv.addItemDecoration(new DividerItemDecoration(this,
      DividerItemDecoration.VERTICAL));
    rv.setAdapter(adapter);

    getSupportLoaderManager().initLoader(0, null, this);
  }

  public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
    return(new CursorLoader(this, CalendarContract.Events.CONTENT_URI,
                            PROJECTION, null, null,
                            CalendarContract.Events.DTSTART));
  }

  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    adapter.changeCursor(cursor);
  }

  public void onLoaderReset(Loader<Cursor> loader) {
    adapter.changeCursor(null);
  }

  private static class RVCursorAdapter extends RecyclerView.Adapter<RowHolder> {
    private Cursor cursor;
    private final LayoutInflater inflater;

    private RVCursorAdapter(LayoutInflater inflater) {
      this.inflater=inflater;
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                        int viewType) {
      View row=inflater.inflate(R.layout.row, parent, false);

      return new RowHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder,
                                 int position) {
      cursor.moveToPosition(position);
      holder.bind(cursor);
    }

    @Override
    public int getItemCount() {
      return cursor==null ? 0 : cursor.getCount();
    }

    private void changeCursor(Cursor cursor) {
      if (this.cursor!=null) this.cursor.close();
      this.cursor=cursor;
      notifyDataSetChanged();
    }
  }

  private static class RowHolder extends RecyclerView.ViewHolder {
    private final TextView title, dtstart, dtend;

    RowHolder(View itemView) {
      super(itemView);
      title=itemView.findViewById(R.id.title);
      dtstart=itemView.findViewById(R.id.dtstart);
      dtend=itemView.findViewById(R.id.dtend);
    }

    public void bind(Cursor cursor) {
      title.setText(cursor.getString(0));
      dtstart.setText(getFormattedDate(cursor, 1));
      dtend.setText(getFormattedDate(cursor, 2));
    }

    private String getFormattedDate(Cursor cursor, int column) {
      long time=cursor.getLong(column);

      return
        DateUtils.formatDateTime(dtstart.getContext(), time,
          DateUtils.FORMAT_ABBREV_RELATIVE);
    }
  }
}