/***
  Copyright (c) 2008-2016 CommonsWare, LLC
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

package com.commonsware.android.multiwindow;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends ListActivity {
  private EventAdapter adapter;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);

    getListView().setTranscriptMode(
      ListView.TRANSCRIPT_MODE_NORMAL);
    adapter=new EventAdapter();
    setListAdapter(adapter);

    adapter.add(new Event("onCreate()"));
  }

  @Override
  protected void onPause() {
    super.onPause();

    adapter.add(new Event("onPause()"));
  }

  @Override
  protected void onRestart() {
    super.onRestart();

    adapter.add(new Event("onRestart()"));
  }

  @Override
  protected void onResume() {
    super.onResume();

    adapter.add(new Event("onResume()"));
  }

  @Override
  protected void onStart() {
    super.onStart();

    adapter.add(new Event("onStart()"));
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    adapter.add(new Event("onSaveInstanceState()"));
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);

    adapter.add(new Event("onRestoreInstanceState()"));
  }

  @Override
  protected void onStop() {
    super.onStop();

    adapter.add(new Event("onStop()"));
  }

  @Override
  public void onUserInteraction() {
    super.onUserInteraction();

    adapter.add(new Event("onUserInteraction()"));
  }

  @Override
  protected void onUserLeaveHint() {
    super.onUserLeaveHint();

    adapter.add(new Event("onUserLeaveHint()"));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    adapter.add(new Event("onDestroy()"));
  }

  private class EventAdapter extends ArrayAdapter<Event> {
    private final DateFormat formatter;

    EventAdapter() {
      super(MainActivity.this, R.layout.row, R.id.message,
        new ArrayList<Event>());

      formatter=DateFormat.getTimeInstance();
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
      View row=super.getView(position, convertView, parent);
      TextView timestamp=(TextView)row.findViewById(R.id.timestamp);
      Event event=getItem(position);

      timestamp.setText(formatter.format(new Date(event.timestamp)));

      return(row);
    }
  }
}
