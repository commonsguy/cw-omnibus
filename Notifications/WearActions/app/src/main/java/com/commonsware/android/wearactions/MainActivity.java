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

package com.commonsware.android.wearactions;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class MainActivity extends Activity {
  private static final String CHANNEL_WHATEVER="channel_whatever";
  private static final int NOTIFY_ID=1337;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    NotificationManager mgr=
      (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
      mgr.getNotificationChannel(CHANNEL_WHATEVER)==null) {
      mgr.createNotificationChannel(new NotificationChannel(CHANNEL_WHATEVER,
        "Whatever", NotificationManager.IMPORTANCE_DEFAULT));
    }

    NotificationCompat.Builder normal=buildNormal();
    NotificationCompat.Action.Builder wearActionBuilder=
        new NotificationCompat.Action.Builder(android.R.drawable.ic_media_pause,
                                              getString(R.string.pause),
                                              buildPendingIntent(Settings.ACTION_DATE_SETTINGS));

    NotificationCompat.Builder extended=
        new NotificationCompat.WearableExtender()
            .addAction(wearActionBuilder.build())
            .extend(normal);

    NotificationManagerCompat.from(this).notify(NOTIFY_ID, extended.build());

    finish();
  }

  private NotificationCompat.Builder buildNormal() {
    NotificationCompat.Builder b=
      new NotificationCompat.Builder(this, CHANNEL_WHATEVER);

    b.setAutoCancel(true)
     .setDefaults(Notification.DEFAULT_ALL)
     .setContentTitle(getString(R.string.download_complete))
     .setContentText(getString(R.string.fun))
     .setContentIntent(buildPendingIntent(Settings.ACTION_SECURITY_SETTINGS))
     .setSmallIcon(android.R.drawable.stat_sys_download_done)
     .addAction(android.R.drawable.ic_media_play,
        getString(R.string.play),
        buildPendingIntent(Settings.ACTION_SETTINGS));

    return(b);
  }

  private PendingIntent buildPendingIntent(String action) {
    Intent i=new Intent(action);

    return(PendingIntent.getActivity(this, 0, i, 0));
  }
}
