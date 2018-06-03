/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.broadcast.fanout;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import java.util.List;

public class MainActivity extends FragmentActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
      getSupportFragmentManager().beginTransaction()
                          .add(android.R.id.content,
                               new EventLogFragment()).commit();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent i=null;

    switch(item.getItemId()) {
      case R.id.explicit:
        i=new Intent(this, TestReceiver.class);
        break;

      case R.id.implicit:
        i=new Intent(BuildConfig.APPLICATION_ID+".TEST");
        break;

      case R.id.fanout:
        i=new Intent(BuildConfig.APPLICATION_ID+".TEST")
          .putExtra(TestReceiver.EXTRA_IS_FANOUT, true);
        break;
    }

    if (i==null) {
      return(super.onOptionsItemSelected(item));
    }

    i.putExtra(TestReceiver.EXTRA_TIME, System.currentTimeMillis());

    if (item.getItemId()==R.id.fanout) {
      sendImplicitBroadcast(this, i);
    }
    else {
      sendBroadcast(i);
    }

    return(true);
  }

  private static void sendImplicitBroadcast(Context ctxt, Intent i) {
    PackageManager pm=ctxt.getPackageManager();
    List<ResolveInfo> matches=pm.queryBroadcastReceivers(i, 0);

    for (ResolveInfo resolveInfo : matches) {
      Intent explicit=new Intent(i);
      ComponentName cn=
        new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName,
          resolveInfo.activityInfo.name);

      explicit.setComponent(cn);
      ctxt.sendBroadcast(explicit);
    }
  }
}