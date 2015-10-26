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

package com.commonsware.andprojector;

import android.app.ListActivity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import com.commonsware.android.webserver.WebServerService;
import de.greenrobot.event.EventBus;

public class MainActivity extends ListActivity {
  private static final int REQUEST_SCREENSHOT=59706;
  private MenuItem start, stop;
  private MediaProjectionManager mgr;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Window window=getWindow();

    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    window.setStatusBarColor(
      getResources().getColor(R.color.primary_dark));

    mgr=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
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
    if (item.getItemId()==R.id.start) {
      startActivityForResult(mgr.createScreenCaptureIntent(),
          REQUEST_SCREENSHOT);
    }
    else {
      stopService(new Intent(this, ProjectorService.class));
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (requestCode==REQUEST_SCREENSHOT) {
      if (resultCode==RESULT_OK) {
        Intent i=
            new Intent(this, ProjectorService.class)
                .putExtra(ProjectorService.EXTRA_RESULT_CODE,
                  resultCode)
                .putExtra(ProjectorService.EXTRA_RESULT_INTENT,
                  data);

        startService(i);
      }
    }
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
