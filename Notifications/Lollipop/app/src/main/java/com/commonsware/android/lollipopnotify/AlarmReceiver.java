/***
 Copyright (c) 2014 CommonsWare, LLC
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

package com.commonsware.android.lollipopnotify;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
  private static final int NOTIFY_ID=1337;
  static final String EXTRA_TYPE="type";

  @Override
  public void onReceive(Context ctxt, Intent i) {
    NotificationManagerCompat mgr=NotificationManagerCompat.from(ctxt);

    switch (i.getIntExtra(EXTRA_TYPE, -1)) {
      case 0:
        notifyPrivate(ctxt, mgr);
        break;

      case 1:
        notifyPublic(ctxt, mgr);
        break;

      case 2:
        notifySecret(ctxt, mgr);
        break;

      case 3:
        notifyHeadsUp(ctxt, mgr);
        break;
    }
  }

  private void notifyPrivate(Context ctxt, NotificationManagerCompat mgr) {
    Notification pub=buildPublic(ctxt).build();

    mgr.notify(NOTIFY_ID, buildNormal(ctxt).setPublicVersion(pub).build());
  }

  private void notifyPublic(Context ctxt, NotificationManagerCompat mgr) {
    mgr.notify(NOTIFY_ID,
        buildNormal(ctxt)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build());
  }

  private void notifySecret(Context ctxt, NotificationManagerCompat mgr) {
    mgr.notify(NOTIFY_ID,
        buildNormal(ctxt)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .build());
  }

  private void notifyHeadsUp(Context ctxt, NotificationManagerCompat mgr) {
    mgr.notify(NOTIFY_ID,
        buildNormal(ctxt)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build());
  }

  private NotificationCompat.Builder buildNormal(Context ctxt) {
    NotificationCompat.Builder b=new NotificationCompat.Builder(ctxt);

    b.setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_ALL)
        .setContentTitle(ctxt.getString(R.string.download_complete))
        .setContentText(ctxt.getString(R.string.fun))
        .setContentIntent(buildPendingIntent(ctxt, Settings.ACTION_SECURITY_SETTINGS))
        .setSmallIcon(android.R.drawable.stat_sys_download_done)
        .setTicker(ctxt.getString(R.string.download_complete))
        .addAction(android.R.drawable.ic_media_play,
            ctxt.getString(R.string.play),
            buildPendingIntent(ctxt, Settings.ACTION_SETTINGS));

    return(b);
  }

  private NotificationCompat.Builder buildPublic(Context ctxt) {
    NotificationCompat.Builder b=new NotificationCompat.Builder(ctxt);

    b.setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_ALL)
        .setContentTitle(ctxt.getString(R.string.public_title))
        .setContentText(ctxt.getString(R.string.public_text))
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
