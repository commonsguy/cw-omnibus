/***
  Copyright (c) 2013 CommonsWare, LLC
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

package com.commonsware.android.eventbus.lbm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import java.util.Calendar;
import java.util.Random;

public class ScheduledService extends JobIntentService {
  private static int NOTIFY_ID=1337;
  private static final int UNIQUE_JOB_ID=1337;
  private static final String CHANNEL_WHATEVER="channel_whatever";
  private Random rng=new Random();

  static void enqueueWork(Context ctxt) {
    enqueueWork(ctxt, ScheduledService.class, UNIQUE_JOB_ID,
      new Intent(ctxt, ScheduledService.class));
  }

  @Override
  public void onHandleWork(Intent i) {
    Intent event=new Intent(EventLogFragment.ACTION_EVENT);
    long now=Calendar.getInstance().getTimeInMillis();
    int random=rng.nextInt();

    event.putExtra(EventLogFragment.EXTRA_RANDOM, random);
    event.putExtra(EventLogFragment.EXTRA_TIME, now);

    if (!LocalBroadcastManager.getInstance(this).sendBroadcast(event)) {
      NotificationManager mgr=
        (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

      if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
        mgr.getNotificationChannel(CHANNEL_WHATEVER)==null) {
        mgr.createNotificationChannel(new NotificationChannel(CHANNEL_WHATEVER,
          "Whatever", NotificationManager.IMPORTANCE_DEFAULT));
      }

      NotificationCompat.Builder b=new NotificationCompat.Builder(this, CHANNEL_WHATEVER);
      Intent ui=new Intent(this, EventDemoActivity.class);

      b.setAutoCancel(true).setDefaults(Notification.DEFAULT_SOUND)
       .setContentTitle(getString(R.string.notif_title))
       .setContentText(Integer.toHexString(random))
       .setSmallIcon(android.R.drawable.stat_notify_more)
       .setTicker(getString(R.string.notif_title))
       .setContentIntent(PendingIntent.getActivity(this, 0, ui, 0));
      
      mgr.notify(NOTIFY_ID, b.build());
    }
  }
}
