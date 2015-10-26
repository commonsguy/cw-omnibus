/***
 Copyright (c) 2015 CommonsWare, LLC
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

package com.commonsware.android.webserver.websockets;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.greenrobot.event.EventBus;

public class MainActivity extends ListActivity {
  private MenuItem start, stop;

  @Override
  protected void onResume() {
    super.onResume();

    EventBus.getDefault().registerSticky(this);
  }

  @Override
  protected void onPause() {
    EventBus.getDefault().unregister(this);

    super.onPause();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    start=menu.findItem(R.id.start);
    stop=menu.findItem(R.id.stop);

    WebServerService.ServerStartedEvent event=
      EventBus.getDefault().getStickyEvent(WebServerService.ServerStartedEvent.class);

    if (event!=null) {
      handleStartEvent(event);
    }

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent i=new Intent(this, WebServerService.class);

    if (item.getItemId()==R.id.start) {
      startService(i);
    }
    else {
      stopService(i);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    startActivity(new Intent(Intent.ACTION_VIEW,
      Uri.parse(getListAdapter().getItem(position).toString())));
  }

  public void onEventMainThread(WebServerService.ServerStartedEvent event) {
    if (start!=null) {
      handleStartEvent(event);
    }
  }

  public void onEventMainThread(WebServerService.ServerStoppedEvent event) {
    if (start!=null) {
      start.setVisible(true);
      stop.setVisible(false);
      setListAdapter(null);
    }
  }

  private void handleStartEvent(WebServerService.ServerStartedEvent event) {
    start.setVisible(false);
    stop.setVisible(true);

    setListAdapter(new ArrayAdapter<String>(this,
      android.R.layout.simple_list_item_1, event.getUrls()));
  }
}
