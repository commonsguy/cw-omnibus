/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.prognotify;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

public class SillyService extends IntentService {
  private static int NOTIFICATION_ID=1337;

  public SillyService() {
    super("SillyService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    NotificationManager mgr=
        (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    NotificationCompat.Builder builder=
        new NotificationCompat.Builder(this);

    builder.setTicker(getText(R.string.ticker))
           .setContentTitle(getString(R.string.progress_notification))
           .setContentText(getString(R.string.busy))
           .setContentIntent(buildContentIntent())
           .setSmallIcon(R.drawable.ic_stat_notif_small_icon)
           .setOngoing(true);

    for (int i=0; i < 20; i++) {
      builder.setProgress(20, i, false);
      mgr.notify(NOTIFICATION_ID, builder.build());

      SystemClock.sleep(1000);
    }

    builder.setContentText(getString(R.string.done))
           .setProgress(0, 0, false).setOngoing(false);

    mgr.notify(NOTIFICATION_ID, builder.build());
  }

  private PendingIntent buildContentIntent() {
    Intent i=new Intent(Settings.ACTION_SETTINGS);

    return(PendingIntent.getActivity(this, 0, i, 0));
  }
}