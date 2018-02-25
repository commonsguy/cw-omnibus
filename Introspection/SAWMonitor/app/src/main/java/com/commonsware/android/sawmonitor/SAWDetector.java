/***
 Copyright (c) 2016-2018 CommonsWare, LLC
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

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ChangedPackages;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import java.util.HashSet;
import java.util.Set;
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;

class SAWDetector {
  static final int NOTIFY_ID=3431;
  private static final int JOB_ID=1337;
  private static final String PREF_SEQUENCE="seq";
  private static final String CHANNEL_WHATEVER="channel_whatever";

  private static boolean hasSAW(Context ctxt, String pkg) {
    SharedPreferences prefs=
      PreferenceManager.getDefaultSharedPreferences(ctxt);
    Set<String> whitelist=
      prefs.getStringSet(WhitelistReceiver.PREF_WHITELIST,
        new HashSet<String>());

    if (whitelist.contains(pkg)) {
      return false;
    }

    PackageManager pm=ctxt.getPackageManager();

    return(pm.checkPermission(SYSTEM_ALERT_WINDOW, pkg)==
      PackageManager.PERMISSION_GRANTED);
  }

  static void seeSAW(Context ctxt, String pkg, String operation) {
    if (hasSAW(ctxt, pkg)) {
      Uri pkgUri=Uri.parse("package:"+pkg);
      Intent manage=
        new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);

      manage.setData(pkgUri);

      Intent whitelist=new Intent(ctxt, WhitelistReceiver.class);

      whitelist.setData(pkgUri);

      Intent main=new Intent(ctxt, MainActivity.class);

      main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

      NotificationManager mgr=
        (NotificationManager)ctxt.getSystemService(
          Context.NOTIFICATION_SERVICE);

      if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
        mgr.getNotificationChannel(CHANNEL_WHATEVER)==null) {
        mgr.createNotificationChannel(new NotificationChannel(CHANNEL_WHATEVER,
          "Whatever", NotificationManager.IMPORTANCE_DEFAULT));
      }

      NotificationCompat.Builder b=
        new NotificationCompat.Builder(ctxt, CHANNEL_WHATEVER);
      String text=String.format(ctxt.getString(R.string.msg_requested), operation,pkg);

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

      mgr.notify(NOTIFY_ID, b.build());
    }
  }

  @TargetApi(Build.VERSION_CODES.O)
  static void seeSAW(Context ctxt) {
    SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(ctxt);
    int sequence=prefs.getInt(PREF_SEQUENCE, 0);
    PackageManager pm=ctxt.getPackageManager();
    ChangedPackages delta=pm.getChangedPackages(sequence);

    if (delta!=null) {
      prefs.edit().putInt(PREF_SEQUENCE, delta.getSequenceNumber()).apply();

      if (sequence>0) {
        String msg=ctxt.getString(R.string.msg_something);

        for (String pkg : delta.getPackageNames()) {
          SAWDetector.seeSAW(ctxt, pkg, msg);
        }
      }
    }
  }

  @TargetApi(Build.VERSION_CODES.O)
  static void scheduleJobs(Context ctxt) {
    SAWDetector.seeSAW(ctxt);

    ComponentName cn=new ComponentName(ctxt, PollingService.class);
    JobInfo.Builder b=new JobInfo.Builder(JOB_ID, cn)
      .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
      .setPeriodic(15 * 60 * 1000, 60000)
      .setPersisted(true)
      .setRequiresCharging(false)
      .setRequiresDeviceIdle(false);

    ctxt.getSystemService(JobScheduler.class).schedule(b.build());
  }

  @TargetApi(Build.VERSION_CODES.O)
  static void cancelJobs(Context ctxt) {
    ctxt.getSystemService(JobScheduler.class).cancelAll();
  }
}
