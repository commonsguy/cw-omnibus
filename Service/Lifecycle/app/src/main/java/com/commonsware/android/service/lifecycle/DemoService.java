/***
  Copyright (c) 2013-2017 CommonsWare, LLC
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

package com.commonsware.android.service.lifecycle;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class DemoService extends Service {
  static final String EXTRA_FOREGROUND="fg";
  static final String EXTRA_IMPORTANTISH="importantish";
  private static final String CHANNEL_MIN="channel_min";
  private static final String CHANNEL_LOW="channel_low";
  private NotificationManager mgr;

  static void startMeUp(Context ctxt, boolean foreground, boolean importantish) {
    Intent i=new Intent(ctxt, DemoService.class)
      .putExtra(EXTRA_FOREGROUND, foreground)
      .putExtra(EXTRA_IMPORTANTISH, importantish);

    if (foreground && Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
      ctxt.startForegroundService(i);
    }
    else {
      ctxt.startService(i);
    }
  }

  @Override
  public void onCreate() {
    super.onCreate();

    mgr=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
      initChannels();
    }

    Log.d("DemoService", "onCreate()");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d("DemoService", "onStartCommand()");

    boolean foreground=intent.getBooleanExtra(EXTRA_FOREGROUND, false);
    boolean importantish=intent.getBooleanExtra(EXTRA_IMPORTANTISH, false);

    if (foreground) {
      String channel=importantish ? CHANNEL_LOW : CHANNEL_MIN;

      startForeground(1337, buildForegroundNotification(channel));
    }

    return(super.onStartCommand(intent, flags, startId));
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    Log.d("DemoService", "onBind()");

    throw new IllegalStateException("WTF?");
  }

  @Override
  public void onDestroy() {
    Log.d("DemoService", "onDestroy()");

    super.onDestroy();
  }

  @TargetApi(Build.VERSION_CODES.O)
  private void initChannels() {
    NotificationChannel channel=
      new NotificationChannel(CHANNEL_MIN, getString(R.string.channel_min),
        NotificationManager.IMPORTANCE_MIN);

    mgr.createNotificationChannel(channel);

    channel=
      new NotificationChannel(CHANNEL_LOW, getString(R.string.channel_low),
        NotificationManager.IMPORTANCE_LOW);
    mgr.createNotificationChannel(channel);
  }

  private Notification buildForegroundNotification(String channel) {
    NotificationCompat.Builder b=new NotificationCompat.Builder(this, channel);

    b.setOngoing(true)
      .setContentTitle(getString(R.string.notify_working))
      .setSmallIcon(android.R.drawable.stat_sys_download);

    return(b.build());
  }
}
