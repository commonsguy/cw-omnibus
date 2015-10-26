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

package com.commonsware.android.webserver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class StopReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    Intent i=
      new Intent(context.getString(R.string.service_action))
        .setPackage(context.getPackageName());

    PackageManager mgr=context.getPackageManager();

    for (ResolveInfo ri : mgr.queryIntentServices(i, 0)) {
      ComponentName cn=
        new ComponentName(ri.serviceInfo.applicationInfo.packageName,
          ri.serviceInfo.name);
      Intent stop=new Intent().setComponent(cn);

      context.stopService(stop);
    }
  }
}
