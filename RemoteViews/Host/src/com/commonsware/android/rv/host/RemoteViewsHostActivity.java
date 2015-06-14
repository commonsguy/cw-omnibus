/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.rv.host;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;


public class RemoteViewsHostActivity extends Activity {
  public static final String ACTION_CALL_FOR_PLUGINS=
      "com.commonsware.android.rv.host.CALL_FOR_PLUGINS";
  public static final String ACTION_REGISTER_PLUGIN=
      "com.commonsware.android.rv.host.REGISTER_PLUGIN";
  public static final String ACTION_CALL_FOR_CONTENT=
      "com.commonsware.android.rv.host.CALL_FOR_CONTENT";
  public static final String ACTION_DELIVER_CONTENT=
      "com.commonsware.android.rv.host.DELIVER_CONTENT";
  public static final String EXTRA_COMPONENT="component";
  public static final String EXTRA_CONTENT="content";
  public static final String PERM_ACT_AS_PLUGIN=
      "com.commonsware.android.rv.host.ACT_AS_PLUGIN";
  private ComponentName pluginCN=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
  }

  @Override
  public void onResume() {
    super.onResume();

    IntentFilter pluginFilter=new IntentFilter();

    pluginFilter.addAction(ACTION_REGISTER_PLUGIN);
    pluginFilter.addAction(ACTION_DELIVER_CONTENT);

    registerReceiver(plugin, pluginFilter, PERM_ACT_AS_PLUGIN, null);

    if (pluginCN == null) {
      sendBroadcast(new Intent(ACTION_CALL_FOR_PLUGINS));
    }
  }

  @Override
  public void onPause() {
    unregisterReceiver(plugin);

    super.onPause();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.options, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (R.id.refresh == item.getItemId()) {
      refreshPlugin();
      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  private void refreshPlugin() {
    Intent call=new Intent(ACTION_CALL_FOR_CONTENT);

    call.setComponent(pluginCN);
    sendBroadcast(call);
  }

  private BroadcastReceiver plugin=new BroadcastReceiver() {
    @Override
    public void onReceive(Context ctxt, Intent i) {
      if (ACTION_REGISTER_PLUGIN.equals(i.getAction())) {
        pluginCN=(ComponentName)i.getParcelableExtra(EXTRA_COMPONENT);
      }
      else if (ACTION_DELIVER_CONTENT.equals(i.getAction())) {
        RemoteViews rv=(RemoteViews)i.getParcelableExtra(EXTRA_CONTENT);
        ViewGroup frame=(ViewGroup)findViewById(android.R.id.content);

        frame.removeAllViews();

        View pluginView=rv.apply(RemoteViewsHostActivity.this, frame);

        frame.addView(pluginView);
      }
    }
  };
}