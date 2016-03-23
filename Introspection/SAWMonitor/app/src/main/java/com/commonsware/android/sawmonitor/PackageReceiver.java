/***
 Copyright (c) 2016 CommonsWare, LLC
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

package com.commonsware.android.sawmonitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;
import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.content.Intent.ACTION_PACKAGE_REPLACED;

public class PackageReceiver extends BroadcastReceiver {
  static final int NOTIFY_ID=3431;
  private static final long ADD_THEN_REPLACE_DELTA=2000L;
  private static HashMap<String, Long> ADD_TIMESTAMPS=
    new HashMap<String, Long>();

  static boolean hasSAW(Context ctxt, String pkg) {
    SharedPreferences prefs=
      PreferenceManager.getDefaultSharedPreferences(ctxt);
    Set<String> whitelist=
      prefs.getStringSet(WhitelistReceiver.PREF_WHITELIST,
        new HashSet<String>());

    if (whitelist.contains(pkg)) {
      return (false);
    }

    PackageManager pm=ctxt.getPackageManager();

    return(pm.checkPermission(SYSTEM_ALERT_WINDOW, pkg)==
      PackageManager.PERMISSION_GRANTED);
  }

  @Override
  public void onReceive(Context ctxt, Intent intent) {
    String pkg=intent.getData().getSchemeSpecificPart();

    if (ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
      ADD_TIMESTAMPS.put(pkg, SystemClock.uptimeMillis());
      seeSAW(ctxt, pkg, false);
    }
    else if (ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
      Long added=ADD_TIMESTAMPS.get(pkg);

      if (added==null ||
        (SystemClock.uptimeMillis()-added)>ADD_THEN_REPLACE_DELTA) {
        seeSAW(ctxt, pkg, true);

        if (added!=null) {
          ADD_TIMESTAMPS.remove(pkg);
        }
      }
    }
  }

  private void seeSAW(Context ctxt, String pkg, boolean isReplace) {
    if (hasSAW(ctxt, pkg)) {
      Uri pkgUri=Uri.parse("package:"+pkg);
      Intent manage=
        new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);

      manage.setData(pkgUri);

      Intent whitelist=new Intent(ctxt, WhitelistReceiver.class);

      whitelist.setData(pkgUri);

      Intent main=new Intent(ctxt, MainActivity.class);

      main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

      NotificationCompat.Builder b=new NotificationCompat.Builder(ctxt);
      String text=
        String.format(ctxt.getString(R.string.msg_requested),
          isReplace ?
            ctxt.getString(R.string.msg_upgraded) :
            ctxt.getString(R.string.msg_installed),
          pkg);

      b.setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_ALL)
        .setWhen(System.currentTimeMillis())
        .setContentTitle(ctxt.getString(R.string.msg_detected))
        .setContentText(text)
        .setSmallIcon(android.R.drawable.stat_notify_error)
        .setTicker(ctxt.getString(R.string.msg_detected))
        .setContentIntent(
          PendingIntent.getActivity(ctxt, 0, manage,
            PendingIntent.FLAG_UPDATE_CURRENT))
        .addAction(R.drawable.ic_verified_user_24dp,
          ctxt.getString(R.string.msg_whitelist),
          PendingIntent.getBroadcast(ctxt, 0, whitelist, 0))
        .addAction(R.drawable.ic_settings_24dp,
          ctxt.getString(R.string.msg_settings),
          PendingIntent.getActivity(ctxt, 0, main, 0));

      NotificationManager mgr=
        (NotificationManager)ctxt.getSystemService(
          Context.NOTIFICATION_SERVICE);

      mgr.notify(NOTIFY_ID, b.build());
    }
  }
}
