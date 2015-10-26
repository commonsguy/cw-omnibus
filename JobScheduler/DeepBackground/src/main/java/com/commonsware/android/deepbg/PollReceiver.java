/***
 Copyright (c) 2014-2015 CommonsWare, LLC
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

package com.commonsware.android.deepbg;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import java.lang.reflect.Field;

public class PollReceiver extends WakefulBroadcastReceiver {
  static final String EXTRA_PERIOD="period";
  static final String EXTRA_IS_DOWNLOAD="isDownload";

  @Override
  public void onReceive(Context ctxt, Intent i) {
    Log.d(getClass().getSimpleName(), "Received alarm broadcast");

    boolean isDownload=i.getBooleanExtra(EXTRA_IS_DOWNLOAD, false);
    startWakefulService(ctxt,
        new Intent(ctxt, DemoScheduledService.class)
            .putExtra(EXTRA_IS_DOWNLOAD, isDownload));

    long period=i.getLongExtra(EXTRA_PERIOD, -1);

    if (period>0) {
      scheduleExactAlarm(ctxt,
          (AlarmManager)ctxt.getSystemService(Context.ALARM_SERVICE),
          period, isDownload);
    }
  }

  static void scheduleExactAlarm(Context ctxt, AlarmManager alarms,
                                 long period, boolean isDownload) {
    Intent i=buildBaseIntent(ctxt)
        .putExtra(EXTRA_PERIOD, period)
        .putExtra(EXTRA_IS_DOWNLOAD, isDownload);
    PendingIntent pi=PendingIntent.getBroadcast(ctxt, 0, i, 0);

    if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1) {
      Log.e("PollReceiver", "allow while idle");
      alarms.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
          SystemClock.elapsedRealtime() + period, pi);
    }
    else if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
      alarms.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
          SystemClock.elapsedRealtime() + period, pi);
    }
    else {
      alarms.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
          SystemClock.elapsedRealtime() + period, pi);
    }
  }

  static void scheduleInexactAlarm(Context ctxt, AlarmManager alarms,
                                   long period, boolean isDownload) {
    Intent i=buildBaseIntent(ctxt).putExtra(EXTRA_IS_DOWNLOAD, isDownload);
    PendingIntent pi=PendingIntent.getBroadcast(ctxt, 0, i, 0);

    alarms.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
        SystemClock.elapsedRealtime() + period, period, pi);
  }

  static void cancelAlarm(Context ctxt, AlarmManager alarms) {
    Intent i=buildBaseIntent(ctxt);
    PendingIntent pi=PendingIntent.getBroadcast(ctxt, 0, i, 0);

    alarms.cancel(pi);
  }

  static Intent buildBaseIntent(Context ctxt) {
    return(new Intent(ctxt, PollReceiver.class));
  }

  static boolean doesPendingIntentExist(Context ctxt) {
    return(PendingIntent
        .getBroadcast(ctxt, 0, buildBaseIntent(ctxt),
            PendingIntent.FLAG_NO_CREATE)!=null);
  }
}
