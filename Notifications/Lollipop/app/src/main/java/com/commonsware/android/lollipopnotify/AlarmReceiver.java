/***
 Copyright (c) 2014 CommonsWare, LLC
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

package com.commonsware.android.lollipopnotify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
  private static final String CHANNEL_WHATEVER="channel_whatever";
  private static final String CHANNEL_HEADS_UP="channel_heads_up";
  private static final int NOTIFY_ID=1337;
  static final String EXTRA_TYPE="type";

  @Override
  public void onReceive(Context ctxt, Intent i) {
    NotificationManager mgr=
      (NotificationManager)ctxt.getSystemService(Context.NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
      mgr.getNotificationChannel(CHANNEL_WHATEVER)==null) {
      mgr.createNotificationChannel(new NotificationChannel(CHANNEL_WHATEVER,
        "Whatever", NotificationManager.IMPORTANCE_DEFAULT));
      mgr.createNotificationChannel(new NotificationChannel(CHANNEL_HEADS_UP,
        "Heads Up!", NotificationManager.IMPORTANCE_HIGH));
    }

    NotificationManagerCompat mgrCompat=NotificationManagerCompat.from(ctxt);

    switch (i.getIntExtra(EXTRA_TYPE, -1)) {
      case 0:
        notifyPrivate(ctxt, mgrCompat);
        break;

      case 1:
        notifyPublic(ctxt, mgrCompat);
        break;

      case 2:
        notifySecret(ctxt, mgrCompat);
        break;

      case 3:
        notifyHeadsUp(ctxt, mgrCompat);
        break;
    }
  }

  private void notifyPrivate(Context ctxt, NotificationManagerCompat mgr) {
    Notification pub=
      buildBase(ctxt, CHANNEL_WHATEVER, R.string.public_title).build();

    mgr.notify(NOTIFY_ID,
      buildBase(ctxt, CHANNEL_WHATEVER, R.string.private_title).setPublicVersion(pub).build());
  }

  private void notifyPublic(Context ctxt, NotificationManagerCompat mgr) {
    mgr.notify(NOTIFY_ID,
        buildBase(ctxt, CHANNEL_WHATEVER, R.string.public_title)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build());
  }

  private void notifySecret(Context ctxt, NotificationManagerCompat mgr) {
    mgr.notify(NOTIFY_ID,
        buildBase(ctxt, CHANNEL_WHATEVER, R.string.secret_title)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .build());
  }

  private void notifyHeadsUp(Context ctxt, NotificationManagerCompat mgr) {
    mgr.notify(NOTIFY_ID,
        buildBase(ctxt, CHANNEL_HEADS_UP, R.string.headsup_title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build());
  }

  private NotificationCompat.Builder buildBase(Context ctxt, String channel,
                                               int titleId) {
    NotificationCompat.Builder b=
      new NotificationCompat.Builder(ctxt, channel);

    b.setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_ALL)
        .setContentTitle(ctxt.getString(titleId))
        .setContentIntent(buildPendingIntent(ctxt, Settings.ACTION_SECURITY_SETTINGS))
        .setSmallIcon(android.R.drawable.stat_sys_download_done)
        .addAction(android.R.drawable.ic_media_play,
            ctxt.getString(R.string.play),
            buildPendingIntent(ctxt, Settings.ACTION_SETTINGS));

    return(b);
  }

  private PendingIntent buildPendingIntent(Context ctxt, String action) {
    Intent i=new Intent(action);

    return(PendingIntent.getActivity(ctxt, 0, i, 0));
  }
}
