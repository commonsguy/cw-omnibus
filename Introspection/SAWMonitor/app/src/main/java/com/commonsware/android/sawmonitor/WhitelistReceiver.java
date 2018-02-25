/***
 Copyright (c) 2016 CommonsWare, LLC
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

package com.commonsware.android.sawmonitor;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.HashSet;
import java.util.Set;

public class WhitelistReceiver extends BroadcastReceiver {
  static final String PREF_WHITELIST="whitelist";

  @Override
  public void onReceive(Context ctxt, Intent intent) {
    String pkg=intent.getData().getSchemeSpecificPart();
    SharedPreferences prefs=
      PreferenceManager.getDefaultSharedPreferences(ctxt);
    Set<String> whitelist=prefs.getStringSet(PREF_WHITELIST,
      new HashSet<String>());

    whitelist.add(pkg);

    prefs.edit().putStringSet(PREF_WHITELIST, whitelist).apply();

    NotificationManager mgr=
      (NotificationManager)ctxt.getSystemService(
        Context.NOTIFICATION_SERVICE);

    mgr.cancel(SAWDetector.NOTIFY_ID);
  }
}
