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

package com.commonsware.android.debug.webserver;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.commonsware.android.webserver.WebServerService;
import java.util.ArrayList;
import de.greenrobot.event.EventBus;

public class PicassoDiagnosticActivity extends ListActivity {
  private MenuItem record, stop;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

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

    record=menu.findItem(R.id.record);
    stop=menu.findItem(R.id.stop);

    WebServerService.ServerStartedEvent event=
      EventBus.getDefault().getStickyEvent(WebServerService.ServerStartedEvent.class);

    if (event!=null) {
      onEventMainThread(event);
    }

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent i=new Intent(this, PicassoDiagnosticService.class);

    if (item.getItemId()==R.id.record) {
      startService(i);
    }
    else {
      stopService(i);
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    startActivity(new Intent(Intent.ACTION_VIEW,
      Uri.parse(getListAdapter().getItem(position).toString())));
  }

  public void onEventMainThread(WebServerService.ServerStartedEvent event) {
    if (record!=null) {
      record.setVisible(false);
      stop.setVisible(true);

      ArrayList<String> diagUrls=new ArrayList<String>();

      for (String url : event.getUrls()) {
        diagUrls.add(url+"picasso.hbs");
      }

      setListAdapter(new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_1, diagUrls));
    }
  }

  public void onEventMainThread(WebServerService.ServerStoppedEvent event) {
    if (record!=null) {
      record.setVisible(true);
      stop.setVisible(false);
      setListAdapter(null);
    }
  }
}
