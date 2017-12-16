/***
  Copyright (c) 2015 CommonsWare, LLC
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

package com.commonsware.android.task.pcanary;

import android.app.Activity;
import android.content.Intent;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
  private static final String STATE_CREATION_TIME="creationTime";
  private long creationTime=-1L;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    setContentView(R.layout.activity_main);

    Log.d(getClass().getSimpleName(), "std onCreate() called");
  }

  @Override
  public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
    super.onCreate(savedInstanceState, persistentState);
    setContentView(R.layout.activity_main);

    Log.d(getClass().getSimpleName(), "persistable onCreate() called");
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);

    dumpBundleToLog("restore", savedInstanceState);
    creationTime=savedInstanceState.getLong(STATE_CREATION_TIME, -1L);

    Log.d(getClass().getSimpleName(), "onRestoreInstanceState() called");
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
    super.onRestoreInstanceState(savedInstanceState, persistentState);

    Log.d(getClass().getSimpleName(), "persistable onRestoreInstanceState() called");
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putLong(STATE_CREATION_TIME, getCreationTime());
    dumpBundleToLog("save", outState);

    Log.d(getClass().getSimpleName(), "onSaveInstanceState() called");
  }

  @Override
  public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
    super.onSaveInstanceState(outState, outPersistentState);

    Log.d(getClass().getSimpleName(), "persistable onSaveInstanceState() called");
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.settings) {
      startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
    }

    return(super.onOptionsItemSelected(item));
  }

  private long getCreationTime() {
    if (creationTime==-1L) {
      creationTime=System.currentTimeMillis();
    }

    return(creationTime);
  }

  // inspired by http://stackoverflow.com/a/14948713/115145

  private void dumpBundleToLog(String msg, BaseBundle b) {
    for (String key: b.keySet()) {
      Log.d(getClass().getSimpleName(),
          String.format("(%s) %s: %s", msg, key, b.get(key)));
    }
  }
}
