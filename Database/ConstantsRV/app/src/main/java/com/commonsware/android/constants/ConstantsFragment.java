/***
  Copyright (c) 2008-2014 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.constants;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class ConstantsFragment extends Fragment implements
    DialogInterface.OnClickListener {
  private DatabaseHelper db;
  private Cursor current;
  private AsyncTask task;
  private RVCursorAdapter adapter;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setHasOptionsMenu(true);
    setRetainInstance(true);

    adapter=new RVCursorAdapter(getLayoutInflater());
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.main, container, false);
  }

  @Override
  public void onViewCreated(View v, Bundle savedInstanceState) {
    super.onViewCreated(v, savedInstanceState);

    RecyclerView rv=v.findViewById(android.R.id.list);

    rv.setLayoutManager(new LinearLayoutManager(getActivity()));
    rv.addItemDecoration(new DividerItemDecoration(getActivity(),
      DividerItemDecoration.VERTICAL));
    rv.setAdapter(adapter);

    if (current==null) {
      db=new DatabaseHelper(getActivity());
      task=new LoadCursorTask().execute();
    }
    else {
      adapter.changeCursor(current);
    }
  }

  @Override
  public void onDestroy() {
    if (task!=null) {
      task.cancel(false);
    }

    current.close();
    db.close();

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
    EditText title=dlg.findViewById(R.id.title);
    EditText value=dlg.findViewById(R.id.value);

    values.put(DatabaseHelper.TITLE, title.getText().toString());
    values.put(DatabaseHelper.VALUE, value.getText().toString());

    task=new InsertTask().execute(values);
  }

  abstract private class BaseTask<T> extends AsyncTask<T, Void, Cursor> {
    @Override
    public void onPostExecute(Cursor result) {
      adapter.changeCursor(result);
      current=result;
      task=null;
    }

    Cursor doQuery() {
      Cursor result=db
        .getReadableDatabase()
        .query(DatabaseHelper.TABLE,
            new String[] {DatabaseHelper.TITLE,
                          DatabaseHelper.VALUE},
            null, null, null, null, DatabaseHelper.TITLE);

      result.getCount();

      return(result);
    }
  }

  private class LoadCursorTask extends BaseTask<Void> {
    @Override
    protected Cursor doInBackground(Void... params) {
      return(doQuery());
    }
  }

  private class InsertTask extends BaseTask<ContentValues> {
    @Override
    protected Cursor doInBackground(ContentValues... values) {
      db.getWritableDatabase().insert(DatabaseHelper.TABLE,
                                      DatabaseHelper.TITLE, values[0]);

      return(doQuery());
    }
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
    private final TextView title;
    private final TextView value;

    RowHolder(View itemView) {
      super(itemView);
      title=itemView.findViewById(R.id.title);
      value=itemView.findViewById(R.id.value);
    }

    public void bind(Cursor cursor) {
      title.setText(cursor.getString(0));
      value.setText(String.format("%g", cursor.getDouble(1)));
    }
  }
}
